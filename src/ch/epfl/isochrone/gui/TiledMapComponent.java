package ch.epfl.isochrone.gui;

import ch.epfl.isochrone.tiledmap.TileProvider;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * Classe modélisant le provider qui se charge d'"empiler" les couches de données (OSM, Isochrone...) sous forme de JComponent
 * @author Tristan Deloche (234045)
 */

public final class TiledMapComponent extends JComponent {
    private int zoom;
    private LinkedList<TileProvider> tileProviders = new LinkedList<>();

    /**
     * Constructeur principal de la classe
     * @param zoom
     *      Zoom lié
     */
    public TiledMapComponent(int zoom) {
        this.setLayout(new BorderLayout());
        this.zoom = zoom;
    }

    /**
     * Ajoute un provider au component
     * @param provider
     *      Provider à ajouter
     */
    public void addProvider(TileProvider provider) {
        tileProviders.add(provider);
        repaint();
    }

    /**
     * Calcule la taille "idéale" du provider
     * @return
     *     Renvoie cette taille
     */
    @Override
    public Dimension getPreferredSize() {
        int dimension = (int) Math.pow(2, zoom+8);
        return new Dimension(dimension, dimension);
    }

    /**
     * Méthode ajoutant "réellement" les données dans la fenêtre
     * @param g0
     *      Objet servant au "dessin"
     */
    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D graphics2D = (Graphics2D) g0;

        int minX = (int) (getVisibleRect().getMinX() / 256);
        int maxX = (int) (getVisibleRect().getMaxX() / 256);
        int minY = (int) (getVisibleRect().getMinY() / 256);
        int maxY = (int) (getVisibleRect().getMaxY() / 256);

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                for (TileProvider aTileProvider : tileProviders) {
                    graphics2D.drawImage(aTileProvider.tileAt(zoom, i, j).getBufferedImage(), null, i*256, j*256);
                }
            }
        }
    }

    /**
     * Change le zoom du component
     * @param newZoom
     *      Nouveau zoom
     */
    public void setZoom(int newZoom) {
        this.zoom = newZoom;
    }

    /**
     * Getter sur le zoom
     * @return
     *      Le zoom actuel du component
     */
    public int zoom() {
        return zoom;
    }

    /**
     * Vide la liste des providers (permet de les changer)
     */
    public void clearProviders() {
        tileProviders.clear();
    }
}
