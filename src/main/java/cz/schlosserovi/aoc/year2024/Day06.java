package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Day06 {
    private static int LOOP_LIMIT;

    public static void main(String... args) {
        List<char[]> tmp = new ArrayList<>();
        AtomicInteger startRow = new AtomicInteger();
        AtomicInteger startCol = new AtomicInteger();
        Utils.processFile("/2024/day-06-1", line -> {
            int tmpCol;
            if ((tmpCol = line.indexOf('^')) != -1) {
                startRow.set(tmp.size());
                startCol.set(tmpCol);
            }
            tmp.add(line.toCharArray());
        });
        char[][] map = tmp.toArray(char[][]::new);
        LOOP_LIMIT = map.length * map[0].length;
        Character guard = new Character(startRow.get(), startCol.get(), Direction.UP);

        Set<Position> possibleObstacles = new HashSet<>();

        while (guard.row >= 0 && guard.row < map.length && guard.col >= 0 && guard.col < map[guard.row].length) {
            // mark visited position
            map[guard.row][guard.col] = 'X';

            // is there a free cell in front?
            Character tmpGuard = guard.move();
            if (tmpGuard.row >= 0 && tmpGuard.row < map.length && tmpGuard.col >= 0 && tmpGuard.col < map[tmpGuard.row].length) {
                if (map[tmpGuard.row][tmpGuard.col] == '#') {
                    // obstacle, turn
                    guard = guard.turn();
                } else {
                    if (map[tmpGuard.row][tmpGuard.col] == '.') {
                        // place obstacle
                        map[tmpGuard.row][tmpGuard.col] = '#';

                        if (willItLoop(map, guard.turn())) {
                            possibleObstacles.add(new Position(tmpGuard.row, tmpGuard.col));
                        }

                        // remove obstacle
                        map[tmpGuard.row][tmpGuard.col] = '.';
                    }

                    guard = tmpGuard;
                }
            } else {
                break;
            }
        }

        long starOne = 0;

        for (char[] row : map) {
            for (char cell : row) {
                starOne += cell == 'X' ? 1 : 0;
            }
        }

        // 5331
        System.out.println("Star one: " + starOne);

        long starTwo = possibleObstacles.size();

        // 1812
        System.out.println("Star two: " + starTwo);

    }

    private static boolean willItLoop(char[][] map, Character character) {
        int counter = 0;

        Set<Character> visitedPlaces = new HashSet<>();
        while (counter < LOOP_LIMIT) {
            visitedPlaces.add(character);

            // is there a free cell in front?
            Character tmpChar = character.move();
            if (tmpChar.row >= 0 && tmpChar.row < map.length && tmpChar.col >= 0 && tmpChar.col < map[tmpChar.row].length) {
                if (map[tmpChar.row][tmpChar.col] == '#') {
                    // obstacle, turn
                    character = character.turn();
                } else {
                    character = tmpChar;
                }
            } else {
                return false;
            }

            if (visitedPlaces.contains(character)) {
                return true;
            }
        }

        return true;
    }

    private record Character(int row, int col, Direction direction) {
        public Character move() {
            return new Character(row + direction.dRow, col + direction.dCol,  direction);
        }

        public Character turn() {
            return new Character(row, col, direction.right());
        }
    }

    private record Position(int x, int y){}

    private enum Direction {
        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1)
        ;
        public final int dRow;
        public final int dCol;

        Direction(int dX, int dY) {
            this.dRow = dX;
            this.dCol = dY;
        }

        public Direction right() {
            return switch (this) {
                case UP ->  RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }
    }
}
