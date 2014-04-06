package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.math.Math;

/**
 * Classe de gestion des Dates.
 * @author Tristan Deloche (234045)
 */

/**
 * On évite des warnings inutiles liés à l'utilisation de java.util.Date
 */

public final class Date implements Comparable<Date> {

    /**
     * Création d'un tableau contentant les données suivantes : [ 0:JOUR | 1:MOIS | 2:ANNÉE ]
     */
    private final int[] dateTable = new int[3];

    /**
     * Énumération des jours de la semaine
     */
    public enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    /**
     * Énumération des mois
     */
    public enum Month {
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER,
        DECEMBER
    }

    /**
     * Constructeur principal des dates
     *
     * @param day   Jour du mois
     * @param month Mois de l'année (JANUARY,..., DECEMBER)
     * @param year  Année
     */
    public Date(int day, Month month, int year) {
        if (day <= 0) {
            throw new IllegalArgumentException("day <= 0");
        } else if (monthToInt(month) <= 0) {
            throw new IllegalArgumentException("month <= 0");
        } else if (monthToInt(month) > 12) {
            throw new IllegalArgumentException("month > 12");
        } else if (day > getNumberOfDaysForMonth(monthToInt(month), year)) {
            throw new IllegalArgumentException("day > maxDay for this month (maxDay = " + getNumberOfDaysForMonth(monthToInt(month), year) + ")");
        }

        dateTable[0] = day;
        dateTable[1] = monthToInt(month);
        dateTable[2] = year;
    }

    /**
     * Constructeur prenant un numéro de mois plutôt que son type énuméré
     *
     * @param day   Jour du mois
     * @param month Mois de l'année (1-12)
     * @param year  Année
     */
    public Date(int day, int month, int year) {
        if (day <= 0) {
            throw new IllegalArgumentException("day <= 0");
        } else if (month <= 0) {
            throw new IllegalArgumentException("month <= 0");
        } else if (month > 12) {
            throw new IllegalArgumentException("month > 12");
        } else if (day > getNumberOfDaysForMonth(month, year)) {
            throw new IllegalArgumentException("day > maxDay for this month (maxDay = " + getNumberOfDaysForMonth(month, year) + ")");
        }

        dateTable[0] = day;
        dateTable[1] = month;
        dateTable[2] = year;
    }

    /**
     * Constructeur de copie à partir d'une Date du package java.util)
     *
     * @param date Date de JAVA.
     *
     * On évite au passage les warnings inutiles de deprecation liés à l'utilisation de java.util.Date car on le sait déjà.
     */
    @SuppressWarnings("deprecation")
    public Date(java.util.Date date) {
        if (date.getDate() <= 0) {
            throw new IllegalArgumentException("day <= 0");
        } else if (date.getMonth() + 1 <= 0) {
            throw new IllegalArgumentException("month <= 0");
        } else if (date.getMonth() + 1 > 12) {
            throw new IllegalArgumentException("month > 12");
        } else if (date.getDate() > getNumberOfDaysForMonth(date.getMonth() + 1, date.getYear() + 1900)) {
            throw new IllegalArgumentException("day > maxDay for this month (maxDay = " + getNumberOfDaysForMonth(date.getMonth() + 1, date.getYear() + 1900) + ")");
        }

        dateTable[0] = date.getDate();
        dateTable[1] = date.getMonth() + 1;
        dateTable[2] = date.getYear() + 1900;
    }

    /**
     * Gestion du nombre de jours dans le mois pour les exceptions.
     *
     * @param month Mois à étudier
     * @param year  Année en question, au cas où bissextile
     * @return Durée en jours du mois envoyé sur l'année envoyée
     */
    private int getNumberOfDaysForMonth(int month, int year) {
        switch (month) {
        case 1:
            return 31;
        case 2:
            return isBissextile(year) ? 29 : 28;
        case 3:
            return 31;
        case 4:
            return 30;
        case 5:
            return 31;
        case 6:
            return 30;
        case 7:
            return 31;
        case 8:
            return 31;
        case 9:
            return 30;
        case 10:
            return 31;
        case 11:
            return 30;
        case 12:
            return 31;
        default:
            throw new IllegalArgumentException("Month int out of range in getNumberDaysForMonth(month, year)");
        }
    }

    /**
     * @return Le jour du mois lié à la date en question
     */
    public int day() {
        return this.dateTable[0];
    }

    /**
     * @return Le mois lié à la date en question
     */
    public Month month() {
        return intToMonth(this.intMonth());
    }

    /**
     * @return Le numéro du mois lié à la date en question (1-12)
     */
    public int intMonth() {
        return this.dateTable[1];
    }

    /**
     * @return Renvoie l'année de la date en question
     */
    public int year() {
        return this.dateTable[2];
    }

