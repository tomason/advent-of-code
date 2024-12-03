package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DayEighteen {
    public static void main(String[] args) {
        List<String> lines = Utils.readLines("2023/day-18-example");

        List<Instruction> instructions = lines.stream().map(Instruction::parseLine).toList();

        long one = plotMapAndCountInterior(instructions);
        System.out.println("Solution for part one: " + one);
        System.out.println("Example for part one:  62");


        List<Instruction> decodedInstructions = instructions.stream().map(Instruction::decodeFromColor).toList();
        long two = plotMapAndCountInterior(decodedInstructions);
        System.out.println("Solution for part two: " + two);
        System.out.println("Example for part two:  952408144115");
    }

    private static long plotMapAndCountInterior(List<Instruction> instructions) {
        Collection<Point> mapPoints = new HashSet<>();

        int maxRow = 0;
        int minRow = 0;
        int maxCol = 0;
        int minCol = 0;

        int row = 0;
        int col = 0;

        // insert corner
        mapPoints.add(new Point(row, col));

        // plot map
        for (Instruction instruction : instructions) {
            for (int i = 0; i < instruction.steps; i++) {
                switch (instruction.direction) {
                    case 'U' -> row--;
                    case 'D' -> row++;
                    case 'R' -> col--;
                    case 'L' -> col++;
                }
                mapPoints.add(new Point(row, col));
            }
            maxRow = Math.max(maxRow, row);
            minRow = Math.min(minRow, row);
            maxCol = Math.max(maxCol, col);
            minCol = Math.min(minCol, col);
        }

        System.out.println("Got the map!");

        final Set<Point> examinedPoints = new HashSet<>();
        long emptyPoints = 0L;

        final Queue<Point> pointsToExamine = new LinkedList<>();

        for (int i = minRow; i <= maxRow; i++) {
            if (!mapPoints.contains(new Point(i, minCol))) {
                Point emptyPoint = new Point(i, minCol);
                emptyPoints++;
                examinedPoints.add(emptyPoint);
                pointsToExamine.offer(new Point(i, minCol + 1));
            }
            if (!mapPoints.contains(new Point(i, maxCol))) {
                Point emptyPoint = new Point(i, maxCol);
                emptyPoints++;
                examinedPoints.add(emptyPoint);
                pointsToExamine.offer(new Point(i, maxCol - 1));
            }
        }

        for (int i = minCol; i <= maxCol; i++) {
            if (!mapPoints.contains(new Point(minRow, i))) {
                Point emptyPoint = new Point(minRow, i);
                emptyPoints++;
                examinedPoints.add(emptyPoint);
                pointsToExamine.offer(new Point(minRow + 1, i));
            }
            if (!mapPoints.contains(new Point(maxRow, i))) {
                Point emptyPoint = new Point(maxRow, i);
                emptyPoints++;
                examinedPoints.add(emptyPoint);
                pointsToExamine.offer(new Point(maxRow - 1, i));
            }
        }

        while (!pointsToExamine.isEmpty()) {
            Point toExamine = pointsToExamine.poll();
            if (!examinedPoints.contains(toExamine) &&
                    toExamine.row > minRow && toExamine.row < maxRow &&
                    toExamine.col > minCol && toExamine.col < maxCol &&
                    !mapPoints.contains(new Point(toExamine.row, toExamine.col))) {
                examinedPoints.add(toExamine);
                emptyPoints++;

                pointsToExamine.add(new Point(toExamine.row + 1, toExamine.col));
                pointsToExamine.add(new Point(toExamine.row - 1, toExamine.col));
                pointsToExamine.add(new Point(toExamine.row, toExamine.col + 1));
                pointsToExamine.add(new Point(toExamine.row, toExamine.col - 1));
            }
        }

        return (maxRow - minRow + 1L) * (maxCol - minCol + 1L) - emptyPoints;
    }

    private record Point(int row, int col) {
    }

    private record Instruction(char direction, int steps, String color) {
        static Instruction parseLine(String line) {
            String[] split = line.split(" ");

            char direction = split[0].charAt(0);
            int steps = Integer.parseInt(split[1]);
            String color = split[2];

            return new Instruction(direction, steps, color);
        }

        public Instruction decodeFromColor() {
            int steps = Integer.decode(color.substring(1, 7));
            char direction = switch (color.charAt(7)) {
                case '0' -> 'R';
                case '1' -> 'D';
                case '2' -> 'L';
                case '3' -> 'U';
                default -> throw new IllegalArgumentException("Wrong direction in color");
            };

            return new Instruction(direction, steps, "");
        }
    }
}
