package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Day22 {
    private static final Map<Long, Long> RANDOM_SEQUENCE_CACHE = new HashMap<>();

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-22-1");

        long starOne = 0L;

        for (String line : lines) {
            long secretNumber = Long.parseLong(line);

            for (int i = 0; i < 2000; i++) {
                secretNumber = RANDOM_SEQUENCE_CACHE.computeIfAbsent(secretNumber, Day22::generateNextSecretNumber);
            }

            starOne += secretNumber;
        }

        System.out.println(RANDOM_SEQUENCE_CACHE.size());

        // 18261820068
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        Map<String, Integer> overallBest = new HashMap<>();

        for (String line : lines) {
            long secretNumber = Long.parseLong(line);
            int lastPrice = (int) (secretNumber % 10);
            Map<String, Integer> bestResults = new HashMap<>();

            PriceChangeSequence sequence = new PriceChangeSequence();

            for (int i = 0; i < 2000; i++) {
                secretNumber = RANDOM_SEQUENCE_CACHE.computeIfAbsent(secretNumber, Day22::generateNextSecretNumber);

                int price = (int) (secretNumber % 10);
                int priceChange = price - lastPrice;
                lastPrice = price;

                // push price change to the sequence
                sequence.add(priceChange);

                // store the information
                if (sequence.size() == 4) {
                    bestResults.putIfAbsent(sequence.toString(), price);
                }
            }

            bestResults.forEach((seq, price) -> overallBest.merge(seq, price, Integer::sum));
        }

        starTwo = overallBest.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(entry -> {
                    System.out.println("Best sequence: " + entry.getKey());
                    return entry.getValue();
                })
                .orElseThrow();


        // 2044
        System.out.println("Star two: " + starTwo);

    }

    private static long generateNextSecretNumber(long secretNumber) {
        long result = secretNumber;

        // step one
        result = (result ^ (result * 64)) % 16777216;

        // step two
        result = (result ^ (result / 32)) % 16777216;

        // step three
        result = (result ^ (result * 2048)) % 16777216;

        return result;
    }

    private static class PriceChangeSequence {
        private final List<Integer> sequence = new ArrayList<>();

        public void add(int nextPrice) {
            if (sequence.size() == 4) {
                sequence.removeFirst();
            }
            sequence.add(nextPrice);
        }

        public int size() {
            return sequence.size();
        }

        @Override
        public String toString() {
            return sequence.toString();
        }
    }
}
