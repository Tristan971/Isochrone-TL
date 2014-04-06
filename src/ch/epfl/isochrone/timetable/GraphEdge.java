package ch.epfl.isochrone.timetable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe gérant la notion d'arc de graphe
 * @author Tristan Deloche (234045)
 */

public final class GraphEdge {

    /**
     * Un arc de graphe est caractérisé par sa desitnation, son temps de parcours à pied, et les autres façon de le parcourir associées à leurs temps de parcours
     */
    private Stop destination;
    private int walktime;
    private Set<Integer> packedTrips;

    /**
     * Constructeur principal
     * @param destination
     *          Destination de l'arc
     * @param walkingTime
     *          Temps de marche
     * @param packedTrips
     *          Ensemble des trajets, qui consistent en leur heure de départ et leur durée
     */
    public GraphEdge(Stop destination, int walkingTime, Set<Integer> packedTrips) {
        if (walkingTime < -1) {
            throw new IllegalArgumentException("(-1>=walkingTime) non satisfait!");
        }
        this.destination=destination;
        this.walktime=walkingTime;
        this.packedTrips= new HashSet<>(packedTrips);
    }

    /**
     * Encode une heure de départ et d'arrivée en un seul entier représentant l'heure de départ et la durée du trajet
     * @param departureTime
     *          Heure de départ en secondes après minuit
     * @param arrivalTime
     *          Heure d'arrivée en secondes après minuit
     * @return
     *          L'heure de départ en secondes après minuit et la durée du trajet sous la forme <DEPART>000<DUREE> (durée est aussi en secondes après minuit)
     */
    public static int packTrip(int departureTime, int arrivalTime) {
        if (departureTime < 0 || departureTime > 107999) {
            throw new IllegalArgumentException("departureTime out of range!");
        }
        return 10000*departureTime+(arrivalTime-departureTime);
    }

    /**
     * Récupère l'heure de départ depuis l'entier la "codant"
     * @param packedTrip
     *          Entier représentant l'heure de départ et la durée de façon "codée"
     * @return
     *          Heure de départ (en spm)
     */
    public static int unpackTripDepartureTime(int packedTrip) {
        return ch.epfl.isochrone.math.Math.divF(packedTrip,10000);
    }

    /**
     * Récupère la durée du trajet lié
     * @param packedTrip
     *          Entier représentant l'heure de départ et la durée de façon "codée"
     * @return
     *          Durée du trajet (en spm)
     */
    public static int unpackTripDuration(int packedTrip){
        return ch.epfl.isochrone.math.Math.modF(packedTrip,10000);
    }

    /**
     * Récupère l'heure d'arrivée
     * @param packedTrip
     *          Entier représentant l'heure de départ et la durée de façon "codée"
     * @return
     *          Heure d'arrivée (en spm)
     */
    public static int unpackTripArrivalTime(int packedTrip) {
        return unpackTripDepartureTime(packedTrip)+unpackTripDuration(packedTrip);
    }

    /**
     * @return
     *      La destination de l'arc
     */
    public Stop destination() {
        return destination;
    }

    /**
     * Calcule le trajet le plus rapide entre celui à pied et le plus rapide en transports publics et renvoie l'heure d'arrivée qui y est associée
     * @param departureTime
     *          Heure de départ en secondes après minuit
     * @return
     *          L'heure d'arrivée la plus proche en utilisant cet arc
     */
    public int earliestArrivalTime(int departureTime) {
        int publicTransportationTime = minimalPublicTransportationTime(departureTime);

        if (publicTransportationTime < walktime) {
            return publicTransportationTime;
        } else {
            return walktime;
        }
    }

    /**
     * Recherche du trajet le plus rapide en transports publics
     * @param departureTime
     *          Heure de départ en secondes après minuit
     * @return
     *          Le temps mis par le trajet le plus court
     */
    private int minimalPublicTransportationTime(int departureTime) {
        Integer[] packedTripsArray = packedTrips.toArray(new Integer[packedTrips.size()]);
        Arrays.sort(packedTripsArray);

        int minimalTime = SecondsPastMidnight.INFINITE;

        for(int i = 0; i < packedTrips.size(); i++) {
            if (packedTripsArray[i] < minimalTime && unpackTripDepartureTime(packedTripsArray[i]) >= departureTime) {
                minimalTime = packedTripsArray[i];
            }
        }

        return minimalTime;
    }

    /**
     * Bâtisseur servant à créér pas-à-pas son arc de graphe
     */
    public final static class Builder {
        private Stop destination;
        private int walktime;
        private Set<Integer> packedTrips = new HashSet<>();

        /**
         * Constructeur principal
         * @param destination
         *      Destination de l'arc à créér
         */
        public Builder(Stop destination) {
            this.destination=destination;
            this.walktime=-1;
        }

        /**
         * Méthode servant à définir le temps de trajet à pied d'un graphe
         * @param newWalkingTime
         *          Temps à entrer
         * @return
         *          Le bâtisseur pour permettre les appels chaînés
         */
        public GraphEdge.Builder setWalkingTime(int newWalkingTime) {
            if (newWalkingTime < -1) {
                throw new IllegalArgumentException("Walking time CAN NOT BE < -1");
            }
            walktime = newWalkingTime;
            return this;
        }

        /**
         * Ajout d'un trajet possible à un graphe
         * @param departureTime
         *          Heure de départ en secondes après minuit
         * @param arrivalTime
         *          Heure d'arrivée en secondes après minuit
         * @return
         *          Le bâtisseur pour permettre les appels chaînés
         */
        public GraphEdge.Builder addTrip(int departureTime, int arrivalTime) {
            if (departureTime < 0 || departureTime > 107999) {
                throw new IllegalArgumentException("departureTime out of range!");
            }
            packedTrips.add(10000*departureTime+(arrivalTime-departureTime));
            return this;
        }

        /**
         * Crée un arc de graphe avec les données que la bâtisseur a reçues
         * @return
         *      Un nouvel arc de graphe
         */
        public GraphEdge build() {
            return new GraphEdge(destination, walktime, packedTrips);
        }
    }
}
