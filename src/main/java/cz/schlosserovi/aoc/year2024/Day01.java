package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day01 {
    public static void main(String... args) {
        final List<Integer> left = new ArrayList<>();
        final List<Integer> right = new ArrayList<>();

        Utils.processFile("/2024/day-01-1", line -> {
            String[] values = line.split(" ", 2);
            left.add(Integer.parseInt(values[0].trim()));
            right.add(Integer.parseInt(values[1].trim()));
        });

        Collections.sort(left);
        Collections.sort(right);

        long result = IntStream.range(0, left.size())
                .mapToLong(i -> Math.abs(left.get(i) - right.get(i)))
                .sum();

        System.out.println("Star one: " + result);

        final Map<Integer, Integer> appearances = new HashMap<>();
        right.forEach(number -> appearances.merge(number, 1, Integer::sum));

        long second = left.stream()
                .mapToLong(number -> (long) number * appearances.getOrDefault(number, 0))
                .sum();

        System.out.println("Star two: " + second);
    }
}
