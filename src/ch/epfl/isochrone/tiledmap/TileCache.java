package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Table associative servant de cache aux tiles
 * @author Tristan Deloche (234045)
 */

public class TileCache extends LinkedHashMap {
    private static final int MAX_SIZE = 100;

    private LinkedHashMap<Integer[], Tile> cacheMap = new LinkedHashMap<Integer[], Tile>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer[],Tile> e){
                    return size() > MAX_SIZE;
                }
    };

    public Tile get(int zoom, int x, int y) {
        return cacheMap.get(new Tile(zoom, x, y, null).packCoordinates());
    }

    public void put(int zoom, int x, int y, Tile tile) {
        cacheMap.put(tile.packCoordinates(), tile);
    }

}