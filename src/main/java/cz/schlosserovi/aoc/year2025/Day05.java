package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Comparator;

public class Day05 {
    public static void main(String... args) {
        var lines = Utils.readLines("/2025/day-05-1");

        var ranges = new ArrayList<Range>();
        var values = new ArrayList<Long>();

        var readingRanges = true;
        for (var line : lines) {
            if (line.isBlank()) {
                readingRanges = false;
                continue;
            }

            if (readingRanges) {
                var split = line.split("-");
                // assuming well-formed lines
                ranges.add(new Range(Long.parseLong(split[0]), Long.parseLong(split[1])));
            } else {
                values.add(Long.parseLong(line));
            }
        }

        var one = 0L;

        one = values.stream()
            .filter(value -> ranges.stream().anyMatch(range -> value >= range.start() && value <= range.end()))
            .count();

        // 770
        System.out.println("Part one: " + one);

        var two = 0L;

        ranges.sort(Comparator.comparingLong(Range::start));

        var currentStart = ranges.getFirst().start();
        var currentEnd = ranges.getFirst().end();

        for (var range : ranges) {
            if (currentEnd < range.start()) {
                // no overlap, count the numbers and reset
                two += diffInclusive(currentStart, currentEnd);
                currentStart = range.start();
            }
            if (range.end() > currentEnd) {
                currentEnd = range.end();
            }
        }

        // add the last range
        two += diffInclusive(currentStart, currentEnd);

        // 357674099117260
        System.out.println("Part two: " + two);
    }

    private static long diffInclusive(long start, long end) {
        var diff = (end - start) + 1L;
        //System.out.printf("Adding range %s-%s, diff: %s%n", start, end, diff);
        return diff;
    }

    private record Range(long start, long end) {
    }
}