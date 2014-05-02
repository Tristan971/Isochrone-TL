package ch.epfl.isochrone.tiledmap;

import java.awt.*;
import java.util.Collections;
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

        Collections.reverse(colorLinkedListB);

        this.colorLinkedList = colorLinkedListB;
        this.divDuration = dividingDuration;
    }

    public static Color blend(Color c0, Color c1) {
        double r = (c0.getRed() + c1.getRed()) / 2;
        double g = (c0.getGreen() + c1.getGreen()) /2;
        double b = (c0.getBlue() + c1.getBlue())/2;

        return new Color((int) r, (int) g, (int) b);
    }

    public LinkedList<Integer> getDurations() {
        LinkedList<Integer> durationList = new LinkedList<>();

        for (int i = (colorLinkedList.size())*(divDuration)-1; i >= 0; i-= divDuration) {
            durationList.add(i);
        }
        return durationList;
    }

    public int getNumberOfDurations() {
        return getDurations().size();
    }

    public Color getColorForDuration(int time) {
        int i = (int) Math.floor(time/divDuration);
        return colorLinkedList.get(i);
    }

    public Color getColorForIndex(int index) {
        return colorLinkedList.get(index);
    }
}