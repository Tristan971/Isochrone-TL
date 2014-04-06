package ch.epfl.isochrone.timetable;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Classe de test des services
 * @author Tristan Deloche (234045)
 */
public class TestService {

    /*
    @Test
    @Ignore

    public void namesAreOk() {
        Date d = new Date(1, Date.Month.JANUARY, 2000);
        Service s = new Service("s",
                d, d,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(),
                Collections.<Date> emptySet());
        s.name();
        s.isOperatingOn(d);

        Service.Builder sb = new Service.Builder("s", d, d);
        sb.name();
        sb.addOperatingDay(Date.DayOfWeek.MONDAY);
        sb.addExcludedDate(d);
        sb.addIncludedDate(d);
        sb.build();
    }
    */

    @Test
    public void testName() throws Exception {
        assertEquals(new Service("test", new Date(1,1,1), new Date(1,1,1), new HashSet<Date.DayOfWeek>(), new HashSet<Date>(), new HashSet<Date>()).name(), "test");
    }

    @Test
    public void testIsOperatingOn() throws Exception {
        Set<Date.DayOfWeek> testOpDaysSet = new HashSet<>();
        Collections.addAll(testOpDaysSet, Date.DayOfWeek.values());

        assertTrue(new Service("test", new Date(1, 1, 1), new Date(1, 1, 1), testOpDaysSet, new HashSet<Date>(), new HashSet<Date>()).isOperatingOn(new Date(1, 1, 1)));
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new Service("test", new Date(1,1,1), new Date(1,1,1), new HashSet<Date.DayOfWeek>(), new HashSet<Date>(), new HashSet<Date>()).toString(), "test");
    }
}
