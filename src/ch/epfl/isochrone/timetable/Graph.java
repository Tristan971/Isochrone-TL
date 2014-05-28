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
        this.stops = new HashSet<>(Collections.unmodifiableSet(stops));
        this.outgoingEdges = new HashMap<>(Collections.unmodifiableMap(outgoingEdges));
    }

    public FastestPathTree fastestPaths(Stop startingStop, int departureTime) {
        if (!stops.contains(startingStop)) {
            throw new IllegalArgumentException("Stops don't include the said startingStop : " + startingStop);
        }
        if (departureTime < 0) {
            throw new IllegalArgumentException("departureTime out of bounds : "+departureTime);
        }
        final FastestPathTree.Builder fptBuilder = new FastestPathTree.Builder(startingStop, departureTime);
        PriorityQueue<Stop> V = new PriorityQueue<>(this.stops.size(), new Comparator<Stop>() {
            @Override
            public int compare(Stop o1, Stop o2) {
                return Integer.compare(fptBuilder.arrivalTime(o1), fptBuilder.arrivalTime(o2));
            }
        });

        V.addAll(stops);

        while(!V.isEmpty()) {
            Stop testedStop = V.remove();

            if (fptBuilder.arrivalTime(testedStop) >= SecondsPastMidnight.INFINITE) {
                break;
            }

            for (GraphEdge aGraphEdge : outgoingEdges.get(testedStop)) {
                int arrivalTimeToStopFromTestedStop = aGraphEdge.earliestArrivalTime(fptBuilder.arrivalTime(testedStop));

                if (arrivalTimeToStopFromTestedStop < fptBuilder.arrivalTime(aGraphEdge.destination())) {
                    fptBuilder.setArrivalTime(aGraphEdge.destination(), arrivalTimeToStopFromTestedStop, testedStop);
                    V.remove(aGraphEdge.destination());
                    V.add(aGraphEdge.destination());
                }
            }
        }

        return fptBuilder.build();

    }

    /**
     * Getteur sur les stops
     * @return
     *      Renvoie les stops sous forme de set
     */
    public Set<Stop> getStops() {
        return stops;
    }

    /**
     * Bâtisseur permettant de créer un graphe pas-à-pas
     */
    public final static class Builder {
        /**
         * On crée l'ensemble des stops et une double table associative qui initialise les bâtisseurs d'arcs associés aux stops que l'on utilise par la suite
         */
        private Set<Stop> stops = new HashSet<>();
        private Map<Stop, Map<Stop, GraphEdge.Builder>> doubleTable = new HashMap<>();
        private Map<Stop, List<GraphEdge>> outgoingEdges = new HashMap<>();

        /**
         * Constructeur par défaut du bâtisseur
         * @param stops
         *      Ensemble des arrêts contenus
         */
        public Builder(Set<Stop> stops) {
            this.stops=stops;
            for (Stop aStop : stops) {
                outgoingEdges.put(aStop, new ArrayList<GraphEdge>());
            }
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
                throw new IllegalArgumentException("departure or arrival time is < 0 ; or arrival is before departure (dep : "+departureTime+" ; arr : "+arrivalTime);
            }

            getBuilder(fromStop, toStop).addTrip(departureTime, arrivalTime);

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
                throw new IllegalArgumentException("maxWalkingTime < O is not possible : "+maxWalkingTime);
            } else if (walkingSpeed <= 0) {
                throw new IllegalArgumentException("walkingSpeed <= 0 is not possible");
            }

            for (Stop stop1 : stops) {
                for (Stop stop2 : stops) {

                    double walkingtime = ((stop1.position().distanceTo(stop2.position())) / walkingSpeed);

                    if (walkingtime <= maxWalkingTime) {
                        getBuilder(stop1, stop2).setWalkingTime((int) Math.round(walkingtime));
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
        private GraphEdge.Builder getBuilder(Stop fromStop, Stop toStop) {
            if (doubleTable.get(fromStop) == null) {
                Map<Stop, GraphEdge.Builder> maMap = new HashMap<>();
                maMap.put(toStop, new GraphEdge.Builder(toStop));
                doubleTable.put(fromStop, maMap);
            }
            if (doubleTable.get(fromStop).get(toStop) == null) {
                doubleTable.get(fromStop).put(toStop, new GraphEdge.Builder(toStop));
            }

            return doubleTable.get(fromStop).get(toStop);
        }

        /**
         * Construit un Graphe à l'aide des informations reçues par le bâtisseur jusque là
         * @return
         *      Le nouveau graphe
         */
        public Graph build() {
            List<GraphEdge> tempList;
            for (Stop aStop : stops) {
                tempList = new LinkedList<>();
                for (Stop aStop2 : stops) {
                    tempList.add(getBuilder(aStop, aStop2).build());
                }

                outgoingEdges.put(aStop, tempList);
            }
            return new Graph(stops, outgoingEdges);
        }
    }
}
