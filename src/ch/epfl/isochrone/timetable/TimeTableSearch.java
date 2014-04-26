package ch.epfl.isochrone.timetable;

import java.io.IOException;
import java.util.*;

/**
 * Classe principale du projet à ce stade ; permet de communiquer avec le code
 * @author Tristan Deloche (234045)
 */

public class TimeTableSearch {
    public static void main(String[] arg) throws IOException {

        /**
         * On sait jamais vaut mieux vérifier...
         */
        if (arg.length < 3) {
            throw new IllegalArgumentException("NEEDS MORE ARGUMENTS");
        } else if (arg.length > 3) {
            throw new IllegalArgumentException("NEEDS LESS ARGUMENTS");
        }

        /**
         * Création et lecture des données de l'Horaire
         */
        TimeTableReader myTimeTableReader = new TimeTableReader("/time-table/");
        TimeTable myTimeTable = myTimeTableReader.readTimeTable();

        String[] argDateArray = arg[1].split("-");
        Integer[] dateArray = new Integer[3];

        for (int i = 0; i < argDateArray.length; i++) {
            dateArray[i] = Integer.parseInt(argDateArray[i]);
        }

        Set<Stop> stopSet = new HashSet<>(myTimeTable.stops());

        /**
         * Création du graphe
         */
        Graph myGraph = myTimeTableReader.readGraphForServices(stopSet, new HashSet<>(myTimeTable.servicesForDate(new Date(dateArray[2], dateArray[1], dateArray[0]))), 300, 1.25);

        /**
         * LinkedList modifiée pour classer par ordre alphabétique ses élements car ils ne sont pas des String
         */
        List<Stop> stopList = new LinkedList<>();
        stopList.addAll(stopSet);
        Collections.sort(stopList, new Comparator<Stop>() {
            @Override
            public int compare(Stop s1, Stop s2) {
                return s1.name().compareTo(s2.name());
            }
        });

        Stop firstStop = new Stop("NULL", null);

        for (Stop aStop : stopList) {
            if (aStop.name().equals(arg[0])) {
                firstStop = aStop;
            }
        }

        String[] hourArray = arg[2].split(":");

        /**
         * Initialisation du fastestpath et appel de l'algorithme de Dijkstra
         */
        FastestPathTree fastestPaths = myGraph.fastestPaths(firstStop, SecondsPastMidnight.fromHMS(Integer.parseInt(hourArray[0]), Integer.parseInt(hourArray[1]), Integer.parseInt(hourArray[2])));

        /**
         * Print dans l'ordre le plus rapide des stops, chemins etc...
         */
        for (Stop aStop : stopList) {
            if (fastestPaths.arrivalTime(aStop) != SecondsPastMidnight.INFINITE) {
                System.out.println(aStop.name() + " : " + SecondsPastMidnight.toString(fastestPaths.arrivalTime(aStop)));
                System.out.println(" via : " + fastestPaths.pathTo(aStop));
            }
        }
    }
}
