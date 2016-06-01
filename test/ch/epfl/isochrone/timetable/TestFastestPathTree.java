package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import static java.lang.Math.toRadians;

 

public class TestFastestPathTree {
    
    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testStartingTime() {
        Stop stop1 = new Stop("Stand", new PointWGS84(toRadians(6.5624795866),toRadians(46.5327194855)));
        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop1, -1);
    }
    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testSetArrivalTime() {
        Stop stop1 = new Stop("Stand", new PointWGS84(toRadians(6.5624795866),toRadians(46.5327194855)));
        Stop stop2 = new Stop("EPFL", new PointWGS84(toRadians(6.56591465573),toRadians(46.5221889086)));
        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop1, 10);
        fb.setArrivalTime(stop1, 8, stop2);
        
    }

    // A compléter avec de véritables méthodes de test...
}
