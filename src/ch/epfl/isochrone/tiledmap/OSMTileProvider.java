package ch.epfl.isochrone.tiledmap;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

/**
 * Fournisseur de tuiles correspondant à OpenStreetMap (pour les fonds de carte)
 * @author Tristan Deloche (234045)
 */

public class OSMTileProvider implements TileProvider {
    private String baseServerURL;

    /**
     * Constructeur principal de la classe
     * @param baseServerURL
     *          Adresse url du serveur OSM à utiliser pour récupérer les données
     */
    public OSMTileProvider(String baseServerURL) {
        this.baseServerURL = baseServerURL;
    }

    /**
     * Télécharge l'image (256 pixels de côté) liée à la zone voulue
     * @param zoom
     *      Zoom lié
     * @param x
     *      Longitude liée
     * @param y
     *      Latitude liée
     * @return
     *      Une image .PNG sous forme de Tile contenant une BufferedImage de la zone démandée sur OSM
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        String urlString = baseServerURL+zoom+"/"+x+"/"+y+".png";
        try {
            URL tileURL = new URL(urlString);
            return new Tile(zoom, x, y, ImageIO.read(tileURL));
        } catch (IOException OSMURLException) {
            try {
                OSMURLException.printStackTrace();
                return new Tile(zoom, x, y, ImageIO.read(getClass().getResource("/images/error-tile.png")));
            } catch (IOException absentErrorImageException) {
                absentErrorImageException.printStackTrace();
            }
        }
        throw new UnsupportedOperationException();
    }
}
