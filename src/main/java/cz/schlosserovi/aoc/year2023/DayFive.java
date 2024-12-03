package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class DayFive {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayFive.class.getResourceAsStream("/2023/day-5"))))) {
            final List<Long> seeds = new ArrayList<>();

            final RangeMap seedToSoil = new RangeMap();
            final RangeMap soilToFertilizer = new RangeMap();
            final RangeMap fertilizerToWater = new RangeMap();
            final RangeMap waterToLight = new RangeMap();
            final RangeMap lightToTemperature = new RangeMap();
            final RangeMap temperatureToHumidity = new RangeMap();
            final RangeMap humidityToLocation = new RangeMap();

            String line;
            RangeMap container = null;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    // empty line after category
                    container = null;
                } else if (container == null) {
                    // new category
                    container = switch (line.substring(0, line.indexOf(':'))) {
                        case "seeds":
                            seeds.addAll(parseSeeds(line));
                            yield null;
                        case "seed-to-soil map":
                            yield seedToSoil;
                        case "soil-to-fertilizer map":
                            yield soilToFertilizer;
                        case "fertilizer-to-water map":
                            yield fertilizerToWater;
                        case "water-to-light map":
                            yield waterToLight;
                        case "light-to-temperature map":
                            yield lightToTemperature;
                        case "temperature-to-humidity map":
                            yield temperatureToHumidity;
                        case "humidity-to-location map":
                            yield humidityToLocation;
                        default:
                            throw new RuntimeException("Unknown input " + line);
                    };
                } else {
                    // just the ranges
                    parseLine(line, container);
                }


            }

            // part one
            long one = seeds.stream()
                    .map(seedToSoil::map)
                    .map(soilToFertilizer::map)
                    .map(fertilizerToWater::map)
                    .map(waterToLight::map)
                    .map(lightToTemperature::map)
                    .map(temperatureToHumidity::map)
                    .map(humidityToLocation::map)
                    .min(Long::compareTo)
                    .orElseThrow();

            // part two
            Collection<SeedTuple> extendedSeeds = new HashSet<>();
            for (int i = 0; i < seeds.size(); i += 2) {
                long start = seeds.get(i);
                long range = seeds.get(i + 1);

                extendedSeeds.add(new SeedTuple(start, range));
            }
            System.out.println(LocalDateTime.now());
            long two = extendedSeeds.parallelStream()
                    .flatMapToLong(tuple -> LongStream.range(tuple.start, tuple.start + tuple.range))
                    .map(seedToSoil::map)
                    .map(soilToFertilizer::map)
                    .map(fertilizerToWater::map)
                    .map(waterToLight::map)
                    .map(lightToTemperature::map)
                    .map(temperatureToHumidity::map)
                    .map(humidityToLocation::map)
                    .min()
                    .orElseThrow();
            System.out.println(LocalDateTime.now());

            System.out.println("Final value: " + one);
            System.out.println("Final value (part two): " + two);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static Collection<Long> parseSeeds(String line) {
        String numbersOnly;
        if (line.startsWith("seeds: ")) {
            numbersOnly = line.substring(7);
        } else {
            numbersOnly = line;
        }

        return Arrays.stream(numbersOnly.split(" "))
                .filter(str -> !str.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private static void parseLine(String line, RangeMap container) {
        String[] split = line.split(" ");
        long destinationStart = Long.parseLong(split[0]);
        long sourceStart = Long.parseLong(split[1]);
        long range = Long.parseLong(split[2]);

        container.addRangeTriplet(new RangeTriplet(sourceStart, destinationStart, range));
    }

    private static class RangeMap {
        private final Collection<RangeTriplet> ranges = new HashSet<>();

        public void addRangeTriplet(RangeTriplet triplet) {
            ranges.add(triplet);
        }

        public long map(long source) {
            return ranges.stream()
                    // only take triplet that matches the source range
                    .filter(triplet -> source >= triplet.sourceStart && source < triplet.sourceStart + triplet.range)
                    // map the source to destination
                    .map(triplet -> (source - triplet.sourceStart) + triplet.destinationStart)
                    .findFirst()
                    // if no source was found, return the same
                    .orElse(source);
        }
    }

    private record RangeTriplet(long sourceStart, long destinationStart, long range) {
    }

    private record SeedTuple(long start, long range) {
    }
}
