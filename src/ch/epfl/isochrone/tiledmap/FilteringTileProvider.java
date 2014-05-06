package ch.epfl.isochrone.tiledmap;

/**
 * Abstraction du concept de filtreur de tuiles
 * @author Tristan Deloche (234045)
 */

public abstract class FilteringTileProvider implements TileProvider {
    abstract public int transformARGB(int argb);
}
