package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayTwentyFive {
    public static void main(String... args) {
        List<String> input = Utils.readLines("2023/day-25-example");

        Map<String, Collection<String>> connections = input.stream()
                .map(line -> line.split(":"))
                .map(split -> new AbstractMap.SimpleEntry<>(split[0], split[1].split(" ")))
                .flatMap(entry -> Arrays.stream(entry.getValue())
                        .map(String::trim)
                        .filter(value -> !value.isEmpty())
                        .flatMap(group -> Stream.of(new AbstractMap.SimpleEntry<>(entry.getKey(), Collections.singleton(group)), new AbstractMap.SimpleEntry<>(group, Collections.singleton(entry.getKey())))))
                .sorted(Comparator.<AbstractMap.SimpleEntry<String, Set<String>>>comparingInt(entry -> entry.getValue().size()).reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (collection1, collection2) -> Stream.concat(collection1.stream(), collection2.stream()).collect(Collectors.toSet())));

        connections.forEach((key, conn) -> System.out.printf("%s --> %s%n", key, conn));

        Collection<String> ungroupedNodes = new HashSet<>(connections.keySet());

    }
}
