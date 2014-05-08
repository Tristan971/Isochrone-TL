package ch.epfl.isochrone.gui;

import ch.epfl.isochrone.tiledmap.Tile;
import ch.epfl.isochrone.tiledmap.TileProvider;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Tristan Deloche (234045)
 */

public final class TiledMapComponent extends JComponent {
    int zoom;
    private LinkedList<TileProvider> tileProviders = new LinkedList<>();

    public TiledMapComponent(int zoom) {
        this.setLayout(new BorderLayout());
        this.zoom = zoom;
    }

    public void addProvider(TileProvider provider) {
        tileProviders.add(provider);
    }

    @Override
    public Dimension getPreferredSize() {
        int dimension = (int) Math.pow(2, zoom+8);
        return new Dimension(dimension, dimension);
    }

    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D graphics2D = (Graphics2D) g0;

        int tileX = (int) (getVisibleRect().getX() / 256);
        int tileY = (int) (getVisibleRect().getY() / 256);

        for (TileProvider aTileProvider : tileProviders) {
            Tile tileImage = aTileProvider.tileAt(zoom, tileX, tileY);
            graphics2D.drawImage(tileImage.getBufferedImage(), null, null);
        }
        
    }

    public int zoom() {
        return zoom;
    }
}
