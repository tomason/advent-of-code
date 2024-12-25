package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Day24 {
    private static final Pattern LOGICAL_GATE_PATTERN = Pattern.compile("(\\w+) (AND|OR|XOR) (\\w+) -> (\\w+)");

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-24-1");

        Map<String, Boolean> initialValues = new HashMap<>();
        Set<LogicalGate> logicalGates = new HashSet<>();

        boolean parsingInitialValues = true;
        for (String line : lines) {
            if (parsingInitialValues) {
                if (line.isBlank()) {
                    parsingInitialValues = false;
                    continue;
                }
                String[] split = line.split(": ");
                initialValues.put(split[0], "1".equals(split[1]));
            } else {
                Matcher matcher = LOGICAL_GATE_PATTERN.matcher(line);
                if (matcher.find()) {
                    BiFunction<Boolean, Boolean, Boolean> operation = switch (matcher.group(2)) {
                        case "AND" -> Boolean::logicalAnd;
                        case "OR" -> Boolean::logicalOr;
                        case "XOR" -> Boolean::logicalXor;
                        default -> throw new IllegalArgumentException("UnknownError operation " + matcher.group(2));
                    };
                    logicalGates.add(new LogicalGate(matcher.group(1), matcher.group(3), matcher.group(4), operation));
                }
            }
        }

        long starOne = 0L;

        Map<String, Boolean> output = simulateSystem(initialValues, logicalGates);

        starOne = buildNumber("z", output);

        // 55114892239566
        System.out.println("Star one: " + starOne);

        String starTwo = "";

        // never found a good solution to the second part, in the end I manually sorted the logical gates
        // in the input file to find the faulty ones

        // cdj,dhm,gfm,mrb,qjd,z08,z16,z32
        System.out.println("Star two: " + starTwo);
    }

    private static Map<String, Boolean> simulateSystem(Map<String, Boolean> initialState, Set<LogicalGate> logicalGates) {
        final Map<String, Boolean> result = new HashMap<>(initialState);
        final Map<String, Set<LogicalGate>> outputMapping = mapOutputsToLogicalGates(logicalGates);

        List<String> wiresToProcess = new ArrayList<>(initialState.keySet());

        while (!wiresToProcess.isEmpty()) {
            String wire = wiresToProcess.removeFirst();
            outputMapping.getOrDefault(wire, Set.of()).forEach(logicalGate -> {
                // if values are not yet present, they will be processed later with the other input
                if (result.containsKey(logicalGate.input1) && result.containsKey(logicalGate.input2)) {
                    result.put(logicalGate.output, logicalGate.operator.apply(
                            result.get(logicalGate.input1),
                            result.get(logicalGate.input2)));
                    wiresToProcess.add(logicalGate.output);
                }
            });
        }

        return result;
    }

    private static Map<String, Set<LogicalGate>> mapOutputsToLogicalGates(Set<LogicalGate> logicalGates) {
        final Map<String, Set<LogicalGate>> outputMapping = new HashMap<>();

        logicalGates.forEach(logicalGate -> {
            outputMapping.computeIfAbsent(logicalGate.input1, ignored -> new HashSet<>())
                    .add(logicalGate);
            outputMapping.computeIfAbsent(logicalGate.input2, ignored -> new HashSet<>())
                    .add(logicalGate);
        });

        return outputMapping;
    }

    private static long buildNumber(String keyPrefix, Map<String, Boolean> state) {
        String outputNumber = state.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(keyPrefix))
                .sorted(Map.Entry.<String, Boolean>comparingByKey().reversed())
                .map(Map.Entry::getValue)
                .map(bool -> bool ? "1" : "0")
                .collect(Collectors.joining());


        return Long.parseLong(outputNumber, 2);
    }

    private record LogicalGate(String input1, String input2, String output,
                               BiFunction<Boolean, Boolean, Boolean> operator) {
    }
}
