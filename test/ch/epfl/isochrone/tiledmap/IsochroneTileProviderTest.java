package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static ch.epfl.isochrone.RandomStuffGenerator.generateColorTable;

public class IsochroneTileProviderTest {

    @Test
    public void test() throws IOException {

    }

    @Test
    public void testTileAt() throws Exception {
        String[] arg = new String[3];
        arg[0] = "EPFL";
        arg[1] = "2013-05-20";
        arg[2] = "11:08:00";

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

        ColorTable myColorTable = generateColorTable();
        new IsochroneTileProvider(fastestPaths, myColorTable, 10).tileAt(17, 67926, 46355);
    }
}