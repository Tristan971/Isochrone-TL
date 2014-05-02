package ch.epfl.isochrone.tiledmap;

import java.awt.*;
import java.util.LinkedList;

/**
 * Gestion des tables de couleur
 * @author Tristan Deloche (234045)
 */

public final class ColorTable {

    private LinkedList<Color> colorLinkedList;
    private int divDuration;

    public ColorTable(int dividingDuration, LinkedList<Color> colorList) {
        LinkedList<Color> colorLinkedListB = new LinkedList<>();

        for (Color aColor : colorList) {
            colorLinkedListB.add(aColor);
            if (colorList.indexOf(aColor) < colorList.size() - 1) {
                colorLinkedListB.add(blend(aColor, colorList.get(colorList.indexOf(aColor) + 1)));
            }
        }

        this.colorLinkedList = colorLinkedListB;
        this.divDuration = dividingDuration;
    }

    public static Color blend(Color c0, Color c1) {
        double totalAlpha = c0.getAlpha() + c1.getAlpha();
        double weight0 = c0.getAlpha() / totalAlpha;
        double weight1 = c1.getAlpha() / totalAlpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    public LinkedList<Integer> getDurations() {
        LinkedList<Integer> durationList = new LinkedList<>();
        for (int i = (colorLinkedList.size()-1)*(divDuration); i >= 0; i-= divDuration) {
            durationList.add(i);
        }
        return durationList;
    }

    public int getNumberOfDurations() {
        return getDurations().size();
    }

    public Color getColorForDuration(int time) {
        int i = (int) Math.ceil((double) time/(double) divDuration);
        return colorLinkedList.get(i);
    }

    public Color getColorForIndex(int index) {
        return colorLinkedList.get(index);
    }
}