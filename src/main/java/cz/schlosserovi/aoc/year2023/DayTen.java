package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

public class DayTen {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTen.class.getResourceAsStream("/2023/day-10"))))) {

            char[][] map = new char[140][];
            int startRow = -1;
            int startCol = -1;
            for (int row = 0; row < map.length; row++) {
                String line = reader.readLine();
                map[row] = line.toCharArray();

                if (line.contains("S")) {
                    startRow = row;
                    startCol = line.indexOf('S');
                }
            }

            // part one
            PipeMap scenario = new PipeMap(map, new Position(startRow, startCol));
            int[][] ranks = scenario.rankPlaces();
            int sumOne = Arrays.stream(ranks, 0, ranks.length)
                    .flatMapToInt(row -> Arrays.stream(row, 0, row.length))
                    .max().orElseThrow();

            // part two
            int sumTwo = 0;

            boolean inTheLoop = false;
            Character terminalCharacterIn = null;
            Character terminalCharacterOut = null;
            for (int row = 0; row < ranks.length; row++) {
                for (int col = 0; col < ranks[row].length; col++) {
                    if (ranks[row][col] != PipeMap.NO_VALUE) {
                        char direction = map[row][col];
                        if (terminalCharacterIn != null && terminalCharacterIn == direction) {
                            inTheLoop = true;
                            terminalCharacterIn = null;
                            terminalCharacterOut = null;
                        } else if (terminalCharacterOut != null && terminalCharacterOut == direction) {
                            inTheLoop = false;
                            terminalCharacterIn = null;
                            terminalCharacterOut = null;
                        } else if (direction == '|') {
                            inTheLoop = !inTheLoop;
                        } else {
                            switch (direction) {
                                case 'L' -> {
                                    terminalCharacterIn = inTheLoop ? 'J' : '7';
                                    terminalCharacterOut = inTheLoop ? '7' : 'J';
                                }
                                case 'F' -> {
                                    terminalCharacterIn = inTheLoop ? '7' : 'J';
                                    terminalCharacterOut = inTheLoop ? 'J' : '7';
                                }
                            }
                        }
                    } else if (inTheLoop) {
                        sumTwo++;
                    }
                }
            }

            System.out.println("Final value: " + sumOne);
            System.out.println("Final value (part two): " + sumTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private record PipeMap(char[][] map, Position startingPoint) {
        private static final int NO_VALUE = -1;

        public int[][] rankPlaces() {
            int[][] result = new int[map.length][];

            // init empty ranking
            for (int row = 0; row < map.length; row++) {
                result[row] = new int[map[row].length];
                for (int col = 0; col < map[row].length; col++) {
                    result[row][col] = NO_VALUE;
                }
            }

            result[startingPoint.row][startingPoint.col] = 0;

            int score = 0;
            Position previous = startingPoint;
            Position current = new Position(startingPoint.row, startingPoint.col - 1);
            Position next;

            while (true) {
                score++;
                if (result[current.row][current.col] == NO_VALUE || result[current.row][current.col] > score) {
                    result[current.row][current.col] = score;
                } else {
                    // finish early
                    break;
                }

                next = nextPosition(previous, current);
                previous = current;
                current = next;
            }

            score = 0;
            previous = startingPoint;
            current = new Position(startingPoint.row, startingPoint.col + 1);

            // find the two ends
            while (true) {
                score++;
                if (result[current.row][current.col] == NO_VALUE || result[current.row][current.col] > score) {
                    result[current.row][current.col] = score;
                } else {
                    // finish early
                    break;
                }

                next = nextPosition(previous, current);
                previous = current;
                current = next;
            }


            return result;
        }

        private Position nextPosition(Position previousPosition, Position currentPosition) {
            return switch (map[currentPosition.row][currentPosition.col]) {
                case '|' -> {
                    if (currentPosition.row > previousPosition.row) {
                        yield new Position(currentPosition.row + 1, currentPosition.col);
                    } else {
                        yield new Position(currentPosition.row - 1, currentPosition.col);
                    }
                }
                case '-' -> {
                    if (currentPosition.col > previousPosition.col) {
                        yield new Position(currentPosition.row, currentPosition.col + 1);
                    } else {
                        yield new Position(currentPosition.row, currentPosition.col - 1);
                    }
                }
                case 'L' -> {
                    if (currentPosition.row > previousPosition.row) {
                        yield new Position(currentPosition.row, currentPosition.col + 1);
                    } else {
                        yield new Position(currentPosition.row - 1, currentPosition.col);
                    }
                }
                case 'J' -> {
                    if (currentPosition.row > previousPosition.row) {
                        yield new Position(currentPosition.row, currentPosition.col - 1);
                    } else {
                        yield new Position(currentPosition.row - 1, currentPosition.col);
                    }
                }
                case '7' -> {
                    if (currentPosition.row < previousPosition.row) {
                        yield new Position(currentPosition.row, currentPosition.col - 1);
                    } else {
                        yield new Position(currentPosition.row + 1, currentPosition.col);
                    }
                }
                case 'F' -> {
                    if (currentPosition.row < previousPosition.row) {
                        yield new Position(currentPosition.row, currentPosition.col + 1);
                    } else {
                        yield new Position(currentPosition.row + 1, currentPosition.col);
                    }
                }
                default -> throw new IllegalArgumentException("Invalid pipe");
            };
        }
    }

    private record Position(int row, int col) {
    }

}
