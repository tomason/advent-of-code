package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.LinkedList;
import java.util.List;

public class DayEighteenPartTwo {

    public static void main(String[] args) {
        List<String> lines = Utils.readLines("2023/day-18");

        List<Instruction> instructions = lines.stream()
                .map(Instruction::parseLine)
                .toList();

        long one = countInteriorWithShoelaceAlgorithm(instructions);
        System.out.println("Solution for part one: " + one);
        System.out.println("Example for part one:  62");

        List<Instruction> decodedInstructions = instructions.stream()
                .map(Instruction::decodeFromColor)
                //.peek(System.out::println)
                .toList();
        long two = countInteriorWithShoelaceAlgorithm(decodedInstructions);
        System.out.println("Solution for part two: " + two);
        System.out.println("Example for part two:  952408144115");
    }

    private static long countInteriorWithShoelaceAlgorithm(List<Instruction> instructions) {
        LinkedList<Point> leftPoints = new LinkedList<>();
        LinkedList<Point> rightPoints = new LinkedList<>();

        int row = 0;
        int col = 0;
        Instruction previousInstruction = instructions.get(instructions.size() - 1);
        for (Instruction currentInstruction : instructions) {
            // determine point by previous and current direction
            Corner corner = Corner.fromDirections(previousInstruction.direction, currentInstruction.direction);
            leftPoints.add(corner.mapRowAndColToLeftCoordinates(row, col));
            rightPoints.add(corner.mapRowAndColToRightCoordinates(row, col));

            // move to the next corner
            row = row + currentInstruction.direction.deltaX * currentInstruction.steps;
            col = col + currentInstruction.direction.deltaY * currentInstruction.steps;

            previousInstruction = currentInstruction;
        }

        System.out.println("Right points: " + rightPoints);

        long resultRight = shoelaceCountPoints(rightPoints);
        long resultLeft = shoelaceCountPoints(leftPoints);

        System.out.printf("Results: right %d, left: %d%n", resultRight, resultLeft);

        return Math.max(resultRight, resultLeft);
    }

    private static long shoelaceCountPoints(List<Point> points) {
        long result = 0L;
        Point previousPoint = points.get(points.size() - 1);

        for (Point currentPoint : points) {
            result += (long) previousPoint.x * currentPoint.y - (long) previousPoint.y * currentPoint.x;

            previousPoint = currentPoint;
        }

        if (result % 2 != 0) {
            System.out.println("Double the determinant is odd, maybe wrong result?");
        }

        return result / 2;
    }

    private record Point(int x, int y) {
    }

    private record Instruction(Direction direction, int steps, String color) {
        static Instruction parseLine(String line) {
            String[] split = line.split(" ");

            Direction direction = Direction.fromChar(split[0].charAt(0));
            int steps = Integer.parseInt(split[1]);
            String color = split[2];

            return new Instruction(direction, steps, color);
        }

        public Instruction decodeFromColor() {
            int steps = Integer.decode(color.substring(1, 7));
            Direction direction = Direction.fromChar(color.charAt(7));

            return new Instruction(direction, steps, "");
        }
    }

    private enum Direction {
        UP(1, 0),
        DOWN(-1, 0),
        RIGHT(0, 1),
        LEFT(0, -1);

        private final int deltaX;
        private final int deltaY;

        Direction(int deltaX, int deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }

        public static Direction fromChar(char input) {
            return switch (input) {
                case '0', 'R' -> RIGHT;
                case '1', 'D' -> DOWN;
                case '2', 'L' -> LEFT;
                case '3', 'U' -> UP;

                default -> throw new IllegalArgumentException("Unknown direction " + input);
            };
        }
    }

    @SuppressWarnings("unused")
    // the values are retrieved programmatically, not directly, hence marked unused
    private enum Corner {
        // clockwise motion - "left" ones are outer corners, "right" ones are inner corners
        UtoR(1, -1, 0, 0),
        RtoD(0, -1, 1, 0),
        DtoL(0, 0, 1, -1),
        LtoU(1, 0, 0, -1),

        // counterclockwise motion - "right" are outer corners, "left" are inner corners
        UtoL(1, 0, 0, -1),
        LtoD(0, 0, 1, -1),
        DtoR( 0, -1, 1, 0),
        RtoU(1, -1, 0, 0);

        private final int rightDeltaRow;
        private final int rightDeltaCol;
        private final int leftDeltaRow;
        private final int leftDeltaCol;

        Corner(int rightDeltaCol, int rightDeltaRow, int leftDeltaCol, int leftDeltaRow) {
            this.rightDeltaRow = rightDeltaRow;
            this.rightDeltaCol = rightDeltaCol;
            this.leftDeltaRow = leftDeltaRow;
            this.leftDeltaCol = leftDeltaCol;
        }

        public Point mapRowAndColToLeftCoordinates(int row, int col) {
            int x = row + leftDeltaRow;
            int y = col + leftDeltaCol;

            return new Point(x, y);
        }

        public Point mapRowAndColToRightCoordinates(int row, int col) {
            int x = row + rightDeltaRow;
            int y = col + rightDeltaCol;

            return new Point(x, y);
        }

        public static Corner fromDirections(Direction first, Direction second) {
            String key = first.name().charAt(0) + "to" + second.name().charAt(0);

            return Corner.valueOf(key);
        }
    }
}
