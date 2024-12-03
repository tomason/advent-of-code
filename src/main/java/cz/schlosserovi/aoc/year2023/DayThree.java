package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DayThree {
    private static final Collection<Coordinates> symbols = new ArrayList<>();
    private static final Map<Coordinates, List<Integer>> gears = new HashMap<>();

    public static void main(String... args) {
        List<String> lines = new ArrayList<>();


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayThree.class.getResourceAsStream("/2023/day-3"))))) {
            String line;
            int rowNumber = 0;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                readSymbols(line, rowNumber);
                rowNumber++;
            }

            int sumOne = 0;

            rowNumber = 0;
            for (String line2 : lines) {
                // part one
                sumOne += readNumbers(line2, rowNumber);
                rowNumber++;
            }

            int sumTwo = gears.values().stream()
                    .filter(integers -> integers.size() == 2)
                    .map(list -> list.get(0) * list.get(1))
                    .reduce(0, Integer::sum);

            System.out.println("Final value: " + sumOne);
            System.out.println("Final value (part two): " + sumTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static void readSymbols(String line, int rowNumber) {
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '.' || Character.isDigit(character)) {
                // not a symbol
                continue;
            }
            Coordinates coordinates = new Coordinates(rowNumber, i);
            symbols.add(coordinates);
            if (character == '*') {
                gears.put(coordinates, new ArrayList<>());
            }
        }
    }

    private static int readNumbers(String line, int rowNumber) {
        int lineSum = 0;

        boolean parsingNumber = false;
        int parsedNumber = 0;
        Set<Coordinates> neighborhood = new HashSet<>();

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (Character.isDigit(character)) {
                parsingNumber = true;
                parsedNumber = parsedNumber * 10 + Character.digit(character, 10);

                // add all possible symbol locations
                neighborhood.add(new Coordinates(rowNumber - 1, i - 1));
                neighborhood.add(new Coordinates(rowNumber - 1, i));
                neighborhood.add(new Coordinates(rowNumber - 1, i + 1));
                neighborhood.add(new Coordinates(rowNumber, i - 1));
                neighborhood.add(new Coordinates(rowNumber, i + 1));
                neighborhood.add(new Coordinates(rowNumber + 1, i - 1));
                neighborhood.add(new Coordinates(rowNumber + 1, i));
                neighborhood.add(new Coordinates(rowNumber + 1, i + 1));
            } else if (parsingNumber) {
                lineSum += handleParsedValue(parsedNumber, neighborhood);

                // reset temporary variables
                parsingNumber = false;
                parsedNumber = 0;
                neighborhood.clear();
            }
        }

        if (parsingNumber) {
            // digit at the end of line, handle leftovers
            lineSum += handleParsedValue(parsedNumber, neighborhood);
        }

        return lineSum;
    }

    private static int handleParsedValue(final int number, Collection<Coordinates> neighbors) {
        gears.entrySet().stream()
                .filter(entry -> neighbors.contains(entry.getKey()))
                .forEach(entry -> entry.getValue().add(number));

        if (neighbors.stream().anyMatch(symbols::contains)) {
            return number;
        } else {
            return 0;
        }
    }

    private record Coordinates(int row, int column) {
    }
}
