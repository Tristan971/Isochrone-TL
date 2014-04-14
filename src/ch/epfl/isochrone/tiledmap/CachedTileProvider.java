package ch.epfl.isochrone.tiledmap;

import java.util.Map;

/**
 * DEFCLASSE
 *
 * @author Tristan Deloche (234045)
 */
public class CachedTileProvider implements TileProvider {
    private Map<String, Tile> cachedTilesMap;
    private OSMTileProvider osmTileProvider;

    public CachedTileProvider(OSMTileProvider osmTileProvider) {
        this.osmTileProvider = osmTileProvider;
    }

    public Tile tileAt(int zoom, int x, int y) {
        String associatedString = ""+zoom+""+x+""+y+"";
        if (cachedTilesMap.containsKey(associatedString)) {
            return cachedTilesMap.get(associatedString);
        } else {
            cachedTilesMap.put(associatedString, osmTileProvider.tileAt(zoom, x, y));
            return cachedTilesMap.get(associatedString);
        }
    }
}
