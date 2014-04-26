package ch.epfl.isochrone.tiledmap;

/**
 * Abstraction du concept de filtreur de tuiles
 * @author Tristan Deloche (234045)
 */

public abstract class FilteringTileProvider {
    abstract public int transformARGB(int argb);
    abstract public Tile transform(Tile originalTile);
}
