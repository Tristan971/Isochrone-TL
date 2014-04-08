package ch.epfl.isochrone.timetable;

import java.io.IOException;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

public class TestTimeTableReader {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() throws IOException {
        TimeTableReader r = new TimeTableReader("");
        TimeTable t = r.readTimeTable();
        Graph g = r.readGraphForServices(t.stops(), Collections.<Service>emptySet(), 0, 0d);
        System.out.println(g); // Evite l'avertissement que g n'est pas utilisé
    }
}
