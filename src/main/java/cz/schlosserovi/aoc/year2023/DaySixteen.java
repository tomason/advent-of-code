package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class DaySixteen {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTwelve.class.getResourceAsStream("/2023/day-16"))))) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            char[][] map = new char[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                map[i] = lines.get(i).toCharArray();
            }


            // part one
            long one = countEnergized(map, new Move(0, 0, Direction.RIGHT));
            System.out.println("Final value (part one):    " + one);

            // part two
            long two = 0;
            for (int i = 0; i < map.length; i++) {
                two = Math.max(two, countEnergized(map, new Move(i, 0, Direction.RIGHT)));
                two = Math.max(two, countEnergized(map, new Move(i, map[i].length - 1, Direction.LEFT)));
            }
            for (int i = 0; i < map[0].length; i++) {
                two = Math.max(two, countEnergized(map, new Move(0, i, Direction.DOWN)));
                two = Math.max(two, countEnergized(map, new Move(map.length - 1, i, Direction.UP)));
            }

            System.out.println("Final value (part two):    " + two);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static long countEnergized(char[][] map, Move startingMove) {
        int[][] result = new int[map.length][];
        for (int i = 0; i < map.length; i++) {
            result[i] = new int[map[i].length];
            Arrays.fill(result[i], 0);
        }

        Queue<Move> beamsToProcess = new LinkedList<>();
        beamsToProcess.offer(startingMove);

        List<Move> previousMoves = new ArrayList<>();
        while (!beamsToProcess.isEmpty()) {
            Move nextMove = beamsToProcess.poll();

            beamProcessing:
            while (!previousMoves.contains(nextMove)) {
                if (nextMove.row < 0 || nextMove.row >= map.length ||
                        nextMove.col < 0 || nextMove.col >= map[nextMove.row].length) {
                    // out of bounds
                    break;
                }

                // record previous move
                previousMoves.add(nextMove);

                // record light passing
                result[nextMove.row][nextMove.col]++;

                // calculate next move
                switch (map[nextMove.row][nextMove.col]) {
                    case '.' -> nextMove = nextMove.moveForward();
                    case '/' -> {
                        if (nextMove.dir == Direction.RIGHT || nextMove.dir == Direction.LEFT)
                            nextMove = nextMove.turnCounterClockwise();
                        else
                            nextMove = nextMove.turnClockwise();
                    }
                    case '\\' -> {
                        if (nextMove.dir == Direction.RIGHT || nextMove.dir == Direction.LEFT)
                            nextMove = nextMove.turnClockwise();
                        else
                            nextMove = nextMove.turnCounterClockwise();
                    }
                    case '|' -> {
                        if (nextMove.dir == Direction.RIGHT || nextMove.dir == Direction.LEFT) {
                            beamsToProcess.add(nextMove.turnClockwise());
                            beamsToProcess.add(nextMove.turnCounterClockwise());
                            break beamProcessing;
                        } else {
                            nextMove = nextMove.moveForward();
                        }
                    }
                    case '-' -> {
                        if (nextMove.dir == Direction.RIGHT || nextMove.dir == Direction.LEFT) {
                            nextMove = nextMove.moveForward();
                        } else {
                            beamsToProcess.add(nextMove.turnClockwise());
                            beamsToProcess.add(nextMove.turnCounterClockwise());
                            break beamProcessing;
                        }
                    }
                }

            }
        }

        return Arrays.stream(result).flatMapToInt(Arrays::stream).filter(value -> value > 0).count();
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
    }

    private record Move(int row, int col, Direction dir) {
        Move moveForward() {
            return new Move(row + dir.deltaRow, col + dir.deltaCol, dir);
        }

        Move turnClockwise() {
            Direction newDir = dir.clockwise();
            return new Move(row + newDir.deltaRow, col + newDir.deltaCol, newDir);
        }

        Move turnCounterClockwise() {
            Direction newDir = dir.counterClockwise();
            return new Move(row + newDir.deltaRow, col + newDir.deltaCol, newDir);
        }
    }
}
