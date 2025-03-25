package com.satanja.naive;

import com.satanja.rangetree.Point;
import com.satanja.rangetree.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedPointsInRange {

    final List<Point> points;

    public SortedPointsInRange(final List<Point> points) {
        points.sort(Comparator.comparingDouble(Point::getX));
        this.points = points;
    }

    public List<Point> search(final Window window) {
        final List<Point> result = new ArrayList<>();
        final int index = Collections.binarySearch(points, new Point(window.getXMin(), 0), Comparator.comparingDouble(Point::getX));
        final int start = index < 0 ? -1 * (index + 1) : index;
        for (int i = start; i < points.size(); i++) {
            final Point point = points.get(i);

            if (window.getXMin() <= point.getX() && point.getX() <= window.getXMax() &&
                    window.getYMin() <= point.getY() && point.getY() <= window.getYMax()) {
                result.add(point);
            } else if (point.getX() > window.getXMax()) {
                break;
            }
        }
        return result;
    }
}
