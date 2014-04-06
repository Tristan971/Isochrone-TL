package ch.epfl.isochrone.timetable;

import org.junit.Test;

import java.util.Collections;

public class TestGraphEdge {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    //@Ignore
    public void namesAreOk() {
        int i1 = GraphEdge.packTrip(0, 0);
        i1 = GraphEdge.unpackTripDepartureTime(0);
        i1 = GraphEdge.unpackTripDuration(0);
        i1 = GraphEdge.unpackTripArrivalTime(0) + i1;
        Stop s = null;
        GraphEdge e = new GraphEdge(s, 0, Collections.<Integer>emptySet());
        s = e.destination();
        i1 = e.earliestArrivalTime(0);

        GraphEdge.Builder b = new GraphEdge.Builder(s);
        b.setWalkingTime(0);
        b.addTrip(0, 0);
        e = b.build();
    }

    // A compléter avec de véritables méthodes de test...
}
