package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Stop;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Fournisseur de tuiles de carte isochrone
 * @author Tristan Deloche (234045)
 */

/*
COUCHES :
    - 0 : +40 (0,0,0)
    - 1 : -40 (c0+c2)*0,5
    - 2 : -35 (0,0,1)
    - 3 : -30 (c2+c4)*0,5
    - 4 : -25 (0,1,0)
    - 5 : -20 (c4+c6)*0,5
    - 6 : -15 (1,1,0)
    - 7 : -10 (c6+c8)*0,5
    - 8 : -05 (1,0,0)
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
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setColor(colorTable.getColorOfDuration(SecondsPastMidnight.INFINITE));
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        for (Integer anInteger : colorTable.getDurations()) {
            for (Stop aStop : fastestPathTree.stops()) {

                int xPoint = (int) Math.floor(aStop.position().toOSM(zoom).x()/256);
                int yPoint = (int) Math.floor(aStop.position().toOSM(zoom).y()/256);

                if (xPoint == x && yPoint == y) {
                    int time = anInteger - ch.epfl.isochrone.math.Math.divF(fastestPathTree.arrivalTime(aStop) - fastestPathTree.startingTime(),60);
                    if (time > 0) {
                        graphics2D.setColor(colorTable.getColorOfDuration(anInteger));
                        graphics2D.fill(new Ellipse2D.Double(aStop.position().toOSM(zoom).x(), aStop.position().toOSM(zoom).y(), getRayonAtScale(time*walkingSpeed, zoom), getRayonAtScale(time*walkingSpeed, zoom)));
                    }
                }
            }
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
