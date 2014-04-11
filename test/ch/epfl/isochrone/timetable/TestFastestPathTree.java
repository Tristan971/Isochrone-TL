package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * TESTCLASS : FPT.
 * @author Tristan Deloche (234045)
 */

public class TestFastestPathTree {

    private Stop s1 = new Stop("s1", new PointWGS84(0, 1));
    private Stop s2 = new Stop("s2", new PointWGS84(1, 0));
    private Stop s3 = new Stop("s3", new PointWGS84(-1, 1));

    @Test
    public void testStartingStop() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 4);
        tempsArrive.put(s2, 5);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s2, s2);
        assertEquals(s1, new FastestPathTree(s1, tempsArrive, predecessors).startingStop());
    }

    @Test
    public void testStops() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, null);
        tempsArrive.put(s2, null);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s2, s2);
        Set<Stop> s = new HashSet<>();
        s.add(s1);
        s.add(s2);
        assertEquals(s, new FastestPathTree(s1, tempsArrive, predecessors).stops());
    }

    @Test
    public void testStartingTime() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 6);
        tempsArrive.put(s2, 7);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s2, s2);
        assertEquals(6, new FastestPathTree(s1, tempsArrive, predecessors).startingTime());
    }

    @Test
    public void testArrivalTime() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 6);
        tempsArrive.put(s2, 1);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s2, s2);
        assertEquals(1, new FastestPathTree(s1, tempsArrive, predecessors).arrivalTime(s2));
    }

    @Test
    public void testPathTo() {
        Stop s4 = new Stop("stop4", new PointWGS84(1, 0));
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 1);
        tempsArrive.put(s2, 10);
        tempsArrive.put(s3, 11);
        tempsArrive.put(s4, 13);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s4, s3);
        predecessors.put(s2, s1);
        predecessors.put(s3, s2);
        List<Stop> list = new LinkedList<>();
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        assertEquals(list, new FastestPathTree(s1, tempsArrive, predecessors).pathTo(s4));
    }

    @Test
    public void testArrivalTimeInfinite() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 2);
        tempsArrive.put(s2, 3);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s2, s2);
        assertEquals(SecondsPastMidnight.INFINITE, new FastestPathTree(s1, tempsArrive, predecessors).arrivalTime(s3));
    }

    @Test
    public void testBuilder() {
        FastestPathTree.Builder fB = new FastestPathTree.Builder(s1, 1);
        fB.setArrivalTime(s2, 10, s1);
        fB.setArrivalTime(s3, 20, s2);
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 1);
        tempsArrive.put(s2, 10);
        tempsArrive.put(s3, 20);
        Map<Stop, Stop> predecessor = new HashMap<>();
        predecessor.put(s2, s1);
        predecessor.put(s3, s2);
        FastestPathTree tree = new FastestPathTree(s1, tempsArrive, predecessor);
        assertEquals(fB.build().arrivalTime(s1), tree.arrivalTime(s1));
        assertEquals(fB.build().arrivalTime(s2), tree.arrivalTime(s2));
        assertEquals(fB.build().arrivalTime(s3), tree.arrivalTime(s3));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorException() {
        Map<Stop, Integer> tempsArrive = new HashMap<>();
        tempsArrive.put(s1, 1);
        tempsArrive.put(s2, 10);
        Map<Stop, Stop> predecessors = new HashMap<>();
        predecessors.put(s3, s3);
        predecessors.put(s2, s2);
        new FastestPathTree(s3, tempsArrive, predecessors);
    }

    @Test
    public void testBuildertempsArriveInfinite() {
        assertEquals(SecondsPastMidnight.INFINITE, new FastestPathTree.Builder(s1, 2).arrivalTime(s2));
    }

    @Test
    public void testBuildersetArrivalTime() {
        Random r = new Random();
        for (int i = 0; i < 100; ++i) {
            int time = r.nextInt(100) + 1;
            assertEquals(time, new FastestPathTree.Builder(s1, 1).setArrivalTime(s2, time, s3).arrivalTime(s2));
        }
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuildersetBadArrivalTime() {
        new FastestPathTree.Builder(s1, 2).setArrivalTime(s2, 1, s3);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilderBadConstructor() {
        new FastestPathTree.Builder(s1, -1);
    }
}
