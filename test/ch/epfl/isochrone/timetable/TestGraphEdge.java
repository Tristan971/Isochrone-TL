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
        Stop stop1 = new Stop("stop1", new PointWGS84(0,1));
        Stop stop2 = new Stop("stop2", new PointWGS84(-1, 0));
        Stop stop3 = new Stop("stop3", new PointWGS84(-1, 1));
        Stop stop4 = new Stop("stop4", new PointWGS84(-0.5, 0.5));
        Stop stop5 = new Stop("stop5", new PointWGS84(0, 0.5));

        GraphEdge.Builder b1 = new GraphEdge.Builder(stop1);
        GraphEdge.Builder b2 = new GraphEdge.Builder(stop2);
        GraphEdge.Builder b3 = new GraphEdge.Builder(stop3);
        GraphEdge.Builder b4 = new GraphEdge.Builder(stop4);
        GraphEdge.Builder b5 = new GraphEdge.Builder(stop5);

        b1.addTrip(1002, 2500).addTrip(2000, 2300).addTrip(6000, 6100).setWalkingTime(-1);
        b2.setWalkingTime(-1);
        b3.setWalkingTime(1000);
        b4.addTrip(100, 3000).addTrip(2000, 3500).addTrip(7000, 7500).setWalkingTime(100);
        b5.addTrip(100, 6000).addTrip(1500, 3500).addTrip(4000, 4500).setWalkingTime(4000);

        assertEquals(2500, b1.build().earliestArrivalTime(1000));
        assertEquals(SecondsPastMidnight.INFINITE , b2.build().earliestArrivalTime(3000));
        assertEquals(3000 , b3.build().earliestArrivalTime(2000));
        assertEquals(1600 , b4.build().earliestArrivalTime(1500));
        assertEquals(3500 , b5.build().earliestArrivalTime(1250));
    }
}
