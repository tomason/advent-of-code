package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Day07 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-07-1");

        long starOne = 0;

        starOne = lines.stream()
                .mapToLong(line -> isItAnEquation(line, Day07::add, Day07::multiply))
                .sum();

        // 28730327770375
        System.out.println("Star one: " + starOne);

        long starTwo = 0;

        starTwo = lines.stream()
                .mapToLong(line -> isItAnEquation(line,
                        Day07::add,
                        Day07::multiply,
                        Day07::concatenate))
                .sum();

        // 424977609625985
        System.out.println("Star two: " + starTwo);

    }

    private static long isItAnEquation(String line, BiFunction<Long, Integer, Long>... operations) {
        long result = Long.parseLong(line.substring(0, line.indexOf(':')));

        List<Integer> values = Stream.of(line.substring(line.indexOf(':') + 1).split(" "))
                .map(String::trim)
                .filter(val -> !val.isBlank())
                .map(Integer::parseInt)
                .toList();

        if (evaluate(result, values, operations, values.get(0), 1)) {
            return result;
        } else {
            return 0L;
        }
    }

    private static boolean evaluate(long expectedResult, List<Integer> values, BiFunction<Long, Integer, Long>[] operations, long currentResult, int index) {
        if (currentResult > expectedResult) {
            return false;
        }
        if (index == values.size()) {
            return expectedResult == currentResult;
        }

        boolean result = false;
        for (BiFunction<Long, Integer, Long> operation : operations) {
            result = evaluate(expectedResult, values, operations, operation.apply(currentResult, values.get(index)), index + 1);
            if (result) {
                // end early
                return result;
            }
        }

        return result;
    }

    private static long add(long one, long two) {
        return one + two;
    }

    private static long multiply(long one, long two) {
        return one * two;
    }

    private static long concatenate(long one, long two) {
        long tens = 10;

        while (two / tens != 0) {
            tens = tens * 10;
        }

        return one * tens + two;
    }
}
