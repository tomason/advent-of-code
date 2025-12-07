package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

public class Day07 {
    public static void main(String... args) {
        var lines = Utils.readLines("/2025/day-07-1");

        var height = lines.size();
        var width = lines.getFirst().length();
        Position start = null;
        var splitters = new HashSet<Position>();

        for (var row = 0; row < height; row++) {
            var line = lines.get(row).toCharArray();
            for (var col = 0; col < width; col++) {
                if (line[col] == 'S') {
                    start = new Position(row, col);
                }
                if (line[col] == '^') {
                    splitters.add(new Position(row, col));
                }
            }
        }

        var one = 0L;

        var usedSplitters = new HashSet<>();
        Queue<Position> beams = new ArrayDeque<>();

        beams.add(start);
        while (!beams.isEmpty()) {
            var current = beams.remove();

            // exit
            if (current.row() == height - 1) {
                continue;
            }

            // split
            if (splitters.contains(current)) {
                usedSplitters.add(current);

                var leftBeam = new Position(current.row() + 1, current.col() - 1);
                if (!beams.contains(leftBeam)) {
                    beams.add(leftBeam);
                }

                var rightBeam = new Position(current.row() + 1, current.col() + 1);
                if (!beams.contains(rightBeam)) {
                    beams.add(rightBeam);
                }
            } else {
                var continuousBeam = new Position(current.row() + 1, current.col());
                if (!beams.contains(continuousBeam)) {
                    beams.add(continuousBeam);
                }
            }
        }

        one = usedSplitters.size();

        // 1490
        System.out.println("Part one: " + one);

        var two = 0L;

        var beamMagnitudes = new HashMap<Position, Long>();
        beamMagnitudes.put(start, 1L);

        beams.add(start);
        while (!beams.isEmpty()) {
            var current = beams.remove();
            var magnitude = beamMagnitudes.get(current);

            // exit
            if (current.row() == height - 1) {
                two += magnitude;
                continue;
            }

            if (splitters.contains(current)) {
                var leftBeam = new Position(current.row() + 1, current.col() - 1);
                beamMagnitudes.put(leftBeam, beamMagnitudes.getOrDefault(leftBeam, 0L) + magnitude);
                if (!beams.contains(leftBeam)) {
                    beams.add(leftBeam);
                }

                var rightBeam = new Position(current.row() + 1, current.col() + 1);
                beamMagnitudes.put(rightBeam, beamMagnitudes.getOrDefault(rightBeam, 0L) + magnitude);
                if (!beams.contains(rightBeam)) {
                    beams.add(rightBeam);
                }
            } else {
                var nextPosition = new Position(current.row() + 1, current.col());
                beamMagnitudes.put(nextPosition, beamMagnitudes.getOrDefault(nextPosition, 0L) + magnitude);
                if (!beams.contains(nextPosition)) {
                    beams.add(nextPosition);
                }
            }
        }

        // 3806264447357
        System.out.println("Part two: " + two);
    }

    private record Position(int row, int col) {
    }
}