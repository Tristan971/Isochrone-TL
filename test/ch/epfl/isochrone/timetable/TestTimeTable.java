package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests pour TimeTable
 * @author Tristan Deloche (234045)
 */

public class TestTimeTable {
    @Test
    public void testStops() throws Exception {
        Set<Stop> testStopSet = new HashSet<>();
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            String uuid = UUID.randomUUID().toString();
            double randomValue = r.nextDouble();
            testStopSet.add(new Stop(uuid, new PointWGS84(randomValue, randomValue)));
        }

        Set<Service> servicesTest = new HashSet<>();
        servicesTest.add(new Service("test", new Date(1,1,1), new Date(1,1,1), new HashSet<Date.DayOfWeek>(), new HashSet<Date>(), new HashSet<Date>()));

        assertEquals(new TimeTable(testStopSet, servicesTest).stops(), testStopSet);
    }

    @Test
    public void testServicesForDate() throws Exception {
        Set<Stop> testStopSet = new HashSet<>();
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            String uuid = UUID.randomUUID().toString();
            double randomValue = r.nextDouble();
            testStopSet.add(new Stop(uuid, new PointWGS84(randomValue, randomValue)));
        }

        Set<Service> servicesTest = new HashSet<>();
        servicesTest.add(new Service("test", new Date(1,1,1), new Date(1,1,1), new HashSet<Date.DayOfWeek>(), new HashSet<Date>(), new HashSet<Date>()));

        assertEquals(new TimeTable(testStopSet, servicesTest).servicesForDate(new Date(1,1,1)), servicesTest);
    }
}
