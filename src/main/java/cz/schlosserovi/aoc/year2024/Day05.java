package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Day05 {
    public static void main(String... args) {
        final List<String> rules = new ArrayList<>();
        final List<String> inputs = new ArrayList<>();
        final AtomicBoolean rulesRead = new AtomicBoolean(false);
        Utils.processFile("/2024/day-05-1", line -> {
            if (line.isEmpty()) {
                rulesRead.set(true);
            } else if (rulesRead.get()) {
                inputs.add(line);
            } else {
                rules.add(line);
            }
        });

        long starOne = 0;

        final List<String[]> broken = new ArrayList<>();

        for (String input : inputs) {
            String[] parts = input.split(",");
            if (isValidOrder(parts, rules)) {
                // no rules broken, get the middle number
                starOne += Integer.parseInt(parts[parts.length / 2]);
            } else {
                // rules broken do not count
                broken.add(parts);
            }
        }




        // 6505
        System.out.println("Star one: " + starOne);

        long starTwo = 0;

        bigLoop: while (!broken.isEmpty()) {
            String[] brokenParts = broken.remove(0);

            for (int i = 0; i < brokenParts.length - 1; i++) {
                for (int j = i; j < brokenParts.length; j++) {
                    if (rules.contains(brokenParts[j] + "|" + brokenParts[i])) {
                        // still broken, switch indices and add back
                        String tmp = brokenParts[j];
                        brokenParts[j] = brokenParts[i];
                        brokenParts[i] = tmp;
                        broken.add(brokenParts);
                        continue bigLoop;
                    }
                }
            }

            // all good now, add it to total
            starTwo += Integer.parseInt(brokenParts[brokenParts.length / 2]);
        }


        // 6897
        System.out.println("Star two: " + starTwo);

    }

    private static boolean isValidOrder(String[] inputParts, List<String> rules) {
        for (int i = 0; i < inputParts.length - 1; i++) {
            for (int j = i; j < inputParts.length; j++) {
                if (rules.contains(inputParts[j] + "|" + inputParts[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}
