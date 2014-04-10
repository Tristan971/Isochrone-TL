package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestGraph {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        // Graph n'a aucune méthode publique à ce stade...

        Set<Stop> stops = null;
        Stop stop = null;
        Graph.Builder gb = new Graph.Builder(stops);
        gb.addTripEdge(stop, stop, 0, 0);
        gb.addAllWalkEdges(0, 0);
        gb.build();
    }

    @Test
    public void testFastestPaths() {

        List<Stop> lStop2 = new ArrayList<Stop>();
        List<Stop> lStop3 = new ArrayList<Stop>();
        List<Stop> lStop4 = new ArrayList<Stop>();
        List<Stop> lStop5 = new ArrayList<Stop>();
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-0.5, 0.5));
        Stop stop5 = new Stop("stop5", new PointWGS84(0, 0.5));

        lStop2.add(stop1);
        lStop2.add(stop2);

        lStop3.add(stop1);
        lStop3.add(stop2);
        lStop3.add(stop3);

        lStop4.add(stop1);
        lStop4.add(stop2);
        lStop4.add(stop3);
        lStop4.add(stop4);

        lStop5.add(stop1);
        lStop5.add(stop2);
        lStop5.add(stop3);
        lStop5.add(stop4);
        lStop5.add(stop5);

        Set<Stop> mesStop = new HashSet<>();
        mesStop.add(stop5);
        mesStop.add(stop4);
        mesStop.add(stop3);
        mesStop.add(stop2);
        mesStop.add(stop1);

        Graph.Builder b = new Graph.Builder(mesStop);
        b.addTripEdge(stop1, stop2, 150, 200);
        b.addTripEdge(stop1, stop3, 1000, 1200);
        b.addTripEdge(stop1, stop5, 100, 2000);

        b.addTripEdge(stop2, stop3, 300, 500);
        b.addTripEdge(stop3, stop4, 700, 800);
        b.addTripEdge(stop2, stop4, 1000, 3000);
        b.addTripEdge(stop4, stop5, 900, 1000);
        b.addAllWalkEdges(0, 0.5);
        Graph g = b.build();

        FastestPathTree f = g.fastestPaths(stop1, 50);
        assertEquals(lStop3, f.pathTo(stop3));
        assertEquals(lStop2, f.pathTo(stop2));
        assertEquals(lStop4, f.pathTo(stop4));
        assertEquals(lStop5, f.pathTo(stop5));
        for (Stop stop : mesStop) {
            System.out.println(f.arrivalTime(stop) + " " + stop.name());
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestFromStop() {

        Set<Stop> setDeStop = new HashSet<Stop>();
        setDeStop.add(new Stop("stop1", new PointWGS84(0, 1)));
        setDeStop.add(new Stop("stop2", new PointWGS84(-1, 0)));
        setDeStop.add(new Stop("stop3", new PointWGS84(-1, 1)));
        setDeStop.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(setDeStop);

        monBuilder.addTripEdge(new Stop("stopRaté", new PointWGS84(1, 0)),
                new Stop("stop1", new PointWGS84(0, 1)), 3, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestToStop() {

        Set<Stop> setDeStop = new HashSet<>();
        setDeStop.add(new Stop("stop1", new PointWGS84(0, 1)));
        setDeStop.add(new Stop("stop2", new PointWGS84(-1, 0)));
        setDeStop.add(new Stop("stop3", new PointWGS84(-1, 1)));
        setDeStop.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(setDeStop);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stopRaté", new PointWGS84(1, 0)), 3, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestDepartureTime() {

        Set<Stop> setDeStop = new HashSet<Stop>();
        setDeStop.add(new Stop("stop1", new PointWGS84(0, 1)));
        setDeStop.add(new Stop("stop2", new PointWGS84(-1, 0)));
        setDeStop.add(new Stop("stop3", new PointWGS84(-1, 1)));
        setDeStop.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(setDeStop);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stop2", new PointWGS84(-1, 0)), -30, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestArrivalTime() {

        Set<Stop> setDeStop = new HashSet<Stop>();
        setDeStop.add(new Stop("stop1", new PointWGS84(0, 1)));
        setDeStop.add(new Stop("stop2", new PointWGS84(-1, 0)));
        setDeStop.add(new Stop("stop3", new PointWGS84(-1, 1)));
        setDeStop.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(setDeStop);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stop2", new PointWGS84(-1, 0)), 2, -30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTripEdgeTestArrivalTimeAndDepartureTime() {

        Set<Stop> setDeStop = new HashSet<Stop>();
        setDeStop.add(new Stop("stop1", new PointWGS84(0, 1)));
        setDeStop.add(new Stop("stop2", new PointWGS84(-1, 0)));
        setDeStop.add(new Stop("stop3", new PointWGS84(-1, 1)));
        setDeStop.add(new Stop("stop4", new PointWGS84(-1, 0.5)));

        Graph.Builder monBuilder = new Graph.Builder(setDeStop);

        monBuilder.addTripEdge(new Stop("stop1", new PointWGS84(0, 1)),
                new Stop("stop2", new PointWGS84(-1, 0)), 2, 1);
    }

    @Test
    public void TestAddAllWalkEdge() {

        ArrayList<Set<Stop>> setDeStop = new ArrayList<Set<Stop>>();
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-1, 0.5));

        for (int i = 0; i < 4; i++) {
            setDeStop.add(new HashSet<Stop>());
            setDeStop.get(i).add(stop1);
            setDeStop.get(i).add(stop2);
            setDeStop.get(i).add(stop3);
            setDeStop.get(i).add(stop4);
        }

        ArrayList<Graph.Builder> mesBuilder = new ArrayList<Graph.Builder>();

        for (int i = 0; i < 4; i++) {
            mesBuilder.add(new Graph.Builder(setDeStop.get(i)));

            mesBuilder.get(i).addTripEdge(stop1, stop3, 2, 4);
            mesBuilder.get(i).addTripEdge(stop2, stop3, 2, 3);
            mesBuilder.get(i).addTripEdge(stop1, stop4, 1, 5);
            mesBuilder.get(i).addTripEdge(stop4, stop3, 3, 6);

            mesBuilder.get(i).addAllWalkEdges(12, 3);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestAddAllWalkEdgesArgIllegal() {
        ArrayList<Set<Stop>> setDeStop = new ArrayList<Set<Stop>>();
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-1, 0.5));

        for (int i = 0; i < 4; i++) {
            setDeStop.add(new HashSet<Stop>());
            setDeStop.get(i).add(stop1);
            setDeStop.get(i).add(stop2);
            setDeStop.get(i).add(stop3);
            setDeStop.get(i).add(stop4);
        }

        ArrayList<Graph.Builder> mesBuilder = new ArrayList<Graph.Builder>();

        for (int i = 0; i < 4; i++) {
            mesBuilder.add(new Graph.Builder(setDeStop.get(i)));

            mesBuilder.get(i).addTripEdge(stop1, stop3, 2, 4);
            mesBuilder.get(i).addTripEdge(stop2, stop3, 2, 3);
            mesBuilder.get(i).addTripEdge(stop1, stop4, 1, 5);
            mesBuilder.get(i).addTripEdge(stop4, stop3, 3, 6);

            mesBuilder.get(i).addAllWalkEdges(
                    0 + i * ((int) (Math.pow(-1, i)) * 3),
                    ((int) Math.pow(-1, i + 1) * 2));
        }
    }

    @Test
    public void TestBuild() {
        Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-1, 0.5));
        Set<Stop> setDeStop = new HashSet<Stop>();

        setDeStop.add(stop1);
        setDeStop.add(stop2);
        setDeStop.add(stop3);
        setDeStop.add(stop4);

        Graph.Builder monBuilder = new Graph.Builder(setDeStop);

        monBuilder.addTripEdge(stop1, stop3, 200, 400);
        monBuilder.addTripEdge(stop2, stop3, 223, 323);
        monBuilder.addTripEdge(stop1, stop4, 141, 541);
        monBuilder.addTripEdge(stop4, stop3, 354, 654);

        monBuilder.addAllWalkEdges(12, 3);
    }
}
