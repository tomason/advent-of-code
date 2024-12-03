package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayTwo {
    private static final int RED_CUBES = 12;
    private static final int GREEN_CUBES = 13;
    private static final int BLUE_CUBES = 14;

    private static final Pattern RED_PATTERN = Pattern.compile("(\\d+) red");
    private static final Pattern GREEN_PATTERN = Pattern.compile("(\\d+) green");
    private static final Pattern BLUE_PATTERN = Pattern.compile("(\\d+) blue");

    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTwo.class.getResourceAsStream("/2023/day-2"))))) {
            String line;
            int sumOne = 0;
            int sumTwo = 0;
            while ((line = reader.readLine()) != null) {
                GameTriplet gameTriplet = parseLine(line);
                //System.out.println(gameTriplet);

                // part one
                if (isValid(gameTriplet)) {
                    sumOne += gameTriplet.id();
                }

                // part two
                sumTwo += (gameTriplet.red() * gameTriplet.green() * gameTriplet.blue());
            }

            System.out.println("Final value: " + sumOne);
            System.out.println("Final value (part two): " + sumTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static GameTriplet parseLine(String line) {
        int id = Integer.parseInt(line.substring(5, line.indexOf(':')));
        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;

        for (String showing : line.substring(line.indexOf(':')).split(";")) {
            Matcher redMatcher = RED_PATTERN.matcher(showing);
            if (redMatcher.find()) {
                maxRed = Math.max(maxRed, Integer.parseInt(redMatcher.group(1)));
            }
            Matcher greenMatcher = GREEN_PATTERN.matcher(showing);
            if (greenMatcher.find()) {
                maxGreen = Math.max(maxGreen, Integer.parseInt(greenMatcher.group(1)));
            }
            Matcher blueMatcher = BLUE_PATTERN.matcher(showing);
            if (blueMatcher.find()) {
                maxBlue = Math.max(maxBlue, Integer.parseInt(blueMatcher.group(1)));
            }
        }

        return new GameTriplet(id, maxRed, maxGreen, maxBlue);
    }

    private static boolean isValid(GameTriplet triplet) {
        return triplet.red() <= RED_CUBES && triplet.green() <= GREEN_CUBES && triplet.blue() <= BLUE_CUBES;
    }

    private record GameTriplet(int id, int red, int green, int blue) {
    }

}
