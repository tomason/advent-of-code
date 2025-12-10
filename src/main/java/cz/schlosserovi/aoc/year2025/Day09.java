package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Day09 {
    static void main() {
        var lines = Utils.readLines("/2025/day-09-1");

        var points = new ArrayList<Point>();

        var one = 0L;

        for (var line : lines) {
            var point = new Point(line);

            for (Point b : points) {
                var area = area(point, b);
                if (area > one) {
                    one = area;
                }
            }

            points.add(point);
        }

        // 4772103936
        System.out.println("Part one: " + one);

        var two = 0L;

        var outerPerimeter = outerPerimeter(points);

        var horizontals = new HashSet<Line>();
        var verticals = new HashSet<Line>();
        for (var i = 0; i < outerPerimeter.size(); i++) {
            var a = outerPerimeter.get(i);
            var b = outerPerimeter.get((i + 1) % outerPerimeter.size());
                if (a.x == b.x) {
                    verticals.add(Line.of(a, b));
                } else if (a.y == b.y) {
                    horizontals.add(Line.of(a, b));
                } else {
                    System.out.printf("%s --- %s%n", a, b);
                    throw new RuntimeException("Non-continuous edge!");
                }
        }

        for (var i = 0; i < points.size(); i++) {
            var a = points.get(i);
            nextRectangle: for (var j = i + 1; j < points.size(); j++) {
                var c = points.get(j);

                var minX = min(a.x, c.x);
                var maxX = max(a.x, c.x) + 1;
                var minY = min(a.y, c.y) - 1;
                var maxY = max(a.y, c.y);

                for (var vertical : verticals) {
                    if (minX < vertical.a.x && vertical.a.x < maxX) {
                        if ((vertical.a.y < maxY && vertical.b.y >= maxY) ||
                            (vertical.a.y <= minY && vertical.b.y > minY)) {
                            continue nextRectangle;
                        }
                    }
                }
                for (var horizontal : horizontals) {
                    if (minY < horizontal.a.y && horizontal.a.y < maxY) {
                        if ((horizontal.a.x < maxX && horizontal.b.x >= maxX) ||
                            (horizontal.a.x <= minX && horizontal.b.x > minX)
                        ) {
                            continue nextRectangle;
                        }
                    }
                }

                // valid non-crossing rectangle
                two = max(two, area(a, c));
            }
        }

        // 1529675217
        System.out.println("Part two: " + two);
    }

    private static long area(Point a, Point b) {
        return (abs(a.x() - b.x()) + 1L) * (abs(a.y() - b.y()) + 1L);
    }

    private static double area(List<Point> points) {
        var result = 0L;
        for (var i = 0; i < points.size(); i++) {
            var a = points.get(i);
            var b = points.get((i + 1) % points.size());

            result += (long) a.x * b.y - (long) a.y * b.x;
        }

        return abs(result / 2d);
    }

    private static List<Point> outerPerimeter(List<Point> points) {
        var leftEdge = 0L;
        var rightEdge = 0L;
        var leftPoints = new ArrayList<Point>();
        var rightPoints = new ArrayList<Point>();

        var lastPoint = points.getLast();
        for (var i = 0; i < points.size(); i++) {
            var start = points.get(i);
            var end = points.get((i + 1) % points.size());

            if (lastPoint.x == start.x) {
                if (lastPoint.y < start.y) {
                    // up then
                    if (start.x < end.x) {
                        // right
                        leftPoints.add(start);
                        rightPoints.add(new Point(start.x + 1, start.y - 1));
                    } else {
                        // left
                        leftPoints.add(new Point(start.x, start.y - 1));
                        rightPoints.add(new Point(start.x + 1, start.y));
                    }
                } else {
                    // down then
                    if (start.x < end.x) {
                        // right
                        leftPoints.add(new Point(start.x + 1, start.y));
                        rightPoints.add(new Point(start.x, start.y - 1));
                    } else {
                        // left
                        leftPoints.add(new Point(start.x + 1, start.y - 1));
                        rightPoints.add(start);
                    }
                }
            }
            if (lastPoint.y == start.y) {
                if (lastPoint.x < start.x) {
                    // right then
                    if (start.y > end.y) {
                        // down
                        leftPoints.add(new Point(start.x + 1, start.y));
                        rightPoints.add(new Point(start.x, start.y - 1));
                    } else {
                        // up
                        leftPoints.add(start);
                        rightPoints.add(new Point(start.x + 1, start.y - 1));
                    }
                } else {
                    // left then
                    if (start.y > end.y) {
                        // down
                        leftPoints.add(new Point(start.x + 1, start.y - 1));
                        rightPoints.add(start);
                    } else {
                        // up
                        leftPoints.add(new Point(start.x, start.y - 1));
                        rightPoints.add(new Point(start.x + 1, start.y));
                    }
                }
            }
            lastPoint = start;

            if (i > 0) {
                var a = leftPoints.get(i);
                var b = leftPoints.get(i - 1);
                leftEdge += abs(a.x - b.x) + abs(a.y - b.y);

                var c = rightPoints.get(i);
                var d = rightPoints.get(i - 1);
                rightEdge += abs(c.x - d.x) + abs(c.y - d.y);
            }
        }

        if (leftEdge > rightEdge) {
            return leftPoints;
        } else {
            return rightPoints;
        }
    }

    private record Point(int x, int y) {
        public Point(String inputLine) {
            var split = inputLine.split(",");
            this(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point(int x1, int y1))) return false;
            return x == x1 && y == y1;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private record Line(Point a, Point b) {
        public static Line of(Point a, Point b) {
            // always order points so a.x < b.x
            if (a.y() == b.y() && a.x() > b.x()) {
                return new Line(b, a);
            }
            // always order points so a.y < b.y
            if (a.x() == b.x() && a.y() > b.y()) {
                return new Line(b, a);
            }

            return new Line(a, b);
        }
    }
}