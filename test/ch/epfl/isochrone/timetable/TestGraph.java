package ch.epfl.isochrone.timetable;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class TestGraph {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        // Graph n'a aucune méthode publique à ce stade...

        Set<Stop> stops = null;
        Stop stop = null;
        Graph.Builder gb = new Graph.Builder(stops);
        gb.addTripEdge(stop, stop, 0, 0);
        gb.addAllWalkEdges(0, 0);
        gb.build();
    }

    // A compléter avec de véritables méthodes de test...
}
