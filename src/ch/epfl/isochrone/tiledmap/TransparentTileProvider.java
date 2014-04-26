package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;

import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;


/**
 * Fournisseur de tuile servant à appliquer une opacité pixel par pixel à une tuile tierce
 * @author Tristan Deloche (234045)
 */

public class TransparentTileProvider extends FilteringTileProvider {

    private double opacity;

    /**
     * Constructeur principal de la classe
     * @param opacity
     *      Opacité désirée
     */
    public TransparentTileProvider(double opacity) {
        if (opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Opacity out of bounds : "+opacity);
        }
        this.opacity = opacity;
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
        argb -= (1/255)*modF(divF(argb, (int) Math.pow(2,24)), (int) Math.pow(2,8));
        return argb+(int) Math.pow(2,24)* (int) Math.round(255*opacity);
    }

    /**
     * Transformation pixel par pixel d'une image associée à une tuile
     * @param originalTile
     *          Tuile dont on veut modifier l'image
     * @return
     *          Copie de cette tuile avec l'image liée modifiée
     */
    @Override
    public Tile transform(Tile originalTile) {
        BufferedImage bufferedImage = originalTile.getBufferedImage();

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                bufferedImage.setRGB(i, j, transformARGB(bufferedImage.getRGB(i, j)));
            }
        }

        return new Tile(originalTile.getZoom(), originalTile.getLongitude(), originalTile.getLatitude(), bufferedImage);
    }

    /**
     * Getter sur l'opacité
     * @return
     *      Renvoie l'opacité
     */
    public double getOpacity() {
        return opacity;
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
}
