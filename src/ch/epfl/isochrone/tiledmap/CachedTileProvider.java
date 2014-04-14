package ch.epfl.isochrone.tiledmap;

/**
 * Fournisseur de tiles en cache
 * @author Tristan Deloche (234045)
 */

public class CachedTileProvider implements TileProvider {
    private TileCache tileCache = new TileCache();
    private OSMTileProvider osmTileProvider;

    public CachedTileProvider(OSMTileProvider osmTileProvider) {
        this.osmTileProvider = osmTileProvider;
    }

    public Tile tileAt(int zoom, int x, int y) {
        String associatedString = ""+zoom+""+x+""+y+"";
        if (tileCache.containsKey(associatedString)) {
            return tileCache.get(zoom, x, y);
        } else {
            tileCache.put(zoom, x, y, osmTileProvider.tileAt(zoom, x, y));
            return tileCache.get(zoom, x, y);
        }
    }
}
