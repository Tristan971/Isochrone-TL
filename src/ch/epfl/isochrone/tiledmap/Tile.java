package ch.epfl.isochrone.tiledmap;
import java.awt.image.BufferedImage;

/**
 * Gestion du concept de tuile
 * @author Tristan Deloche (234045)
 */

public final class Tile {
    private int zoom, longitude, latitude;
    private BufferedImage bufferedImage;

    /**
     * Constructeur principal de la classe
     * @param zoom
     *          Zoom de la tuile (système OSM)
     * @param longitude
     *          Longitude de la zone liée à la tuile (OSM)
     * @param latitude
     *          Latitude de la zone liée à la tuile (OSM)
     * @param bufferedImage
     *          Représentation graphique de la tuile ; contient un .PNG
     */
    public Tile(int zoom, int longitude, int latitude, BufferedImage bufferedImage) {
        this.zoom = zoom;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bufferedImage = bufferedImage;
    }

    /**
     * Simple getter sur la latitude
     * @return
     *      La latitude
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Simple getter sur la longitude
     * @return
     *      La longitude
     */
    public int getLongitude() {
        return longitude;
    }

    /**
     * Simple getter sur le zoom
     * @return
     *      Lz zoon
     */
    public int getZoom() {
        return zoom;
    }

    /**
     * Permet de contenir toutes les coordonnées de la tuile dans un seul objet ; utile ensuite pour
     * @return
     */
    public long packCoordinates() {
        return getZoom()+100*getLongitude()+(int) Math.pow(10,9)*getLatitude();
    }

    /**
     * Renvoie le PNG lié à la tuile en question
     * @return
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    /**
     * Base de méthode equals pour pouvoir comparer facilement deux tuiles
     * @param o
     *      Tuile à comparer avec *this*
     * @return
     *      Booléen ; true si idem, false sinon
     */
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

    /**
     * Redéfinition de la méthode hascode (car redef de equals nécessaire)
     * @return
     *      un entier symbolisant le hashcode
     */
    @Override
    public int hashCode() {
        int result = zoom;
        result = 31 * result + longitude;
        result = 31 * result + latitude;
        result = 31 * result + (bufferedImage != null ? bufferedImage.hashCode() : 0);
        return result;
    }
}
