package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DayEightPartTwo {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayEightPartTwo.class.getResourceAsStream("/2023/day-8"))))) {
            String line;

            String instructionsLine = reader.readLine();

            // skip line
            reader.readLine();

            Map<String, Pair> directions = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                parseLine(line, directions);
            }

            ExecutorService multithreading = Executors.newFixedThreadPool(7);
            Map<Long, AtomicInteger> results = new ConcurrentHashMap<>();

            System.out.println("Part two start: " + LocalDateTime.now());

            long stepsTwo = 0;

            System.out.println("Steps length: " + instructionsLine.length());

            List<DirectionFinder> currentPlaces = directions.keySet()
                    .parallelStream().filter(id -> id.endsWith("A"))
                    .peek(start -> {
                        int steps = 0;
                        String currentId = start;
                        StringBuilder result = new StringBuilder();

                        result.append("Start: ").append(start).append("\n");

                        while (steps < 1_000_000) {
                            char step = instructionsLine.charAt(steps % instructionsLine.length());

                            currentId = switch (step) {
                                case 'L' -> directions.get(currentId).left;
                                case 'R' -> directions.get(currentId).right;
                                default -> throw new RuntimeException("Invalid direction " + step);
                            };
                            steps++;

                            if (currentId.endsWith("Z")) {
                                result.append("  End: ").append(currentId).append(", steps: ").append(steps).append("\n");
                            }
                        }

                        System.out.println(result);
                    })
                    .map(start -> new DirectionFinder(start, instructionsLine, directions, results))
                    .peek(multithreading::execute)
                    .toList();
            System.out.println("Number of starting points: " + currentPlaces.size());

            while (stepsTwo == 0) {
                stepsTwo = results.entrySet().stream()
                        .filter(entry -> entry.getValue().get() == 5)
                        .mapToLong(Map.Entry::getKey)
                        .min().orElse(0L);
            }

            System.out.println("Part two end: " + LocalDateTime.now());

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

    private static class DirectionFinder implements Runnable {
        private boolean run = true;

        private String currentId;
        private final String instructions;
        private final Map<String, Pair> map;
        private final Map<Long, AtomicInteger> results;

        public DirectionFinder(String start, String instructions, Map<String, Pair> map, Map<Long, AtomicInteger> results) {
            this.instructions = instructions;
            this.map = map;
            this.results = results;

            currentId = start;
        }

        @Override
        public void run() {
            long steps = 0L;

            while (run && steps < Long.MAX_VALUE) {
                char step = instructions.charAt((int) (steps % instructions.length()));

                currentId = switch (step) {
                    case 'L' -> map.get(currentId).left;
                    case 'R' -> map.get(currentId).right;
                    default -> throw new RuntimeException("Invalid direction " + step);
                };

                steps++;

                if (currentId.endsWith("Z")) {
                    results.computeIfAbsent(steps, ignored -> new AtomicInteger(0)).incrementAndGet();
                }
            }
        }

        public void stop() {
            run = false;
        }
    }
}
