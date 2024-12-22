package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Day16 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-16-1");

        char[][] map = new char[lines.size()][];

        Point start = null;
        Point end = null;

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            map[row] = line.toCharArray();

            if (line.contains("S")) {
                start = new Point(row, line.indexOf('S'));
            }
            if (line.contains("E")) {
                end = new Point(row, line.indexOf('E'));
            }
        }

        final Point endPoint = end;

        long starOne = 0L;

        Map<Reindeer, Integer> bestScores = new HashMap<>();
        List<Reindeer> toCheck = new ArrayList<>();

        toCheck.add(new Reindeer(start, Direction.RIGHT));

        while (!toCheck.isEmpty()) {
            Reindeer checking = toCheck.remove(0);
            int startingScore = bestScores.getOrDefault(checking, 0);


            for (Direction nextDirection : Direction.values()) {
                int nextScore = startingScore + 1 + 1000 * nextDirection.diff(checking.direction);
                int nextRow = checking.position.row + nextDirection.dRow;
                int nextCol = checking.position.col + nextDirection.dCol;

                if (map[nextRow][nextCol] != '#') {
                    Reindeer nextReindeer = new Reindeer(new Point(nextRow, nextCol), nextDirection);
                    if (bestScores.getOrDefault(nextReindeer, Integer.MAX_VALUE) > nextScore) {
                        bestScores.put(nextReindeer, nextScore);
                        toCheck.add(nextReindeer);
                    }
                }
            }
        }

        starOne = bestScores.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getKey().position, endPoint))
                .mapToLong(Map.Entry::getValue)
                .min()
                .orElseThrow();

        // 72400
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        Map<List<Reindeer>, Integer> allPathsScore = new HashMap<>();
        Map<List<Reindeer>, Integer> finishedPathsScore = new HashMap<>();

        List<List<Reindeer>> incompletePaths = new ArrayList<>();

        incompletePaths.add(List.of(new Reindeer(start, Direction.RIGHT)));

        while (!incompletePaths.isEmpty()) {
            List<Reindeer> incompletePath = incompletePaths.remove(0);
            int startingScore = allPathsScore.getOrDefault(incompletePath, 0);

            Reindeer checking = incompletePath.get(incompletePath.size() - 1);

            if (map[checking.position.row][checking.position.col] == 'E') {
                finishedPathsScore.put(incompletePath, startingScore);
            }

            for (Direction nextDirection : Direction.values()) {
                int nextScore = startingScore + 1 + 1000 * nextDirection.diff(checking.direction);
                int nextRow = checking.position.row + nextDirection.dRow;
                int nextCol = checking.position.col + nextDirection.dCol;

                if (map[nextRow][nextCol] != '#') {
                    Reindeer nextReindeer = new Reindeer(new Point(nextRow, nextCol), nextDirection);
                    // already have the best scores per node, so let's reuse it
                    if (nextScore == bestScores.get(nextReindeer)) {
                        List<Reindeer> newPath = new ArrayList<>(incompletePath);
                        newPath.add(nextReindeer);

                        incompletePaths.add(newPath);
                        allPathsScore.put(newPath, nextScore);
                    }
                }
            }
        }

        final long lowestScore = starOne;
        // already have it from previous task, but here's a calculation
        //finishedPathsScore.values().stream()
        //    .mapToInt(Integer::intValue)
        //    .min().orElseThrow();

        starTwo = finishedPathsScore.entrySet().stream()
                .filter(entry -> entry.getValue() == lowestScore)
                .map(Map.Entry::getKey)
                .flatMap(path -> path.stream().map(Reindeer::position))
                .distinct()
                .count();

        // 435
        System.out.println("Star two: " + starTwo);

    }

    private record Reindeer(Point position, Direction direction) {
    }

    private record Point(int row, int col) {
    }

    private enum Direction {
        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1);

        public final int dRow;
        public final int dCol;

        Direction(int dRow, int dCol) {
            this.dRow = dRow;
            this.dCol = dCol;
        }

        public int diff(Direction other) {
            if (this == other) {
                return 0;
            } else if (Math.abs(ordinal() - other.ordinal()) == 2) {
                return 2;
            } else {
                return 1;
            }
        }
    }
}
