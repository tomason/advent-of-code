package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Day10 {
    private static final Map<Point, List<Point>> ZENITH_CACHE = new ConcurrentHashMap<>();

    private static int[][] map;
    private static int maxRows;
    private static int maxCols;

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-10-1");

        maxRows = lines.size();
        maxCols = lines.stream().mapToInt(String::length).max().orElseThrow();

        Set<Trailhead> trailheads = new HashSet<>();

        map = new int[maxRows][];
        for (int row = 0; row < maxRows; row++) {
            char[] line = lines.get(row).toCharArray();
            map[row] = new int[maxCols];
            for (int col = 0; col < maxCols; col++) {
                int height = line[col] - '0';
                map[row][col] = height;
                if (height == 0) {
                    trailheads.add(new Trailhead(new Point(row, col)));
                }
            }
        }

        long starOne = 0L;

        for (Trailhead start : trailheads) {
            starOne += planPossibleEnds(start.start).stream().distinct().count();
        }

        // 754
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        for (Trailhead start : trailheads) {
            starTwo += planPossibleEnds(start.start).size();
        }

        // 1609
        System.out.println("Star two: " + starTwo);

    }

    private static List<Point> planPossibleEnds(Point startingPoint) {
        int startingHeight = map[startingPoint.row][startingPoint.col];

        if (startingHeight == 9) {
            return Collections.singletonList(startingPoint);
        }

        if (!ZENITH_CACHE.containsKey(startingPoint)) {
            List<Point> result = new ArrayList<>();

            // check up
            if (startingPoint.row - 1 >= 0 && map[startingPoint.row - 1][startingPoint.col] == startingHeight + 1) {
                result.addAll(planPossibleEnds(new Point(startingPoint.row - 1, startingPoint.col)));
            }
            // check right
            if (startingPoint.col + 1 < maxCols && map[startingPoint.row][startingPoint.col + 1] == startingHeight + 1) {
                result.addAll(planPossibleEnds(new Point(startingPoint.row, startingPoint.col + 1)));
            }
            // check down
            if (startingPoint.row + 1 < maxRows && map[startingPoint.row + 1][startingPoint.col] == startingHeight + 1) {
                result.addAll(planPossibleEnds(new Point(startingPoint.row + 1, startingPoint.col)));
            }
            // check right
            if (startingPoint.col - 1 >= 0 && map[startingPoint.row][startingPoint.col - 1] == startingHeight + 1) {
                result.addAll(planPossibleEnds(new Point(startingPoint.row, startingPoint.col - 1)));
            }
            ZENITH_CACHE.put(startingPoint, result);
        }

        return ZENITH_CACHE.get(startingPoint);
    }

    private static class Trailhead {
        public final Point start;
        public Set<Point> ends = new HashSet<>();

        public Trailhead(Point start) {
            this.start = start;
        }
    }

    private record Point (int row, int col) {
    }
}
