package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day06 {
    public static void main(String... args) {
        var lines = Utils.readLines("/2025/day-06-1");

        var one = 0L;

        var operations = Stream.of(lines.getLast().split("\\s+"))
            .map(sign -> switch (sign) {
                case "+" -> Map.<BiFunction<Long, Long, Long>, Long>entry(Long::sum, 0L);
                case "*" -> Map.<BiFunction<Long, Long, Long>, Long>entry((a,  b) -> a * b, 1L);
                default -> throw new IllegalArgumentException("Unknown operation '" + sign + "'");
            })
            .toList();
        var results = new long[operations.size()];
        for (var col = 0; col < results.length; col++) {
            results[col] = operations.get(col).getValue();
        }

        for (var line = 0; line < lines.size() - 1; line++) {
            var split = lines.get(line).trim().split("\\s+");
            for (var col = 0; col < results.length; col++) {
                results[col] = operations.get(col).getKey().apply(results[col], Long.valueOf(split[col]));
            }
        }

        one = LongStream.of(results).sum();

        // 6757749566978
        System.out.println("Part one: " + one);

        var two = 0L;

        var results2 = new long[operations.size()];
        for (var col = 0; col < results.length; col++) {
            results2[col] = operations.get(col).getValue();
        }

        var maxLength = lines.stream().mapToInt(String::length).max().orElseThrow();
        var currentResult = results2.length - 1;
        // run from left to right
        for (var i = maxLength - 1; i >= 0; i--) {
            var number = 0L;
            // go from top to bottom through all number lines
            for (var line = 0; line < lines.size() - 1; line++) {
                // check that the character is within the line (there are no trailing spaces at the end)
                // check that there is a number (or that there is no space at least)
                if (i < lines.get(line).length() && lines.get(line).charAt(i) != ' ') {
                    number = number * 10 + (lines.get(line).charAt(i) - '0');
                }
            }
            // apply the operation to the resulting number
            results2[currentResult] = operations.get(currentResult).getKey().apply(results2[currentResult], number);
            // operation is always at the start of a column
            if (i < lines.getLast().length() && lines.getLast().charAt(i) != ' ') {
                // end of input
                currentResult--;
                // skip empty column
                i--;
            }
        }

        two = LongStream.of(results2).sum();

        // 10603075273949
        System.out.println("Part two: " + two);
    }

}