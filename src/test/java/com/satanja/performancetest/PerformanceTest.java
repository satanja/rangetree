package com.satanja.performancetest;

import com.satanja.grid.GridRange;
import com.satanja.rangetree.Point;
import com.satanja.rangetree.RangeTree;
import com.satanja.rangetree.Window;
import com.satanja.naive.PointsInRange;
import com.satanja.naive.SortedPointsInRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import com.satanja.semirangetree.SemiRangeTree;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 20)
@State(Scope.Benchmark)
public class PerformanceTest {

    private RangeTree rangeTree;
    private PointsInRange pointsInRange;
    private SortedPointsInRange sortedPointsInRange;
    private SemiRangeTree semiRangeTree;
    private GridRange gridRange;

    private List<Point> points;
    private Window window;

    @Param({"10", "20", "100", "200", "500", "1000", "2000", "5000", "8000", "10000", "12000", "15000", "17000", "20000", "40000", "50000", "60000", "70000", "80000", "90000", "100000"})
    public int input_size;

    @Setup(Level.Trial)
    public void setup() {
        points = generate(input_size);
    }

    List<Point> generate(final int n) {
        Random r = new Random(0);
        final List<Point> points = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            final double x = r.nextDouble() * 40 - 20;
            final double y = r.nextDouble() * 40 - 20;
            points.add(new Point(x, y));
        }
        return points;
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkRangeTree(final Blackhole blackhole) {
        rangeTree = new RangeTree(points);
        for (final Point p : points) {
            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
            List<Point> points  = rangeTree.search(window);
            blackhole.consume(points);
        }
    }

//    @Benchmark
//    @BenchmarkMode(Mode.SingleShotTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void benchmarkNaive(final Blackhole blackhole) {
//        pointsInRange = new PointsInRange(points);
//        for (final Point p : points) {
//            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
//            List<Point> points = pointsInRange.search(window);
//            blackhole.consume(points);
//        }
//    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkSortedPoints(final Blackhole blackhole) {
        sortedPointsInRange = new SortedPointsInRange(points);
        for (final Point p : points) {
            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
            List<Point> points = sortedPointsInRange.search(window);
            blackhole.consume(points);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkSemiRangeTree(final Blackhole blackhole) {
        semiRangeTree = new SemiRangeTree(points);
        for (final Point p : points) {
            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
            List<Point> points = semiRangeTree.search(window);
            blackhole.consume(points);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkGridRange(final Blackhole blackhole) {
        gridRange = new GridRange(points, 20);
        for (final Point p : points) {
            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
            List<Point> points = gridRange.search(window);
            blackhole.consume(points);
        }
    }

    @Test
    public void test() {
        points = generate(4000);
        rangeTree = new RangeTree(points);
        pointsInRange = new PointsInRange(points);

        for (final Point p : points) {
            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
            final List<Point> expected = pointsInRange.search(window);
            final List<Point> points = rangeTree.search(window);

            Assertions.assertEquals(expected.size(), points.size());
            final Set<Point> expectedSet = new HashSet<>(points);

            for (final Point point : points) {
                Assertions.assertTrue(expectedSet.contains(point));
            }
        }
    }

    @Test
    public void testGridRange() {
        points = generate(4000);
        gridRange = new GridRange(points, 20);
        pointsInRange = new PointsInRange(points);

        for (final Point p : points) {
            final Window window = new Window(p.getX() - 1, p.getX() + 1, p.getY() - 1, p.getY() + 1);
            final List<Point> expected = pointsInRange.search(window);
            final List<Point> points = gridRange.search(window);

            Assertions.assertEquals(expected.size(), points.size());
            final Set<Point> expectedSet = new HashSet<>(points);

            for (final Point point : points) {
                Assertions.assertTrue(expectedSet.contains(point));
            }
        }
    }

    @Test
    public void benchmark() throws Exception {
        String[] argv = {};
        org.openjdk.jmh.Main.main(argv);
    }
}
