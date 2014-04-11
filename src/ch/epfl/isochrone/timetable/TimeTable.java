package ch.epfl.isochrone.timetable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe gérant la notion d'Horaire
 * @author Tristan Deloche (234045)
 */

public final class TimeTable {

    /**
     * Un horaire est composé d'arrêts et de services
     */
    private Set<Stop> stops;
    private Set<Service> services;

    /**
     * Constructeur principal des horaires
     * @param stops
     *          Arrêts liés à l'horaire
     * @param services
     *          Services liés à cet horaire
     */
    public TimeTable(Set<Stop> stops, Collection<Service> services) {
        this.stops = new HashSet<>(stops);
        this.services = new HashSet<>(services);
    }

    /**
     * Getter sur les arrêts
     * @return
     *      Renvoie l'ensemble des arrêts de façon read-only (pour garantir l'immuabilité)
     */
    public Set<Stop> stops() {
        return Collections.unmodifiableSet(stops);
    }

    /**
     * Getter sur les services à une date donnée
     * @param date
     *      Date dont on veut connaître les services associés
     * @return
     *      Ensemble des services actifs à la date donnée
     */
    public Set<Service> servicesForDate(Date date) {
        Set<Service> serviceSet = new HashSet<>();
        for (Service aService : services) {
            if (aService.isOperatingOn(date)) {
                serviceSet.add(aService);
            }
        }
        return serviceSet;
    }

    /**
     * Bâtisseur permettant de créér pas-à-pas notre Horaire
     */
    final static class Builder {
        private Set<Stop> stops = new HashSet<>();
        private Set<Service> services = new HashSet<>();

        /**
         * Constrcuteur (vide ici)
         */
        public Builder() {

        }

        /**
         * Ajout d'un stop
         * @param newStop
         *      Stop à ajouter
         * @return
         *      Le bâtisseur pour permettre les appels chaînés
         */
        public Builder addStop(Stop newStop) {
            stops.add(newStop);
            return this;
        }

        /**
         * Ajout d'un service
         * @param newService
         *      Service à ajouter
         * @return
         *      Le bâtisseur pour permettres les appels chaînés
         */
        public Builder addService(Service newService) {
            services.add(newService);
            return this;
        }

        /**
         * Crée un Horaire à l'aide des informations dont dispose le bâtisseur
         * @return
         *      Un nouvel Horaire créé avec les donnés du bâtisseur
         */
        public TimeTable build() {
            return new TimeTable(stops, services);
        }
    }
}
