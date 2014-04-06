package ch.epfl.isochrone.geo;

import java.lang.IllegalArgumentException;
import java.lang.Math;

/**
 * Un point dans le système de coordonnées WGS84
 * @author Tristan Deloche (234045)
 */

public final class PointWGS84 {
    private final double longitude, latitude;

    /**
     * Constructeur de la classe
     * @param longitude
     *      Longitude du point
     * @param latitude
     *      Latitude du point
     * @throws IllegalArgumentException
     *      Si la longitude est hors de [-π;π] car c'est un angle en radians. De même pour la latitude qui doit être dans [-π/2;π/2].
     */
    public PointWGS84(double longitude, double latitude) {
        if (-(Math.PI) > longitude || longitude > (Math.PI)){
            throw new IllegalArgumentException();
        } else if ((-(Math.PI)/2) > latitude || latitude > (Math.PI/2)) {
            throw new IllegalArgumentException();
        }

        this.longitude=longitude;
        this.latitude=latitude;
    }

    /**
     * @return Longitude du point
     */
    public double longitude() {return longitude;}

    /**
     * @return Latitude du point
     */
    public double latitude() {return latitude;}

    /**
     * Calcule la distance entre deux points dans un système de coordonnées WGS84
     * @param that
     *      Point dont la distance à celui sur lequel la méthode est appliquée est retournée. Doit être dans un système WGS84 sinon nécessite projection!
     * @return Distance à la surface sur la Terre approximée comme sphère parfaite entre les points "this" et "that".
     */
    public double distanceTo(PointWGS84 that) {
        return 2*6378137*Math.asin(Math.sqrt(ch.epfl.isochrone.math.Math.haversin(this.latitude - that.latitude)+Math.cos(this.latitude)*Math.cos(that.latitude)*ch.epfl.isochrone.math.Math.haversin(this.longitude-that.longitude)));
    }

    /**
     * Méthode servant à projeter d'un système WGS84 à un système OSM.
     * @param zoom
     *      Niveau de zoom à utiliser pour la projection dans le système OSM.
     * @throws java.lang.IllegalArgumentException
     *      Si le zoom demandé est négatif.
     * @return La projection du point dans un système de coordonnées OSM (au niveau de zoom "zoom")
     */
    public PointOSM toOSM(int zoom){

        if (zoom<0) {
            throw new IllegalArgumentException();
        }

        double s = Math.pow(2,zoom+8);

        double x = (s/(2* Math.PI))*(this.longitude()+Math.PI);
        double y = (s/(2* Math.PI))*(Math.PI-ch.epfl.isochrone.math.Math.asinh(Math.tan(this.latitude())));

        return new PointOSM(zoom, x, y);

    }

    /**
     * Override de toString pour avoir des retours plus appropriés à des points dans un système de coordonnées WGS84
     * @return Une String de la forme "(longitude, latitude)".
     */
    @Override
    public String toString() {
        return "("+Math.toDegrees(this.longitude())+","+Math.toDegrees(this.latitude())+")";
    }
}
