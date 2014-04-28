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
        int i = dividingDuration * colorList.size();
        for (Color aColor : colorList) {

            colorMap.put(i, aColor);
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

    public LinkedList<Integer> getDurations() {
        return durationList;
    }

    public int getNumberOfDurations() {
        return colorMap.keySet().size();
    }

    public Color getColorOfDuration(int duration) {
        return colorMap.get((int) Math.ceil(duration/dividingDuration) * dividingDuration);
    }
}