package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DaySix {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DaySix.class.getResourceAsStream("/2023/day-6"))))) {
            reader.mark(1_000);
            final Collection<Race> races = parseRaces(reader.readLine(), reader.readLine());

            // part one
            /* original idea, worked fine for small numbers
            int one = races.stream()
                    .map(race -> IntStream.range(0, race.time())
                                    .map(buttonPushTime -> buttonPushTime * (race.time() - buttonPushTime))
                                    .filter(distanceTravelled -> distanceTravelled > race.distance())
                                    .count()
                            race.time() + 1 - 2 * findFirstWinningTime(race))
                    .reduce(1L, Math::multiplyExact);
             */
            long one = races.stream()
                    // include 0 in the race time, then find first winning race and subtract this index from both ends
                    // of allotted time
                    .map(race -> race.time() + 1 - 2 * findFirstWinningTime(race))
                    .reduce(1L, Math::multiplyExact);


            // reread the file
            reader.reset();
            final Race singleRace = parseSingleRace(reader.readLine(), reader.readLine());
            long two = singleRace.time() + 1 - 2 * findFirstWinningTime(singleRace);

            // Final value: 781200
            // Final value (part two): 49240091
            System.out.println("Final value: " + one);
            System.out.println("Final value (part two): " + two);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static Collection<Race> parseRaces(String timesLine, String distanceLine) {
        List<Integer> times = splitLine(timesLine);
        List<Integer> distances = splitLine(distanceLine);

        return IntStream.range(0, times.size())
                .mapToObj(i -> new Race(times.get(i), distances.get(i)))
                .collect(Collectors.toList());
    }

    private static Race parseSingleRace(String timeLine, String distanceLine) {
        return new Race(mergeLine(timeLine), mergeLine(distanceLine));
    }

    private static List<Integer> splitLine(String line) {
        return Arrays.stream(line.substring(line.indexOf(':') + 1).split(" "))
                .filter(str -> !str.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static long mergeLine(String line) {
        return Long.parseLong(line.substring(line.indexOf(':') + 1).replace(" ", ""));
    }

    private static long findFirstWinningTime(Race race) {
        for (long i = 0; i < race.time(); i++) {
            if (i * (race.time() - i) > race.distance()) {
                return i;
            }
        }

        throw new RuntimeException("This race can't be won " + race);
    }

    private record Race(long time, long distance) {
    }

}
