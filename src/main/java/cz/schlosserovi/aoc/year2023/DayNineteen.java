package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayNineteen {

    public static void main(String[] args) {
        List<String> lines = Utils.readLines("2023/day-19");

        Map<String, Workflow> workflows = new HashMap<>();
        Map<Part, String> partWorkflowMapping = new HashMap<>();

        Queue<Part> partsToProcess = new LinkedList<>();
        boolean parsingRules = true;
        for (String line : lines) {
            if (line.isEmpty()) {
                parsingRules = false;
            } else if (parsingRules) {
                Workflow workflow = Workflow.fromLine(line);
                workflows.put(workflow.name, workflow);
            } else {
                Part part = Part.fromLine(line);
                partWorkflowMapping.put(part, "in");
                partsToProcess.offer(part);
            }
        }

        while (!partsToProcess.isEmpty()) {
            Part part = partsToProcess.poll();
            Workflow workflow = workflows.get(partWorkflowMapping.get(part));

            String result = workflow.processPart(part);
            partWorkflowMapping.put(part, result);
            if (!result.matches("[AR]")) {
                partsToProcess.offer(part);
            }
        }

        System.out.println(partWorkflowMapping);

        int one = partWorkflowMapping.entrySet().stream()
                .filter(entry -> "A".equals(entry.getValue()))
                .mapToInt(entry -> entry.getKey().x + entry.getKey().m + entry.getKey().a + entry.getKey().s)
                .sum();
        System.out.println("Solution for part one: " + one);
        System.out.println("Example for part one:  19114");


        List<TheoreticalPart> workingParts = new ArrayList<>();
        Map<TheoreticalPart, String> theoreticalPartWorkflowMap = new HashMap<>();
        Queue<TheoreticalPart> theoreticalPartsToProcess = new LinkedList<>();

        {
            TheoreticalPart initialPart = new TheoreticalPart();
            theoreticalPartsToProcess.offer(initialPart);
            theoreticalPartWorkflowMap.put(initialPart, "in");
        }

        while (!theoreticalPartsToProcess.isEmpty()) {
            TheoreticalPart part = theoreticalPartsToProcess.poll();
            String workflow = theoreticalPartWorkflowMap.get(part);

            if ("A".equals(workflow)) {
                workingParts.add(part);
                // terminal state, remove the theoretical part from workflow map to save space
                theoreticalPartWorkflowMap.remove(part);
                continue;
            }
            if ("R".equals(workflow)) {
                // terminal state, remove the theoretical part from workflow map to save space
                theoreticalPartWorkflowMap.remove(part);
                continue;
            }

            for (Rule rule : workflows.get(workflow).rules) {
                if (rule.property == null) {
                    // unconditional jump
                    theoreticalPartWorkflowMap.put(part, rule.target);
                    theoreticalPartsToProcess.offer(part);
                } else {
                    // conditional jump

                    // copy the theoretical part, follow with it to the next workflow
                    TheoreticalPart continuePart = new TheoreticalPart(part);
                    switch (rule.property + rule.operation) {
                        case "x>" -> continuePart.minX = Math.max(part.minX, rule.number + 1);
                        case "x<" -> continuePart.maxX = Math.min(part.maxX, rule.number - 1);
                        case "m>" -> continuePart.minM = Math.max(part.minM, rule.number + 1);
                        case "m<" -> continuePart.maxM = Math.min(part.maxM, rule.number - 1);
                        case "a>" -> continuePart.minA = Math.max(part.minA, rule.number + 1);
                        case "a<" -> continuePart.maxA = Math.min(part.maxA, rule.number - 1);
                        case "s>" -> continuePart.minS = Math.max(part.minS, rule.number + 1);
                        case "s<" -> continuePart.maxS = Math.min(part.maxS, rule.number - 1);
                    }
                    theoreticalPartWorkflowMap.put(continuePart, rule.target);
                    theoreticalPartsToProcess.offer(continuePart);

                    // negate the condition and continue processing
                    switch (rule.property + rule.operation) {
                        case "x>" -> part.maxX = Math.min(part.maxX, rule.number);
                        case "x<" -> part.minX = Math.max(part.minX, rule.number);
                        case "m>" -> part.maxM = Math.min(part.maxM, rule.number);
                        case "m<" -> part.minM = Math.max(part.minM, rule.number);
                        case "a>" -> part.maxA = Math.min(part.maxA, rule.number);
                        case "a<" -> part.minA = Math.max(part.minA, rule.number);
                        case "s>" -> part.maxS = Math.min(part.maxS, rule.number);
                        case "s<" -> part.minS = Math.max(part.minS, rule.number);
                    }
                }
            }
        }

        long two = workingParts
                .stream().mapToLong(TheoreticalPart::countPossibilities)
                        .sum();
        System.out.println("Solution for part two: " + two);
        System.out.println("Example for part two:  167409079868000");
    }

    private static class TheoreticalPart {
        private static final AtomicInteger ID_COUNTER = new AtomicInteger();

        public TheoreticalPart() {
            this.id = ID_COUNTER.getAndIncrement();
        }

        public TheoreticalPart(TheoreticalPart other) {
            this();
            this.minX = other.minX;
            this.maxX = other.maxX;
            this.minM = other.minM;
            this.maxM = other.maxM;
            this.minA = other.minA;
            this.maxA = other.maxA;
            this.minS = other.minS;
            this.maxS = other.maxS;
        }

        private final int id;
        int minX = 1;
        int maxX = 4000;
        int minM = 1;
        int maxM = 4000;
        int minA = 1;
        int maxA = 4000;
        int minS = 1;
        int maxS = 4000;

        public long countPossibilities() {
            return (maxX - minX + 1L) * (maxM - minM + 1L) * (maxA - minA + 1L) * (maxS - minS + 1L);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TheoreticalPart part)) return false;
            return id == part.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private record Part(int x, int m, int a, int s) {
        private static final Function<String, Integer> termParser = term -> Integer.parseInt(term.substring(2));

        public static Part fromLine(String line) {
            String[] split = line.substring(1, line.length() - 1).split(",");

            int x = 0;
            int m = 0;
            int a = 0;
            int s = 0;
            for (String term : split) {
                switch (term.charAt(0)) {
                    case 'x' -> x = termParser.apply(term);
                    case 'm' -> m = termParser.apply(term);
                    case 'a' -> a = termParser.apply(term);
                    case 's' -> s = termParser.apply(term);
                }
            }

            return new Part(x, m, a, s);
        }
    }

    private record Workflow(String name, List<Rule> rules) {
        public static Workflow fromLine(String line) {
            String name = line.substring(0, line.indexOf('{'));
            List<Rule> rules = Arrays.stream(line.substring(line.indexOf('{') + 1, line.indexOf('}')).split(","))
                    .map(Rule::fromLine)
                    .toList();

            return new Workflow(name, rules);
        }

        public String processPart(Part part) {
            for (Rule rule : rules) {
                if (rule.matchesPart(part)) {
                    return rule.target;
                }
            }

            throw new RuntimeException("Part " + part + " did not match any rule " + rules);
        }
    }

    private record Rule(Predicate<Part> condition, String target, String property, String operation, int number) {
        private static final Pattern rulePattern = Pattern.compile("([xmas])([><=])(\\d+):(\\w+)");

        public Rule(Predicate<Part> condition, String target) {
            this(condition, target, null, null, 0);
        }

        public static Rule fromLine(String line) {
            Matcher matcher = rulePattern.matcher(line);
            Predicate<Part> condition;
            String target;

            Rule result;
            if (matcher.matches()) {
                String property = matcher.group(1);
                String sign = matcher.group(2);
                final int number = Integer.parseInt(matcher.group(3));
                target = matcher.group(4);

                ToIntFunction<Part> propertyAccessor = switch (property) {
                    case "x" -> Part::x;
                    case "m" -> Part::m;
                    case "a" -> Part::a;
                    case "s" -> Part::s;
                    default -> throw new IllegalArgumentException("Unknown part property");
                };
                IntPredicate conditionFunction = switch (sign) {
                    case ">" -> i -> i > number;
                    case "<" -> i -> i < number;
                    case "=" -> i -> i == number;
                    default -> throw new IllegalArgumentException("Unknown compare sign");
                };

                condition = part -> conditionFunction.test(propertyAccessor.applyAsInt(part));
                result = new Rule(condition, target, property, sign, number);
            } else {
                // non-matching, assume this is the default branch
                condition = part -> true;
                target = line;
                result = new Rule(condition, target);
            }

            return result;
        }

        public boolean matchesPart(Part part) {
            return condition.test(part);
        }

    }
}
