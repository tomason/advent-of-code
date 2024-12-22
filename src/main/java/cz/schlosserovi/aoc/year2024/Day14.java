package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {
    private static final Pattern LINE_MATCH = Pattern.compile("p=([-\\d]+),([-\\d]+) v=([-\\d]+),([-\\d]+)");
    private static final int width = 101; //11; // 101
    private static final int height = 103;//7; // 103

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-14-1");

        List<Robot> robots = lines.stream().map(LINE_MATCH::matcher)
                .filter(Matcher::matches)
                .map(matcher -> new Robot(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4)))
                .toList();

        long starOne = 0L;

        List<Point> finalPoints = new ArrayList<>();

        for (Robot robot : robots) {
            Point currentPoint = robot.start;

            for (int i = 0; i < 100; i++) {
                    if (robot.alreadyVisited.contains(currentPoint)) {
                        currentPoint = robot.alreadyVisited.get((robot.alreadyVisited.indexOf(currentPoint) + 1) % robot.alreadyVisited.size());
                    } else {
                        robot.alreadyVisited.add(currentPoint);
                        int row = (currentPoint.x + robot.dX + width) % width;
                        int col = (currentPoint.y + robot.dY + height) % height;
                        currentPoint = new Point(row, col);
                    }
            }

            finalPoints.add(currentPoint);
        }

        // count quadrants
        long quadrant1 = finalPoints.stream().filter(point -> point.x < width / 2 && point.y < height / 2).count();
        long quadrant2 = finalPoints.stream().filter(point -> point.x < width / 2 && point.y > height / 2).count();
        long quadrant3 = finalPoints.stream().filter(point -> point.x > width / 2 && point.y < height / 2).count();
        long quadrant4 = finalPoints.stream().filter(point -> point.x > width / 2 && point.y > height / 2).count();

        starOne = quadrant1 * quadrant2 * quadrant3 * quadrant4;

        System.out.printf("Q1: %s * Q2: %s * Q3:%s * Q4: %s = %s%n", quadrant1, quadrant2, quadrant3, quadrant4, starOne);

        // 231221760
        System.out.println("Star one: " + starOne);

        // ensure all possible locations are discovered
        for (Robot robot : robots) {
            robot.alreadyVisited.clear();
            Point currentPoint = robot.start;

            for (int i = 0; i < (width * height); i++) {
                if (robot.alreadyVisited.contains(currentPoint)) {
                    currentPoint = robot.alreadyVisited.get((robot.alreadyVisited.indexOf(currentPoint) + 1) % robot.alreadyVisited.size());
                } else {
                    robot.alreadyVisited.add(currentPoint);
                    int row = (currentPoint.x + robot.dX + width) % width;
                    int col = (currentPoint.y + robot.dY + height) % height;
                    currentPoint = new Point(row, col);
                }
            }

            finalPoints.add(currentPoint);
        }


        long starTwo = 0L;

        char[][] picture = new char[height][];
        for (int row = 0; row < height; row++) {
            picture[row] = new char[width];
        }

        for (int i = 4347; i > 0 && i < Integer.MAX_VALUE; i+=101) {
            for (int row = 0; row < height; row ++) {
                Arrays.fill(picture[row], '.');
            }

            for (Robot robot : robots) {
                Point place = robot.alreadyVisited.get(i % robot.alreadyVisited.size());

                picture[place.y][place.x] = 'X';
            }

            System.out.println("Iteration " + i);
            for (int row = 0; row < height; row++) {
                System.out.println(new String(picture[row]));
            }

            // put a breakpoint here and check pictures in console :(
            // in the end found a sequence that repeats itself, so was able to iterate 101 pictures at a time
            // not a solution I'd recommend
            System.out.println();
        }

        // 6771
        System.out.println("Star two: " + starTwo);

    }

    private record Point(int x, int y) {
    }

    private record Robot(Point start, int dX, int dY, List<Point> alreadyVisited) {
        public Robot(String x, String y, String dX, String dY) {
            this(new Point(Integer.parseInt(x), Integer.parseInt(y)),
                    Integer.parseInt(dX),
                    Integer.parseInt(dY),
                    new ArrayList<>());
        }
    }
}
