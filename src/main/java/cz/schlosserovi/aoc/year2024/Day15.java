package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Day15 {
    public static void main(String... args) {
        List<String> mapLines = new ArrayList<>();
        List<String> moveLines = new ArrayList<>();
        AtomicBoolean parsingMap = new AtomicBoolean(true);

        Utils.processFile("/2024/day-15-1", line -> {
            if (parsingMap.get()) {
                if (line.isBlank()) {
                    parsingMap.set(false);
                } else {
                    mapLines.add(line);
                }
            } else {
                moveLines.add(line);
            }
        });

        char[][] map = new char[mapLines.size()][];
        int height = map.length;
        int width = 0;

        int robotRow = -1;
        int robotCol = -1;

        for (int row = 0; row < height; row++) {
            String line = mapLines.get(row);
            map[row] = line.toCharArray();

            width = Integer.max(width, map[row].length);
            if (line.contains("@")) {
                robotRow = row;
                robotCol = line.indexOf('@');
            }
        }

        List<Direction> moves = String.join("", moveLines).chars()
                .mapToObj(dir -> Direction.forChar((char)dir))
                .toList();

        long starOne = 0L;

        for (Direction direction : moves) {
            if (move(map, robotRow, robotCol, direction)) {
                robotRow = robotRow + direction.dRow;
                robotCol = robotCol + direction.dCol;
            }
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (map[row][col] == 'O') {
                    starOne += row * 100L + col;
                }
            }
        }

        // 1438161
        System.out.println("Star one: " + starOne);

        for (char[] line : map) {
            System.out.println(new String(line));
        }

        List<String> largerMapLines = mapLines.stream()
                .map(line -> line.replaceAll("#", "##"))
                .map(line -> line.replaceAll("O", "[]"))
                .map(line -> line.replaceAll("\\.", ".."))
                .map(line -> line.replaceAll("@", "@."))
                .toList();

        map = new char[largerMapLines.size()][];
        height = map.length;
        width = 0;

        robotRow = -1;
        robotCol = -1;

        for (int row = 0; row < height; row++) {
            String line = largerMapLines.get(row);
            map[row] = line.toCharArray();

            width = Integer.max(width, map[row].length);
            if (line.contains("@")) {
                robotRow = row;
                robotCol = line.indexOf('@');
            }
        }

        long starTwo = 0L;

        for (Direction direction : moves) {
            if (move(map, robotRow, robotCol, direction)) {
                robotRow = robotRow + direction.dRow;
                robotCol = robotCol + direction.dCol;
            }
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (map[row][col] == '[') {
                    starTwo += row * 100L + col;
                }
            }
        }

        // 1437981
        System.out.println("Star two: " + starTwo);

        for (char[] line : map) {
            System.out.println(new String(line));
        }

    }

    private static boolean move(char[][] map, int row, int col, Direction direction) {
        int nextRow = row + direction.dRow;
        int nextCol = col + direction.dCol;

        switch (map[row][col]) {
            case '.':
                return true;
            case '@', 'O':
                if (move(map, nextRow, nextCol, direction)) {
                    map[nextRow][nextCol] = map[row][col];
                    map[row][col] = '.';
                    return true;
                }
                break;
            case '[':
                if (canMove(map, row, col, direction)) {
                    if (direction == Direction.LEFT) {
                        move(map, row, col - 1, direction);
                        map[row][col - 1] = '[';
                        map[row][col] = ']';
                        map[row][col + 1] = '.';
                    } else if (direction == Direction.RIGHT) {
                        move(map, row, col + 2, direction);
                        map[row][col] = '.';
                        map[row][col + 1] = '[';
                        map[row][col + 2] = ']';
                    } else {
                        move(map, nextRow, nextCol, direction);
                        move(map, nextRow, nextCol + 1, direction);
                        map[nextRow][nextCol] = '[';
                        map[nextRow][nextCol + 1] = ']';
                        map[row][col] = '.';
                        map[row][col + 1] = '.';
                    }

                    return true;
                }
                break;
            case ']':
                return move(map, row, col - 1, direction);
        }

        return false;
    }

    private static boolean canMove(char[][] map, int row, int col, Direction d) {
        if (map[row][col] == '#') {
            return false;
        }
        if (map[row][col] == '.') {
            return true;
        }

        if (map[row][col] == '[') {
            if (d == Direction.RIGHT) {
                return canMove(map, row, col + 1, d);
            } else if (d == Direction.UP || d == Direction.DOWN) {
                return canMove(map, row + d.dRow, col, d) && canMove(map, row + d.dRow, col + 1, d);
            }
        }
        if (map[row][col] == ']') {
            if (d == Direction.LEFT) {
                return canMove(map, row, col - 1, d);
            } else if (d == Direction.UP || d == Direction.DOWN) {
                return canMove(map, row + d.dRow, col, d) && canMove(map, row + d.dRow, col - 1, d);
            }
        }

        return canMove(map, row + d.dRow, col + d.dCol, d);
    }

    private enum Direction {
        UP('^', -1, 0),
        RIGHT('>', 0, 1),
        DOWN('v', 1, 0),
        LEFT('<', 0, -1);

        private final char representation;
        public final int dRow;
        public final int dCol;

        private Direction(char representation, int dRow, int dCol) {
            this.representation = representation;
            this.dRow = dRow;
            this.dCol = dCol;
        }

        public static Direction forChar(char representation) {
            return Arrays.stream(Direction.values()).filter(direction -> direction.representation == representation)
                    .findFirst().orElseThrow();
        }
    }
}
