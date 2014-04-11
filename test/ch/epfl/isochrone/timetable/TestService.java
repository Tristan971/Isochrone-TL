package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestService {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Service s = new Service("s", d, d,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet());
        s.name();
        s.isOperatingOn(d);

        Service.Builder sb = new Service.Builder("s", d, d);
        sb.name();
        sb.addOperatingDay(DayOfWeek.MONDAY);
        sb.addExcludedDate(d);
        sb.addIncludedDate(d);
        sb.build();
    }

    // A compléter avec de véritables méthodes de test...

    Date dateE = new Date(3, Month.DECEMBER, 2000);
    Date date = new Date(3, Month.DECEMBER, 1999);
    Set<Date> exclud = new HashSet<Date>();
    Set<Date> includ = new HashSet<Date>();
    Set<Date.DayOfWeek> operating = new HashSet<Date.DayOfWeek>();
    Date dateD = new Date(1, Month.APRIL, 2001);
    Date dateA = new Date(1, Month.APRIL, 2000);

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructor() {

        Service nouveau = new Service("nouveau", dateD, dateA,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet());
        System.out.println(nouveau);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1() {
        Set<Date.DayOfWeek> c = new HashSet<Date.DayOfWeek>();
        date.dayOfWeek();
        c.add(DayOfWeek.FRIDAY);
        Date ex = new Date(1, Month.APRIL, 2002);
        Set<Date> b = new HashSet<Date>();
        b.add(ex);
        Service n = new Service("n", dateD, dateA, c, b,
                Collections.<Date> emptySet());
        System.out.println(n);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructor2() {

        includ.add(dateE);
        exclud.add(dateE);

        Service nouveau = new Service("nouveau", dateA, dateD,
                Collections.<Date.DayOfWeek> emptySet(), exclud, includ);
        System.out.println(nouveau);

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructor3() {

        exclud.clear();
        exclud.add(date);
        Service nouveau = new Service("nouveau", dateA, dateD,
                Collections.<Date.DayOfWeek> emptySet(), exclud,
                Collections.<Date> emptySet());
        System.out.println(nouveau);

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilder() {
        Service.Builder build = new Service.Builder("name", dateD, dateA);
        System.out.println(build);
    }

    @Test
    public void testIsOperatingOn() {
        includ.clear();
        exclud.clear();

        includ.add(dateE);

        Service service = new Service("test", dateA, dateD, operating, exclud,
                includ);

        assertEquals(true, service.isOperatingOn(dateE));

        includ.clear();

        exclud.add(dateE);
        assertEquals(false, service.isOperatingOn(dateE));
        exclud.clear();
        assertEquals(false, service.isOperatingOn(dateE));

        assertEquals(false, service.isOperatingOn(date));

    }

    @Test
    public void TestIsoperatingOn() {
        operating.add(DayOfWeek.SUNDAY);
        Service service = new Service("test", dateA, dateD, operating, exclud,
                includ);

        assertEquals(true, service.isOperatingOn(dateE));

    }
}
