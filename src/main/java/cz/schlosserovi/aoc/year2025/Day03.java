package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.Arrays;

public class Day03 {
    public static void main(String... args) {
        var lines = Utils.readLines("/2025/day-03-1");

        var one = 0L;

        one = lines.stream()
            .mapToInt(Day03::highestJoltage)
                .sum();

        // 17332
        System.out.println("Part one: " + one);

        var two = 0L;

        two = lines.stream()
            .mapToLong(line -> highestJoltage(line, 12))
            .sum();

        // 172516781546707
        System.out.println("Part two: " + two);
    }

    public static int highestJoltage(String line) {
        var digits = line.chars().map(character -> character - '0').toArray();

        var first = 0;
        var second = 0;

        for (var i = 0; i < line.length(); i++) {
            var digit = digits[i];

            if (i < digits.length - 1 && first < digit) {
                first = digit;
                second = 0;
            } else if (second < digit) {
                second = digit;
            }

            if (first == 9 && second == 9) {
                // won't find higher number than 99
                break;
            }
        }

        return first * 10 + second;
    }

    public static long highestJoltage(String line, int batteries) {
        var digits = line.chars().map(character -> character - '0').toArray();
        var result = new int[batteries];
        Arrays.fill(result, 0);

        var lastIndex = 0;
        for (int resultIndex = 0; resultIndex < result.length; resultIndex++) {
            for (int i = lastIndex; i < digits.length - result.length + resultIndex + 1; i++) {
                if (result[resultIndex] < digits[i]) {
                    result[resultIndex] = digits[i];
                    // start on the next digit
                    lastIndex = i + 1;
                }
                if (result[resultIndex] == 9) {
                    // end early if 9 was found
                    break;
                }
            }
        }

        var resultNumber = 0L;
        for (int digit : result) {
            resultNumber = resultNumber * 10 + digit;
        }

        return resultNumber;
    }
}