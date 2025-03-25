package com.satanja.semirangetree;

import com.satanja.rangetree.Point;
import com.satanja.rangetree.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class SemiRangeTree {

    private final NavigableMap<Double, NavigableMap<Double, Point>> tree;

    public SemiRangeTree(final List<Point> points) {
        this.tree = new TreeMap<>();
        for (final Point point : points) {
            if (tree.containsKey(point.getX())) {
                final NavigableMap<Double, Point> innerTree = tree.get(point.getX());
                innerTree.put(point.getY(), point);
            } else {
                final NavigableMap<Double, Point> innerTree = new TreeMap<>();
                innerTree.put(point.getY(), point);
                tree.put(point.getX(), innerTree);
            }
        }
    }

    public List<Point> search(final Window window) {
        final List<Point> result = new ArrayList<>();

        final NavigableMap<Double, NavigableMap<Double, Point>> subset = tree.subMap(window.getXMin(), true, window.getXMax(), true);
        for (final NavigableMap<Double, Point> innerTree : subset.values()) {
            final NavigableMap<Double, Point> points = innerTree.subMap(window.getYMin(), true, window.getYMax(), true);
            result.addAll(points.values());
        }

        return result;
    }
}
