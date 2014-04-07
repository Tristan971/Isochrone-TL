package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TestGraph {

    @Test
    public void namesAreOk() {
        Set<Stop> stops = new HashSet<>();
        Stop stop = new Stop("test", new PointWGS84(0, 0.8));;
        stops.add(stop);

        Graph.Builder gb = new Graph.Builder(stops);
        gb.addTripEdge(stop, stop, 0, 0);
        gb.addAllWalkEdges(0, 0);
        gb.build();
    }
}
