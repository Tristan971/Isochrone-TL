package ch.epfl.isochrone.tiledmap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Table associative servant de cache aux tiles
 * @author Tristan Deloche (234045)
 */

public class TileCache extends LinkedHashMap {
    private Map<Integer[], Tile> cacheMap = new HashMap<>();

    public Tile get(int zoom, int x, int y) {
        return cacheMap.get(new Tile(zoom, x, y, null).makeParameterTable());
    }

    public void put(int zoom, int x, int y, Tile tile) {
        cacheMap.put(tile.makeParameterTable(), tile);
    }



}