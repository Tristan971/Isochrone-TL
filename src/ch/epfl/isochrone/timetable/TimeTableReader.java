package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Classe créant un horaire à l'aide de fichiers CSV de données
 * @author Tristan Deloche (234045)
 */

public final class TimeTableReader {
    InputStream calendarInputStream, calendarDatesInputStream, stopsInputStream, stopTimesInputStream;

    /**
     * Constructeur principal des TTR
     * @param baseResourceName
     *          Dossier dans lequel chercher les données
     * @throws IOException
     *          Si les fichiers sont introuvables ou illisibles
     */
    public TimeTableReader(String baseResourceName) throws IOException {
        this.calendarInputStream = getClass().getResourceAsStream(baseResourceName+"calendar.csv");
        this.calendarDatesInputStream = getClass().getResourceAsStream(baseResourceName+"calendar_dates.csv");
        this.stopsInputStream = getClass().getResourceAsStream(baseResourceName+"stops.csv");
        this.stopTimesInputStream = getClass().getResourceAsStream(baseResourceName+"stop_times.csv");
    }

    /**
     * Méthode servant à créer un Horaire (ch.epfl.isochrone.timetable.TimeTable) en lisant les fichiers
     * @return
     *      Un horaire généré à partir des données fournies
     * @throws IOException
     *      S'il y a un problème de lecture/accès de fichiers quelque part
     */
    public TimeTable readTimeTable() throws IOException {
        Map<String, Service.Builder> stringBuilderMap = new HashMap<>();
        Set<Stop> stopSet = new HashSet<>();
        String currentLine;

        //Reader des Services
        BufferedReader reader = makeReaderWithStream(calendarInputStream);
        while ((currentLine = reader.readLine()) != null) {
            String[] lineDataArray = currentLine.split(";");

            stringBuilderMap.put(lineDataArray[0], new Service.Builder(lineDataArray[0], makeDateWithString(lineDataArray[8]), makeDateWithString(lineDataArray[9])));

            for (Date.DayOfWeek aDay : getOperatingDays(lineDataArray)) {
                stringBuilderMap.get(lineDataArray[0]).addOperatingDay(aDay);
            }
        }
        reader.close();

        //Reader des exceptions des services
        reader = makeReaderWithStream(calendarDatesInputStream);
        while ((currentLine = reader.readLine()) != null) {
            String[] lineArray = currentLine.split(";");
            if (Integer.parseInt(lineArray[2])==2) {
                stringBuilderMap.get(lineArray[0]).addExcludedDate(makeDateWithString(lineArray[1]));
            } else if (Integer.parseInt(lineArray[2])==1) {
                stringBuilderMap.get(lineArray[0]).addIncludedDate(makeDateWithString(lineArray[1]));
            }
        }
        reader.close();

        //Reader des Stops
        reader = makeReaderWithStream(stopsInputStream);
        while ((currentLine = reader.readLine()) != null) {
            String[] stopsArray = currentLine.split(";");
            stopSet.add(makeStopWithLine(stopsArray));
        }
        reader.close();


        Set<Service> serviceSet = new HashSet<>();
        for (String aString : stringBuilderMap.keySet()) {
            serviceSet.add(stringBuilderMap.get(aString).build());
        }

        return new TimeTable(stopSet, serviceSet);
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
     * Méthode privée créant une date à l'aide d'une chaine de format D;M;Y;....
     * @param s
     *      Chaîne à utiliser
     * @return
     *      Une date créée à partir de la chaîne passée en argument
     */
    private Date makeDateWithString (String s) {
        Integer[] dateArray = new Integer[3];
        dateArray[0]=Integer.parseInt(s.substring(6,8));
        dateArray[1]=Integer.parseInt(s.substring(4,6));
        dateArray[2]=Integer.parseInt(s.substring(0,4));

        return new Date(dateArray[0],dateArray[1],dateArray[2]);
    }

    /**
     * Méthode cherchant l'ensemble des jours d'activité d'un service à l'aide d'un tableau de format "[...;bool;bool;bool;bool;bool;bool;bool;..........]"
     * @param lineArray
     *      Chaîne à utiliser pour récupérer l'information
     * @return
     *      L'ensemble des jours d'activité pour le service correspondant au tableau envoyée en argument
     */
    private Set<Date.DayOfWeek> getOperatingDays(String[] lineArray) {
        Set<Date.DayOfWeek> operatingDays = new HashSet<>();
        for (int i = 0; i <= 6; i++) {
            if (Boolean.parseBoolean(lineArray[i+1])) {
                operatingDays.add(Date.DayOfWeek.values()[i]);
            }
        }
        return  operatingDays;
    }

    /**
     * Crée un stop à l'aide d'une chaîne au format "Nom;Latitude;Longitude"
     * @param line
     *      Chaîne dont on veut le stop associé
     * @return
     *      Le stop associé
     */
    private Stop makeStopWithLine (String[] line) {
        return new Stop(line[0], new PointWGS84(Math.toRadians(Double.parseDouble(line[1])), Math.toRadians(Double.parseDouble(line[2]))));
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

        BufferedReader stopTimesReader = new BufferedReader(new InputStreamReader(stopTimesInputStream, StandardCharsets.UTF_8));

        while (stopTimesReader.readLine() != null) {
            String[] lineDataArray = stopTimesReader.readLine().split(";");
            if (stringServiceMap.containsKey(lineDataArray[0]) && stringStopMap.containsKey(lineDataArray[1]) && stringStopMap.containsKey(lineDataArray[3])) {
                graphBuilder.addTripEdge(stringStopMap.get(lineDataArray[1]), stringStopMap.get(lineDataArray[3]), Integer.parseInt(lineDataArray[2]), Integer.parseInt(lineDataArray[4]));
            }
        }

        graphBuilder.addAllWalkEdges(walkingTime, walkingSpeed);

        return graphBuilder.build();
    }
}
