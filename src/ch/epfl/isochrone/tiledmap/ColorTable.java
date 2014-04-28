package ch.epfl.isochrone.tiledmap;

import java.awt.*;
import java.util.*;

/**
 * Gestion des tables de couleur
 * @author Tristan Deloche (234045)
 */

public final class ColorTable {

    private Map<Integer, Color> colorMap = new HashMap<>();

    public ColorTable(int dividingDuration, LinkedList<Color> colorList) {
        int i = dividingDuration * colorList.size();
        for (Color aColor : colorList) {
            colorMap.put(i, aColor);
        }
    }

    public Set<Integer> getDurations() {
        return colorMap.keySet();
    }

    public int getNumberOfDurations() {
        return colorMap.keySet().size();
    }

    public Color getColorOfDuration(int duration) {
        return colorMap.get((int) Math.ceil(duration/5) * 5);
    }
}