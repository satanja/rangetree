package com.satanja.naive;

import com.satanja.rangetree.Point;
import com.satanja.rangetree.Window;

import java.util.ArrayList;
import java.util.List;

public class PointsInRange {

    final List<Point> points;

    public PointsInRange(final List<Point> points) {
        this.points = points;
    }

    public List<Point> search(final Window window) {
        final List<Point> result = new ArrayList<>();
        for (final Point point : points) {
            if (window.getXMin() <= point.getX() && point.getX() <= window.getXMax() &&
                    window.getYMin() <= point.getY() && point.getY() <= window.getYMax()) {
                result.add(point);
            }
        }

        return result;
    }
}
