package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Day08 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-08-1");

        final int maxRow = lines.size();
        final int maxCol = lines.get(0).length();

        char[][] map = new char[lines.size()][];
        Map<Character, List<Point>> antenae = new HashMap<>();

        for (int row = 0; row < maxRow; row++) {
            String line = lines.get(row);
            map[row] = new char[maxCol];
            for (int col = 0; col < maxCol; col++) {
                char point = line.charAt(col);
                map[row][col] = point;
                if (point != '.') {
                    antenae.computeIfAbsent(point, ignore -> new ArrayList<>()).add(new Point(row, col));
                }
            }
        }

        long starOne = 0L;

        Set<Point> uniqueAntinodes = new HashSet<>();
        for (List<Point> sameFrequencyAntenae : antenae.values()) {
            for (int i = 0; i < sameFrequencyAntenae.size() - 1; i++) {
                for (int j = i + 1; j < sameFrequencyAntenae.size(); j++) {
                    Point antenaA = sameFrequencyAntenae.get(i);
                    Point antenaB = sameFrequencyAntenae.get(j);

                    int dRow = antenaA.row - antenaB.row;
                    int dCol = antenaA.col - antenaB.col;

                    // TODO antinodes two thirds between the antenae (does not occur in the map)

                    Stream.of(
                                    new Point(antenaA.row + dRow, antenaA.col + dCol),
                                    new Point(antenaB.row - dRow, antenaB.col - dCol)
                            )
                            .filter(point -> point.row >= 0 && point.row < maxRow && point.col >= 0 && point.col < maxCol)
                            .forEach(uniqueAntinodes::add);
                }
            }
        }

        starOne = uniqueAntinodes.size();

        // 311
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        Set<Point> harmonics = new HashSet<>();
        for (List<Point> sameFrequencyAntenae : antenae.values()) {
            for (int i = 0; i < sameFrequencyAntenae.size() - 1; i++) {
                for (int j = i + 1; j < sameFrequencyAntenae.size(); j++) {
                    Point antenaA = sameFrequencyAntenae.get(i);
                    Point antenaB = sameFrequencyAntenae.get(j);

                    int dRow = antenaA.row - antenaB.row;
                    int dCol = antenaA.col - antenaB.col;

                    int newRow = antenaA.row;
                    int newCol = antenaA.col;

                    while (newRow >= 0 && newRow < maxRow && newCol >= 0 && newCol < maxCol) {
                        harmonics.add(new Point(newRow, newCol));
                        newRow = newRow - dRow;
                        newCol = newCol - dCol;
                    }

                    newRow = antenaA.row;
                    newCol = antenaA.col;

                    while (newRow >= 0 && newRow < maxRow && newCol >= 0 && newCol < maxCol) {
                        harmonics.add(new Point(newRow, newCol));
                        newRow = newRow + dRow;
                        newCol = newCol + dCol;
                    }
                }
            }
        }

        starTwo = harmonics.size();

        // 1115
        System.out.println("Star two: " + starTwo);

    }

    private record Point(int row, int col) {
    }
}
