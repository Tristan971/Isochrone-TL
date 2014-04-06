package ch.epfl.isochrone.timetable;

/**
 * Classe de gestion des Secondes.
 * @author Tristan Deloche (234045)
 */

public final class SecondsPastMidnight {

    /**
     * Constructeur par défaut car cette classe ne doit pas être instanciable telle quelle
     */
    private SecondsPastMidnight() {}

    /**
     * Temps considéré comme étant la longueur maximale d'une journée
     * (bien que 30h = 108000 secondes et non 200000)
     */
    public static final int INFINITE = 200000;

    /**
     * Convertit un triple représentant un moment dans la journée en un nombre de secondes a écoulé depuis minuit ce jour
     * @param hours
     *          Heures écoulées
     * @param minutes
     *          Minutes écoulées depuis dernière heure
     * @param seconds
     *          Secondes écoulées depuis dernière minute
     * @throws java.lang.IllegalArgumentException
     *          Si les valeurs entrées ne sont pas cohérentes (négatives, hors de [0;30] ou hors de [0;60] selon les variables)
     * @return
     *          Secondes écoulées depuis minuit
     */
    public static int fromHMS(int hours, int minutes, int seconds) {
        if (hours < 0 || minutes < 0 || seconds < 0) {
            throw new IllegalArgumentException("Une durée est une valeur POSITIVE");
        }
        if (hours > 29 || minutes > 59 || seconds > 59) {
            throw new IllegalArgumentException("H < 30, M < 60 et S < 60. DEAL WITH IT.");
        }
        return (3600*hours+60*minutes+seconds);
    }

    /**
     * Transformation d'un moment contenu dans une date du paquetage java.util en SecondsPastMidnight
     * @param date
     *      Date à transformer
     * @return
     *      Temps écoulé depuis minuit au moment contenu dans la date passée en argument
     */
    public static int fromJavaDate(java.util.Date date) {
        return (3600*date.getHours()+60*date.getMinutes()+date.getSeconds());
    }

    /**
     * Récupération du nombre d'heures correspondant
     * @param spm
     *      spm étudié
     * @return
     *      Quotient de spm et 3600 car 3600 secondes par heure
     */
    public static int hours(int spm) {
        return ch.epfl.isochrone.math.Math.divF(spm,3600);
    }

    /**
     * Récupération du nombre de minutes correspondant
     * @param spm
     *      spm étudié
     * @return
     *      Minutes écoulées (spm-<tps représenté par les heures> le tout modulo 60)
     */
    public static int minutes(int spm) {
        return ch.epfl.isochrone.math.Math.divF(spm-3600*hours(spm), 60);
    }

    /**
     * Récupération du nombre de secondes correspondant
     * @param spm
     *      spm étudié
     * @return
     *      Secondes correspondantes. (temps non représenté par les heures et les minutes)
     */
    public static int seconds(int spm) {
        return spm-(minutes(spm)*60+hours(spm)*3600);
    }

    /**
     * toString() overridé pour les besoins du projet
     * @param spm
     *      spm à afficher
     * @return
     *      Le triplet associé à la spm, au format HH-MM-SS
     */
    public static String toString(int spm) {
        return String.format("%02d:%02d:%02d", hours(spm), minutes(spm), seconds(spm));
    }
}
