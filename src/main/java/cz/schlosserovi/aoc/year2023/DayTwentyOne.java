package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayTwentyOne {
    private static final int ITERATIONS1 = 64;
    private static final int ITERATIONS2 = 26501365;

    public static void main(String... args) {
        List<String> lines = Utils.readLines("2023/day-21");

        int startRow = 0;
        int startCol = 0;

        GamePlan map = GamePlan.fromLines(lines);
        startLookup:
        for (int row = 0; row < map.rows; row++) {
            for (int col = 0; col < map.cols; col++) {
                if (map.charAtPoint(row, col) == 'S') {
                    startRow = row;
                    startCol = col;
                    break startLookup;
                }
            }
        }

        Set<Point> reachablePoints = new HashSet<>();
        reachablePoints.add(new Point(startRow, startCol));

        for (int i = 0; i < ITERATIONS1; i++) {
            reachablePoints = reachablePoints.stream()
                    // add all neighboring points
                    .flatMap(point -> Stream.of(
                            new Point(point.row - 1, point.col),
                            new Point(point.row + 1, point.col),
                            new Point(point.row, point.col - 1),
                            new Point(point.row, point.col + 1)))
                    // remove points out of boundary
                    .filter(point -> map.containsPoint(point.row, point.col))
                    // remove stones
                    .filter(point -> map.charAtPoint(point.row, point.col) != '#')
                    .collect(Collectors.toSet());
        }

        // mark the plots on the map and print it
        /*
        for (Point point : reachablePoints) {
            map.map[point.row][point.col] = 'O';
        }
        for (char[] line : map.map) {
            System.out.println(Arrays.toString(line));
        }
        */

        System.out.printf("Points reachable after exactly %d steps: %d%n", ITERATIONS1, reachablePoints.size());

        /*
        List<Set<Point>> allMaps = new ArrayList<>();
        List<Integer> hashTransitions = new ArrayList<>();

        allMaps.add(Collections.singleton(Point.of(startRow, startCol)));

        Map<Integer, Integer> hashTransitionMap = new HashMap<>();
        Map<Integer, List<Integer>> hashToAdditionalDimensions = new HashMap<>();
        Map<Integer, Integer> hashToUsableMoves = new HashMap<>();

        for (int i = 0; i < ITERATIONS2; i++) {
            List<Set<Point>> newMaps = new ArrayList<>();
            List<Integer> newTransitions = new ArrayList<>();

            // process state hashes
            for (int stateHash : hashTransitions) {
                // add next states
                newTransitions.add(hashTransitionMap.get(stateHash));
                // add additional transitions
                newTransitions.addAll(hashToAdditionalDimensions.getOrDefault(stateHash, Collections.emptyList()));
            }

            for (Set<Point> reachable : allMaps) {
                Set<Point> newReachable = new HashSet<>();
                int stateHash = reachable.hashCode();
                // save state if it doesn't exist
                hashToUsableMoves.putIfAbsent(stateHash, reachable.size());

                if (hashTransitionMap.containsKey(stateHash)) {
                    // retrieve the data from caches
                    newTransitions.add(hashTransitionMap.get(stateHash));
                    newTransitions.addAll(hashToAdditionalDimensions.getOrDefault(stateHash, Collections.emptyList()));
                } else {
                    // process the moves as it is the first time
                    Set<Point> additionalRight = new HashSet<>();
                    Set<Point> additionalLeft = new HashSet<>();
                    Set<Point> additionalTop = new HashSet<>();
                    Set<Point> additionalBottom = new HashSet<>();
                    for (Point start : reachable) {
                        // move right
                        if (start.col + 1 < map.cols) {
                            // still within bounds
                            if (map.charAtPoint(start.row, start.col + 1) != '#') {
                                newReachable.add(Point.of(start.row, start.col + 1));
                            }
                        } else {
                            if (map.charAtPoint(start.row, 0) != '#') {
                                additionalRight.add(Point.of(start.row, 0));
                            }
                        }
                        // move left
                        if (start.col - 1 >= 0) {
                            // still within bounds
                            if (map.charAtPoint(start.row, start.col - 1) != '#') {
                                newReachable.add(Point.of(start.row, start.col - 1));
                            }
                        } else {
                            if (map.charAtPoint(start.row, map.cols - 1) != '#') {
                                additionalLeft.add(Point.of(start.row, map.cols - 1));
                            }
                        }
                        // move up
                        if (start.row - 1 >= 0) {
                            // still within bounds
                            if (map.charAtPoint(start.row - 1, start.col) != '#') {
                                newReachable.add(Point.of(start.row - 1, start.col));
                            }
                        } else {
                            if (map.charAtPoint(map.rows - 1, start.col) != '#') {
                                additionalTop.add(Point.of(map.rows - 1, start.col));
                            }
                        }
                        // move down
                        if (start.row + 1 < map.rows) {
                            // still within bounds
                            if (map.charAtPoint(start.row + 1, start.col) != '#') {
                                newReachable.add(Point.of(start.row + 1, start.col));
                            }
                        } else {
                            if (map.charAtPoint(0, start.col) != '#') {
                                additionalBottom.add(Point.of(0, start.col));
                            }
                        }
                    }
                    List<Integer> additionalMaps = new ArrayList<>();
                    if (additionalBottom.size() > 0) {
                        newMaps.add(additionalBottom);
                        additionalMaps.add(additionalBottom.hashCode());
                    }
                    if (additionalRight.size() > 0) {
                        newMaps.add(additionalRight);
                        additionalMaps.add(additionalRight.hashCode());
                    }
                    if (additionalLeft.size() > 0) {
                        newMaps.add(additionalLeft);
                        additionalMaps.add(additionalLeft.hashCode());
                    }
                    if (additionalTop.size() > 0) {
                        newMaps.add(additionalTop);
                        additionalMaps.add(additionalTop.hashCode());
                    }
                    hashToAdditionalDimensions.put(stateHash, additionalMaps);
                }

                // cache the results
                hashToUsableMoves.putIfAbsent(newReachable.hashCode(), newReachable.size());
                hashTransitionMap.put(stateHash, newReachable.hashCode());
                newMaps.add(newReachable);
            }

            allMaps = newMaps;
            hashTransitions = newTransitions;

            System.out.printf("Iteration: %8d, reachable: %20d%n",
                    i + 1,
                    allMaps.parallelStream().mapToLong(Set::size).sum() +
                            hashTransitions.parallelStream().mapToLong(hashToUsableMoves::get).sum()
            );
        }

        */

        Map<Point, Set<Dimension>> pointsInAllDimensions = new HashMap<>();
        pointsInAllDimensions.put(new Point(startRow, startCol), Collections.singleton(Dimension.of(0, 0)));

        for (int i = 0; i < ITERATIONS2; i++) {
            Map<Point, Set<Dimension>> newPointsInAllDimensions = new HashMap<>();

            for (Map.Entry<Point, Set<Dimension>> pointInAllDimensions : pointsInAllDimensions.entrySet()) {
                for (Point newPoint : new Point[]{
                        new Point(pointInAllDimensions.getKey().row - 1, pointInAllDimensions.getKey().col),
                        new Point(pointInAllDimensions.getKey().row + 1, pointInAllDimensions.getKey().col),
                        new Point(pointInAllDimensions.getKey().row, pointInAllDimensions.getKey().col - 1),
                        new Point(pointInAllDimensions.getKey().row, pointInAllDimensions.getKey().col + 1)
                }) {
                    int deltaX;
                    int deltaY;
                    if (newPoint.row < 0) {
                        // overflow over top (roll over by adding number of rows and moving all the dimensions by one up)
                        newPoint = Point.of(newPoint.row + map.rows, newPoint.col);
                        deltaX = 1;
                        deltaY = 0;
                    } else if (newPoint.row >= map.rows) {
                        // overflow over the bottom (subtract number of rows and move all dimensions down by one)
                        newPoint = Point.of(newPoint.row - map.rows, newPoint.col);
                        deltaX = -1;
                        deltaY = 0;
                    } else if (newPoint.col < 0) {
                        // overflow to left
                        newPoint = Point.of(newPoint.row, newPoint.col + map.cols);
                        deltaX = 0;
                        deltaY = -1;
                    } else if (newPoint.col >= map.cols) {
                        // overflow to right
                        newPoint = Point.of(newPoint.row, newPoint.col - map.cols);
                        deltaX = 0;
                        deltaY = 1;
                    } else {
                        deltaX = 0;
                        deltaY = 0;
                    }

                    // store the result
                    if (map.charAtPoint(newPoint.row, newPoint.col) != '#') {
                        // count the dimensions here (to save computing time if there is a stone
                        Set<Dimension> dimensions = deltaX == 0 && deltaY == 0 ? pointInAllDimensions.getValue() :
                                pointInAllDimensions.getValue().stream()
                                        .map(dimension -> Dimension.of(dimension.x + deltaX, dimension.y + deltaY))
                                        .collect(Collectors.toSet());

                        newPointsInAllDimensions.merge(newPoint, dimensions, (set1, set2) ->
                                Stream.concat(set1.stream(), set2.stream()).collect(Collectors.toSet()));
                    }
                }
            }

            pointsInAllDimensions = newPointsInAllDimensions;

            Set<Dimension> dimensionsList = pointsInAllDimensions.values().parallelStream().flatMap(Set::stream).collect(Collectors.toSet());
            Map<Point, Set<Dimension>> finalPointsInAllDimensions = pointsInAllDimensions;
            Map<Dimension, Long> reachablePerDimension = dimensionsList.parallelStream()
                            .map(dimension -> new AbstractMap.SimpleEntry<>(dimension, finalPointsInAllDimensions.entrySet().parallelStream().filter(entry -> entry.getValue().contains(dimension)).count()))
                                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            System.out.printf("%8d,%20d,%d,|,%d,%d,%d,%d,|,%d,%d,%d,%d,|,%d,%d%n",
                    i + 1,
                    pointsInAllDimensions.values().stream().mapToLong(Set::size).sum(),
                    // base
                    reachablePerDimension.get(Dimension.of(0,0)),
                    // sides
                    reachablePerDimension.get(Dimension.of(1,0)),
                    reachablePerDimension.get(Dimension.of(-1,0)),
                    reachablePerDimension.get(Dimension.of(0,1)),
                    reachablePerDimension.get(Dimension.of(0,-1)),
                    // diagonals
                    reachablePerDimension.get(Dimension.of(1,1)),
                    reachablePerDimension.get(Dimension.of(1,-1)),
                    reachablePerDimension.get(Dimension.of(-1,1)),
                    reachablePerDimension.get(Dimension.of(-1,-1)),

                    // just to check
                    reachablePerDimension.get(Dimension.of(2,0)),
                    reachablePerDimension.get(Dimension.of(2,2))
            );
            // do not expect to run until the end, there is bound to come OOM
        }
    }

    //private static long

    private record Point(int row, int col) {
        private static final Collection<Point> CACHE = new HashSet<>();

        public static Point of(int row, int col) {
            return CACHE.parallelStream()
                    .filter(point -> point.row == row && point.col == col)
                    .findFirst().orElseGet(
                            () -> {
                                Point newOne = new Point(row, col);
                                CACHE.add(newOne);
                                return newOne;
                            }
                    );
        }
    }

    private record GamePlan(char[][] map, int rows, int cols) {
        public static GamePlan fromLines(List<String> lines) {
            char[][] map = new char[lines.size()][];
            for (int row = 0; row < map.length; row++) {
                map[row] = lines.get(row).toCharArray();
            }

            return new GamePlan(map);
        }

        public GamePlan(char[][] map) {
            this(map, map.length, countMaxCols(map));
        }

        public boolean containsPoint(int row, int col) {
            return row >= 0 && row < rows && col >= 0 && col < cols;
        }

        public char charAtPoint(int row, int col) {
            return map[row][col];
        }
    }

    private record Dimension(int x, int y) {
        private static final Collection<Dimension> CACHE = new HashSet<>();

        public static Dimension of(int x, int y) {
            return CACHE.parallelStream()
                    .filter(dimension -> dimension.x == x && dimension.y == y)
                    .findFirst().orElseGet(
                            () -> {
                                Dimension newOne = new Dimension(x, y);
                                CACHE.add(newOne);
                                return newOne;
                            }
                    );
        }
    }

    private static int countMaxCols(char[][] map) {
        int result = 0;
        for (char[] line : map) {
            result = Math.max(result, line != null ? line.length : 0);
        }
        return result;
    }
}
