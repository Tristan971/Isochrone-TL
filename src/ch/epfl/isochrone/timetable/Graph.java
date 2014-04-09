package ch.epfl.isochrone.timetable;

import java.util.*;

/**
 * Classe gérant la notion de Graphe
 * @author Tristan Deloche (234045)
 */

public final class Graph {
    /**
     * Un graphe est défini par les arrêts qu'il décrit, et par les arcs de graphe liant lesdits arrêts entre eux. On a donc un système de deux tables associatives dont une contient des listes.
     */
    private Set<Stop> stops = new HashSet<>();
    private Map<Stop, List<GraphEdge>> outgoingEdges = new HashMap<>();

    /**
     * Constructeur principal de Graph
     * @param stops
     *          Ensemble des arrêts contenus
     * @param outgoingEdges
     *          Map des arcs liés à chacun des arrêts
     */
    private Graph(Set<Stop> stops, Map<Stop, List<GraphEdge>> outgoingEdges) {
        stops = new HashSet<>(stops);
        outgoingEdges = new HashMap<>(outgoingEdges);
    }

    public FastestPathTree fastestPaths(Stop startingStop, int departureTime) {
        FastestPathTree.Builder FPTBuilder = new FastestPathTree.Builder(startingStop, departureTime);



        return FPTBuilder.build();
    }

    /**
     * Bâtisseur permettant de créer un graphe pas-à-pas
     */
    public final static class Builder {
        /**
         * On crée l'ensemble des stops et une double table associative qui initialise les bâtisseurs d'arcs associés aux stops que l'on utilise par la suite
         */
        private Set<Stop> stops = new HashSet<>();
        private Map<Stop, Map<Stop, GraphEdge.Builder>> batisseurArcDepuisVers = new HashMap<>();
        private Map<Stop, List<GraphEdge>> outgoingEdges = new HashMap<>();

        /**
         * Constructeur par défaut du bâtisseur
         * @param stops
         *      Ensemble des arrêts contenus
         */
        public Builder(Set<Stop> stops) {
            this.stops=stops;
        }

        /**
         * Méthode ajoutant un arc de graphe précis d'un stop à un autre
         * @param fromStop
         *          Stop de départ
         * @param toStop
         *          Stop d'arrivée
         * @param departureTime
         *          Heure de départ
         * @param arrivalTime
         *          Heure d'arrivée
         * @return
         *          Le bâtisseur pour permettre les appels chaînés
         */
        public Builder addTripEdge(Stop fromStop, Stop toStop, int departureTime, int arrivalTime) {
            if (!stops.contains(fromStop) || !stops.contains(toStop)) {
                throw new IllegalArgumentException("Adding an edge from or to a stop that isn't valid");
            } else if (departureTime < 0 || arrivalTime < 0 || arrivalTime < departureTime) {
                throw new IllegalArgumentException("departure or arrival time is < 0 ; or arrival is before departure");
            }

            GraphEdge.Builder graphBuilder = getBatisseur(fromStop,toStop);
            graphBuilder.addTrip(departureTime,arrivalTime);

            if (outgoingEdges.containsKey(fromStop)) {
                outgoingEdges.get(fromStop).add(graphBuilder.build());
            } else {
                outgoingEdges.put(fromStop, new LinkedList<GraphEdge>());
                outgoingEdges.get(fromStop).add(graphBuilder.build());
            }

            return this;
        }

