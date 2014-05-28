package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Stop;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Fournisseur de tuiles de carte isochrone
 * @author Tristan Deloche (234045)
 */

public final class IsochroneTileProvider implements TileProvider {

    private FastestPathTree fastestPathTree;
    private ColorTable colorTable;
    private double walkingSpeed;

    /**
     * Constructeur principal de la classe
     * @param fastestPathTree
     *          FastetPathTree à utiliser pour calculer les trajets
     * @param colorTable
     *          Table de couleurs à utiliser pour les cercles
     * @param walkingSpeed
     *          Vitesse de marche à considérer
     * @throws IOException
     *          Si problème d'accès aux données OSM
     */
    public IsochroneTileProvider(FastestPathTree fastestPathTree, ColorTable colorTable, double walkingSpeed) throws IOException {
        this.fastestPathTree = fastestPathTree;
        this.colorTable = colorTable;
        this.walkingSpeed = walkingSpeed;
    }

    /**
     * Renvoie la tile aux coordonnées et zoom passés en argument
     * @param zoom
     *      Zoom à considérer
     * @param x
     *      Longitude
     * @param y
     *      Latitude
     * @return
     *      Tile associée
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        BufferedImage bufferedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setColor(colorTable.getColorForIndex(colorTable.getDurations().size()-1));
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        PointOSM point1 = new PointOSM(zoom, x*256, y*256);
        PointOSM point2 = new PointOSM(zoom, x*256, (y+1)*256);
        double distancePerPixel = point1.toWGS84().distanceTo(point2.toWGS84()) /256;

        // Pour chaque tranche, on itère sur chaque stop et on dessine en fonction du temps restant les cercles autour dudit stop
        for (Integer anInteger : colorTable.getDurations()) {
            graphics2D.setColor(colorTable.getColorForDuration(anInteger));
            for (Stop aStop : fastestPathTree.stops()) {
                int time = anInteger - ch.epfl.isochrone.math.Math.divF(fastestPathTree.arrivalTime(aStop) - fastestPathTree.startingTime(),60);
                if (time > 0) {
                    double param = 60 * time *walkingSpeed / distancePerPixel;
                    double paramx = aStop.position().toOSM(zoom).x()-(x*256);
                    double paramy = aStop.position().toOSM(zoom).y()-(y*256);
                    graphics2D.fill(new Ellipse2D.Double(paramx - param, paramy - param, param*2, param*2));
                }
            }
        }

        return new Tile(zoom, x, y, bufferedImage);
    }
}
