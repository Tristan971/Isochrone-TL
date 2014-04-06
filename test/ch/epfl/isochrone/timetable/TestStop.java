package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests de Stop.java
 * @author Tristan Deloche (234045)
 */

public class TestStop {
    @Test
    public void testName() throws Exception {
        assertEquals(new Stop("test", new PointWGS84(0,0)).name(), "test");
    }

    @Test
    public void testPosition() throws Exception {
        Stop testStop = new Stop("test", new PointWGS84(-0.5, 0.1));
        assertTrue(testStop.position().longitude() - new PointWGS84(-0.5, 0.1).longitude() < 0.0001 && testStop.position().latitude() - new PointWGS84(-0.5, 0.1).latitude() < 0.0001);
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new Stop("test", new PointWGS84(0, 0)).toString(), "test");
    }
}
