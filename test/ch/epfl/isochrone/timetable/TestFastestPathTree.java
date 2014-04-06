package ch.epfl.isochrone.timetable;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestFastestPathTree {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Stop stop = null;
        Map<Stop, Integer> arrivalTimes = null;
        Map<Stop, Stop> predecessors = null;
        FastestPathTree f = new FastestPathTree(stop, arrivalTimes, predecessors);
        Stop s = f.startingStop();
        int i = f.startingTime();
        Set<Stop> ss = f.stops();
        i = f.arrivalTime(stop);
        List<Stop> p = f.pathTo(stop);
        System.out.println("" + s + i + ss + p);

        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop, 0);
        fb.setArrivalTime(stop, 0, stop);
        //i = fb.arrivalTime(stop);
        //f = fb.build();
    }

    // A compléter avec de véritables méthodes de test...
}
