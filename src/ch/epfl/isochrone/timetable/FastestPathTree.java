package ch.epfl.isochrone.timetable;

import java.util.*;

/**
 * Classe représentant un trajet optimal entre plusieurs arrêts
 * @author Tristan Deloche (234045)
 */

public final class FastestPathTree {

    /**
     * Un trajet est défini par son point de départ, son heure d'arrivée, et le stop d'arrivée et ses prédécesseurs jusqu'au stop de départ
     */
    private Stop startingStop;
    private Map<Stop, Integer> arrivalTime = new HashMap<>();
    private Map<Stop, Stop> predecessor;

    /**
     * Constructeur principal
     * @param startingStop
     *          Arrêt de départ
     * @param arrivalTime
     *          Heure d'arrivée à chacun des stops (sous forme de map)
     */
    public FastestPathTree(Stop startingStop, Map<Stop, Integer> arrivalTime, Map<Stop, Stop> predecessor) {

        Set<Stop> test = new HashSet<>();
        test.add(startingStop);
        test.addAll(predecessor.keySet());
        if (!test.equals(arrivalTime.keySet())) {
            throw new IllegalArgumentException("startingStop + arrivaltimes keyset != predecessor keyset is NOT OK");
        }

        this.startingStop = startingStop;
        this.arrivalTime = new HashMap<>(Collections.unmodifiableMap(arrivalTime));
        this.predecessor = new HashMap<>(Collections.unmodifiableMap(predecessor));
    }


    /**
     * Simple getter sur l'arrêt de départ
     * @return
     *      L'arrêt de départ
     */
    public Stop startingStop() {
        return startingStop;
    }

    /**
     * Simple getter sur l'heure de début du trajet total
     * @return
     *      Heure à laquelle on arrive au premier arrêt du trajet
     */
    public int startingTime() {
        return arrivalTime.get(startingStop);
    }

    /**
     * Simple getter sur l'ensemble des stops du trajet
     * @return
     *      L'ensemble des arrêts
     */
    public Set<Stop> stops() {
        return Collections.unmodifiableSet(arrivalTime.keySet());
    }

    /**
     * Simple getter sur l'heure arrivée à un arrêt
     * @param stop
     *      L'arrêt dont on veut connaître l'heure d'arrivée
     * @return
     *      Heure à laquelle on arrive à l'arrêt passé en argument
     */
    public int arrivalTime(Stop stop) {
        if (!arrivalTime.containsKey(stop)) {
            return SecondsPastMidnight.INFINITE;
        } else {
            return arrivalTime.get(stop);
        }
    }

    /**
     * Renvoie une liste ordonnée des arrêts nécessaire pour se rendre de l'arrêt de départ à un arrêt donné
     * @param stop
     *      Stop de destination
     * @return
     *      Liste ordonnée représentant le chemin à emprunter
     */
    public List<Stop> pathTo (Stop stop) {
        if (!arrivalTime.containsKey(stop)) {
            throw new IllegalArgumentException("Given stop to go to is NOT in arrivalTime map (stop is : " + stop +")");
        }

        LinkedList<Stop> pathList = new LinkedList<>();
        Stop s1 = stop;

        while (!s1.equals(startingStop())) {
            pathList.addFirst(s1);
            s1=predecessor.get(s1);
        }

        pathList.addFirst(s1);
        return pathList;
    }

    /**
     * Builder pour pouvoir créer son FPT pas-à-pas
     */
    public final static class Builder {
        private Stop startingStop;
        private Map<Stop, Integer> arrivalTime;
        private Map<Stop, Stop> predecessor;
        private int startingTime;
        /**
         * Constructeur principal du bâtisseur
         * @param startingStop
         *          Arrêt de départ
         * @param startingTime
         *          Heure d'arrivée à l'arrêt de départ (et donc heure de début du trajet)
         */
        public Builder(Stop startingStop, int startingTime) {
            if(startingTime < 0) {
                throw new IllegalArgumentException("Starting time is negative!");
            }
            this.startingTime = startingTime;
            this.startingStop = startingStop;

            arrivalTime = new HashMap<>();
            predecessor = new HashMap<>();

            arrivalTime.put(startingStop, startingTime);
        }

        public int arrivalTime(Stop stop) {
            if (!arrivalTime.containsKey(stop)) {
                return SecondsPastMidnight.INFINITE;
            }
            return this.arrivalTime.get(stop);
        }

        /**
         * Permet de définir une heure d'arrivée à un arrêt donné depuis son prédécesseur
         * @param stop
         *          Arrêt dont on définit l'heure d'arrivée
         * @param time
         *          Heure d'arrivée
         * @param predecessor
         *          Prédécesseur de l'arrêt
         * @return
         *          Le bâtisseur pour permettre les appels chaînés
         */
        public Builder setArrivalTime(Stop stop, int time, Stop predecessor) {
            if (time < startingTime) {
                throw new IllegalArgumentException("arrivalTime of a stop is NOT POSSIBLY before startingTime");
            }
            this.arrivalTime.put(stop,time);
            this.predecessor.put(stop,predecessor);
            return this;
        }

        /**
         * Construit un nouvel FPT à l'aide des informations fournies au builder
         * @return
         *      Le nouvel FPT
         */
        public FastestPathTree build() {
            return new FastestPathTree(startingStop, arrivalTime, predecessor);
        }
    }
}
