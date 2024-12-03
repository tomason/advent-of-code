package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DayThirteen {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTen.class.getResourceAsStream("/2023/day-13"))))) {

            List<String> pattern = new ArrayList<>();
            Collection<RockPattern> puzzleInput = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    puzzleInput.add(parseInput(pattern));
                    pattern.clear();
                } else {
                    pattern.add(line);
                }
            }
            // parse also the last input
            if (!pattern.isEmpty()) {
                puzzleInput.add(parseInput(pattern));
            }

            int one = 0;
            int two = 0;
            int index = 1;
            for (RockPattern puzzle : puzzleInput) {
                Map<Integer, Integer> vertical = findVerticalReflection(puzzle);
                Map<Integer, Integer> horizontal = findHorizontalReflection(puzzle);
                one += vertical.getOrDefault(0, 0) + 100 * horizontal.getOrDefault(0, 0);
                two += vertical.getOrDefault(1, 0) + 100 * horizontal.getOrDefault(1, 0);

                System.out.println("Index: " + index++ + ", vetical: " + vertical + ", horizontal: " + horizontal + ", one: " + one +", two: " + two);
            }


            System.out.println("First part result: " + one);
            System.out.println("Second part result: " + two);

        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static RockPattern parseInput(List<String> input) {
        char[][] parsedPattern = new char[input.size()][];

        for (int i = 0; i < parsedPattern.length; i++) {
            parsedPattern[i] = input.get(i).toCharArray();
        }

        return new RockPattern(parsedPattern);
    }

    private static Map<Integer, Integer> findVerticalReflection(RockPattern puzzle) {
        Map<Integer, Integer> result = new HashMap<>();

        // loop through all the columns
        for (int col = 1; col < puzzle.columns; col++) {
            int numberOfErrors = 0;
            // check by distance all the columns
            for (int i = 1; i < puzzle.columns; i++) {
                if (col - i < 0 || col + i > puzzle.columns) {
                    // got to the boundaries, ignore the rest
                    break;
                }
                // check that all the rows match

                for (int row = 0; row < puzzle.rows; row++) {
                    if (puzzle.pattern[row][col - i] != puzzle.pattern[row][col + i - 1]) {
                        numberOfErrors++;
                    }
                }
            }
            result.put(numberOfErrors, col);
            //System.out.println("  column: " + col + ", reflectionErrors: " + numberOfErrors);
        }

        return result;
    }

    private static Map<Integer, Integer> findHorizontalReflection(RockPattern puzzle) {
        Map<Integer, Integer> result = new HashMap<>();
        // loop through all the rows
        for (int row = 1; row < puzzle.rows; row++) {
            int numberOfErrors = 0;
            // check by distance all the rows
            for (int i = 1; i < puzzle.rows; i++) {
                if (row - i < 0 || row + i > puzzle.rows) {
                    // got to the boundaries, ignore the rest
                    break;
                }

                // check that all the columns match
                for (int col = 0; col < puzzle.columns; col++) {
                    if (puzzle.pattern[row - i][col] != puzzle.pattern[row + i - 1][col]) {
                        numberOfErrors++;
                    }
                }
            }
            result.put(numberOfErrors, row);
            //System.out.println("  row: " + row + ", reflectionErrors: " + numberOfErrors);
        }

        return result;
    }

    private record RockPattern(char[][] pattern, int rows, int columns) {
        public RockPattern(char[][] pattern) {
            this(pattern, pattern.length, pattern[0].length);
        }
    }
}
