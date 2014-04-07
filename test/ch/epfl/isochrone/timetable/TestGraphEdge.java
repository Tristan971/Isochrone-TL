package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;
import java.util.HashSet;
import static org.junit.Assert.*;

/**
 * DEFCLASSE
 *
 * @author Tristan Deloche (234045)
 */
public class TestGraphEdge {
    @Test
    public void testPackTrip() throws Exception {
        assertEquals(GraphEdge.packTrip(10,100), 100090);
    }

    @Test
    public void testUnpackTripDepartureTime() throws Exception {
        assertEquals(GraphEdge.unpackTripDepartureTime(100090),10);
    }

    @Test
    public void testUnpackTripDuration() throws Exception {
        assertEquals(GraphEdge.unpackTripDuration(100090),90);
    }

    @Test
    public void testUnpackTripArrivalTime() throws Exception {
        assertEquals(GraphEdge.unpackTripArrivalTime(100090),100);
    }

    @Test
    public void testDestination() throws Exception {
        Stop testStop = new Stop("test", new PointWGS84(0,0));
        assertEquals(new GraphEdge(testStop, 1000, new HashSet<Integer>()).destination(), testStop);
    }

    @Test
    public void testEarliestArrivalTime() throws Exception {

    }
}
