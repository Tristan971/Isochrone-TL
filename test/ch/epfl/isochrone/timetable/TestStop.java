package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;

public class TestStop {

   PointWGS84 testStopPosition = new PointWGS84(toRadians(23.3232), toRadians(89.6765));
   Stop testStop = new Stop("nom", testStopPosition);
   @Test
    public void testName(){
        
        assertEquals(testStop.name(),"nom");
    }
    
    @Test
    public void testPosition(){
       assertEquals(testStopPosition, testStop.position());
    }
    
    @Test
    public void testToString() {
        assertEquals(testStop.name(),"nom");
    }
}
