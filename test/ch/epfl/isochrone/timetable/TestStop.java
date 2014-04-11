package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;

public class TestStop {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
   PointWGS84 point = new PointWGS84(toRadians(6.62901), toRadians(46.51658));
   Stop monStop = new Stop("nom",point); 
   @Test
    public void testName(){
        
        assertEquals(monStop.name(),"nom");
    }
    
    @Test
    public void testPosition(){
       assertEquals(point, monStop.position());
    }
    
    @Test
    public void testToString() {
        assertEquals(monStop.name(),"nom");
    }
}
