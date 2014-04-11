package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * TESTCLASS : GRAPH.
 * @author Tristan Deloche (234045)
 */

public class TestGraph {

    @Test
    public void testFastestPaths() {

        List<Stop> list1 = new LinkedList<>();
        List<Stop> list2 = new LinkedList<>();
        List<Stop> list3 = new LinkedList<>();
        List<Stop> list4 = new LinkedList<>();
        Stop s1 = new Stop("s1", new PointWGS84(0, 1));
        Stop s2 = new Stop("s2", new PointWGS84(-1, 0));
        Stop s3 = new Stop("s3", new PointWGS84(-1, 1));
        Stop s4 = new Stop("s4", new PointWGS84(-0.5, 0.5));
        Stop s5 = new Stop("s5", new PointWGS84(0, 0.5));

        list1.add(s1);
        list1.add(s2);
        list2.add(s1);
        list2.add(s2);
        list2.add(s3);
        list3.add(s1);
        list3.add(s2);
        list3.add(s3);
        list3.add(s4);
        list4.add(s1);
        list4.add(s2);
        list4.add(s3);
        list4.add(s4);
        list4.add(s5);

        Set<Stop> mesStop = new HashSet<>();
        mesStop.add(s5);
        mesStop.add(s4);
        mesStop.add(s3);
        mesStop.add(s2);
        mesStop.add(s1);

        Graph.Builder b = new Graph.Builder(mesStop);
        b.addTripEdge(s1, s2, 150, 200);
        b.addTripEdge(s1, s3, 1000, 1300);
        b.addTripEdge(s1, s5, 100, 2000);
        b.addTripEdge(s2, s3, 300, 500);
        b.addTripEdge(s3, s4, 700, 800);
        b.addTripEdge(s2, s4, 1000, 3000);
        b.addTripEdge(s4, s5, 900, 1000);
        b.addAllWalkEdges(1, 1.5);

        Graph g = b.build();
        FastestPathTree f = g.fastestPaths(s1, 50);
        assertEquals(list2, f.pathTo(s3));
        assertEquals(list1, f.pathTo(s2));
        assertEquals(list3, f.pathTo(s4));
        assertEquals(list4, f.pathTo(s5));
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestFromStop() {

        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(new Stop("stop1", new PointWGS84(0, 1)));
        stopSet.add(new Stop("stop2", new PointWGS84(-1, 0)));
        stopSet.add(new Stop("stop3", new PointWGS84(-1, 1)));
        stopSet.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(stopSet);

        monBuilder.addTripEdge(new Stop("stopRaté", new PointWGS84(1, 0)),
                new Stop("stop1", new PointWGS84(0, 1)), 3, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestToStop() {

        Set<Stop> stopSet = new HashSet<>();
        stopSet.add(new Stop("stop1", new PointWGS84(0, 1)));
        stopSet.add(new Stop("stop2", new PointWGS84(-1, 0)));
        stopSet.add(new Stop("stop3", new PointWGS84(-1, 1)));
        stopSet.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(stopSet);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stopRaté", new PointWGS84(1, 0)), 3, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestDepartureTime() {

        Set<Stop> stopSet = new HashSet<>();
        stopSet.add(new Stop("stop1", new PointWGS84(0, 1)));
        stopSet.add(new Stop("stop2", new PointWGS84(-1, 0)));
        stopSet.add(new Stop("stop3", new PointWGS84(-1, 1)));
        stopSet.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(stopSet);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stop2", new PointWGS84(-1, 0)), -30, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestArrivalTime() {

        Set<Stop> stopSet = new HashSet<>();
        stopSet.add(new Stop("stop1", new PointWGS84(0, 1)));
        stopSet.add(new Stop("stop2", new PointWGS84(-1, 0)));
        stopSet.add(new Stop("stop3", new PointWGS84(-1, 1)));
        stopSet.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(stopSet);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stop2", new PointWGS84(-1, 0)), 2, -30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestArrivalTimeAndDepartureTime() {

        Set<Stop> stopSet = new HashSet<>();
        stopSet.add(new Stop("stop1", new PointWGS84(0, 1)));
        stopSet.add(new Stop("stop2", new PointWGS84(-1, 0)));
        stopSet.add(new Stop("stop3", new PointWGS84(-1, 1)));
        stopSet.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(stopSet);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stop2", new PointWGS84(-1, 0)), 2, 1);
    }

    @Test
    public void TestAddAllWalkEdge() {

        LinkedList<Set<Stop>> stopList = new LinkedList<>();
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-1, 0.5));

        for (int i = 0; i < 4; i++) {
            stopList.add(new HashSet<Stop>());
            stopList.get(i).add(stop1);
            stopList.get(i).add(stop2);
            stopList.get(i).add(stop3);
            stopList.get(i).add(stop4);
        }

        LinkedList<Graph.Builder> buildersList = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            buildersList.add(new Graph.Builder(stopList.get(i)));

            buildersList.get(i).addTripEdge(stop1, stop3, 2, 4);
            buildersList.get(i).addTripEdge(stop2, stop3, 2, 3);
            buildersList.get(i).addTripEdge(stop1, stop4, 1, 5);
            buildersList.get(i).addTripEdge(stop4, stop3, 3, 6);

            buildersList.get(i).addAllWalkEdges(12, 3);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestAddAllWalkEdgesArgIllegal() {
        LinkedList<Set<Stop>> stopSetsList = new LinkedList<>();
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-1, 0.5));

        for (int i = 0; i < 4; i++) {
            stopSetsList.add(new HashSet<Stop>());
            stopSetsList.get(i).add(stop1);
            stopSetsList.get(i).add(stop2);
            stopSetsList.get(i).add(stop3);
            stopSetsList.get(i).add(stop4);
        }

        LinkedList<Graph.Builder> builderLinkedList = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            builderLinkedList.add(new Graph.Builder(stopSetsList.get(i)));

            builderLinkedList.get(i).addTripEdge(stop1, stop3, 2, 4);
            builderLinkedList.get(i).addTripEdge(stop2, stop3, 2, 3);
            builderLinkedList.get(i).addTripEdge(stop1, stop4, 1, 5);
            builderLinkedList.get(i).addTripEdge(stop4, stop3, 3, 6);

            builderLinkedList.get(i).addAllWalkEdges( i * ((int) (Math.pow(-1, i)) * 3),((int) Math.pow(-1, i + 1) * 2));
        }
    }

    @Test
    public void TestBuild() {
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-1, 0.5));
        Set<Stop> stopSet = new HashSet<>();

        stopSet.add(stop1);
        stopSet.add(stop2);
        stopSet.add(stop3);
        stopSet.add(stop4);

        Graph.Builder builder = new Graph.Builder(stopSet);

        builder.addTripEdge(stop1, stop3, 200, 400);
        builder.addTripEdge(stop2, stop3, 223, 323);
        builder.addTripEdge(stop1, stop4, 141, 541);
        builder.addTripEdge(stop4, stop3, 354, 654);

        builder.addAllWalkEdges(12, 3);
    }
}
