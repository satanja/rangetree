package com.satanja.grid;

import com.satanja.rangetree.Point;
import com.satanja.rangetree.Window;
import com.satanja.util.WindowUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GridRange {

    final Cell[][] grid;

    final int columns;
    final int rows;

    final double cellWidth;
    final double cellHeight;

    final Window window;

    public GridRange(final List<Point> points, final int subdivisions) {
        this.window = boundingBox(points);

        final double width = window.getXMax() - window.getXMin();
        this.columns = subdivisions + 1;
        this.cellWidth = width / subdivisions;

        final double height = window.getYMax() - window.getYMin();
        this.rows = subdivisions + 1;
        this.cellHeight = height / subdivisions;

        this.grid = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = new Cell();

                final double minxX = cellWidth * i + window.getXMin();
                final double maxX = cellWidth * (i + 1) + window.getXMin();

                final double minY = cellHeight * j + window.getYMin();
                final double maxY = cellHeight * (j + 1) + window.getYMin();

                final Window range = new Window(minxX, maxX, minY, maxY);
                grid[i][j].setRange(range);
                grid[i][j].setIndexes(i, j);
            }
        }

        for (final Point point : points) {
            final Cell cell = getCell(point);
            cell.addPoint(point);
        }
    }

    public List<Point> search(final Window window) {
        final Point center = new Point((window.getXMin() + window.getXMax()) / 2, (window.getYMin() + window.getYMax()) / 2);
        final Cell centerCell = getCell(center);

        final Deque<Cell> queue = new ArrayDeque<>();
        queue.add(centerCell);

        final List<Point> result = new ArrayList<>();
        final boolean[][] seen = new boolean[rows][columns];
        seen[centerCell.i][centerCell.j] = true;

        while (!queue.isEmpty()) {
            final Cell cell = queue.pop();
            if (cell.isCoveredByWindow(window)) {
                result.addAll(cell.getPoints());
            } else {
                // linear search but can also be sorted by x...
                for (final Point point : cell.getPoints()) {
                    if (WindowUtils.windowContainsPoint(window, point)) {
                        result.add(point);
                    }
                }
            }

            final int i = cell.getI();
            final int j = cell.getJ();

            if (i > 0) {
                final Cell next = grid[i - 1][j];
                if (next.intersects(window) && !seen[next.i][next.j]) {
                    queue.add(next);
                }
                seen[next.i][next.j] = true;
            }

            if (i + 1 < rows) {
                final Cell next = grid[i + 1][j];
                if (next.intersects(window) && !seen[next.i][next.j]) {
                    queue.add(next);
                }
                seen[next.i][next.j] = true;
            }

            if (j > 0) {
                final Cell next = grid[i][j - 1];
                if (next.intersects(window) && !seen[next.i][next.j]) {
                    queue.add(next);
                }
                seen[next.i][next.j] = true;
            }

            if (j + 1 < columns) {
                final Cell next = grid[i][j + 1];
                if (next.intersects(window) && !seen[next.i][next.j]) {
                    queue.add(next);
                }
                seen[next.i][next.j] = true;
            }
        }

        return result;
    }

    private static Window boundingBox(final List<Point> points) {
        double minX = points.get(0).getX();
        double maxX = minX;
        double minY = points.get(0).getY();
        double maxY = minY;

        for (final Point point : points) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
        }

        return new Window(minX, maxX, minY, maxY);
    }

    private Cell getCell(final Point point) {
        final double correctedX = point.getX() - this.window.getXMin();
        final double correctedY = point.getY() - this.window.getYMin();

        final int i = (int) (correctedX / this.cellWidth);
        final int j = (int) (correctedY / this.cellHeight);

        if (i == rows || j == columns) {
            System.out.println("shit");
        }
        return grid[i][j];
    }

    private static class Cell {
        private final List<Point> points = new ArrayList<>();
        private Point bottomLeft;
        private Point topRight;
        private int i;
        private int j;

        void addPoint(final Point point) {
            points.add(point);
        }

        void setRange(final Window range) {
            bottomLeft = new Point(range.getXMin(), range.getYMin());
            topRight = new Point(range.getXMax(), range.getYMax());
        }

        void setIndexes(final int i, final int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        List<Point> getPoints() {
            return points;
        }

        boolean intersects(final Window window) {
            final Point queryBottomLeft = new Point(window.getXMin(), window.getYMin());
            final Point queryTopRight = new Point(window.getXMax(), window.getYMax());

            if (queryBottomLeft.getX() > topRight.getX() || queryTopRight.getX() < bottomLeft.getX()) {
                return false;
            }

            return !(queryBottomLeft.getY() > topRight.getY()) && !(queryTopRight.getY() < bottomLeft.getY());
        }

        boolean isCoveredByWindow(final Window window) {
            final Point queryBottomLeft = new Point(window.getXMin(), window.getYMin());
            final Point queryTopRight = new Point(window.getXMax(), window.getYMax());

            return queryBottomLeft.getX() <= bottomLeft.getX() && queryBottomLeft.getY() <= bottomLeft.getY() &&
                    queryTopRight.getX() >= topRight.getX() && queryTopRight.getY() >= topRight.getY();
        }
    }

    private static class GridCellIndex {
        int i;
        int j;

        public GridCellIndex(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

}
