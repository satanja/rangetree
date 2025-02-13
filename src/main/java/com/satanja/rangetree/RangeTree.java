package com.satanja.rangetree;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RangeTree {

    private final RangeTreeNode root;

    private final List<Point> xPoints;

    public RangeTree(final List<Point> points) {
        this.xPoints = new ArrayList<>(points);
        xPoints.sort(Comparator.comparingDouble(Point::getX));

        List<IndexedPoint> yPoints = sortPointsOnY(xPoints);
        this.root = constructRangeTree(0, points.size(), null, yPoints, false);
    }

    public List<Point> search(final Window window) {
        final SplitNodeResult splitNodeResult = findSplitNode(window);
        final RangeTreeNode splitNode = splitNodeResult.node;
        final FractionalCascadingKey splitKey = splitNodeResult.key;

        final double xMin = window.getXMin();
        final double xMax = window.getXMax();

        final List<Point> points = new ArrayList<>();

        // left branch
        RangeTreeNode leftBranch = splitNode.getLeftSubtree();
        FractionalCascadingKey leftKey = splitKey.getLeftChild();
        while (leftBranch != null) {
            if (leftBranch.getPosition() < xMin) {
                leftBranch = leftBranch.getRightSubtree();
                leftKey = leftKey.getRightChild();
            } else if (leftBranch.getPosition() > xMin) {

                if (leftBranch.getRightSubtree() != null) {
                    points.addAll(leftBranch.getRightSubtree().report(leftKey.getRightChild(), window));
                } else if (leftBranch.isLeafNode()) {
                    points.addAll(leftBranch.report(leftKey, window));
                }
                leftBranch = leftBranch.getLeftSubtree();
                leftKey = leftKey.getLeftChild();
            } else {
                points.addAll(leftBranch.report(leftKey, window));
                break;
            }
        }

        RangeTreeNode rightBranch = splitNode.getRightSubtree();
        FractionalCascadingKey rightKey = splitKey.getRightChild();
        while (rightBranch != null) {
            if (rightBranch.getPosition() > xMax) {
                rightBranch = rightBranch.getLeftSubtree();
                rightKey = rightKey.getLeftChild();
            } else if (rightBranch.getPosition() < xMax) {

                if (rightBranch.getLeftSubtree() != null) {
                    points.addAll(rightBranch.getLeftSubtree().report(rightKey.getLeftChild(), window));
                } else if (rightBranch.isLeafNode()) {
                    points.addAll(rightBranch.report(rightKey, window));
                }
                rightBranch = rightBranch.getRightSubtree();
                rightKey = rightKey.getRightChild();
            } else {
                points.addAll(rightBranch.report(rightKey, window));
                break;
            }
        }

        return points;
    }

    private SplitNodeResult findSplitNode(final Window window) {
        RangeTreeNode currentNode = this.root;
        FractionalCascadingKey fractionalCascadingKey = currentNode.rootSearch(window.getXMin());

        while (currentNode.compareTo(window.getXMin(), window.getXMax()) != 0) {
            if (currentNode.compareTo(window.getXMin(), window.getXMax()) > 0) {
                currentNode = currentNode.getRightSubtree();
                fractionalCascadingKey = fractionalCascadingKey.getRightChild();
            } else {
                currentNode = currentNode.getLeftSubtree();
                fractionalCascadingKey = fractionalCascadingKey.getLeftChild();
            }
        }

        return new SplitNodeResult(currentNode, fractionalCascadingKey);
    }

    private List<IndexedPoint> sortPointsOnY(final List<Point> points) {
        final List<IndexedPoint> sortedPoints = new ArrayList<>(points.size());
        int i = 0;
        for (final Point point : points) {
            sortedPoints.add(new IndexedPoint(i, point));
            i += 1;
        }

        sortedPoints.sort(Comparator.comparingDouble(o -> o.getPoint().getY()));
        return sortedPoints;
    }

    private List<IndexedPoint> constructYSubset(final int startIndex, final int endIndex, final List<IndexedPoint> ySubset) {
        final List<IndexedPoint> result = new ArrayList<>();
        for (final IndexedPoint indexedPoint : ySubset) {
            if (startIndex <= indexedPoint.getIndex() && indexedPoint.getIndex() < endIndex) {
                result.add(indexedPoint);
            }
        }
        return result;
    }

    private List<Point> convertToPoints(final List<IndexedPoint> indexedPoints) {
        return indexedPoints.stream().map(IndexedPoint::getPoint).collect(Collectors.toList());
    }

    private RangeTreeNode constructRangeTree(final int startIndex,
                                             final int endIndex,
                                             final FractionalCascading parent,
                                             final List<IndexedPoint> parentYPoints,
                                             final boolean isLhs) {
        final List<IndexedPoint> currentYIndexedPoints = constructYSubset(startIndex, endIndex, parentYPoints);
        final List<Point> currentYPoints = convertToPoints(currentYIndexedPoints);
        final FractionalCascading current = parent == null ? new FractionalCascading(currentYPoints) : new FractionalCascading(parent, currentYPoints, isLhs);

        if (currentYPoints.size() == 1) {
            return new RangeTreeNode(currentYPoints.getFirst().getX(), current);
        } else {
            final int size = endIndex - startIndex;
            final int median = size % 2 == 0 ? size / 2 - 1 : size / 2;

            final int leftEndIndex = startIndex + median + 1;

            final RangeTreeNode rangeTreeNode = new RangeTreeNode(xPoints.get(median + startIndex).getX(), current);
            rangeTreeNode.setLeftSubtree(constructRangeTree(startIndex, leftEndIndex, current, currentYIndexedPoints, true));
            rangeTreeNode.setRightSubtree(constructRangeTree(leftEndIndex, endIndex, current, currentYIndexedPoints, false));

            return rangeTreeNode;
        }
    }

    @Data
    @AllArgsConstructor
    private static class SplitNodeResult {
        private final RangeTreeNode node;
        private final FractionalCascadingKey key;
    }

    @Data
    @AllArgsConstructor
    private static class IndexedPoint {
        private final int index;
        private final Point point;
    }

    public static void main(String[] args) {
        final Point a = new Point(0, 0);
        final Point b = new Point(1, 1);
        final Point c = new Point(2, 2);
        final Point d = new Point(3, 3);
        final Point e = new Point(4, 4);

        final List<Point> points = new ArrayList<>();
        points.add(a);
        points.add(b);
        points.add(c);
        points.add(d);
        points.add(e);

        final RangeTree rangeTree = new RangeTree(points);

        final Window window = new Window(0, 4, 0, 3);
        final List<Point> query = rangeTree.search(window);

        assert query.size() == 4;
    }
}
