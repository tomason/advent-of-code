package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-21-1");

        long starOne = 0L;

        for (String input : lines) {
            String finalSequence = numericKeypadInstructions(input).stream()
                    //.peek(first -> System.out.printf("First robot:  %75s%n", first))
                    .map(Day21::directionalKeypadInstructions)
                    //.peek(second -> System.out.printf("Second robot: %75s%n", second))
                    .map(Day21::directionalKeypadInstructions)
                    //.peek(me -> System.out.printf("Me:           %75s%n", me))
                    .min(Comparator.comparingInt(String::length))
                    .orElseThrow();

            int complexity = finalSequence.length() * Integer.parseInt(input.substring(0, input.length() - 1));
            System.out.printf("Input: %s, Sequence=%75s, length=%s, complexity=%5d%n",
                    input, finalSequence, finalSequence.length(), complexity);

            starOne += complexity;
        }


        // 94426
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        for (String input : lines) {
            Stream<Map<String, Long>> stream;

            stream = numericKeypadInstructionsInParts(input).stream()
                    .map(Day21::directionalKeypadInstructionsInParts);

            for (int i = 1; i < 25; i++) {
                stream = stream.map(Day21::directionalKeypadInstructionsInParts);
            }

            long finalSequenceLength = stream.mapToLong(finalMap -> finalMap.entrySet().stream()
                            .mapToLong(entry -> entry.getKey().length() * entry.getValue()).sum())
                    .min()
                    .orElseThrow();

            long complexity = finalSequenceLength * Integer.parseInt(input.substring(0, input.length() - 1));

            starTwo += complexity;
        }

        System.out.println(TRANSLATION_CACHE);

        // 118392478819140
        System.out.println("Star two: " + starTwo);

    }

    private static Set<List<String>> numericKeypadInstructionsInParts(String input) {
        NumericKey position = NumericKey.KEY_A;

        Set<List<String>> result = new HashSet<>();
        result.add(List.of());

        for (char digit : input.toCharArray()) {
            NumericKey key = NumericKey.forInput(digit);
            int dRow = position.row - key.row;
            int dCol = position.col - key.col;

            Set<List<String>> possibilities = new HashSet<>();
            String horizontalPart = "";
            String verticalPart = "";

            while (dRow > 0) {
                verticalPart += "^";
                dRow--;
            }
            while (dRow < 0) {
                verticalPart += "v";
                dRow++;
            }
            while (dCol > 0) {
                horizontalPart += "<";
                dCol--;
            }
            while (dCol < 0) {
                horizontalPart += ">";
                dCol++;
            }

            for (List<String> previous : result) {
                if (position.col != 1 || position.row + verticalPart.length() != 4) {
                    possibilities.add(Stream.concat(previous.stream(), Stream.of(verticalPart + horizontalPart + "A")).toList());
                }

                if (position.row != 4 || position.col - horizontalPart.length() != 1) {
                    possibilities.add(Stream.concat(previous.stream(), Stream.of(horizontalPart + verticalPart + "A")).toList());
                }
            }

            result.clear();
            result.addAll(possibilities);

            position = key;
        }

        return result;
    }

    private static Map<String, Long> directionalKeypadInstructionsInParts(List<String> input) {
        return directionalKeypadInstructionsInParts(input.stream().collect(Collectors.toMap(Function.identity(), ignored -> 1L, Long::sum)));
    }

    private static final Map<String, List<String>> TRANSLATION_CACHE = new HashMap<>();

    private static Map<String, Long> directionalKeypadInstructionsInParts(Map<String, Long> input) {
        Map<String, Long> result = new HashMap<>();

        for (Map.Entry<String, Long> entry : input.entrySet()) {
            List<String> outputs = TRANSLATION_CACHE.computeIfAbsent(entry.getKey(),
                    Day21::directionalKeypadInstructionsInParts);
            for (String outputPart : outputs) {
                result.merge(outputPart, entry.getValue(), Math::addExact);
            }
        }

        return result;
    }

    private static List<String> directionalKeypadInstructionsInParts(String input) {
        List<String> result = new ArrayList<>();

        DirectionalKey position = DirectionalKey.KEY_A;

        for (char digit : input.toCharArray()) {
            StringBuilder part = new StringBuilder();
            DirectionalKey key = DirectionalKey.forInput(digit);
            int dRow = position.row - key.row;
            int dCol = position.col - key.col;

            if (position.col == 1) {
                while (dCol < 0) {
                    part.append('>');
                    dCol++;
                }
                while (dRow > 0) {
                    part.append('^');
                    dRow--;
                }
            } else if (key.col == 1) {
                while (dRow < 0) {
                    part.append('v');
                    dRow++;
                }
                while (dCol > 0) {
                    part.append('<');
                    dCol--;
                }
            } else {
                while (dCol > 0) {
                    part.append('<');
                    dCol--;
                }
                while (dRow > 0) {
                    part.append('^');
                    dRow--;
                }
                while (dRow < 0) {
                    part.append('v');
                    dRow++;
                }
                while (dCol < 0) {
                    part.append('>');
                    dCol++;
                }
            }

            part.append('A');
            position = key;

            result.add(part.toString());
        }

        return result;
    }

    private static Set<String> numericKeypadInstructions(String input) {
        return numericKeypadInstructionsInParts(input).stream().map(parts -> String.join("", parts)).collect(Collectors.toSet());
        /*
        NumericKey position = NumericKey.KEY_A;

        Set<String> result = new HashSet<>();
        result.add("");

        for (char digit : input.toCharArray()) {
            NumericKey key = NumericKey.forInput(digit);
            int dRow = position.row - key.row;
            int dCol = position.col - key.col;

            Set<String> possibilities = new HashSet<>();
            String horizontalPart = "";
            String verticalPart = "";

            while (dRow > 0) {
                verticalPart += "^";
                dRow--;
            }
            while (dRow < 0) {
                verticalPart += "v";
                dRow++;
            }
            while (dCol > 0) {
                horizontalPart += "<";
                dCol--;
            }
            while (dCol < 0) {
                horizontalPart += ">";
                dCol++;
            }

            for (String previous : result) {
                if (position.col != 1 || position.row + verticalPart.length() != 4) {
                    possibilities.add(previous + verticalPart + horizontalPart + "A");
                }

                if (position.row != 4 || position.col - horizontalPart.length() != 1) {
                    possibilities.add(previous + horizontalPart + verticalPart + "A");
                }
            }

            result.clear();
            result.addAll(possibilities);

            position = key;
        }

        return result;
         */
    }

    private static String directionalKeypadInstructions(String input) {
        return String.join("", directionalKeypadInstructionsInParts(input));
        /*
        StringBuilder result = new StringBuilder();
        DirectionalKey position = DirectionalKey.KEY_A;

        for (char digit : input.toCharArray()) {
            DirectionalKey key = DirectionalKey.forInput(digit);
            int dRow = position.row - key.row;
            int dCol = position.col - key.col;


            while (dCol < 0) {
                result.append('>');
                dCol++;
            }
            while (dRow < 0) {
                result.append('v');
                dRow++;
            }
            while (dCol > 0) {
                result.append('<');
                dCol--;
            }
            while (dRow > 0) {
                result.append('^');
                dRow--;
            }

            result.append('A');
            position = key;
        }
        return result.toString();
        */
    }

    private enum NumericKey {
        KEY_7('7', 1, 1),
        KEY_8('8', 1, 2),
        KEY_9('9', 1, 3),
        KEY_4('4', 2, 1),
        KEY_5('5', 2, 2),
        KEY_6('6', 2, 3),
        KEY_1('1', 3, 1),
        KEY_2('2', 3, 2),
        KEY_3('3', 3, 3),
        KEY_0('0', 4, 2),
        KEY_A('A', 4, 3);

        private final char digit;
        public final int row;
        public final int col;

        NumericKey(char digit, int row, int col) {
            this.digit = digit;
            this.row = row;
            this.col = col;
        }

        public static NumericKey forInput(char digit) {
            return Stream.of(values()).filter(key -> key.digit == digit)
                    .findFirst().orElseThrow();
        }
    }

    private enum DirectionalKey {
        KEY_UP('^', 1, 2),
        KEY_A('A', 1, 3),
        KEY_LEFT('<', 2, 1),
        KEY_DOWN('v', 2, 2),
        KEY_RIGHT('>', 2, 3);

        private final char digit;
        public final int row;
        public final int col;

        DirectionalKey(char digit, int row, int col) {
            this.digit = digit;
            this.row = row;
            this.col = col;
        }

        public static DirectionalKey forInput(char digit) {
            return Stream.of(values()).filter(key -> key.digit == digit)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown directional key " + digit));
        }
    }
}
