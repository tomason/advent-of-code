package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

public class Day01 {
    public static void main(String... args) {
        var lines = Utils.readLines("/2025/day-01-1");

        var position = 50;
        var zeroCount = 0;

        for (String line : lines) {
            var sign = line.startsWith("L") ? -1 : 1;
            var steps = Integer.parseInt(line.substring(1));

            position = (position + (sign * steps) + 100) % 100;

            if (position == 0) {
                zeroCount++;
            }
        }

        // 1102
        System.out.println("Part one: " + zeroCount);

        // reset
        position = 50;
        var zeroPassCount = 0;

        for (String line : lines) {
            var startPosition = position;
            var sign = line.startsWith("L") ? -1 : 1;
            var steps = Integer.parseInt(line.substring(1));

            // count overflows in any direction
            zeroPassCount += steps / 100;
            steps = steps % 100;

            position += sign * steps;

            // ignore if starting at position 0 and going left because that zero is already counted
            if ((position <= 0 && startPosition > 0) || position > 99) {
                zeroPassCount++;
            }

            position = (position + 100) % 100;
        }

        // 6175
        System.out.println("Part two: " + zeroPassCount);
    }
}
