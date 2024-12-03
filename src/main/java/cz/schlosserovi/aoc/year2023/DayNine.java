package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DayNine {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayNine.class.getResourceAsStream("/2023/day-9"))))) {
            String line;

            List<int[]> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(parseLine(line));
            }

            // part one
            int sumOne = lines.parallelStream()
                    .mapToInt(DayNine::extrapolateNextValue)
                    .sum();

            // part two
            int sumTwo = lines.parallelStream()
                    .mapToInt(DayNine::extrapolatePreviousValue)
                    .sum();

            System.out.println("Final value: " + sumOne);
            System.out.println("Final value (part two): " + sumTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static int[] parseLine(String line) {
        return Arrays.stream(line.split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private static int extrapolateNextValue(int[] input) {
        int[] diffs = new int[input.length - 1];
        boolean isAllZeroes = true;

        for (int i = 1; i < input.length; i++) {
            diffs[i - 1] = input[i] - input[i - 1];
            isAllZeroes &= diffs[i - 1] == 0;
        }

        if (isAllZeroes) {
            return input[input.length - 1];
        } else {
            return input[input.length - 1] + extrapolateNextValue(diffs);
        }
    }

    private static int extrapolatePreviousValue(int[] input) {
        int[] diffs = new int[input.length - 1];
        boolean isAllZeroes = true;

        for (int i = 1; i < input.length; i++) {
            diffs[i - 1] = input[i] - input[i - 1];
            isAllZeroes &= diffs[i - 1] == 0;
        }

        if (isAllZeroes) {
            return input[0];
        } else {
            return input[0] - extrapolatePreviousValue(diffs);
        }
    }
}
