package ch.epfl.isochrone.timetable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe gérant la notion de service
 * @author Tristan Deloche (234045)
 */

public final class Service {

    /**
     * Un service est défini par son nom, sa plage d'application, les jours concernés et les exceptions.
     */
    private final String name;
    private final Date startingDate, endingDate;
    private final Set<Date.DayOfWeek> operatingDays;
    private final Set<Date> excludedDates, includedDates;

    /**
     * Constructeur principal
     * @param name
     *          Nom du service
     * @param startingDate
     *          Début de la plage d'activité
     * @param endingDate
     *          Fin de la plage d'activité
     * @param operatingDays
     *          Jours de la semaine concernés
     * @param excludedDates
     *          Dates spéciales exclues
     * @param includedDates
     *          Dates spéciales inclues
     */
    public Service(String name, Date startingDate, Date endingDate, Set<Date.DayOfWeek> operatingDays, Set<Date> excludedDates, Set<Date> includedDates) {

        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.operatingDays = new HashSet<>(Collections.unmodifiableSet(operatingDays));
        this.excludedDates = new HashSet<>(Collections.unmodifiableSet(excludedDates));
        this.includedDates = new HashSet<>(Collections.unmodifiableSet(includedDates));

        if (startingDate.compareTo(endingDate) == 1) {
            throw new IllegalArgumentException("startingDate ne peut pas être postérieure à endingDate");
        }

        for (Date aDate : excludedDates) {
            if (aDate.compareTo(endingDate) == 1 || aDate.compareTo(startingDate) == -1) {
                throw new IllegalArgumentException("EXCL inutile si déjà hors de limites");
            }
        }

        for (Date aDate : includedDates) {
            if (excludedDates.contains(aDate)) {
                throw new IllegalArgumentException("INCL in EXCL (dates)");
            }
        }
    }

    /**
     * @return
     *      Le nom du service
     */
    public String name() {
        return this.name;
    }

    /**
     * Verifies if a given service is active on a given date
     * @param date
     *      Date to check
     * @return
     *      A boolean, of value true for active and false for inactive
     */
    boolean isOperatingOn(Date date) {
        boolean isBetweenStartingAndEnding = date.compareTo(startingDate) >= 0 && date.compareTo(endingDate) <= 0;
        boolean isInOperatingDay = operatingDays.contains(date.dayOfWeek());
        boolean isInExcludedDates = excludedDates.contains(date);
        boolean isInIncludedDates = includedDates.contains(date);

        return (((isInOperatingDay) && !isInExcludedDates) || isInIncludedDates) && isBetweenStartingAndEnding;
    }

    /**
     * Override de toString() pour les besoins du projet
     * @return
     *      Imprime le nom du service
     */
    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Bâtisseur permettant de créer pas à pas son service
     */
    final static class Builder {

        /**
         * Idem que ci-dessus
         */
        private final String name;
        private final Date startingDate, endingDate;
        private Set<Date.DayOfWeek> operatingDays = new HashSet<Date.DayOfWeek>();
        private Set<Date> excludedDates = new HashSet<Date>(), includedDates = new HashSet<Date>();

        public Builder(String name, Date startingDate, Date endingDate) {

            if (startingDate.compareTo(endingDate) >= 1) {
                throw new IllegalArgumentException("ending > starting est OBLIGATOIRE.");
            }

            this.name = name;
            this.startingDate = startingDate;
            this.endingDate = endingDate;
        }

        public String name() {
            return name;
        }

        /**
         * Ajouter une date spéciale exclue
         * @param date
         *      Date à exclure
         * @return
         *      Le bâtisseur, pour les appels chaînés
         */
        public Builder addExcludedDate(Date date) {
            if (includedDates.contains(date) || date.compareTo(startingDate) < 0 || date.compareTo(endingDate) > 0) {
                throw new IllegalArgumentException("new EXCL date out of range");
            }
            excludedDates.add(date);
            return this;
        }

        /**
         * Ajouter une date spéciale inclue
         * @param date
         *      Date à inclure
         * @return
         *      Le bâtisseur, pour les appels chaînés
         */
        public Builder addIncludedDate(Date date) {
            if (excludedDates.contains(date)) {
                throw new IllegalArgumentException("INCL date can not be EXCL too");
            } else if (date.compareTo(startingDate) < 0 || date.compareTo(endingDate) > 0) {
                throw new IllegalArgumentException("new INCL date out of range");
            }
            includedDates.add(date);
            return this;
        }

        /**
         * Ajouter un jour de la semaine aux jours d'opération
         * @param day
         *      Jour à ajouter
         * @return
         *      Le bâtisseur pour les appels chaînés
         */
        public Builder addOperatingDay(Date.DayOfWeek day) {
            operatingDays.add(day);
            return this;
        }

        /**
         * Construit le service à l'aide des informations dont le bâtisseur dispose dorénavant
         * @return
         *      Un nouveau service ayant pour données celles fournies au bâtisseur
         */
        public Service build() {
            return new Service(name, startingDate, endingDate, operatingDays, excludedDates, includedDates);
        }

    }

}