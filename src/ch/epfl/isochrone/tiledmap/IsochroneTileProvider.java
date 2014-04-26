package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.timetable.FastestPathTree;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Fournisseur de tuiles de carte isochrone
 * @author Tristan Deloche (234045)
 */

public class IsochroneTileProvider implements TileProvider {

    private FastestPathTree fastestPathTree;
    private ColorTable colorTable;
    private int walkingSpeed;

    BufferedImage associatedBufferedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

    Integer[] durationArray;

    public IsochroneTileProvider(FastestPathTree fastestPathTree, ColorTable colorTable, int walkingSpeed) throws IOException {

        this.fastestPathTree = fastestPathTree;
        this.colorTable = colorTable;
        this.walkingSpeed = walkingSpeed;


        Graphics2D graphics2DContext = associatedBufferedImage.createGraphics();

        durationArray = colorTable.getDurations().toArray(durationArray);
        Arrays.sort(durationArray);

        graphics2DContext.setColor(colorTable.getColorOfDuration(durationArray[durationArray.length-1]));
        graphics2DContext.fillRect(0, 0, associatedBufferedImage.getWidth(), associatedBufferedImage.getHeight());

        //TODO : wtf? idek what to do lol
        graphics2DContext.setColor(Color.RED);
        graphics2DContext.fill(new Ellipse2D.Double(-50, -50, 125, 125));

    }

    //TODO : stub
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        return new Tile(zoom, x, y, associatedBufferedImage);
    }
}
