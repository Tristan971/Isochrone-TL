package ch.epfl.isochrone.tiledmap;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Gestion des tables de couleur
 * @author Tristan Deloche (234045)
 */

public final class ColorTable {

    private Map<Integer, Color> colorMap;

    public ColorTable(Map<Integer, Color> colorMap) {
        this.colorMap = new HashMap<>(colorMap);
    }

    public Set<Integer> getDurations() {
        return colorMap.keySet();
    }

    public int getNumberOfDurations() {
        return colorMap.keySet().size();
    }

    public Color getColorOfDuration(int duration) {
        return colorMap.get(duration);
    }

}