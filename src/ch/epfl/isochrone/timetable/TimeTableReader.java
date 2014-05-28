package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe créant un horaire à l'aide de fichiers CSV de données
 * @author Tristan Deloche (234045)
 */

public final class TimeTableReader {
    private String baseResourceName;

    /**
     * Constructeur de classe par défaut
     * @param baseResourceName
     *          Dossier contenant les fichiers .csv
     */
    public TimeTableReader(String baseResourceName) {
        this.baseResourceName = baseResourceName;
    }

    /**
     * Lis l'horaire
     * @return
     *      L'horaire lié
     * @throws IOException
     *      Si erreur d'accès fichier
     */
    public TimeTable readTimeTable() throws IOException {
        TimeTable.Builder TTBuilder = new TimeTable.Builder();

        Map<String, Service.Builder> stringBuilderHashMap = new HashMap<>();

        BufferedReader bufferedReader = readerFromInputStream(getClass().getResourceAsStream(baseResourceName + "stops.csv"));
        String currentLine;

        /**
         * On lit les stops
         */
        while ((currentLine = bufferedReader.readLine()) != null) {
            TTBuilder.addStop(makeStopWithLine(currentLine));
        }
        bufferedReader.close();

        /**
         * On lit les services
         */
        bufferedReader = readerFromInputStream(getClass().getResourceAsStream(baseResourceName + "calendar.csv"));
        while ((currentLine = bufferedReader.readLine()) != null) {
            Service.Builder s = makeServiceWithLine(currentLine);
            stringBuilderHashMap.put(s.name(), s);
        }
        bufferedReader.close();

        /**
         * On lit les exceptions des services (inclues et exclues)
         */
        bufferedReader = readerFromInputStream(getClass().getResourceAsStream(baseResourceName + "calendar_dates.csv"));
        while ((currentLine = bufferedReader.readLine()) != null) {
            Date date = new Date(Integer.parseInt((currentLine.split(";")[1]).substring(6, 8)), Integer.parseInt((currentLine.split(";")[1]).substring(4, 6)), Integer.parseInt((currentLine.split(";")[1]).substring(0, 4)));

            if (!currentLine.split(";")[2].equals("2")) {
                stringBuilderHashMap.get(currentLine.split(";")[0]).addIncludedDate(date);
            } else {
                stringBuilderHashMap.get(currentLine.split(";")[0]).addExcludedDate(date);
            }
        }
        bufferedReader.close();

        for (Service.Builder serviceBuilder : stringBuilderHashMap.values()) {
            TTBuilder.addService(serviceBuilder.build());
        }

        /**
         * On se sert des données pour bâtir l'horaire
         */
        return TTBuilder.build();
    }

    /**
     * On crée un stop avec la ligne en argument qui provient de stops.csv
     * @param currentLine
     *          Ligne de stops.csv à utiliser
     * @return
     *          Stop lié
     */
    private Stop makeStopWithLine(String currentLine) {
        return new Stop(currentLine.split(";")[0], new PointWGS84(Math.toRadians(Double.parseDouble(currentLine.split(";")[2])), Math.toRadians(Double.parseDouble(currentLine.split(";")[1]))));
    }

    /**
     * On crée un bâtisseur de service avec une ligne
     * @param currentLine
     *          Ligne de calendar.csv à utiliser
     * @return
     *          Le bâtisseur du service lié
     */
    private Service.Builder makeServiceWithLine(String currentLine) {

        String[] currentLineArray = currentLine.split(";");

        Date serviceStartingDate = new Date(Integer.parseInt(currentLineArray[8].substring(6, 8)),
                Integer.parseInt(currentLineArray[8].substring(4, 6)),
                Integer.parseInt (currentLineArray[8].substring(0, 4)));

        Date serviceEndingDate = new Date(Integer.parseInt(currentLineArray[9].substring(6, 8)),
                Integer.parseInt(currentLineArray[9].substring(4, 6)),
                Integer.parseInt (currentLineArray[9].substring(0, 4)));

        Service.Builder monService = new Service.Builder(currentLine.split(";")[0], serviceStartingDate, serviceEndingDate);

        for (int i = 1; i < 8; i++) {
            if (Integer.parseInt(currentLine.split(";")[i]) == 1) {
                monService.addOperatingDay(Date.DayOfWeek.values()[i - 1]);
            }
        }

        return monService;
    }

    /**
     * Crée un BufferedReader à l'aide du paramètre
     * @param stream
     *      InputStream à utiliser
     * @return
     *      Un nouveau BufferedReader
     */
    private BufferedReader readerFromInputStream(InputStream stream) {
        return new  BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    /**
     * Méthode créant le graphe associant un ensemble d'arrêts et de services
     * @param stops
     *      Ensemble des arrêts considérés
     * @param services
     *      Ensemble des services considérés
     * @param walkingTime
     *      Temps de marche maximal accepté
     * @param walkingSpeed
     *      Vitesse de marche
     * @return
     *      Le graphe associant les services et stops donnés
     */
    public Graph readGraphForServices(Set<Stop> stops, Set<Service> services, int walkingTime, double walkingSpeed) throws IOException {
        Graph.Builder graphBuilder = new Graph.Builder(stops);

        Map<String, Stop> stringStopMap = new HashMap<>();
        for (Stop aStop : stops) {
            stringStopMap.put(aStop.name(), aStop);
        }

        Map<String, Service> stringServiceMap = new HashMap<>();
        for (Service aService : services) {
            stringServiceMap.put(aService.name(), aService);
        }

        String currentLine;
        BufferedReader reader = readerFromInputStream(getClass().getResourceAsStream(baseResourceName + "stop_times.csv"));
        while ((currentLine = reader.readLine()) != null) {
            String[] lineDataArray = currentLine.split(";");
            if (stringServiceMap.containsKey(lineDataArray[0]) && stringStopMap.containsKey(lineDataArray[1]) && stringStopMap.containsKey(lineDataArray[3])) {
               graphBuilder.addTripEdge(stringStopMap.get(lineDataArray[1]), stringStopMap.get(lineDataArray[3]), Integer.parseInt(lineDataArray[2]), Integer.parseInt(lineDataArray[4]));
            }
        }
        reader.close();
        return graphBuilder.addAllWalkEdges(walkingTime, walkingSpeed).build();
    }
}
