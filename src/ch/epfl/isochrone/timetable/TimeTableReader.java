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
    /**
     * InputStreams modélisant les streams de chaque fichier
     */
    InputStream calendarInputStream, calendarDatesInputStream, stopsInputStream, stopTimesInputStream;

    /**
     * Constructeur de classe par défaut
     * @param baseResourceName
     *          Dossier contenant les fichiers .csv
     */
    public TimeTableReader(String baseResourceName) {
        this.stopsInputStream = getClass().getResourceAsStream(baseResourceName + "stops.csv");
        this.stopTimesInputStream = getClass().getResourceAsStream(baseResourceName + "stop_times.csv");
        this.calendarInputStream = getClass().getResourceAsStream(baseResourceName + "calendar.csv");
        this.calendarDatesInputStream = getClass().getResourceAsStream(baseResourceName + "calendar_dates.csv");
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

        BufferedReader bufferedReader = makeReaderWithStream(stopsInputStream);
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
        bufferedReader = makeReaderWithStream(calendarInputStream);
        while ((currentLine = bufferedReader.readLine()) != null) {
            stringBuilderHashMap.put(makeServiceWithLine(currentLine).name(), makeServiceWithLine(currentLine));
        }
        bufferedReader.close();

        /**
         * On lit les exceptions des services (inclues et exclues)
         */
        bufferedReader = makeReaderWithStream(calendarDatesInputStream);
        while ((currentLine = bufferedReader.readLine()) != null) {
            TTBuilder.addService(finishBuildingService(stringBuilderHashMap.get(currentLine.split(";")[0]), currentLine).build());
        }
        bufferedReader.close();

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
     * On complète les services avec les données récupérées après leur création
     * @param sb
     *          Bâtisseur à finir de remplir
     * @param line
     *          Ligne à utiliser
     * @return
     *          Le bâtisseur du service totalement rempli
     */
    private Service.Builder finishBuildingService(Service.Builder sb, String line) {
        int year = Integer.parseInt((line.split(";")[1]).substring(0, 4));
        int monthInt = Integer.parseInt((line.split(";")[1]).substring(4, 6));
        int day = Integer.parseInt((line.split(";")[1]).substring(6, 8));
        if (!line.split(";")[2].equals("2")) {
            sb.addIncludedDate(new Date(day, monthInt, year));
        } else {
            sb.addExcludedDate(new Date(day, monthInt, year));
        }
        return sb;
    }

    /**
     * Crée un BufferedReader à l'aide du paramètre
     * @param stream
     *      InputStream à utiliser
     * @return
     *      Un nouveau BufferedReader
     */
    private BufferedReader makeReaderWithStream(InputStream stream) {
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
        BufferedReader reader = makeReaderWithStream(stopTimesInputStream);
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
