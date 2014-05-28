package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Table associative servant de cache aux tiles
 * @author Tristan Deloche (234045)
 */

public class TileCache extends LinkedHashMap {
    private static final int MAX_SIZE = 100;

    //Hashmap ayant une taille maximale et stopckant le cache à proprement parler
    private LinkedHashMap<Long, Tile> cacheMap = new LinkedHashMap<Long, Tile>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long,Tile> e){
                    return size() > MAX_SIZE;
                }
    };

    /**
     * Renvoie la tile associée à la valeur en argument
     * @param aLong
     *      Coordonnées "packées" de la tile recherchée
     * @return
     *      La tile demandée
     */
    public Tile get(Long aLong) {
        return cacheMap.get(aLong);
    }

    /**
     * Ajoute une tile au cache
     * @param tile
     *      Tile à ajouter
     */
    public void put(Tile tile) {
        cacheMap.put(tile.packCoordinates(), tile);
    }

    /**
     * Ovveride la contenance d'une tile dans le cache
     * @param key
     *      Clef de la tile recherchée
     * @return
     *      true si dans le cache, false sinon
     */
    @Override
    public boolean containsKey(Object key) {
        return cacheMap.containsKey(key);
    }
}