package ch.epfl.isochrone.timetable;

import java.io.IOException;
import java.util.*;

/**
 * Classe principale du projet à ce stade ; permet de communiquer avec le code
 * @author Tristan Deloche (234045)
 */

public class TimeTableSearch {
    public static void main(String[] arg) throws IOException {
        TimeTableReader myTimeTableReader = new TimeTableReader("/time-table/");
        TimeTable myTimeTable = myTimeTableReader.readTimeTable();

        String[] argDateArray = arg[1].split("-");
        Integer[] dateArray = new Integer[3];

        for (int i = 0; i < argDateArray.length; i++) {
            dateArray[i] = Integer.parseInt(argDateArray[i]);
        }

        Set<Stop> stopSet = new HashSet<>(myTimeTable.stops());

        Graph myGraph = myTimeTableReader.readGraphForServices(stopSet, new HashSet<>(myTimeTable.servicesForDate(new Date(dateArray[2], dateArray[1], dateArray[0]))), 300, 5);

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
        FastestPathTree fastestPaths = myGraph.fastestPaths(firstStop, SecondsPastMidnight.fromHMS(Integer.parseInt(hourArray[0]), Integer.parseInt(hourArray[1]), Integer.parseInt(hourArray[2])));

        for (Stop aStop : stopList) {
            System.out.println(aStop.name()+" : "+SecondsPastMidnight.toString(fastestPaths.arrivalTime(aStop)));
            System.out.println(" via : "+fastestPaths.pathTo(aStop));
        }

    }
}
