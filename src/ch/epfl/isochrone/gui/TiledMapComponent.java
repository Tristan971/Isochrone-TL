package ch.epfl.isochrone.gui;

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
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int dimension = (int) Math.pow(2, zoom+8);
        return new Dimension(dimension, dimension);
    }

    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D graphics2D = (Graphics2D) g0;

        int minX = (int) (getVisibleRect().getMinX() / 256);
        int maxX = (int) (getVisibleRect().getMaxX() / 256);
        int minY = (int) (getVisibleRect().getMinY() / 256);
        int maxY = (int) (getVisibleRect().getMaxY() / 256);

        System.out.println("minx : "+minX+" ; maxx : "+maxX+" ; miny : "+minY+" ; maxy : "+maxX);

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; i <= maxY; i++) {
                for (TileProvider aTileProvider : tileProviders) {
                    graphics2D.drawImage(aTileProvider.tileAt(zoom, i, j).getBufferedImage(), null, 0, 0);
                }
            }
        }
    }

    public int zoom() {
        return zoom;
    }
}
