package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Day20 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-20-1");

        int width = 0;
        int height = lines.size();
        Point start = null;
        Point end = null;

        char[][] memoryMap = new char[height][];
        for (int row = 0; row < height; row++) {
            String line = lines.get(row);

            memoryMap[row] = line.toCharArray();
            width = Math.max(width, line.length());

            if (line.contains("S")) {
                start = new Point(row, line.indexOf('S'));
            }
            if (line.contains("E")) {
                end = new Point(row, line.indexOf('E'));
            }
        }

        if (height == 0 || width == 0 || start == null || end == null) {
            throw new IllegalArgumentException("Bad map");
        }

        Map<Point, Integer> fastestMap = findFastestRoutes(memoryMap, start);
        int fastest = fastestMap.get(end);
        List<Point> pathPoints = fastestMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .filter(entry -> memoryMap[entry.getKey().row()][entry.getKey().col()] != '#')
                .map(Map.Entry::getKey)
                .toList();

        System.out.println("Fastest path without cheating: " + fastest);

        long starOne = 0L;

        Set<Cheat> allCheats = new HashSet<>();
        for (Point point : pathPoints) {
            allCheats.addAll(generateCheats(memoryMap, fastestMap, point, 2));
        }

        System.out.println("All possible cheats: " + allCheats.size());

        Map<Cheat, Integer> fastestRoutesWithCheats = new HashMap<>();
        for (Cheat cheat : allCheats) {
            int fastestRoute = timeSavedWithCheat(fastestMap, cheat);
            if (fastestRoute < fastest) {
                fastestRoutesWithCheats.put(cheat, fastestRoute);
            }
        }

        starOne = fastestRoutesWithCheats.entrySet().stream()
                .filter(entry -> entry.getValue() >= 100)
                .count();

        Map<Integer, Set<Cheat>> bestCheats = new HashMap<>();

        fastestRoutesWithCheats.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .forEach(entry -> bestCheats.computeIfAbsent(entry.getValue(), ignored -> new HashSet<>()).add(entry.getKey()));

        bestCheats.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(entry -> System.out.printf("Shortening by %2d, there are %2d cheats%n", entry.getKey(), entry.getValue().size()));

        // 1511
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        Map<SimpleCheat, Integer> fastestRoutesWithSimpleCheats = new HashMap<>();
        Set<SimpleCheat> simpleCheats = new HashSet<>();

        for (Point cheatStart : pathPoints) {
            pathPoints.stream()
                    // no point taking shortcut to a point we already have been to
                    .filter(point -> fastestMap.get(point) > fastestMap.get(cheatStart))
                    .flatMap(cheatEnd -> generateCheat(cheatStart, cheatEnd, 20).stream())
                    .forEach(cheat -> {
                        simpleCheats.add(cheat);
                        fastestRoutesWithSimpleCheats.put(cheat, timeSavedWithCheat(fastestMap, cheat));
                    });
        }
        System.out.println("All possible cheats: " + simpleCheats.size());

        starTwo = fastestRoutesWithSimpleCheats.entrySet().stream()
                .filter(entry -> entry.getValue() >= 100)
                .count();

        Map<Integer, Set<SimpleCheat>> bestSimpleCheats = new HashMap<>();
        fastestRoutesWithSimpleCheats.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .forEach(entry -> bestSimpleCheats.computeIfAbsent(entry.getValue(), ignored -> new HashSet<>()).add(entry.getKey()));

        bestSimpleCheats.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(entry -> System.out.printf("Shortening by %2d, there are %2d cheats%n", entry.getKey(), entry.getValue().size()));


        // 1020507
        System.out.println("Star two: " + starTwo);

    }

    private static Map<Point, Integer> findFastestRoutes(char[][] map, Point start) {
        Map<Point, Integer> fastestPaths = new HashMap<>();
        List<Point> pointsToCheck = new ArrayList<>();

        pointsToCheck.add(start);
        fastestPaths.put(start, 0);

        while (!pointsToCheck.isEmpty()) {
            Point position = pointsToCheck.removeFirst();
            int score = fastestPaths.get(position);

            for (Point next : position.neighboringPoints()) {
                if (map[next.row][next.col] != '#' && fastestPaths.getOrDefault(next, Integer.MAX_VALUE) > score + 1) {
                    pointsToCheck.add(next);
                    fastestPaths.put(next, score + 1);
                }
            }
        }

        return fastestPaths;
    }

    private static int timeSavedWithCheat(Map<Point, Integer> previousScores, Cheat cheatToUse) {
        return previousScores.get(cheatToUse.end) - (previousScores.get(cheatToUse.start) + cheatToUse.cheatPoints.size() - 1);
    }

    private static int timeSavedWithCheat(Map<Point, Integer> previousScores, SimpleCheat cheatToUse) {
        return previousScores.get(cheatToUse.end) - (previousScores.get(cheatToUse.start) + cheatToUse.length);
    }

    private static Optional<SimpleCheat> generateCheat(Point start, Point end, int maxLength) {
        // shortest path is always the Manhattan distance, only question is whether it is shorter than max length
        int gridDistance = Math.abs(start.row - end.row) + Math.abs(start.col - end.col);
        if (gridDistance <= maxLength) {
            return Optional.of(new SimpleCheat(start, end, gridDistance));
        } else {
            return Optional.empty();
        }
    }

    private static Set<Cheat> generateCheats(char[][] map, Map<Point, Integer> previousFastest, Point start, int length) {
        Map<Cheat, Integer> result = new HashMap<>();

        List<List<Point>> pathsToProbe = new ArrayList<>();
        // find out the neighboring walls
        for (Point neighbor : start.neighboringPoints()) {
            if (map[neighbor.row][neighbor.col] == '#') {
                pathsToProbe.add(List.of(start, neighbor));
            }
        }

        while (!pathsToProbe.isEmpty()) {
            List<Point> currentPath = pathsToProbe.removeFirst();

            Point lastPoint = currentPath.getLast();

            if (map[lastPoint.row][lastPoint.col] != '#') {
                // ended up on the path
                Cheat cheat = new Cheat(currentPath);
                if (
                    // found a better path to the target start -> end
                        result.getOrDefault(cheat, Integer.MAX_VALUE) > cheat.cheatPoints.size() &&
                                // better path with cheat than previously computed
                                previousFastest.get(cheat.end) > (previousFastest.get(cheat.start) + cheat.cheatPoints.size())
                ) {
                    // found a better path to the target
                    result.put(cheat, cheat.cheatPoints.size());
                }
            }

            if (currentPath.size() <= length && map[lastPoint.row][lastPoint.col] != 'E') {
                // generate some more
                for (Point nextPoint : lastPoint.neighboringPoints()) {
                    if (
                        // no turning back
                        //!currentPath.contains(nextPoint) &&
                        // no going off the map
                            (nextPoint.row >= 0 && nextPoint.row < map.length && nextPoint.col >= 0 && nextPoint.col < map[nextPoint.row].length)
                    ) {
                        List<Point> newPath = new ArrayList<>(currentPath);
                        newPath.add(nextPoint);

                        pathsToProbe.add(newPath);
                    }
                }
            }
        }

        return result.keySet();
    }

    private record Point(int row, int col) {
        public List<Point> neighboringPoints() {
            return List.of(
                    new Point(row - 1, col),
                    new Point(row + 1, col),
                    new Point(row, col - 1),
                    new Point(row, col + 1)
            );
        }
    }

    private record SimpleCheat(Point start, Point end, int length) {

    }

    private record Cheat(Point start, Point end, List<Point> cheatPoints) {
        public Cheat(List<Point> cheatPoints) {
            this(cheatPoints.getFirst(), cheatPoints.getLast(), cheatPoints);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cheat cheat = (Cheat) o;
            return Objects.equals(start, cheat.start) && Objects.equals(end, cheat.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }
}
