package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 {
    public static void main(String... args) {
        String input = String.join("", Utils.readLines("/2024/day-03-1"));

        Matcher matcher = Pattern.compile("mul\\(([0-9]+),([0-9]+)\\)").matcher(input);
        long starOne = 0;
        while (matcher.find()) {
            int a = Integer.parseInt(matcher.group(1));
            int b = Integer.parseInt(matcher.group(2));

            starOne += (long) a * b;
        }


        // 174960292
        System.out.println("Star one: " + starOne);

        Matcher matcherTwo = Pattern.compile("mul\\((\\d+),(\\d+)\\)|(do\\(\\))|(don't\\(\\))").matcher(input);
        long starTwo = 0;
        boolean enabled = true;
        while (matcherTwo.find()) {
            String command = matcherTwo.group().substring(0, matcherTwo.group().indexOf('('));
            switch (command) {
                case "do":
                    enabled = true;
                    break;
                case "don't":
                    enabled = false;
                    break;
                case "mul":
                    if (enabled) {
                        int a = Integer.parseInt(matcherTwo.group(1));
                        int b = Integer.parseInt(matcherTwo.group(2));

                        starTwo += (long) a * b;
                    }
                    break;
                default:
                    throw new RuntimeException("Bad command: " + command);
            }
        }

        // 56275602
        System.out.println("Star two: " + starTwo);

    }
}
