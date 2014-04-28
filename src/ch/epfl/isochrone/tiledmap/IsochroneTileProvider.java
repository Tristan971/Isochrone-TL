package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.timetable.FastestPathTree;
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

    int timefromstop = 0; // <- STUB

    BufferedImage associatedBufferedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

    public IsochroneTileProvider(FastestPathTree fastestPathTree, ColorTable colorTable, int walkingSpeed) throws IOException {

        this.fastestPathTree = fastestPathTree;
        this.colorTable = colorTable;
        this.walkingSpeed = walkingSpeed;


    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {

        return new Tile(zoom, x, y, associatedBufferedImage);
    }



}
