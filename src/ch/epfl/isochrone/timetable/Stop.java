package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

/**
 * Classe gérant la notion d'arrêt
 * @author Tristan Deloche (234045)
 */

 public final class Stop {

    /**
     * Nom en tant que chaîne, et position dans le système WGS84
     */
    private final String name;
    private final PointWGS84 position;

    /**
     * Constructeur principal
     * @param name
     *      Nom à donner à l'arrêt
     * @param position
     *      Position de l'arrêt à créér
     */
    public Stop(String name, PointWGS84 position) {
        this.name = name;
        if (name.equals("Lausanne-Flon")) {
            System.out.println(name + " valide");
        }
        this.position = position;
    }

    /**
     * Getter sur le nom
     * @return
     *      Le nom de l'arrêt
     */
    public String name() {
        return this.name;
    }

    /**
     * Getter sur la position
     * @return
     *      La position de l'arrêt
     */
    public PointWGS84 position() {
        return this.position;
    }

    /**
     * Override de toString pour les besoins du projet
     * @return
     *      Le nom de l'arrêt
     */
    @Override
    public String toString() {
        return name();
    }
}
