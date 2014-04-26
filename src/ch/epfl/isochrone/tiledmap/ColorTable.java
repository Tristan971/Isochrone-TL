package ch.epfl.isochrone.tiledmap;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestion des tables de couleur
 * @author Tristan Deloche (234045)
 */
public final class ColorTable {
    private Map<Integer, Color> colorMap = new HashMap<>();

    public ColorTable(Integer[] durationDeltas) {
        int i = 0;
        for (Integer anInt : durationDeltas) {
            colorMap.put(i, new Color(anInt, anInt, anInt));
            i++;
        }
    }

    public int getNumberOfColors() {
        return colorMap.keySet().size();
    }

}