        /**
         * Méthode calculant automatiquement tous les temps de trajet à pied et les ajoutant
         * @param maxWalkingTime
         *          Temps de marche maximal considéré comme valant la peine d'être considéré
         * @param walkingSpeed
         *          Vitesse de marche considérée
         * @return
         *          Le bâtisseur pour permettre les appels chaînés
         */
        public Builder addAllWalkEdges(int maxWalkingTime, double walkingSpeed) {
            if (maxWalkingTime < 0) {
                throw new IllegalArgumentException("maxWalkingTime < O is not possible ");
            } else if (walkingSpeed <= 0) {
                throw new IllegalArgumentException("walkingSpeed <= 0 is not possible");
            }

            double maxDistance = maxWalkingTime*walkingSpeed;

            Stop[] stopsArray = stops.toArray(new Stop[stops.size()]);

            for (Stop aStopsArray : stopsArray) {
                for (Stop aStop : stopsArray) {
                    if (aStopsArray.position().distanceTo(aStop.position()) <= maxDistance) {
                        int walkingtime = (int) ((aStopsArray.position().distanceTo(aStop.position())) / walkingSpeed);

                        GraphEdge.Builder builder = getBatisseur(aStopsArray, aStop);
                        builder.setWalkingTime(walkingtime);
                        if (outgoingEdges.containsKey(aStop)) {
                            outgoingEdges.get(aStop).add(builder.build());
                        } else {
                            List<GraphEdge> tempList = new LinkedList<>();
                            tempList.add(builder.build());
                            outgoingEdges.put(aStop, tempList);
                        }
                    }
                }
            }

            return this;
        }

        /**
         * Construit un arc à l'aide de son bâtisseur initialisé plus tôt pour y accéder facilement
         * @param fromStop
         *          Arrêt de départ
         * @param toStop
         *          Arrêt d'arrivée
         * @return
         *          L'arc de graphe associant ces deux arrêts
         */
        private GraphEdge.Builder getBatisseur(Stop fromStop, Stop toStop) {
            if (batisseurArcDepuisVers.containsKey(fromStop)) {
                if (batisseurArcDepuisVers.get(fromStop).containsKey(toStop)){
                    return batisseurArcDepuisVers.get(fromStop).get(toStop);
                } else {
                    batisseurArcDepuisVers.get(fromStop).put(toStop, new GraphEdge.Builder(toStop));
                    return batisseurArcDepuisVers.get(fromStop).get(toStop);
                }
            } else {
                Map<Stop, GraphEdge.Builder> toStopMap = new HashMap<>();
                toStopMap.put(toStop, new GraphEdge.Builder(toStop));
                batisseurArcDepuisVers.put(fromStop, toStopMap);
                return batisseurArcDepuisVers.get(fromStop).get(toStop);
            }
        }

        /**
         * Construit un Graphe à l'aide des informations reçues par le bâtisseur jusque là
         * @return
         *      Le nouveau graphe
         */
        Graph build() {
            return new Graph(stops, outgoingEdges);
        }
    }

    private static class DijkstraPriorityQueue {
        private Map<Stop, Integer> timeToStopMap = new HashMap<>();
        private Stop firstStop;
        private Set<Stop> stopSet;
        private PriorityQueue<Stop> priorityQueue = new PriorityQueue<>(stopSet.size(), new compareEarliestArrivals(timeToStopMap));

        public DijkstraPriorityQueue(Map<Stop, GraphEdge> allEdges, Stop firstStop, int departure) {
            this.stopSet = new HashSet<>(allEdges.keySet());
            this.firstStop = firstStop;

            for (Stop aStop : allEdges.keySet()) {
                timeToStopMap.put(aStop, allEdges.get(aStop).earliestArrivalTime(departure));
            }
        }

        public List<Stop> applyDijkstra (DijkstraPriorityQueue dijkstraPriorityQueue) {
            return new LinkedList<>();
        }

        public Stop getNextElement() {
            return priorityQueue.remove();
        }
    }

    private final static class compareEarliestArrivals implements Comparator<Stop> {
        private Map<Stop, Integer> arrivalMap = new HashMap<>();

        public compareEarliestArrivals(Map<Stop, Integer> arrivalMap) {
            this.arrivalMap = arrivalMap;
        }

        @Override
        public int compare(Stop s1, Stop s2) {
            if (arrivalMap.get(s1) < arrivalMap.get(s2)) {
                return -1;
            } else if (arrivalMap.get(s1).equals(arrivalMap.get(s2))) {
                return 0;
            } else {
                return 1;
            }
        }
    }

}
