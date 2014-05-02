package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.*;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

public class IsochroneTileProviderTest {
    @Test
    public void testTileAt() throws Exception {
        String[] arg = new String[3];
        arg[0] = "Lausanne-Flon";
        arg[1] = "2013-10-01";
        arg[2] = "06:08:00";
        TimeTableReader myTimeTableReader = new TimeTableReader("/time-table/");
        TimeTable myTimeTable = myTimeTableReader.readTimeTable();
        String[] argDateArray = arg[1].split("-");
        Integer[] dateArray = new Integer[3];
        for (int i = 0; i < argDateArray.length; i++) {
            dateArray[i] = Integer.parseInt(argDateArray[i]);
        }
        Set<Stop> stopSet = new HashSet<>(myTimeTable.stops());
        Graph myGraph = myTimeTableReader.readGraphForServices(stopSet, new HashSet<>(myTimeTable.servicesForDate(new Date(dateArray[2], dateArray[1], dateArray[0]))), 300, 1.25);
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

        LinkedList<Color> colorLinkedList = new LinkedList<>();

        colorLinkedList.add(new Color(0, 0, 0));
        colorLinkedList.add(new Color(0, 0, 255));
        colorLinkedList.add(new Color(0, 255, 0));
        colorLinkedList.add(new Color(255, 255, 0));
        colorLinkedList.add(new Color(255, 0, 0));

        ColorTable myColorTable = new ColorTable(5, colorLinkedList);
        new IsochroneTileProvider(fastestPaths, myColorTable, 10).tileAt(11, 1061, 724);
    }
}