package ch.epfl.isochrone.tiledmap;

/**
 * Interface modélisant un "fournisseur" de tuiles
 * @author Tristan Deloche (234045)
 */

public interface TileProvider {
    /**
     * Renvoie la tile associée aux arguments
     * @param zoom
     *      Zoom lié
     * @param x
     *      Longitude
     * @param y
     *      Latitude
     * @return
     *      La tile liée
     */
    Tile tileAt(int zoom, int x, int y);
}
