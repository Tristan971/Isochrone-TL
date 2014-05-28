package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestService {
    
    @Test
    public void testIsOperatingOn() {
        Date start = new Date(22, Month.MARCH, 2014);
        Date end = new Date(30, Month.MARCH, 2014);

        Service.Builder sb = new Service.Builder("s", start, end);
        sb.addOperatingDay(DayOfWeek.FRIDAY);
        Service ss = sb.build();
        
        Date testDate1 = new Date(27, Month.MARCH, 2014);
     //   sb.addIncludedDate(testDate);
        Date testDate2 = new Date(28, Month.MARCH, 2014);
        Date testDate3 = new Date(21, Month.MARCH, 2014);
        Date testDate4 = new Date(26, Month.MARCH, 2014);

        assertEquals(false,ss.isOperatingOn(testDate1));
        assertEquals(true,ss.isOperatingOn(testDate2));
        assertEquals(false,ss.isOperatingOn(testDate3));
        assertEquals(false,ss.isOperatingOn(testDate4));
        
        sb.addOperatingDay(DayOfWeek.THURSDAY);
        Service ss1 = sb.build();
        assertEquals(true,ss1.isOperatingOn(testDate1));
        
       
        sb.addIncludedDate(testDate4);
        Service ss2 = sb.build();
        assertEquals(true,ss2.isOperatingOn(testDate4));
        
        sb.addExcludedDate(testDate2);
        Service ss3 = sb.build();
        assertEquals(false,ss3.isOperatingOn(testDate2));
       
    }
    
    @Test
    public void testImmutability() {
        Date start = new Date(22, Month.MARCH, 2014);
        Date end = new Date(30, Month.MARCH, 2014);
        
        Service.Builder sb = new Service.Builder("s", start, end);
        sb.addOperatingDay(DayOfWeek.FRIDAY);
        Service ss = sb.build();
        
        Date testDate1 = new Date(27, Month.MARCH, 2014);
     //   sb.addIncludedDate(testDate);
        Date testDate2 = new Date(28, Month.MARCH, 2014);
        Date testDate3 = new Date(21, Month.MARCH, 2014);
        Date testDate4 = new Date(26, Month.MARCH, 2014);
        
        assertEquals(false,ss.isOperatingOn(testDate1));
        assertEquals(true,ss.isOperatingOn(testDate2));
        assertEquals(false,ss.isOperatingOn(testDate3));
        assertEquals(false,ss.isOperatingOn(testDate4));
        
        sb.addIncludedDate(testDate1);
        sb.addExcludedDate(testDate2);
        
        assertEquals(false,ss.isOperatingOn(testDate1));
        assertEquals(true,ss.isOperatingOn(testDate2));
     
    }

    // A compléter avec de véritables méthodes de test...
}
