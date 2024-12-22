package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    private static final Map<String, List<String>> PROGRESSION_CACHE = new HashMap<>();

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-11-1");

        List<String> initialStones = Stream.of(lines.get(0).split(" ")).toList();

        long starOne = 0L;

        Map<String, Long> stoneMap = initialStones.stream()
                .collect(Collectors.toMap(Function.identity(), ignored -> 1L));

        for (int i = 0; i < 25; i++) {
            stoneMap = blink(stoneMap);
        }

        starOne = stoneMap.values().stream()
                .mapToLong(count -> count)
                .sum();

        // 183620
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        for (int i = 0; i < 50; i++) {
            stoneMap = blink(stoneMap);
        }

        starTwo = stoneMap.values().stream()
                .mapToLong(count -> count)
                .sum();

        // 220377651399268
        System.out.println("Star two: " + starTwo);

    }

    private static Map<String, Long> blink(Map<String, Long> stones) {
        return stones.entrySet().stream()
                .flatMap(entry -> blink(entry.getKey()).map(newStone -> Map.entry(newStone, entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }

    private static Stream<String> blink(String stone) {
        return PROGRESSION_CACHE.computeIfAbsent(stone, untouchedStone -> {
            if ("0".equals(untouchedStone)) {
                return List.of("1");
            } else if (untouchedStone.length() % 2 == 0) {
                return List.of(
                        untouchedStone.substring(0, untouchedStone.length() / 2),
                        Long.toString(Long.parseLong(untouchedStone.substring(untouchedStone.length() / 2)))
                );
            } else {
                return List.of(Long.toString(Long.parseLong(untouchedStone) * 2024L));
            }
        }).stream();
    }
}
