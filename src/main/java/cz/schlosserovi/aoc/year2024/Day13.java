package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {
    private static final Pattern BUTTON_A_LINE = Pattern.compile("Button A: X\\+(\\d+), Y\\+(\\d+)");
    private static final Pattern BUTTON_B_LINE = Pattern.compile("Button B: X\\+(\\d+), Y\\+(\\d+)");
    private static final Pattern PRIZE_LINE = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-13-1");

        List<ClawMachine> machines = new ArrayList<>();

        Button a = null;
        Button b = null;
        Prize prize = null;
        for (String line : lines) {
            Matcher m;
            if (line.isBlank() && a != null && b != null && prize != null) {
                machines.add(new ClawMachine(a, b, prize));
            }
            if ((m = BUTTON_A_LINE.matcher(line)).matches()) {
                a = new Button(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), 3);
            }
            if ((m = BUTTON_B_LINE.matcher(line)).matches()) {
                b = new Button(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), 1);
            }
            if ((m = PRIZE_LINE.matcher(line)).matches()) {
                prize = new Prize(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            }
        }
        // tail of the file
        if (a != null && b != null && prize != null) {
            machines.add(new ClawMachine(a, b, prize));
        }

        long starOne = 0L;

        for (ClawMachine machine : machines) {
            int minTokens = Integer.MAX_VALUE;

            for (int aPress = 0; aPress <= 100; aPress++) {
                for (int bPress = 0; bPress <= 100; bPress++) {
                    if (
                            aPress * machine.a.dX + bPress * machine.b.dX == machine.target.x &&
                            aPress * machine.a.dY + bPress * machine.b.dY == machine.target.y
                    ) {
                        minTokens = Math.min(minTokens, aPress * machine.a.cost + bPress * machine.b.cost);
                    }
                }
            }

            if (minTokens < Integer.MAX_VALUE) {
                starOne += minTokens;
            }
        }

        // 37297
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        for (ClawMachine machine : machines) {
            long newTargetX = machine.target.x + 10_000_000_000_000L;
            long newTargetY = machine.target.y + 10_000_000_000_000L;

            long tokensNeededA = tokensNeededA(machine.a.dX, machine.a.dY, machine.b.dX, machine.b.dY, newTargetX, newTargetY);
            long tokensNeededB = tokensNeededB(machine.a.dX, machine.a.dY, machine.b.dX, machine.b.dY, newTargetX, newTargetY);

            if (tokensNeededA > -1 && tokensNeededB > -1) {
                starTwo += tokensNeededA * machine.a.cost + tokensNeededB * machine.b.cost;
            }
        }

        // 83197086729371
        System.out.println("Star two: " + starTwo);

    }

    private static long tokensNeededA(long adX, long adY, long bdX, long bdY, long tX, long tY) {
        long numerator = bdY * tX - bdX * tY;
        long denominator = adX * bdY - adY * bdX;

        if (denominator == 0) {
            throw new IllegalStateException("denominator is zero... what?");
        }
        if (numerator % denominator != 0) {
            // not a whole number so not interested
            return -1L;
        }
        return numerator / denominator;
    }

    private static long tokensNeededB(long adX, long adY, long bdX, long bdY, long tX, long tY) {
        long numerator = adY * tX - adX * tY;
        long denominator = adY * bdX - adX * bdY;

        if (denominator == 0) {
            throw new IllegalStateException("denominator is zero... what?");
        }
        if (numerator % denominator != 0) {
            // not a whole number so not interested
            return -1L;
        }
        return numerator / denominator;
    }

    private record Button(int dX, int dY, int cost){}

    private record Prize(int x, int y) {}

    private record ClawMachine(Button a, Button b,  Prize target){}
}
