package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.timetable.SecondsPastMidnight;

import java.awt.*;
import java.util.*;

/**
 * Gestion des tables de couleur
 * @author Tristan Deloche (234045)
 */

public final class ColorTable {

    private Map<Integer, Color> colorMap = new HashMap<>();
    private LinkedList<Integer> durationList;

    private int dividingDuration = SecondsPastMidnight.INFINITE;

    public ColorTable(int dividingDuration, LinkedList<Color> colorList) {
        System.out.println("\n Colorlist size : "+colorList.size()+" and list =\n"+colorList);

        if (colorList.size() >= 2) {
            for (int i = 1, colorListSize = colorList.size() - 1; i < colorListSize; i++) {
                Color aColor = colorList.get(i);
                if (i % 2 == 0) {
                    colorMap.put(i, blend(colorList.get(i - 1), colorList.get(i + 1)));
                } else {
                    colorMap.put(i, colorList.get(i));
                }
            }
        } else {
            for (int i = 0, colorListSize = colorList.size(); i < colorListSize; i++) {
                Color aColor = colorList.get(i);
                colorMap.put(i, aColor);
            }
        }

        this.dividingDuration = dividingDuration;
        this.durationList = new LinkedList<>(colorMap.keySet());
        Collections.sort(durationList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o2, o1);
            }
        });
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
        return durationList;
    }

    public int getNumberOfDurations() {
        return colorMap.keySet().size();
    }

    public Color getColorOfDuration(int duration) {
        return colorMap.get((int) Math.ceil(duration/dividingDuration) * dividingDuration);
    }

    @Override
    public String toString() {
        return "Liste : "+durationList.toString()+"\n"+"Colormap : "+colorMap.toString();
    }
}