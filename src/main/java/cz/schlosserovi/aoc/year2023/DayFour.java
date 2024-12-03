package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DayFour {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayFour.class.getResourceAsStream("/2023/day-4"))))) {
            String line;

            int sumOne = 0;

            Map<Integer, Integer> ticketsCount = new HashMap<>();
            final AtomicInteger ticketId = new AtomicInteger(0);

            while ((line = reader.readLine()) != null) {
                ticketId.incrementAndGet();

                Ticket ticket = parseLine(line);

                // part one
                sumOne += Math.pow(2, ticket.matches.get() - 1);

                // part two
                ticketsCount.merge(ticketId.get(), 1, Integer::sum);  // add one ticket to the count

                for (int i = 1; i <= ticket.matches.get(); i++) {
                    // add the same number of tickets as the number of winning cards
                    ticketsCount.merge(ticketId.get() + i, ticketsCount.get(ticketId.get()), Integer::sum);
                }
            }

            int sumTwo = ticketsCount.entrySet().stream()
                    .filter(entry -> entry.getKey() <= ticketId.get())
                    .mapToInt(Map.Entry::getValue)
                    .sum();

            System.out.println("Final value: " + sumOne);
            System.out.println("Final value (part two): " + sumTwo);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static Ticket parseLine(String line) {
        Ticket result = new Ticket();

        boolean parsing = false;
        int parsedNumber = 0;

        boolean winningNumbers = true;

        for (Character character : (line.substring(line.indexOf(':') + 2) + " ").toCharArray()) {
            if (Character.isDigit(character)) {
                parsing = true;
                parsedNumber = parsedNumber * 10 + Character.digit(character, 10);
            } else if (parsing) {
                // handle the number
                if (winningNumbers) {
                    result.winningNumbers.add(parsedNumber);
                } else {
                    result.ticketNumbers.add(parsedNumber);
                    if (result.winningNumbers.contains(parsedNumber)) {
                        result.matches.incrementAndGet();
                    }
                }

                // reset state
                parsing = false;
                parsedNumber = 0;
            } else if (character == '|') {
                winningNumbers = false;
            }
        }

        return result;
    }

    private record Ticket(Set<Integer> winningNumbers, Set<Integer> ticketNumbers, AtomicInteger matches) {
        Ticket() {
            this(new HashSet<>(), new HashSet<>(), new AtomicInteger(0));
        }
    }
}
