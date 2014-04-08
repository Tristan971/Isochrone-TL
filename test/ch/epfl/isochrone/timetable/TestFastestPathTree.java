package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestFastestPathTree {

    @Test
    public void namesAreOk() {
        Stop stop = new Stop("test", new PointWGS84(0,0));
        Map<Stop, Integer> arrivalTimes = new HashMap<>();
        arrivalTimes.put(stop,1);

        Map<Stop, Stop> predecessors = new HashMap<>();

        FastestPathTree f = new FastestPathTree(stop, arrivalTimes, predecessors);

        Stop s = f.startingStop();
        int i1 = f.startingTime();
        Set<Stop> ss = f.stops();
        int i = f.arrivalTime(stop);
        List<Stop> p = f.pathTo(stop);
        System.out.println("" + s + i + ss + p + i1);

        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop, 0);
        fb.setArrivalTime(stop, 0, stop);
        i = fb.arrivalTimes().get(stop);
        f = fb.build();
    }

}
