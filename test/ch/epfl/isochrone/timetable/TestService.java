package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestService {

    /**
     * TESTCLASS : SERVICE.
     * @author Tristan Deloche (234045)
     */

    Date d = new Date(3, Month.DECEMBER, 2000);
    Date d1 = new Date(3, Month.DECEMBER, 1999);
    Set<Date> EXCL = new HashSet<>();
    Set<Date> INCL = new HashSet<>();
    Set<Date.DayOfWeek> OPR = new HashSet<>();
    Date d2 = new Date(1, Month.APRIL, 2001);
    Date d3 = new Date(1, Month.APRIL, 2000);

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor() {
        Set<Date.DayOfWeek> c = new HashSet<>();
        d1.dayOfWeek();
        c.add(DayOfWeek.FRIDAY);
        Date etalon = new Date(1, Month.JANUARY, 1900);
        Set<Date> b = new HashSet<>();
        b.add(etalon);
        Service n = new Service("n", d2, d3, c, b, Collections.<Date>emptySet());
        System.out.println(n);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilder() {

        Service.Builder build = new Service.Builder("name", d2, d3);
        System.out.println(build);
    }

    @Test
    public void testIsOperatingOn() {
        INCL.clear();
        EXCL.clear();
        INCL.add(d);

        Service service = new Service("test", d3, d2, OPR, EXCL, INCL);
        assertEquals(true, service.isOperatingOn(d));
        INCL.clear();
        EXCL.add(d);
        assertEquals(false, service.isOperatingOn(d));
        EXCL.clear();
        assertEquals(false, service.isOperatingOn(d));

        assertEquals(false, service.isOperatingOn(d1));

        OPR.add(DayOfWeek.SUNDAY);
        service = new Service("test", d3, d2, OPR, EXCL, INCL);

        assertEquals(true, service.isOperatingOn(d));

    }
}
