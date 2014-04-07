package ch.epfl.isochrone.timetable;

import org.junit.Test;
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

    }

    @Test
    public void testUnpackTripDuration() throws Exception {

    }

    @Test
    public void testUnpackTripArrivalTime() throws Exception {

    }

    @Test
    public void testDestination() throws Exception {

    }

    @Test
    public void testEarliestArrivalTime() throws Exception {

    }
}
