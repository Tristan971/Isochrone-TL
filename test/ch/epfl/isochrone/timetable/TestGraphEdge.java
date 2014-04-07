package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;
import java.util.HashSet;
import static org.junit.Assert.*;

/**
 * Classe de test de GraphEdge
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
        Stop test1 = new Stop("test1", new PointWGS84(1,0.1));
        Stop test2 = new Stop("test2", new PointWGS84(-1.1, 0));
        Stop test3 = new Stop("test3", new PointWGS84(-1, 1.1));
        Stop test4 = new Stop("test4", new PointWGS84(-0.3, 0.1));
        Stop test5 = new Stop("test5", new PointWGS84(0, 0.8));

        GraphEdge.Builder builder1 = new GraphEdge.Builder(test1);
        GraphEdge.Builder builder2 = new GraphEdge.Builder(test2);
        GraphEdge.Builder builder3 = new GraphEdge.Builder(test3);
        GraphEdge.Builder builder4 = new GraphEdge.Builder(test4);
        GraphEdge.Builder builder5 = new GraphEdge.Builder(test5);

        builder1.addTrip(1008, 2800).addTrip(2100, 2340).addTrip(7000, 8000).setWalkingTime(-1);
        builder2.setWalkingTime(1000);
        builder3.setWalkingTime(-1);
        builder4.addTrip(200, 6000).addTrip(1000, 2000).addTrip(6000, 8500).setWalkingTime(200);
        builder5.addTrip(100, 6000).addTrip(1500, 3500).addTrip(4000, 4500).setWalkingTime(4000);

        assertEquals(2340, builder1.build().earliestArrivalTime(1000));
        assertEquals(3000, builder2.build().earliestArrivalTime(2000));
        assertEquals(SecondsPastMidnight.INFINITE, builder3.build().earliestArrivalTime(3000));
        assertEquals(1600, builder4.build().earliestArrivalTime(1400));
        assertEquals(3500, builder5.build().earliestArrivalTime(1250));
    }
}
