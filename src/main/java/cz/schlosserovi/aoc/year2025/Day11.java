package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Day11 {
    static void main() {
        var lines = Utils.readLines("/2025/day-11-1");

        var connectionMap = lines.stream()
            .map(line -> {
                var split = line.split(":");
                return Map.entry(split[0], Set.of(split[1].trim().split(" ")));
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var one = 0L;

        one = numberOfPaths("you", "out", connectionMap);

        // 423
        System.out.println("Part one: " + one);

        var two = 0L;

        two = numberOfPaths("svr", "out", Set.of("dac", "fft"), connectionMap);

        // 333657640517376
        System.out.println("Part two: " + two);
    }

    private static final Map<String, Long> PATH_COUNT_CACHE = new ConcurrentHashMap<>();
    private static long numberOfPaths(String start, String end, Map<String, Set<String>> connections) {
        if (PATH_COUNT_CACHE.containsKey(start)) {
            return PATH_COUNT_CACHE.get(start);
        }

        if (start.equals(end)) {
            return 1L;
        }

        var count = 0L;

        for (String connection : connections.get(start)) {
            count += numberOfPaths(connection, end, connections);
        }

        PATH_COUNT_CACHE.put(start, count);
        return count;
    }

    private static final Map<String, Map<Set<String>, Long>> RESTRICTED_PATH_COUNT_CACHE = new ConcurrentHashMap<>();
    private static long numberOfPaths(String start, String end, Set<String> mandatoryNodes, Map<String, Set<String>> connections) {
        if (RESTRICTED_PATH_COUNT_CACHE.containsKey(start)) {
            var innerCache = RESTRICTED_PATH_COUNT_CACHE.get(start);
            if (innerCache.containsKey(mandatoryNodes)) {
                return innerCache.get(mandatoryNodes);
            }
        }

        if (start.equals(end)) {
            return mandatoryNodes.isEmpty() ? 1L : 0L;
        }

        Set<String> remainingMandatoryNodes;
        if (mandatoryNodes.contains(start)) {
            remainingMandatoryNodes = new HashSet<>(mandatoryNodes);
            remainingMandatoryNodes.remove(start);
        } else {
            remainingMandatoryNodes = mandatoryNodes;
        }

        var count = 0L;

        for (String connection : connections.get(start)) {
            count += numberOfPaths(connection, end, remainingMandatoryNodes, connections);
        }

        RESTRICTED_PATH_COUNT_CACHE.computeIfAbsent(start, i -> new HashMap<>()).put(mandatoryNodes, count);
        return count;
    }
}