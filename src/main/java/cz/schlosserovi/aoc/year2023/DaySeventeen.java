package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DaySeventeen {
    private static int[][] map;
    private static int rows = 0;
    private static int cols = 0;

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTwelve.class.getResourceAsStream("/2023/day-17"))))) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                rows++;
                cols = Math.max(cols, line.length());
                lines.add(line);
            }

            map = new int[rows][];
            for (int i = 0; i < rows; i++) {
                char[] mapLine = lines.get(i).toCharArray();

                map[i] = new int[cols];
                for (int j = 0; j < cols; j++) {
                    map[i][j] = Character.digit(mapLine[j], 10);
                }
            }

            // part one
            int one = countLeastHeatLoss(
                    new Move(0, 0, Direction.DOWN),
                    new Move(0, 0, Direction.RIGHT));
            System.out.println("Final value (part one):    " + one);
            // 771
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static int countLeastHeatLoss(Move... startingMoves) {
        // assuming the map is square (both example and input cases)
        int initialBest = IntStream.range(0, rows)
                .map(index -> map[index][index] + (index + 1 < cols ? map[index][index + 1] : 0))
                .sum();


        Move[][][] result = new Move[map.length][][];
        for (int i = 0; i < map.length; i++) {
            result[i] = new Move[map[i].length][];
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = new Move[4];
            }
        }

        // use priority queue to take longest path first
        // sort longest path first so the branches get pruned before filling the memory
        //final BlockingQueue<Move> possibleMoves = new PriorityBlockingQueue<>(1_000_000, Collections.reverseOrder());
        final Queue<Move> possibleMoves = new PriorityQueue<>(1_000_000);
        //final Queue<Move> possibleMoves = new LinkedList<>();
        for (Move startingMove : startingMoves) {
            possibleMoves.offer(startingMove);
        }

        // only care about paths with less heat loss than the diagonal
        final AtomicInteger currentShortestPathToEnd = new AtomicInteger(initialBest);
        System.out.println("Initial guess: " + currentShortestPathToEnd.get());

        Stream.generate(possibleMoves::poll)
                .takeWhile(Objects::nonNull)
                // remove invalid moves
                .filter(DaySeventeen::validateMove)
                // filter out paths longer that current best guess
                .filter(move -> move.heatLoss < currentShortestPathToEnd.get())
                // filter out paths where better path exists
                .filter(move -> {
                    Move previousBest = result[move.row][move.col][move.dir.ordinal()];
                    if (previousBest == null) {
                        result[move.row][move.col][move.dir.ordinal()] = move;
                        return true;
                    } else if (move.heatLoss < previousBest.heatLoss || move.stepsInLine < previousBest.stepsInLine) {
                        result[move.row][move.col][move.dir.ordinal()] = move;
                        return true;
                    }
                    return false;
                })
                .forEach(current -> {
                    if (current.row == rows - 1 && current.col == cols - 1) {
                        // at the end, no need to continue counting
                        if (current.heatLoss == currentShortestPathToEnd.updateAndGet(prevBest -> Math.min(prevBest, current.heatLoss))) {
                            System.out.println("  New best path: " + currentShortestPathToEnd.get() + " with move " + current);
                        }
                    } else {
                        Stream.of(current.moveForward(), current.turnClockwise(), current.turnCounterClockwise())
                                .filter(DaySeventeen::validateMove) // no need to insert invalid moves
                                .forEach(possibleMoves::offer);
                    }
                });

        // return heat loss at the final point
        return currentShortestPathToEnd.get();
    }

    private static boolean validateMove(Move move) {
        return move.row >= 0 && move.row < rows &&
                move.col >= 0 && move.col < cols &&
                move.stepsInLine <= 3;
    }

    private enum Direction {
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1),
        UP(-1, 0);

        public final int deltaRow;
        public final int deltaCol;

        Direction(int deltaRow, int deltaCol) {
            this.deltaRow = deltaRow;
            this.deltaCol = deltaCol;
        }

        Direction clockwise() {
            return switch (this) {
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
                case UP -> RIGHT;
            };
        }

        Direction counterClockwise() {
            return switch (this) {
                case RIGHT -> UP;
                case UP -> LEFT;
                case LEFT -> DOWN;
                case DOWN -> RIGHT;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case DOWN -> "v";
                case UP -> "^";
                case LEFT -> "<";
                case RIGHT -> ">";
            };
        }
    }

    private record Move(int row, int col, Direction dir, int heatLoss, int stepsInLine) implements Comparable<Move> {
        public Move(int row, int col, Direction dir) {
            this(row, col, dir, 0, 1);
        }

        Move moveForward() {
            return performMove(dir);
        }

        Move turnClockwise() {
            return performMove(dir.clockwise());
        }

        Move turnCounterClockwise() {
            return performMove(dir.counterClockwise());
        }

        private Move performMove(Direction newDir) {
            int newRow = row + newDir.deltaRow;
            int newCol = col + newDir.deltaCol;
            int newSteps = newDir == dir ? stepsInLine + 1 : 1;
            int newHeat = within(newRow, rows) && within(newCol, cols) ? heatLoss + map[newRow][newCol] : heatLoss;

            return new Move(newRow, newCol, newDir, newHeat, newSteps);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Move move)) return false;
            return row == move.row && col == move.col && stepsInLine == move.stepsInLine && dir == move.dir;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col, dir, stepsInLine);
        }

        private static boolean within(int number, int endExclusive) {
            return number >= 0 && number < endExclusive;
        }

        @Override
        public int compareTo(Move o) {
            return Integer.compare(heatLoss, o.heatLoss);
        }

        @Override
        public String toString() {
            return String.format("%s %4d", dir, heatLoss);
        }
    }
}
