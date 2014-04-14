package ch.epfl.isochrone.tiledmap;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

/**
 * Fournisseur de tuiles correspondant à OpenStreetMap (pour les fonds de carte)
 * @author Tristan Deloche (234045)
 */

public class OSMTileProvider implements TileProvider {
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        String urlString = "http://a.tile.openstreetmap.org/"+zoom+"/"+x+"/"+y+".png";
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
