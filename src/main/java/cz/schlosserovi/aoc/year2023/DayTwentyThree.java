package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

public class DayTwentyThree {
    public static void main(String[] args) {
        List<String> input = Utils.readLines("2023/day-23");

        HikingMap hikingMap = HikingMap.fromInput(input);
        Position startPosition = null;
        Position finalPosition = null;

        for (int i = 0; i < hikingMap.cols; i++) {
            if (hikingMap.charAt(0, i) == '.') {
                // starting position
                startPosition = new Position(0, i);
            }
            if (hikingMap.charAt(hikingMap.rows - 1, i) == '.') {
                finalPosition = new Position(hikingMap.rows - 1, i);
            }
        }

        if (startPosition == null || finalPosition == null) {
            throw new RuntimeException("Invalid input, start or finish is missing");
        }

        Queue<List<Position>> pathsToCheck = new LinkedList<>();
        Collection<List<Position>> validPaths = new HashSet<>();

        pathsToCheck.offer(List.of(startPosition));

        while (!pathsToCheck.isEmpty()) {
            List<Position> path = pathsToCheck.poll();

            Position currentPosition = path.get(path.size() - 1);
            if (finalPosition.equals(currentPosition)) {
                validPaths.add(path);
                continue;
            }
            Stream.of(
                            new Position(currentPosition.row + 1, currentPosition.col),
                            new Position(currentPosition.row - 1, currentPosition.col),
                            new Position(currentPosition.row, currentPosition.col + 1),
                            new Position(currentPosition.row, currentPosition.col - 1))
                    // remove moves already made
                    .filter(move -> !path.contains(move))
                    // remove invalid moves
                    .filter(hikingMap::isValidLocation)
                    // filter based on map content
                    .filter(nextPosition -> switch (hikingMap.charAt(nextPosition)) {
                        case '#' -> false;
                        case '.' -> true;
                        case '>' -> nextPosition.col > currentPosition.col;
                        case '<' -> nextPosition.col < currentPosition.col;
                        case '^' -> nextPosition.row < currentPosition.row;
                        case 'v' -> nextPosition.row > currentPosition.row;
                        default -> throw new RuntimeException("Invalid map instruction");
                    })
                    .forEach(nextMove -> {
                        List<Position> newPath = new ArrayList<>(path);
                        newPath.add(nextMove);
                        pathsToCheck.offer(newPath);
                    });

        }

        int longestPath = validPaths.parallelStream().mapToInt(List::size)
                .max().orElse(0) - 1;

        System.out.println("Part one solution: " + longestPath);


        // reset and start over
        pathsToCheck.clear();
        validPaths.clear();

        List<Position> startingList = new ArrayList<>();
        startingList.add(startPosition);
        pathsToCheck.offer(startingList);

        while (!pathsToCheck.isEmpty()) {
            List<Position> path = pathsToCheck.poll();

            Position currentPosition = path.get(path.size() - 1);

            List<Position> nextMoves = Collections.emptyList();
            do {
                if (finalPosition.equals(currentPosition)) {
                    validPaths.add(path);
                    continue;
                }
                nextMoves = Stream.of(
                                new Position(currentPosition.row + 1, currentPosition.col),
                                new Position(currentPosition.row - 1, currentPosition.col),
                                new Position(currentPosition.row, currentPosition.col + 1),
                                new Position(currentPosition.row, currentPosition.col - 1))
                        // remove moves already made
                        .filter(move -> !path.contains(move))
                        // remove invalid moves
                        .filter(hikingMap::isValidLocation)
                        // filter based on map content
                        .filter(nextPosition -> hikingMap.charAt(nextPosition) != '#')
                        .toList();

                if (nextMoves.size() != 1) {
                    nextMoves.forEach(nextMove -> {
                        List<Position> newPath = new ArrayList<>(path);
                        newPath.add(nextMove);
                        pathsToCheck.offer(newPath);
                    });
                    break;
                }
            } while (nextMoves.size() == 1);

        }

        int longestPath2 = validPaths.parallelStream().mapToInt(List::size)
                .max().orElse(0) - 1;

        System.out.println("Part two solution: " + longestPath2);
    }

    private record HikingMap(char[][] map, int rows, int cols) {
        public static HikingMap fromInput(List<String> input) {
            int rows = input.size();
            int cols = 0;
            char[][] map = new char[rows][];

            for (int i = 0; i < rows; i++) {
                map[i] = input.get(i).toCharArray();
                cols = Math.max(cols, map[i].length);
            }

            return new HikingMap(map, rows, cols);
        }


        public char charAt(int row, int col) {
            if (!isValidLocation(row, col)) {
                throw new IllegalArgumentException();

            }
            return map[row][col];
        }

        public char charAt(Position position) {
            return charAt(position.row, position.col);
        }

        public boolean isValidLocation(int row, int col) {
            return row >= 0 && row < rows && col >= 0 && col < cols;
        }

        public boolean isValidLocation(Position position) {
            return isValidLocation(position.row, position.col);
        }
    }

    private record Position(int row, int col) {
    }
}
