package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day19 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-19-1");

        List<String> availablePatterns = filterAvailablePatterns(lines.get(0));

        long starOne = 0L;

        for (int i = 2; i < lines.size(); i++) {
            String patternToFind = lines.get(i);

            if (canBuildPattern(patternToFind, availablePatterns)) {
                starOne++;
            }
        }

        // 338
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        List<String> unfilteredPatterns = List.of(lines.get(0).split(", "));

        for (int i = 2; i < lines.size(); i++) {
            String patternToFind = lines.get(i);

            long combinations = countPossibleCombinations(patternToFind, unfilteredPatterns);

            starTwo += combinations;
        }

        // 841533074412361
        System.out.println("Star two: " + starTwo);

    }

    private static boolean canBuildPattern(String pattern, List<String> availablePatterns) {
        if (availablePatterns.contains(pattern)) {
            return true;
        }

        for (String tryPattern : availablePatterns) {
            if (pattern.startsWith(tryPattern) && canBuildPattern(pattern.substring(tryPattern.length()), availablePatterns)) {
                // break early
                return true;
            }
        }

        return false;
    }

    private static List<String> filterAvailablePatterns(String input) {
        final List<String> result = new ArrayList<>();

        // sort by length and any pattern that can be made by previously accepted pattern is rejected
        Stream.of(input.split(", "))
                .sorted(Comparator.comparingInt(String::length))
                .forEach(pattern -> {
                    if (!canBuildPattern(pattern, result)) {
                        result.add(pattern);
                    }
                });

        return result;
    }

    private static final Map<String, Long> CACHE = new HashMap<>();
    private static long countPossibleCombinations(String pattern, Collection<String> availablePatterns) {
        if (pattern.isEmpty()) {
            return 1;
        }
        if (CACHE.containsKey(pattern)) {
            return CACHE.get(pattern);
        }

        long result = 0L;
        for (int i = 1; i < pattern.length() + 1; i++) {
            String prefix = pattern.substring(0, i);
            String suffix = pattern.substring(i);

            if (availablePatterns.contains(prefix)) {
                result += countPossibleCombinations(suffix, availablePatterns);
            }
        }
        CACHE.put(pattern, result);


        return result;
    }
}
