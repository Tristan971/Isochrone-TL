package ch.epfl.isochrone.tiledmap;

/**
 * Interface modélisant un "fournisseur" de tuiles
 * @author Tristan Deloche (234045)
 */

public interface TileProvider {
    public Tile tileAt(int zoom, int x, int y);
}
