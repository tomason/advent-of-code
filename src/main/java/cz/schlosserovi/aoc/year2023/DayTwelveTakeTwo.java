package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class DayTwelveTakeTwo {
    private static final Map<String, Map<String, Long>> solutionCache = new HashMap<>();

    public static void main(String... args) {
        List<ConditionRow> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTwelveTakeTwo.class.getResourceAsStream("/2023/day-12-reduced"))))) {

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
        long one = 0L;
        for (ConditionRow row : rows) {
            long validPermutations = generatePermutations(row.records.toCharArray()).stream()
                    .filter(permutation -> validateWithChecksum(permutation, row.checksums))
                    .count();
            solutionCache.computeIfAbsent(row.records, ignored -> new HashMap<>()).put(Arrays.toString(row.checksums), validPermutations);

            one += validPermutations;
        }
        System.out.println(LocalDateTime.now());
        System.out.println("Final value: " + one);

        System.out.println("\n\n");

        // part two
        System.out.println(LocalDateTime.now());

        AtomicInteger counter = new AtomicInteger(0);
        long two = rows.stream()
                .map(DayTwelveTakeTwo::unfold)
                .mapToLong(DayTwelveTakeTwo::countValidPermutations)
                .peek(result -> System.out.printf("%4d/1000, count: %15d%n", counter.incrementAndGet(), result))
                .sum();

        System.out.println(LocalDateTime.now());
        System.out.println("Final value (part two):    " + two);
        System.out.println("Expected value (part two): " + 525152);
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

    private static long countValidPermutations(ConditionRow row) {
        return countValidPermutations(row.records, row.checksums);
    }

    private static long countValidPermutations(String input, int[] checksums) {
        // if result is already available, just print it
        String checksumString = Arrays.toString(checksums);
        long cached = solutionCache.computeIfAbsent(input, ignored -> new HashMap<>()).getOrDefault(checksumString, Long.MIN_VALUE);
        if (cached != Long.MIN_VALUE) {
            return cached;
        }

        if (checksums.length == 0) {
            if (input.contains("#")) {
                return 0L;
            } else {
                return 1L;
            }
        }
        if (input.isEmpty()) {
            // empty input, but some remaining checksums
            return 0L;
        }

        // let's count then
        long result = 0L;

        int damageInRow = 0;
        boolean counting = false;

        inputLoop:
        for (int inputIndex = 0; inputIndex < input.length(); inputIndex++) {
            char currentChar = input.charAt(inputIndex);
            if (currentChar != '.') {
                if (currentChar == '#') {
                    // there is a fixed damaged spring in place
                    counting = true;
                }
                if (counting) {
                    damageInRow++;
                }
                // we have a # or a ?
                for (int damage = 0; damage < checksums[0] - damageInRow; damage++) {
                    if (inputIndex + damage >= input.length()) {
                        // out of input
                        break inputLoop;
                    }
                    currentChar = input.charAt(inputIndex + damage);
                    if (currentChar == '.') {
                        // checksum split
                        continue inputLoop;
                    }
                }
                int nextIndex = inputIndex + checksums[0] - damageInRow;
                if (nextIndex == input.length()) {
                    result++;
                    break;
                }
                currentChar = input.charAt(nextIndex);
                if (currentChar != '#') {
                    // we have reached the end and there is ? or . so, the checksum fits
                    // check the rest
                    result += countValidPermutations(input.substring(nextIndex + 1), subArray(checksums, 1));
                }
            } else if (counting) {
                // there was at least one fixed spring and we are out of checksum bounds
                // no more possibilities
                break;
            }
        }

        // cache the result
        solutionCache.get(input).put(checksumString, result);
        return result;
    }

    private static int[] subArray(int[] array, int startIndex) {
        if (startIndex > array.length) {
            return new int[0];
        }

        return Arrays.copyOfRange(array, startIndex, array.length);
    }

    private static List<char[]> generatePermutations(char[] input) {
        List<char[]> result = new ArrayList<>();

        Queue<char[]> toBeProcessed = new LinkedList<>();
        toBeProcessed.offer(input);

        processingLoop:
        while (!toBeProcessed.isEmpty()) {
            char[] processing = toBeProcessed.poll();

            for (int i = 0; i < processing.length; i++) {
                if (processing[i] == '?') {
                    char[] newArray = Arrays.copyOf(processing, processing.length);

                    // replace the char in one array
                    processing[i] = '.';
                    toBeProcessed.offer(processing);

                    // replace the char with other option
                    newArray[i] = '#';
                    toBeProcessed.offer(newArray);

                    continue processingLoop;
                }
            }

            // now the array does not contain any ?
            result.add(processing);
        }

        return result;
    }

    private static long newGenerationAndCounting(ConditionRow row) {
        return newGenerationAndCounting(row.records.toCharArray(), 0, row.checksums, 0);
    }

    private static long newGenerationAndCounting(char[] input, int inputIndex, int[] checksums, int checksumIndex) {
        if (checksumIndex == checksums.length) {
            // all checksums are done, check that no more damaged springs are present, count any questionable spaces as
            // one possibility only
            for (; inputIndex < input.length; inputIndex++) {
                if (input[inputIndex] == '#') {
                    return 0L;
                }
            }
            return 1L;
        }

        long result = 0L;

        bigLoop:
        for (; inputIndex < input.length; inputIndex++) {
            if (input[inputIndex] == '#' || input[inputIndex] == '?') {
                // starting a new checksum
                for (int i = 0; i < checksums[checksumIndex]; i++) {
                    if (inputIndex + i >= input.length || input[inputIndex + i] == '.') {
                        // bad sequence, continue
                        continue bigLoop;
                    }
                }
                int nextIndex = inputIndex + checksums[checksumIndex];
                if (nextIndex == input.length || // end of input
                        input[nextIndex] != '#'  // end of damaged springs
                ) {
                    result += newGenerationAndCounting(input, nextIndex + 1, checksums, checksumIndex + 1);
                }
            }

            if (input[inputIndex] == '#') {
                // if damaged spring was encountered, either it has been already counted or it will create a break in line
                break;
            }
        }

        return result;
    }

    private static long generateAndCountValidPermutations(String input, int[] checksums) {
        return generateAndCountValidPermutations(input, checksums, 0, checksums.length);
    }

    private static long generateAndCountValidPermutations(String input, int[] checksums, int checksumsFrom, int checksumsTo) {
        long result = 0L;
        if (input.isEmpty()) {
            return checksumsTo == checksumsFrom ? 1L : 0L;
        }

        int firstSplit = input.indexOf('.');
        if (firstSplit != -1) {
            // divide and conquer
            boolean foundSome = false;
            for (int i = 0; i <= (checksumsTo - checksumsFrom); i++) {
                long first = generateAndCountValidPermutations(input.substring(0, firstSplit), checksums, checksumsFrom, checksumsFrom + i);
                long second = 1L;
                if (first > 0L) {
                    foundSome = true;
                    second = generateAndCountValidPermutations(input.substring(firstSplit + 1), checksums, checksumsFrom + i, checksumsTo);
                } else if (foundSome) {
                    // already found some valid ones, no more now
                    break;
                }

                result += first * second;
            }
        } else {
            // bruteforce the rest
            result += generateAndCountValidPermutations(input.toCharArray(), Arrays.copyOfRange(checksums, checksumsFrom, checksumsTo));
        }

        //System.out.printf(", result: %s%n", result);
        return result;
    }

    private static long generateAndCountValidPermutations(char[] input, int[] checksums) {
        return generateAndCountValidPermutations(checksums, input, 0, Arrays.stream(checksums).sum(), 0, 0, false);
    }

    private static long generateAndCountValidPermutations(int[] checksums, char[] input,
                                                          int index, int maxDamaged, int checksumIndex, int damagedInRow, boolean counting) {
        for (; index < input.length; index++) {
            if (input[index] == '?') {
                long result = 0L;
                // try with .
                input[index] = '.';
                result += generateAndCountValidPermutations(checksums, input, index, maxDamaged, checksumIndex, damagedInRow, counting);

                if (maxDamaged > 0) {
                    // try with #
                    input[index] = '#';
                    result += generateAndCountValidPermutations(checksums, input, index, maxDamaged, checksumIndex, damagedInRow, counting);
                }

                // reset (for the next run)
                input[index] = '?';

                // no more looping
                return result;
            } else if (input[index] == '#') {
                counting = true;
                damagedInRow++;
                maxDamaged--;
            } else if (counting) {
                if (checksumIndex >= checksums.length || damagedInRow != checksums[checksumIndex]) {
                    // fails validation, no point in continuing
                    return 0L;
                }
                checksumIndex++;

                counting = false;
                damagedInRow = 0;
            }
        }

        // handle ending #
        if (counting) {
            if (checksumIndex >= checksums.length || damagedInRow != checksums[checksumIndex]) {
                // fails validation, no point in continuing
                return 0L;
            }
            checksumIndex++;
        }

        // if we reached here, there is a finished and valid sequence
        return checksumIndex == checksums.length ? 1L : 0L;
    }

    private static boolean validateWithChecksum(char[] line, int[] checksum) {
        List<Integer> calculatedChecksums = new ArrayList<>();

        int currentCount = 0;
        boolean counting = false;

        for (char character : line) {
            if (character == '#') {
                currentCount++;
                counting = true;
            } else if (counting) {
                calculatedChecksums.add(currentCount);

                counting = false;
                currentCount = 0;
            }
        }

        // count leftovers
        if (counting) {
            calculatedChecksums.add(currentCount);
        }

        if (calculatedChecksums.size() != checksum.length) {
            return false;
        } else {
            return IntStream.range(0, calculatedChecksums.size())
                    .mapToObj(i -> calculatedChecksums.get(i) == checksum[i])
                    .reduce(true, Boolean::logicalAnd);
        }
    }

    private static ConditionRow unfold(ConditionRow row) {
        String records = String.join("?", row.records, row.records, row.records, row.records, row.records);

        int[] checksums = new int[row.checksums.length * 5];

        for (int i = 0; i < checksums.length; i++) {
            checksums[i] = row.checksums[i % row.checksums.length];
        }

        return new ConditionRow(records, checksums);
    }

    private static int[] concat(int[] input, int times) {
        int[] result = new int[input.length * times];
        for (int i = 0; i < result.length; i++) {
            result[i] = input[i % input.length];
        }
        return result;
    }

    private record ConditionRow(String records, int[] checksums) {
    }
}
