package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.List;
import java.util.stream.Stream;

public class Day02 {
    public static void main(String... args) {
        List<Boolean> safetyRecord = Utils.processFile("/2024/day-02-1", line -> {
            int[] values = Stream.of(line.split(" "))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            return isValid(values);
        });

        // 269
        System.out.println("Star one: " + safetyRecord.stream().filter(safe -> safe).count());

        List<Boolean> dampenedSafetyRecord = Utils.processFile("/2024/day-02-1", line -> {
            int[] values = Stream.of(line.split(" "))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            if (isValid(values)) {
                return true;
            } else {
                for (int i = 0; i < values.length; i++) {
                    if (isValid(removeIndex(values, i))) {
                        return true;
                    }
                }
                return false;
            }
        });

        // 337
        System.out.println("Star two: " + dampenedSafetyRecord.stream().filter(safe -> safe).count());
    }

    private static boolean isValid(int[] sequence) {
        boolean descending = sequence[0] > sequence[1];

        // check validity
        for (int i = 1; i < sequence.length; i++) {
            int diff = sequence[i - 1] - sequence[i];

            if (!descending && (diff > -1 || diff < -3)) {
                return false;
            }
            if (descending && (diff < 1 || diff > 3)) {
                return false;
            }
        }

        return true;
    }

    private static int[] removeIndex(int[] input, int index) {
        // ensure the index is within the array
        int tmp = input[index];

        int[] result = new int[input.length - 1];
        int resultIndex = 0;
        for (int i = 0; i < input.length; i++) {
            if (i == index) {
                continue;
            }
            result[resultIndex] = input[i];
            resultIndex++;
        }

        return result;
    }
}
