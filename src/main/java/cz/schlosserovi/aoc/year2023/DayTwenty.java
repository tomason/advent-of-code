package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DayTwenty {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("2023/day-20");
        lines.forEach(Component::fromLine);

        Component.Button button = Component.createButton();
        Component.rerunAllRegistrations();


        long lowPulses = 0L;
        long highPulses = 0L;

        // for part two
        AtomicBoolean rxLowReached = new AtomicBoolean(false);
        Component.addCustomComponent("rx", (source, signal) -> {
            if (signal == Signal.LOW) {
                rxLowReached.set(true);
            }
        });

        AtomicLong counter = new AtomicLong(0L);
        for (int i = 0; i < 1000; i++) {
            Map<Signal, Integer> result = button.pushButton();
            lowPulses += result.getOrDefault(Signal.LOW, 0);
            highPulses += result.getOrDefault(Signal.HIGH, 0);
            //System.out.printf("%4d/1000  Low: %4d, high: %4d%n", i, lowPulses, highPulses);

            if (!rxLowReached.get()) {
                counter.incrementAndGet();
            }
        }

        System.out.println("Result (part one): " + (lowPulses * highPulses));

        while (!rxLowReached.get()) {
            button.pushButton();
            counter.incrementAndGet();
        }

        System.out.println("Result (part two):" + counter.get());
    }

    private abstract static class Component {
        public final String name;
        protected final List<String> connections;

        private static final Map<String, Component> COMPONENT_MAP = new HashMap<>();
        protected static final Queue<Supplier<int[]>> STEPS_TO_PROCESS = new LinkedList<>();
        private static final String BROADCASTER_NAME = "broadcaster";

        private static void addCustomComponent(String name, BiConsumer<String, Signal> function) {
            if (COMPONENT_MAP.containsKey(name)) {
                throw new IllegalArgumentException("Can't replace builtin component");
            }

            COMPONENT_MAP.put(name, new Component(name, Collections.emptyList()) {
                @Override
                protected Signal processSignal(Component source, Signal signal) {
                    function.accept(source.name, signal);
                    return null;
                }
            });
        }

        public static void fromLine(String line) {
            String[] split = line.split(" -> ");
            String name = split[0];
            String[] connections = split[1].split(", ");

            Component result;
            if (name.startsWith("%")) {
                result = new FlipFlop(name.substring(1), connections);
            } else if (name.startsWith("&")) {
                result = new Conjunction(name.substring(1), connections);
            } else if (name.equals(BROADCASTER_NAME)) {
                result = new Broadcaster(connections);
            } else {
                throw new IllegalArgumentException("Unknown component on line " + line);
            }

            COMPONENT_MAP.put(result.name, result);
        }

        public static Button createButton() {
            return new Button();
        }

        public static void rerunAllRegistrations() {
            COMPONENT_MAP.values().forEach(Component::rerunRegistration);
        }

        protected Component(String name, List<String> connections) {
            this.name = name;
            this.connections = connections;
            rerunRegistration();
        }

        public final void handleSignal(Component source, Signal signal) {
            Signal signalToSend = processSignal(source, signal);
            if (signalToSend == null) {
                // nothing to send
                return;
            }

            connections.stream()
                    .map(COMPONENT_MAP::get)
                    .forEach(component -> {
                        if (component == null) {
                            // add dummy step that just counts the incoming signals
                            STEPS_TO_PROCESS.offer(() -> {
                                int[] result = new int[2];
                                result[signalToSend.ordinal()]++;
                                return result;
                            });
                        } else {
                            STEPS_TO_PROCESS.offer(() -> {
                                // process the signal downstream
                                component.handleSignal(this, signalToSend);

                                // return the signal count
                                int[] result = new int[2];
                                result[signalToSend.ordinal()]++;
                                return result;
                            });
                        }
                    });
        }

        protected abstract Signal processSignal(Component source, Signal signal);

        public void rerunRegistration() {
            connections.stream()
                    .map(COMPONENT_MAP::get)
                    .filter(Objects::nonNull)
                    .forEach(connection -> connection.registerInput(name));
        }

        protected void registerInput(String input) {
            // left empty, some components need to keep track of inputs as well
        }

        private static class Broadcaster extends Component {
            Broadcaster(String... outputs) {
                super(BROADCASTER_NAME, Arrays.asList(outputs));
            }

            @Override
            public Signal processSignal(Component source, Signal signal) {
                // identity
                return signal;
            }
        }

        private static class FlipFlop extends Component {
            private boolean turnedOn = false;

            FlipFlop(String name, String... outputs) {
                super(name, Arrays.asList(outputs));
            }

            @Override
            public Signal processSignal(Component source, Signal signal) {
                if (signal == Signal.LOW) {
                    turnedOn = !turnedOn;

                    return turnedOn ? Signal.HIGH : Signal.LOW;
                }

                // not sending anything
                return null;
            }
        }

        private static class Conjunction extends Component {
            private final Map<String, Signal> signalMemory = new HashMap<>();

            protected Conjunction(String name, String... connections) {
                super(name, Arrays.asList(connections));
            }

            @Override
            public Signal processSignal(Component source, Signal signal) {
                signalMemory.put(source.name, signal);

                if (signalMemory.values().stream().allMatch(memorizedSignal -> memorizedSignal == Signal.HIGH)) {
                    // all signal are high
                    return Signal.LOW;
                } else {
                    return Signal.HIGH;
                }
            }

            @Override
            protected void registerInput(String input) {
                signalMemory.put(input, Signal.LOW);
            }
        }

        private static class Button extends Component {
            Button() {
                super("button", Collections.singletonList(BROADCASTER_NAME));
            }

            @Override
            public Signal processSignal(Component source, Signal signal) {
                return Signal.LOW;
            }

            public Map<Signal, Integer> pushButton() {
                // cleanup before run
                STEPS_TO_PROCESS.clear();

                // start the sequence
                handleSignal(null, null);

                Map<Signal, Integer> result = new HashMap<>();
                while (!STEPS_TO_PROCESS.isEmpty()) {
                    int[] signalCounts = STEPS_TO_PROCESS.poll().get();
                    result.merge(Signal.LOW, signalCounts[Signal.LOW.ordinal()], Integer::sum);
                    result.merge(Signal.HIGH, signalCounts[Signal.HIGH.ordinal()], Integer::sum);
                }

                return result;
            }
        }
    }

    private enum Signal {
        LOW,
        HIGH
    }
}
