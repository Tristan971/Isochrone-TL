package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.GraphEdge.Builder;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestGraphEdge {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.

    // A compléter avec de véritables méthodes de test...
    PointWGS84 position = new PointWGS84(1,1);
    Stop stop = new Stop("nom", position);
    Set<Integer> set = new HashSet<Integer>();
    Builder builder = new Builder(stop);
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructor() {
        GraphEdge fail = new GraphEdge(stop,-2,set);
        System.out.println(fail);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripDepartureTooLow() {
        
        GraphEdge.packTrip(-2, 8000);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripDepartureToBig() {
        
        GraphEdge.packTrip(9999999, 8000);
        
    }
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripWalkingTimeTooLow() {

        GraphEdge.packTrip(1000, 900);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testPackedTripWalkingTimeTooBig() {
        
        GraphEdge.packTrip(1000, 12000);
        
    }
    @Test
    public void testPackingAndUnpackingStuff(){
        assertEquals(345, GraphEdge.unpackTripDepartureTime(GraphEdge.packTrip(345,1000)));
        assertEquals(2222, GraphEdge.unpackTripArrivalTime(GraphEdge.packTrip(234,2222)));
        assertEquals(342-12, GraphEdge.unpackTripDuration(GraphEdge.packTrip(12,342)));
    }
    
    @Test 
    public void testEarliestArrivalTime() {
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
       
       b1.addTrip(1002, 2500).addTrip(2000, 2600).addTrip(6000, 6100).setWalkingTime(-1);
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
   
    
    
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderSetWalkingTime() {
        
       builder.setWalkingTime(-2);
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripDepartureTimeTooLow() {
        
        builder.addTrip(-9, 1500);
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripDepartureTimeTooBig() {
        
        builder.addTrip(108999, 108999);
        
        
        
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripWalkingTooLow() {
        
        builder.addTrip(8000,6000);
    }
    
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddTripWalkingTooBig() {
        
            builder.addTrip(1,12000);
      
    }
    
    
}
