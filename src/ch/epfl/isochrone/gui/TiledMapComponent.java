package ch.epfl.isochrone.gui;

import ch.epfl.isochrone.tiledmap.TileProvider;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tristan Deloche (234045)
 */

public final class TiledMapComponent extends JComponent {
    int zoom;
    LinkedList<TileProvider> tileProviders;

    public TiledMapComponent(int zoom, List<TileProvider> providersList) {
        this.setLayout(new BorderLayout());
        this.zoom = zoom;
        this.tileProviders = new LinkedList<>(providersList);
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


    }
}