    /**
     * Méthode calculant le jour de la semaine lié à la date en question. Part du principe que le 01/01/1970 était un Lundi.
     *
     * @return Jour de la semaine de la date en question.
     */
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.values()[(((this.getIntValue() + 6) % 7) + 7) % 7];
    }

    /**
     * Crée une date en ajoutant un delta (positif ou négatif) à une autre
     *
     * @param daysDiff Delta considéré
     * @return Nouvelle date créée
     */
    public Date relative(int daysDiff) {
        int value = this.getIntValue();
        value += daysDiff;

        return fixedToDate(value);
    }

    /**
     * Convertit en java.util.Date une date de type ch.epfl.isochrone.timetable.Date
     *
     * @return La java.util.Date équivalente
     */
    public java.util.Date toJavaDate() {
        return new java.util.Date(this.year() - 1900, this.intMonth() - 1, this.day());
    }

    /**
     * @return La date passée en argument sous la forme "YYYY-MM-DD"
     */
    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", this.year(), this.intMonth(), this.day());
    }

    /**
     * Vérifie que l'argument est aussi une date, puis qu'elle correspond à la première
     *
     * @param that Date comparée avec this
     * @return Un booléen qui est true si elles sont les mêmes et false sinon
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof Date && this.getIntValue() == ((Date) that).getIntValue();
    }

    /**
     * @return Hashcode de l'objet this. Ici la représentation entière de this.
     */
    @Override
    public int hashCode() {
        return this.getIntValue();
    }

    /**
     * Compare deux dates
     *
     * @param that Seconde date
     * @return 1 si that < this, 0 si that == this, et 1 si that > this.
     *
     * Implémente la méthode reçue de Comparable<E> (ici Date)
     */
    @Override
    public int compareTo(Date that) {
        if (that == null) {
            throw new IllegalArgumentException("Une date dit être initialisée pour être comprée à une autre!");
        }
        if (this.getIntValue() < that.getIntValue()) {
            return -1;
        } else if (this.getIntValue() == that.getIntValue()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Convertir un numéro de mois en son objet énuméré
     *
     * @param intMonth Numéro du mois
     * @return Months correspondant au int passé en argument
     */
    public Month intToMonth(int intMonth) {
        if (intMonth < 1 || intMonth > 12) {
            throw new IllegalArgumentException("Pas un numéro de mois valide : " + intMonth);
        }
        return Month.values()[intMonth - 1];
    }

    /**
     * Transforme un Month en int. Donc un mois énuméré en son index
     *
     * @param month Mois envoyé en paramètre et à transformer
     * @return index de celui-ci (1-12)
     */
    public int monthToInt(Month month) {
        return month.ordinal() + 1;
    }

    /**
     * Transforme une date du calendrier grégorien en sa représentation entière comme jours passés depuis le 1er Javier 1900
     *
     * @param day   Jour
     * @param month Mois de l'année (1-12)
     * @param year  Année
     * @return Représentation entière de la date
     */
    public int dateToFixed(int day, int month, int year) {
        int c, y0 = year - 1;

        if (month <= 2) {
            c = 0;
        } else if (month > 2 && isBissextile(year)) {
            c = -1;
        } else {
            c = -2;
        }

        return 365 * y0 + Math.divF(y0, 4) - Math.divF(y0, 100) + Math.divF(y0, 400) + Math.divF((367 * month - 362), 12) + c + day;
    }


    /**
     * Simple raccourci pour appeler dateToFixed de façon aisée sur une date de notre paquet
     *
     * @return Représentation entière de la date pour laquelle la méthode est appelée.
     */
    public int getIntValue() {
        return dateToFixed(day(), intMonth(), year());
    }


    /**
     * Transforme une représentation entière d'une date en un triplet de valeurs du système du calendrier Grégorien
     *
     * @param intDate Représentation entière passée en argument
     * @return ch.epfl.isochrone.timetable.Date correspondante
     */
    public Date fixedToDate(int intDate) {
        int p, c;

        int d0 = (intDate - 1);
        int n400 = Math.divF(d0, 146097);
        int d1 = Math.modF(d0, 146097);
        int n100 = Math.divF(d1, 36524);
        int d2 = Math.modF(d1, 36524);
        int n4 = Math.divF(d2, 1461);
        int d3 = Math.modF(d2, 1461);
        int n1 = Math.divF(d3, 365);
        int y0 = 400 * n400 + 100 * n100 + 4 * n4 + n1;

        int year, month, day;

        year = (n100 == 4 || n1 == 4) ? y0 : y0 + 1;

        p = intDate - dateToFixed(1, 1, year);

        if (intDate < dateToFixed(1, 3, year)) {
            c = 0;
        } else if (intDate >= dateToFixed(1, 3, year) && isBissextile(year)) {
            c = 1;
        } else {
            c = 2;
        }

        month = Math.divF((12 * (p + c) + 373), 367);
        day = intDate - dateToFixed(1, month, year) + 1;

        return new Date(day, intToMonth(month), year);
    }


    /**
     * Teste si une année est bissextile
     *
     * @param year Année à vérifier
     * @return booléen (1 si bissextile, 0 sinon)
     */
    public boolean isBissextile(int year) {
        return ((Math.modF(year, 4) == 0 && Math.modF(year, 100) != 0) || (Math.modF(year, 400) == 0));
    }
}