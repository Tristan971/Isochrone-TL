package ch.epfl.isochrone.geo;

import java.lang.IllegalArgumentException;

/**
 * Un point dans le système de coordonnées OSM
 * @author Tristan Deloche (234045)
 */

public final class PointOSM {
    private final int zoom;
    private final double x, y;

    /**
     * Constructeur de la classe
     * @param zoom
     *      Niveau de zoom dans le système OSM
     * @param x
     *      Longitude du point
     * @param y
     *      Latitude du point
     * @throws IllegalArgumentException
     *      Si zoom négatif
     */
    public PointOSM(int zoom, double x, double y) {
        if (x < 0 || zoom < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        if (x > Math.pow(2,zoom+8) || y > Math.pow(2, zoom+8)) {
            throw new IllegalArgumentException();
        }
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    /**
     * Une méthode pour obtenir les valeurs max pour X et Y car carte carrée dépendant du zoom
     * @param zoom
     *      Niveau de zoom dans le système OSM
     * @throws IllegalArgumentException
     *      Si le zoom demandé est négatif
     * @return Valeurs maximales (c'est-à-dire longueur des côtés du carré formé par la "carte" OSM
     */
    public static int maxXY(int zoom){
        if (zoom < 0) {
            throw new IllegalArgumentException();
        }
        return (int)Math.pow(2,zoom+8);
    }

    /**
     * @return La longitude du point auquel la méthode est appliqué
     */
    public double x() {return this.x;}

    /**
     * @return La latitude du point auquel la méthode est appliqué
     */
    public double y(){return this.y;}

    /**
     * @return Le niveau de zoom actuel du point testé
     */
    public int zoom() {return this.zoom;}

    /**
     * @return Arrondi de la longitude
     */
    public int roundedX(){
        return (int)Math.round(this.x());
    }

    /**
     * @return Arrondi de la latitude
     */
    public int roundedY(){
        return (int)Math.round(this.y());
    }

    /**
     * Retourne le point dans un système OSM mais à un autre niveau de zoom
     * @param newZoom
     *      Nouveau zoom à utiliser en lieu et place de l'ancien pour le point nouvellement créé
     * @throws IllegalArgumentException
     *      Si zoom négatif
     * @return Un nouveau point qui correspond à l'ancien au niveau de zoom "newZoom"
     */
    public PointOSM atZoom(int newZoom) {

        if (newZoom < 0) {
            throw new IllegalArgumentException("Zoom < 0 isn't valid ("+newZoom+")");
        }

        double newx, newy;
        newx = this.x * Math.pow(2, (newZoom - this.zoom()));
        newy = this.y * Math.pow(2,(newZoom-this.zoom()));

        return new PointOSM(newZoom, newx, newy);
    }

    /**
     * Retourne le point projeté dans le système WGS84
     * @return La Projection du point dans le système WGS84
     */
    public PointWGS84 toWGS84() {
        double s = Math.pow(2,this.zoom()+8);

        double newLongitude, newLatitude;

        newLongitude = ((2*Math.PI)/s)*this.x()-Math.PI;
        newLatitude = Math.atan(Math.sinh((Math.PI-((2*Math.PI*this.y())/s))));

        return new PointWGS84(newLongitude, newLatitude);
    }

    /**
     * Override de String pour avoir un retour approprié à des points
     * @return une String de la forme "(zoom, longitude, latitude)"
     */
    @Override
    public String toString() {
        return "("+this.zoom()+","+Math.toDegrees(this.x())+","+Math.toDegrees(this.y())+")";
    }

}
