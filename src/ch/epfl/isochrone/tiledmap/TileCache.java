package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Table associative servant de cache aux tiles
 * @author Tristan Deloche (234045)
 */

public class TileCache extends LinkedHashMap {
    private static final int MAX_SIZE = 100;

    private LinkedHashMap<Long, Tile> cacheMap = new LinkedHashMap<Long, Tile>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long,Tile> e){
                    return size() > MAX_SIZE;
                }
    };

    public Tile get(Long aLong) {
        return cacheMap.get(aLong);
    }

    public void put(Tile tile) {
        cacheMap.put(tile.packCoordinates(), tile);
    }

    @Override
    public boolean containsKey(Object key) {
        return cacheMap.containsKey(key);
    }
}