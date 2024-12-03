package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DayTwelve {
    private static final Map<String, Long> SOLUTION_CACHE = new ConcurrentHashMap<>();

    public static void main(String... args) {
        List<ConditionRow> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTwelve.class.getResourceAsStream("/2023/day-12"))))) {

            String line;

            while ((line = reader.readLine()) != null) {
                ConditionRow row = parseLine(line);
                rows.add(row);
            }
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }

        // part one
        System.out.println(LocalDateTime.now());
        long one = rows.parallelStream()
                .mapToLong(DayTwelve::newGenerationAndCounting)
                .sum();
        System.out.println(LocalDateTime.now());
        System.out.println("Final value:    " + one);
        System.out.println("Expected value: 7922");

        System.out.println("\n\n");

        // part two
        System.out.println(LocalDateTime.now());

        //AtomicInteger counter = new AtomicInteger(0);
        long two = rows.parallelStream()
                .map(DayTwelve::unfold)
                .mapToLong(DayTwelve::newGenerationAndCounting)
                //.peek(result -> System.out.printf("%4d/1000, count: %15d%n", counter.incrementAndGet(), result))
                .sum();

        System.out.println(LocalDateTime.now());
        System.out.println("Final value (part two):    " + two);
        System.out.println("Expected value (part two): " + 18093821750095L);
    }

    private static ConditionRow parseLine(String line) {
        String records = line.substring(0, line.indexOf(' '))
                // shrink the input
                .replaceAll("\\.+", ".");

        int[] checksums = Arrays.stream(line.substring(line.indexOf(' ') + 1).split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        return new ConditionRow(records, checksums);
    }

    private static long newGenerationAndCounting(ConditionRow row) {
        return newGenerationAndCounting(row.records.toCharArray(), 0, row.checksums, 0);
    }

    private static long newGenerationAndCounting(final char[] input, final int inputIndex, final int[] checksums, final int checksumIndex) {
        String cacheHash = cacheHash(input, inputIndex, checksums, checksumIndex);
        if (SOLUTION_CACHE.containsKey(cacheHash)) {
            return SOLUTION_CACHE.get(cacheHash);
        }

        long result = 0L;

        if (checksumIndex == checksums.length) {
            // all checksums are done, check that no more damaged springs are present, count any questionable spaces as
            // one possibility only
            for (int i = inputIndex; i < input.length; i++) {
                if (input[i] == '#') {
                    SOLUTION_CACHE.put(cacheHash, 0L);
                    return 0L;
                }
            }
            SOLUTION_CACHE.put(cacheHash, 1L);
            return 1L;
        }

        bigLoop:
        for (int index = inputIndex; index < input.length; index++) {
            if (input[index] == '#' || input[index] == '?') {
                // starting counting a new checksum
                for (int i = 0; i < checksums[checksumIndex]; i++) {
                    if (index + i >= input.length) {
                        // out of bounds, this will never fit
                        break bigLoop;
                    }
                    if (input[index + i] == '.') {
                        // bad sequence
                        if (input[index] == '#') {
                            // started with # and the sequence did not fit, break out of the loop, no more possible solutions
                            break bigLoop;
                        } else {
                            // there is still hope after the dot
                            continue bigLoop;
                        }
                    }
                }
                int nextIndex = index + checksums[checksumIndex];
                if (nextIndex == input.length || // end of input
                        input[nextIndex] != '#'  // end of damaged springs
                ) {
                    result += newGenerationAndCounting(input, nextIndex + 1, checksums, checksumIndex + 1);
                }
            }

            if (input[index] == '#') {
                // if damaged spring was encountered, either it has been already counted or it will create a break in line
                break;
            }
        }

        SOLUTION_CACHE.put(cacheHash, result);
        return result;
    }

    private static String cacheHash(char[] input, int inputIndex, int[] checksums, int checksumIndex) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = inputIndex; i < input.length; i++) {
            stringBuilder.append(input[i]);
        }
        stringBuilder.append('|');
        for (int i = checksumIndex; i < checksums.length; i++) {
            stringBuilder.append(checksums[i]).append(",");
        }

        return stringBuilder.toString();
    }

    private static ConditionRow unfold(ConditionRow row) {
        String records = String.join("?", row.records, row.records, row.records, row.records, row.records);

        int[] checksums = new int[row.checksums.length * 5];

        for (int i = 0; i < checksums.length; i++) {
            checksums[i] = row.checksums[i % row.checksums.length];
        }

        return new ConditionRow(records, checksums);
    }

    private record ConditionRow(String records, int[] checksums) {
    }
}
