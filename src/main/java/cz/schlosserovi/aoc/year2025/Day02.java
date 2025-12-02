package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Stream;

public class Day02 {
    public static void main(String... args) {
        var line = Utils.readLines("/2025/day-02-1").getFirst();

        var ranges = Stream.of(line.split(","))
            .map(rangeStr -> {
                var split = rangeStr.split("-");
                return new Range(Long.parseLong(split[0]), Long.parseLong(split[1]));
            })
            .sorted(Comparator.comparingLong(Range::start))
            .toList();

        var maxRange = ranges.getLast().end() + 1;
        var maxPartLength = Long.toString(maxRange).length() / 2;
        var maxPart = (int)Math.pow(10, maxPartLength);

        var invalidIds = new ArrayList<Long>();
        var currentPowerOfTen = 10L;
        for (var part = 1; part < maxPart; part++) {
            if (part == currentPowerOfTen) {
                currentPowerOfTen *= 10;
            }
            invalidIds.add(part + part * currentPowerOfTen);
        }

        var one = 0L;

        for (var range : ranges) {
            one += invalidIds.stream()
                .filter(id -> id >= range.start())
                .filter(id -> id <= range.end())
                .mapToLong(Long::longValue)
                .sum();
        }


        // 21139440284
        System.out.println("Part one: " + one);

        var maxLength = Long.toString(maxRange).length() - 1;

        var invalidIds2 = new HashSet<Long>();
        currentPowerOfTen = 10L;
        for (var part = 1; part < maxPart; part++) {
            // how much to shift each part to the left
            if (part == currentPowerOfTen) {
                currentPowerOfTen *= 10;
                maxLength -= 2;
            }

            // start with two parts
            var invalid = part * currentPowerOfTen + part;
            // copy the part as many times as it fits into maximum range
            for (int i = 0; i < maxLength; i++) {
                if (invalid > maxRange) {
                    break;
                }

                invalidIds2.add(invalid);
                invalid = invalid * currentPowerOfTen + part;
            }
        }

        var two = 0L;

        for (var range : ranges) {
            two += invalidIds2.stream()
                .filter(id -> id >= range.start())
                .filter(id -> id <= range.end())
                .mapToLong(Long::longValue)
                .sum();
        }

        // 38731915928
        System.out.println("Part two: " + two);
    }

    private record Range(long start, long end) {
    }
}