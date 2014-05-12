package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;


/**
 * Fournisseur de tuile servant à appliquer une opacité pixel par pixel à une tuile tierce
 * @author Tristan Deloche (234045)
 */

public class TransparentTileProvider extends FilteringTileProvider {

    private double opacity;
    private TileProvider tileProvider;

    /**
     * Constructeur principal de la classe
     * @param opacity
     *      Opacité désirée
     */
    public TransparentTileProvider(TileProvider tileProvider, double opacity) {
        if (opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Opacity out of bounds : "+opacity);
        }
        this.opacity = opacity;
        this.tileProvider=tileProvider;
    }

    /**
     * On retire la valeur du canal alpha de argb, puis on ajoute celle calculée à partir de l'opacité voulue
     * @param argb
     *      L'argb d'origine
     * @return
     *      Le nouvel argb
     */
    @Override
    public int transformARGB(int argb) {
        int a = argb & 0x00ffffff;
        a += (int) (opacity*255) << 24;
        return a;
    }

    /**
     * Modif de toString
     * @return
     *      L'opacité
     */
    @Override
    public String toString() {
        return ""+opacity;
    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {
        Tile tempTile = tileProvider.tileAt(zoom, x, y);

        BufferedImage bufferedImage = tempTile.getBufferedImage();

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                bufferedImage.setRGB(i, j, transformARGB(bufferedImage.getRGB(i, j)));
            }
        }

        return new Tile(tempTile.getZoom(), tempTile.getLongitude(), tempTile.getLatitude(), bufferedImage);
    }
}
