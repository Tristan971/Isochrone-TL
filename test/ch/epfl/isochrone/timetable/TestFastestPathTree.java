package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestFastestPathTree {

    private Stop stop1 = new Stop("stop1", new PointWGS84(0, 1));
    private Stop stop2 = new Stop("stop2", new PointWGS84(1, 0));
    private Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));

    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Stop stop = null;
        Map<Stop, Integer> tempsArrives = new HashMap<Stop, Integer>();
        Map<Stop, Stop> predecessors = new HashMap<Stop, Stop>();
        FastestPathTree f = new FastestPathTree(stop, tempsArrives,
                predecessors);
        Stop monStop = f.startingStop();
        int i = f.startingTime();
        Set<Stop> mesStops = f.stops();
        i = f.arrivalTime(stop);
        List<Stop> p = f.pathTo(stop);
        System.out.println(" " + monStop + i + mesStops + p);

        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop, 0);
        fb.setArrivalTime(stop, 0, stop);
        i = fb.arrivalTime(stop);
        fb.build();
    }

    @Test
    public void testStartingStop() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 1);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop2, stop2);
        assertEquals(stop1, new FastestPathTree(stop1, tempsArrive,
                predecessors).startingStop());
    }

    @Test
    public void testStartingTime() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 15);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop2, stop2);
        assertEquals(15,
                new FastestPathTree(stop1, tempsArrive, predecessors)
                        .startingTime());
    }

    @Test
    public void testSetStops() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 15);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop2, stop2);
        Set<Stop> s = new HashSet<>();
        s.add(stop1);
        s.add(stop2);
        assertEquals(s,
                new FastestPathTree(stop1, tempsArrive, predecessors).stops());
    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testUnsupportedException() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 15);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop2, stop2);
        new FastestPathTree(stop1, tempsArrive, predecessors).stops().clear();
    }

    @Test
    public void testtempsArrive() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 15);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop2, stop2);
        assertEquals(10,
                new FastestPathTree(stop1, tempsArrive, predecessors)
                        .arrivalTime(stop2));
    }

    @Test
    public void testPathTo() {
        Stop s4 = new Stop("Renens", new PointWGS84(1, 0));
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 1);
        tempsArrive.put(stop2, 10);
        tempsArrive.put(stop3, 10);
        tempsArrive.put(s4, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s4, stop3);
        predecessors.put(stop2, stop1);
        predecessors.put(stop3, stop2);
        List<Stop> list = new LinkedList<Stop>();
        list.add(stop1);
        list.add(stop2);
        list.add(stop3);
        list.add(s4);
        assertEquals(list,
                new FastestPathTree(stop1, tempsArrive, predecessors)
                        .pathTo(s4));
    }

    @Test
    public void testtempsArriveInfinite() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 1);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop2, stop2);
        assertEquals(SecondsPastMidnight.INFINITE, new FastestPathTree(stop1,
                tempsArrive, predecessors).arrivalTime(stop3));
    }

    @Test
    public void testBuilderBuild() {
        FastestPathTree.Builder fB = new FastestPathTree.Builder(stop1, 1);
        fB.setArrivalTime(stop2, 10, stop1);
        fB.setArrivalTime(stop3, 20, stop2);
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 1);
        tempsArrive.put(stop2, 10);
        tempsArrive.put(stop3, 20);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(stop2, stop1);
        predecessor.put(stop3, stop2);
        FastestPathTree tree = new FastestPathTree(stop1, tempsArrive,
                predecessor);
        assertEquals(fB.build().arrivalTime(stop1), tree.arrivalTime(stop1));
        assertEquals(fB.build().arrivalTime(stop2), tree.arrivalTime(stop2));
        assertEquals(fB.build().arrivalTime(stop3), tree.arrivalTime(stop3));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testpathToInvalidity() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 1);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop3, stop3);
        predecessors.put(stop2, stop2);
        new FastestPathTree(stop1, tempsArrive, predecessors).pathTo(stop3);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorInvalidity() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(stop1, 1);
        tempsArrive.put(stop2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(stop3, stop3);
        predecessors.put(stop2, stop2);
        new FastestPathTree(stop3, tempsArrive, predecessors);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilderConstructorInvalidity() {
        new FastestPathTree.Builder(stop1, -1);
    }

    @Test
    public void testBuildertempsArriveInfinite() {
        assertEquals(SecondsPastMidnight.INFINITE, new FastestPathTree.Builder(
                stop1, 2).arrivalTime(stop2));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuildersetArrivalTimeInvalidity() {
        new FastestPathTree.Builder(stop1, 2).setArrivalTime(stop2, 1, stop3);
    }

    @Test
    public void testBuildersetArrivalTime() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int time = rng.nextInt(50) + 1;
            assertEquals(time, new FastestPathTree.Builder(stop1, 1)
                    .setArrivalTime(stop2, time, stop3).arrivalTime(stop2));
        }
    }

}
