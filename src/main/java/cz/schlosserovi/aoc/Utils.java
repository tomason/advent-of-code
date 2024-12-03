package cz.schlosserovi.aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Utils {
    private Utils() {
        // prevent instantiation
    }

    public static void processFile(String resource, Consumer<String> lineProcessor) {
        String absoluteResourceName = resource.startsWith("/") ? resource : ("/" + resource);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                Utils.class.getResourceAsStream(absoluteResourceName))))) {
            Stream.generate(() -> {
                        try {
                            return reader.readLine();
                        } catch (IOException ex) {
                            throw new RuntimeException("Could not read input file " + resource, ex);
                        }
                    })
                    .takeWhile(Objects::nonNull)
                    .forEach(lineProcessor);
        } catch (IOException ex) {
            throw new RuntimeException("Could not open resource " + resource, ex);
        }
    }

    public static <T>  List<T> processFile(String resource, Function<String, T> lineProcessor) {
        String absoluteResourceName = resource.startsWith("/") ? resource : ("/" + resource);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                Utils.class.getResourceAsStream(absoluteResourceName))))) {
            return Stream.generate(() -> {
                        try {
                            return reader.readLine();
                        } catch (IOException ex) {
                            throw new RuntimeException("Could not read input file " + resource, ex);
                        }
                    })
                    .takeWhile(Objects::nonNull)
                    .map(lineProcessor)
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException("Could not open resource " + resource, ex);
        }
    }

    public static List<String> readLines(String resource) {
        return processFile(resource, Function.identity());
    }

    public static int greatestCommonDivisor(int one, int two) {
        if (two > one) {
            // switch
            return greatestCommonDivisor(two, one);
        }

        // stopping condition
        if (two == 0) {
            return one;
        }
        return greatestCommonDivisor(two, one % two);
    }

    public static int greatestCommonDivisor(int... input) {
        if (input.length == 0) {
            throw new IllegalArgumentException("No input");
        }

        int result = input[0];
        for (int number : input) {
            result = greatestCommonDivisor(number, result);
        }

        return result;
    }
}
