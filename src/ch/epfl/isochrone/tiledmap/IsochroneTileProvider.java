package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Stop;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Fournisseur de tuiles de carte isochrone
 * @author Tristan Deloche (234045)
 */

public final class IsochroneTileProvider implements TileProvider {

    private FastestPathTree fastestPathTree;
    private ColorTable colorTable;
    private int walkingSpeed;

    public IsochroneTileProvider(FastestPathTree fastestPathTree, ColorTable colorTable, int walkingSpeed) throws IOException {

        this.fastestPathTree = fastestPathTree;
        this.colorTable = colorTable;
        this.walkingSpeed = walkingSpeed;

    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {
        BufferedImage bufferedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setColor(colorTable.getColorForIndex(colorTable.getDurations().size()-1));
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        for (Integer anInteger : colorTable.getDurations()) {
            graphics2D.setColor(colorTable.getColorForDuration(anInteger));
            for (Stop aStop : fastestPathTree.stops()) {
                int time = anInteger - ch.epfl.isochrone.math.Math.divF(fastestPathTree.arrivalTime(aStop) - fastestPathTree.startingTime(),60);
                if (time > 0) {
                    int param = getRayonAtScale(time*walkingSpeed, zoom);
                    double paramx = aStop.position().toOSM(zoom).x()-(x*256);
                    double paramy = aStop.position().toOSM(zoom).y()-(y*256);
                    graphics2D.fill(new Ellipse2D.Double(paramx - param, paramy - param, param*2, param*2));
                }
            }
        }

        try {
            ImageIO.write(bufferedImage, "PNG", new File("test.PNG"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Tile(zoom, x, y, bufferedImage);
    }

    private int getRayonAtScale(int distanceInMeters, int zoom) {
        PointOSM pointOSM1 = new PointOSM(zoom, 0, 0);
        PointOSM pointOSM2 = new PointOSM(zoom, 1, 0);
        double osmUnitInMeter = pointOSM1.toWGS84().distanceTo(pointOSM2.toWGS84());
        return (int) Math.round(distanceInMeters/osmUnitInMeter);
    }
}
