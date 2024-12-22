package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day18 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-18-1");

        int width = 71;
        int height = 71;

        char[][] memoryMap = new char[height][];
        for (int i = 0; i < height; i++) {
            memoryMap[i] = new char[width];
            Arrays.fill(memoryMap[i], '.');
        }

        long starOne = 0L;

        for (int i = 0; i < 1024; i++) {
            String[] split = lines.get(i).split(",");
            memoryMap[Integer.parseInt(split[0])][Integer.parseInt(split[1])] = '#';
        }

        starOne = getShortestPath(memoryMap, new Point(0, 0), new Point(height - 1, width - 1));

        // 454
        System.out.println("Star one: " + starOne);

        String starTwo = "";

        for (int i = 1025; i < lines.size(); i++) {
            String[] split = lines.get(i).split(",");
            memoryMap[Integer.parseInt(split[0])][Integer.parseInt(split[1])] = '#';

            if (getShortestPath(memoryMap, new Point(0, 0), new Point(height - 1, width - 1)) <= 0) {
                starTwo = lines.get(i);
                break;
            }
        }

        // 8,51
        System.out.println("Star two: " + starTwo);

    }

    private static int getShortestPath(char[][] memoryMap, Point start, Point end) {
        int height = memoryMap.length;
        int width = memoryMap[0].length;

        List<Point> toTry = new ArrayList<>();
        Map<Point, Integer> shortestPaths = new HashMap<>();

        toTry.add(start);

        while (!toTry.isEmpty()) {
            Point currentPoint = toTry.remove(0);
            int nextScore = shortestPaths.getOrDefault(currentPoint, 0) + 1;

            for (Point nextPoint : new Point[]{
                    new Point(currentPoint.row - 1, currentPoint.col),
                    new Point(currentPoint.row + 1, currentPoint.col),
                    new Point(currentPoint.row, currentPoint.col - 1),
                    new Point(currentPoint.row, currentPoint.col + 1)
            }) {
                if (nextPoint.row >= 0 && nextPoint.row < height &&
                        nextPoint.col >= 0 && nextPoint.col < width &&
                        memoryMap[nextPoint.row][nextPoint.col] != '#' &&
                        nextScore < shortestPaths.getOrDefault(nextPoint, Integer.MAX_VALUE)
                ) {
                    toTry.add(nextPoint);
                    shortestPaths.put(nextPoint, nextScore);
                }
            }
        }

        return shortestPaths.getOrDefault(end, -1);
    }

    private record Point(int row, int col){}
}
