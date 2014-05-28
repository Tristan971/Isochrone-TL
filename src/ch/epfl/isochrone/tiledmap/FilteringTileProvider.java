package ch.epfl.isochrone.tiledmap;

/**
 * Abstraction du concept de filtreur de tuiles
 * @author Tristan Deloche (234045)
 */

public abstract class FilteringTileProvider implements TileProvider {
    /**
     * Tansforme un pixel en modifiant l'une de ses caractéristiques RGBa
     * @param argb
     *      Pixel d'origine
     * @return
     *      Pixel modifié
     */
    abstract public int transformARGB(int argb);
}
