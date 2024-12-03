package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DayEight {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayEight.class.getResourceAsStream("/2023/day-8"))))) {
            String line;

            String instructionsLine = reader.readLine();

            // skip line
            reader.readLine();

            Map<String, Pair> directions = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                parseLine(line, directions);
            }

            String currentId = "AAA";
            int stepsOne = 0;

            // part one
            while (!currentId.equals("ZZZ")) {
                char step = instructionsLine.charAt(stepsOne % instructionsLine.length());

                currentId = switch (step) {
                    case 'L' -> directions.get(currentId).left;
                    case 'R' -> directions.get(currentId).right;
                    default -> throw new RuntimeException("Invalid direction " + step);
                };

                stepsOne++;
            }

            System.out.println("Part two start: " + LocalDateTime.now());

            long stepsTwo = 0;

            List<String> currentPlaces = directions.keySet()
                    .stream().filter(id -> id.endsWith("A"))
                    .toList();
            System.out.println("Number of starting points: " + currentPlaces.size());

            final AtomicInteger zCount = new AtomicInteger(0);

            while (zCount.get() != currentPlaces.size()) {
                char step = instructionsLine.charAt((int) (stepsTwo % instructionsLine.length()));

                Function<String, String> directionMapper = switch (step) {
                    case 'L' -> (current) -> directions.get(current).left;
                    case 'R' -> (current) -> directions.get(current).right;
                    default -> throw new RuntimeException("Invalid direction " + step);
                };

                zCount.set(0);
                currentPlaces = currentPlaces.stream()
                        .map(directionMapper)
                        .peek(newDirection -> {
                            if (newDirection.endsWith("Z")) {
                                zCount.incrementAndGet();
                            }
                        })
                        .toList();

                stepsTwo = Math.incrementExact(stepsTwo);
            }

            System.out.println("Part two end: " + LocalDateTime.now());

            System.out.println("Final value: " + stepsOne);
            System.out.println("Final value (part two): " + stepsTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static void parseLine(String line, Map<String, Pair> container) {
        String id = line.substring(0, 3);
        String left = line.substring(7, 10);
        String right = line.substring(12, 15);

        container.put(id, new Pair(left, right));
    }

    private record Pair(String left, String right) {
    }
}
