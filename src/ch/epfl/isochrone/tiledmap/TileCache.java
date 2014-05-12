package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Table associative servant de cache aux tiles
 * @author Tristan Deloche (234045)
 */

public class TileCache extends LinkedHashMap {
    private static final int MAX_SIZE = 100;

    /**
     * Une linkedhashmap qui supprime automatiquement sa plus ancienne valeur lorsqu'elle dépasse la taille critique *MAX_SIZE*
     */
    private LinkedHashMap<Integer[], Tile> cacheMap = new LinkedHashMap<Integer[], Tile>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer[],Tile> e){
                    return size() > MAX_SIZE;
                }
    };

    /**
     * Renvoie la tuile en cache pour les coordonnées décrites en paramètres
     * @param zoom
     *      Zoom lié
     * @param x
     *      Longitude liée
     * @param y
     *      Latitude liée
     * @return
     *      La tuile liée présente dans la LinkedHashMap et ayant pour clé associée le tableau fait à partir des paramètres
     */
    public Tile get(int zoom, int x, int y) {
        return cacheMap.get(new Tile(zoom, x, y, null).packCoordinates());
    }

    /**
     * Permet d'ajouter une tuile à la LinkedHashMap en générant son tableau associé
     * @param zoom
     *      Zoom lié
     * @param x
     *      Longitude liée
     * @param y
     *      Latitude liée
     * @param tile
     *      La tuile à ajouter à la LHM
     */
    public void put(Tile tile) {
        cacheMap.put(tile.packCoordinates(), tile);
    }
}