package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class DayOne {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayOne.class.getResourceAsStream("/2023/day-1"))))) {
            String line;
            int sumOne = 0;
            int sumTwo = 0;
            while ((line = reader.readLine()) != null) {
                // part one
                sumOne += parseLine(line);

                // part two
                String modified = modifyInput(line);
                sumTwo += parseLine(modified);
            }

            System.out.println("Final value: " + sumOne);
            System.out.println("Final value (part two): " + sumTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static int parseLine(String line) {
        int first = Integer.MIN_VALUE;
        int last = 0;

        for (char character : line.toCharArray()) {
            if (Character.isDigit(character)) {
                if (first == Integer.MIN_VALUE) {
                    first = Character.digit(character, 10);
                }
                last = Character.digit(character, 10);
            }
        }

        return first * 10 + last;
    }

    private static String modifyInput(String line) {
        // replace words with numbers (but keep the additional letters for cases like twone --> 21)
        return line
                .replace("one", "one1one")
                .replace("two", "two2two")
                .replace("three", "three3three")
                .replace("four", "four4four")
                .replace("five", "five5five")
                .replace("six", "six6six")
                .replace("seven", "seven7seven")
                .replace("eight", "eight8eight")
                .replace("nine", "nine9nine");
    }
}
