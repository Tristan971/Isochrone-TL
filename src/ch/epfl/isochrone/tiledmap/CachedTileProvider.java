package ch.epfl.isochrone.tiledmap;

/**
 * Fournisseur de tiles en cache (sert d'intermédiaire entre provider et cache pour la mémoïzation)
 * @author Tristan Deloche (234045)
 */

public class CachedTileProvider implements TileProvider {
    private TileCache tileCache = new TileCache();
    private OSMTileProvider osmTileProvider;

    /**
     * Constructeur principal de la classe
     * @param osmTileProvider
     *          Le provider OpenStreetMap utilisé
     */
    public CachedTileProvider(OSMTileProvider osmTileProvider) {
        this.osmTileProvider = osmTileProvider;
    }

    /**
     * Renvoie la tile d'un envroit donné
     * @param zoom
     *      Zoom lié
     * @param x
     *      Longitude liée
     * @param y
     *      Latitude liée
     * @return
     *      La version en cache si cette tuile a déjà été téléchargée, la télécharge et la renvoie sinon
     */
    public Tile tileAt(int zoom, int x, int y) {

        Long packed = new Tile(zoom, x, y, null).packCoordinates();

        if (tileCache.containsKey(packed)) {
            return tileCache.get(packed);
        } else {
            tileCache.put(osmTileProvider.tileAt(zoom, x, y));
            return tileCache.get(packed);
        }
    }
}
