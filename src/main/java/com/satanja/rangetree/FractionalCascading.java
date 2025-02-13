package com.satanja.rangetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class FractionalCascading {

    private final List<FractionalCascadingKey> keys;

    private FractionalCascading leftChild;
    private FractionalCascading rightChild;

    FractionalCascading(final List<Point> sortedKeys) {
        this.keys = new ArrayList<>(sortedKeys.size());
        for (int i = 0; i < sortedKeys.size(); i++) {
            keys.add(new FractionalCascadingKey(sortedKeys.get(i), i));
        }
    }

    FractionalCascading(final FractionalCascading parent, final List<Point> sortedKeysSubset, final boolean isLhs) {
        this(sortedKeysSubset);
        updatePointers(parent, isLhs);
    }

    FractionalCascadingKey rootSearch(final double min) {
        final int index = Collections.binarySearch(keys, new FractionalCascadingKey(new Point(0, min), 0), Comparator.comparingDouble(f -> f.getPoint().getY()));
        if (index < 0) {
            return keys.get(-1 * (index + 1));
        } else {
            return keys.get(index);
        }
    }

    List<Point> report(final FractionalCascadingKey startKey, final Window window) {
        if (startKey == null) {
            return new ArrayList<>();
        }

        final List<Point> result = new ArrayList<>();

        for (int i = startKey.getIndex(); i < keys.size(); i++) {
            final Point point = keys.get(i).getPoint();

            if (window.getXMin() <= point.getX() && point.getX() <= window.getXMax() &&
                    window.getYMin() <= point.getY() && point.getY() <= window.getYMax()) {
                result.add(point);
            }

            if (point.getY() > window.getYMax()) {
                break;
            }
        }

        return result;
    }

    private void updatePointers(final FractionalCascading parent, final boolean isLhs) {
        int indexParent = 0;
        int indexCurrent = 0;
        final List<FractionalCascadingKey> parentKeys = parent.getKeys();

        if (isLhs) {
            parent.setLeftChild(this);
        } else {
            parent.setRightChild(this);
        }

        while (indexParent < parentKeys.size() && indexCurrent < keys.size()) {
            final FractionalCascadingKey parentKey = parentKeys.get(indexParent);
            for (; indexCurrent < keys.size(); indexCurrent++) {
                final FractionalCascadingKey childKey = keys.get(indexCurrent);

                if (parentKey.getPoint().getY() <= childKey.getPoint().getY()) {
                    if (isLhs) {
                        parentKey.setLeft(childKey);
                        indexParent += 1;
                    } else {
                        parentKey.setRight(childKey);
                        indexParent += 1;
                    }
                    break;
                }
            }
        }
    }

    private List<FractionalCascadingKey> getKeys() {
        return keys;
    }

    private void setLeftChild(final FractionalCascading leftChild) {
        this.leftChild = leftChild;
    }

    private void setRightChild(final FractionalCascading rightChild) {
        this.rightChild = rightChild;
    }

}
