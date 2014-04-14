package ch.epfl.isochrone.tiledmap;
import java.awt.image.BufferedImage;

/**
 * Gestion des tuiles
 * @author Tristan Deloche (234045)
 */

public final class Tile {
    private int zoom, longitude, latitude;
    private BufferedImage bufferedImage;

    public Tile(int zoom, int longitude, int latitude, BufferedImage bufferedImage) {
        this.zoom = zoom;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bufferedImage = bufferedImage;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public int getZoom() {
        return zoom;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
