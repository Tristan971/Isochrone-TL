package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        this.calendarInputStream = getCalendarInputStream(baseResourceName);
        this.calendarDatesInputStream = getCalendarDatesInputStream(baseResourceName);
        this.stopsInputStream = getStopsInputStream(baseResourceName);
        this.stopTimesInputStream = getStopTimesInputStream(baseResourceName);
    }

    public static void main(String[] arg) {

    }

    /**
     * Crée un InputStream pour le fichier calendar.csv
     * @param baseResourceName
     *      Dossier dans lequel chercher le fichier
     * @return
     *      L'InputStream de calendar.csv
     */
    private InputStream getCalendarInputStream(String baseResourceName) {
        return getClass().getResourceAsStream(baseResourceName+"calendar.csv");
    }

    /**
     * Crée un InputStream pour le fichier calendar_dates.csv
     * @param baseResourceName
     *      Dossier dans lequel chercher le fichier
     * @return
     *      L'InputStream de calendar_dates.csv
     */
    private InputStream getCalendarDatesInputStream(String baseResourceName) {
        return getClass().getResourceAsStream(baseResourceName+"calendar_dates.csv");
    }

    /**
     * Crée un InputStream pour le fichier stops.csv
     * @param baseResourceName
     *      Dossier dans lequel chercher le fichier
     * @return
     *      L'InputStream de stops.csv
     */
    private InputStream getStopsInputStream(String baseResourceName) {
        return getClass().getResourceAsStream(baseResourceName+"stops.csv");
    }

    /**
     * Crée un InputStream pour le fichier stop_times.csv
     * @param baseResourceName
     *      Dossier dans lequel chercher le fichier
     * @return
     *      L'InputStream de stop_times.csv
     */
    private InputStream getStopTimesInputStream(String baseResourceName) {
        return getClass().getResourceAsStream(baseResourceName+"stop_times.csv");
    }

    /**
     * Méthode servant à créer un Horaire (ch.epfl.isochrone.timetable.TimeTable) en lisant les fichiers
     * @return
     *      Un horaire généré à partir des données fournies
     * @throws IOException
     *      S'il y a un problème de lecture/accès de fichiers quelque part
     */
    public TimeTable readTimeTable() throws IOException {
        BufferedReader calendarReader = new BufferedReader(new InputStreamReader(calendarInputStream, StandardCharsets.UTF_8));
        BufferedReader calendarDatesReader = new BufferedReader(new InputStreamReader(calendarDatesInputStream, StandardCharsets.UTF_8));
        BufferedReader stopsReader = new BufferedReader(new InputStreamReader(stopsInputStream, StandardCharsets.UTF_8));

        Set<Service> serviceSet = new HashSet<>();
        Set<Date> excludedDatesSet = new HashSet<>();
        Set<Date> includedDatesSet = new HashSet<>();
        Set<Stop> stopSet = new HashSet<>();


        while (calendarReader.readLine() != null) {
            String[] lineDataArray = calendarReader.readLine().split(";");
            String name = lineDataArray[0];

            Date startingDate = makeDateWithString(lineDataArray[8]);
            Date endingDate = makeDateWithString(lineDataArray[9]);

            Service.Builder serviceBuilder = new Service.Builder(lineDataArray[0],startingDate,endingDate);

            for (Date.DayOfWeek aDay : getOperatingDays(lineDataArray)) {
                serviceBuilder.addOperatingDay(aDay);
            }

            while (calendarDatesReader.readLine() != null) {
                String[] lineArray = calendarDatesReader.readLine().split(";");
                if (name.equals(lineArray[0]) && Integer.parseInt(lineArray[2])==2) {
                    excludedDatesSet.add(makeDateWithString(lineArray[1]));
                } else if (name.equals(lineArray[0]) && Integer.parseInt(lineArray[2])==1) {
                    includedDatesSet.add(makeDateWithString(lineArray[1]));
                }
            }

            for (Date aDate : excludedDatesSet) {
                serviceBuilder.addExcludedDate(aDate);
            }
            for (Date aDate : includedDatesSet) {
                serviceBuilder.addIncludedDate(aDate);
            }

            serviceSet.add(serviceBuilder.build());
        }

        while (stopsReader.readLine() != null) {
            String[] stopsArray = stopsReader.readLine().split(";");
            stopSet.add(makeStopWithLine(stopsArray));
        }

        return new TimeTable(stopSet, serviceSet);
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
        dateArray[1]=Integer.parseInt(s.substring(4,5));
        dateArray[2]=Integer.parseInt(s.substring(0,3));

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
        //TODO : Gérer unités
        return new Stop(line[0], new PointWGS84(Double.parseDouble(line[1]), Double.parseDouble(line[2])));
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
