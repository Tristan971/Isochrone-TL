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

    public Integer[] makeParameterTable() {
        Integer[] yolo = new Integer[3];
        yolo[0] = getZoom();
        yolo[1] = getLongitude();
        yolo[2] = getLatitude();
        return yolo;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tile tile = (Tile) o;

        if (latitude != tile.latitude) {
            return false;
        }
        if (longitude != tile.longitude) {
            return false;
        }
        if (zoom != tile.zoom) {
            return false;
        }
        if (bufferedImage != null ? !bufferedImage.equals(tile.bufferedImage) : tile.bufferedImage != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = zoom;
        result = 31 * result + longitude;
        result = 31 * result + latitude;
        result = 31 * result + (bufferedImage != null ? bufferedImage.hashCode() : 0);
        return result;
    }
}
