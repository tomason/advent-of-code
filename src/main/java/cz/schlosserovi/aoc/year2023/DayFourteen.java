package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public class DayFourteen {
    public static void main(String... args) {
        PlatformMap input;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayFourteen.class.getResourceAsStream("/2023/day-14"))))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            char[][] map = new char[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                map[i] = lines.get(i).toCharArray();
            }
            input = new PlatformMap(map);
        } catch (IOException ex) {
            System.out.println("Could not load map " + ex);
            return;
        }

        // part one
        input.tiltNorth();
        int one = input.calculateNorthLoad();
        System.out.println("Result of part one: " + one);

        // part two
        // first finish cycle 1
        input.tiltWest();
        input.tiltSouth();
        input.tiltEast();

        // TODO is there a nicer way to find the loop?
        List<Integer> results = new LinkedList<>();
        Map<Integer, List<Integer>> possibleLoops = new HashMap<>();

        // now the remaining cycles
        int two = 0;
        billionLoop: for (int i = 1; i < 1_000_000_000; i++) {
            int northLoad = input.calculateNorthLoad();
            results.add(northLoad);

            if (possibleLoops.getOrDefault(northLoad, Collections.emptyList()).size() > 1) {
                // there are at least two occurrences, let's check numbers in between
                List<Integer> indices = possibleLoops.get(northLoad);
                int start = indices.get(0);
                int end = indices.get(1);
                boolean found = true;
                for (int diff = 0; diff < end - start; diff++) {
                    if (!Objects.equals(results.get(start + diff),results.get(end + diff))) {
                        // nice try, but this is not the loop you are looking for
                        indices.remove(0);
                        found = false;
                        break;
                    }
                }
                if (found) {
                    int cycleSize = end - start;
                    // found a cycle, yay!
                    two = results.get(start + (1_000_000_000 - start) % cycleSize);
                    break billionLoop;
                }
            } else {
                possibleLoops.computeIfAbsent(northLoad, ignored -> new LinkedList<>()).add(i);
            }

            input.spinCycle();
            two = northLoad;
        }

        // now just calculate the final value
        System.out.println("Result of part two: " + two);
    }

    private record PlatformMap(char[][] map) {
        public int calculateNorthLoad() {
            int result = 0;

            for (int row = 0; row < map.length; row++) {
                for (int col = 0; col < map[row].length; col++) {
                    if (map[row][col] == 'O') {
                        result += map.length - row;
                    }
                }
            }

            return result;
        }

        public void spinCycle() {
            tiltNorth();
            tiltWest();
            tiltSouth();
            tiltEast();
        }

        public void tiltNorth() {
            for (int col = 0; col < map[0].length; col++) {
                Queue<Integer> freeIndices = new LinkedList<>();
                for (int row = 0; row < map.length; row++) {
                    switch (map[row][col]) {
                        case '.' -> freeIndices.offer(row);
                        case '#' -> freeIndices.clear();
                        case 'O' -> {
                            if (!freeIndices.isEmpty()) {
                                map[freeIndices.poll()][col] = 'O';
                                map[row][col] = '.';
                                freeIndices.offer(row);
                            }
                        }
                    }
                }
            }
        }

        public void tiltWest() {
            for (int row = 0; row < map.length; row++) {
                Queue<Integer> freeIndices = new LinkedList<>();
                for (int col = 0; col < map[row].length; col++) {
                    switch (map[row][col]) {
                        case '.' -> freeIndices.offer(col);
                        case '#' -> freeIndices.clear();
                        case 'O' -> {
                            if (!freeIndices.isEmpty()) {
                                map[row][freeIndices.poll()] = 'O';
                                map[row][col] = '.';
                                freeIndices.offer(col);
                            }
                        }
                    }
                }
            }
        }

        public void tiltSouth() {
            for (int col = 0; col < map[0].length; col++) {
                Queue<Integer> freeIndices = new LinkedList<>();
                for (int row = map.length - 1; row >= 0; row--) {
                    switch (map[row][col]) {
                        case '.' -> freeIndices.offer(row);
                        case '#' -> freeIndices.clear();
                        case 'O' -> {
                            if (!freeIndices.isEmpty()) {
                                map[freeIndices.poll()][col] = 'O';
                                map[row][col] = '.';
                                freeIndices.offer(row);
                            }
                        }
                    }
                }
            }
        }

        public void tiltEast() {
            for (int row = 0; row < map.length; row++) {
                Queue<Integer> freeIndices = new LinkedList<>();
                for (int col = map[row].length - 1; col >= 0; col--) {
                    switch (map[row][col]) {
                        case '.' -> freeIndices.offer(col);
                        case '#' -> freeIndices.clear();
                        case 'O' -> {
                            if (!freeIndices.isEmpty()) {
                                map[row][freeIndices.poll()] = 'O';
                                map[row][col] = '.';
                                freeIndices.offer(col);
                            }
                        }
                    }
                }
            }
        }
    }
}
