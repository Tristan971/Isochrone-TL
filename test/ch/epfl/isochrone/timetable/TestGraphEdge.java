package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.GraphEdge.Builder;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * TESTCLASS : GraphEdge.
 * @author Tristan Deloche (234045)
 */

public class TestGraphEdge {
    PointWGS84 position = new PointWGS84(1,1);
    Stop stop = new Stop("nom", position);
    Set<Integer> set = new HashSet<>();
    Builder builder = new Builder(stop);
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructor() {
        GraphEdge testedGraph = new GraphEdge(stop,-2,set);
        System.out.println(testedGraph);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripDepartureTooLow() {
        
        GraphEdge.packTrip(-1, 1);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripDepartureToBig() {
        
        GraphEdge.packTrip((int) Math.pow(10,10), 1);
        
    }
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripWalkingTimeTooLow() {

        GraphEdge.packTrip(2, 1);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripWalkingTimeTooBig() {
        
        GraphEdge.packTrip(1, SecondsPastMidnight.INFINITE+1);
        
    }
    
    @Test 
    public void testEarliestArrivalTime() {
        //TODO : Si pas possible de faire sans...
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripDepartureTimeTooLow() {
        
        builder.addTrip(-1, 1500);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripDepartureTimeTooBig() {
        
        builder.addTrip(SecondsPastMidnight.INFINITE, 108999);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripWalkingTooLow() {
        
        builder.addTrip(2,1);
    }
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripWalkingTooBig() {
        
            builder.addTrip(1,SecondsPastMidnight.INFINITE + 1);
      
    }
    
    
}
