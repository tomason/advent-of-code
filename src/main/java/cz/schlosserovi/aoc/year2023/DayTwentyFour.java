package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.List;
import java.util.stream.IntStream;

public class DayTwentyFour {
    private static final long INTERVAL_START = 200_000_000_000_000L;
    private static final long INTERVAL_END = 400_000_000_000_000L;

    public static void main(String... args) {
        List<String> input = Utils.readLines("2023/day-24");

        List<Hailstone> hailstones = input.parallelStream()
                .map(Hailstone::fromInput)
                .toList();

        long collisionCount = IntStream.range(0, hailstones.size() - 1).boxed().flatMap(i -> IntStream.range(i + 1, hailstones.size()).mapToObj(j -> new Tuple<>(i, j)))
                .map(tuple -> new Tuple<>(hailstones.get(tuple.one), hailstones.get(tuple.two)))
                .filter(hailTuple -> pathsIntersectsInTestArea(hailTuple.one, hailTuple.two))
                .count();


                /*
        int collisionCount = 0;
        for (int i = 0; i < hails.size() - 1; i++) {
            Position startOne = startPositions.get(hails.get(i));
            Position endOne = endPositions.get(hails.get(i));
            for (int j = i + 1; j < hails.size(); j++) {
                Position startTwo = startPositions.get(hails.get(j));
                Position endTwo = endPositions.get(hails.get(j));

                if (
                    // paths cross on border
                        startOne.x == startTwo.x ||
                                endOne.x == endTwo.x ||
                                startOne.y == startTwo.y ||
                                endOne.y == endTwo.y ||
                                // paths crossed on x axis
                                (startOne.x > startTwo.x && endOne.x < endTwo.x) ||
                                (startOne.x < startTwo.x && endOne.x > endTwo.x) ||
                                // paths crossed on y axis
                                (startOne.y > startTwo.y && endOne.y < endTwo.y) ||
                                (startOne.y < startTwo.y && endOne.y > endTwo.y)
                ) {
                    collisionCount++;
                }
            }
        }

                 */

        System.out.println("Result for part one: " + collisionCount);
    }

    private static boolean pathsIntersectsInTestArea(Hailstone one, Hailstone two) {
        // one.x + t * one.deltaX = two.x + u * two.deltaX
        // one.y + t * one.deltaY = two.y + u * two.deltaY
        // ---
        // t = (two.x - one.x + u * two.deltaX) / one.deltaX
        // t = (two.y - one.y + u * two.deltaY) / one.deltaY
        // ---
        // (two.x - one.x + u * two.deltaX) * one.deltaY = (two.y - one.y + u * two.deltaY) * one.deltaX
        // u * two.deltaX * one.deltaY + (two.x - one.x) * one.deltaY = u * two.deltaY * one.deltaX + (two.y - one.y) * one.deltaX
        // u * (one.deltaY * two.deltaX - one.deltaX * two.deltaY) = (two.y - one.y) * one.deltaX - (two.x - one.x) * one.deltaY
        // u = ((two.y - one.y) * one.deltaX - (two.x - one.x) * one.deltaY) / (one.deltaY * two.deltaX - one.deltaX * two.deltaY)

        /*
        if (one.deltaX == 0 || one.deltaY == 0 || (one.deltaY * two.deltaX - one.deltaX * two.deltaY) == 0) {
            System.out.println("Division by zero could occur");
            return false;
        }
        */

        double crossTimeTwo = (double) ((two.start.y - one.start.y) * one.deltaX - (two.start.x - one.start.x) * one.deltaY) /
                (one.deltaY * two.deltaX - one.deltaX * two.deltaY);
        double crossTimeOne = (two.start.x - one.start.x + crossTimeTwo * two.deltaX) / one.deltaX;
        if (crossTimeTwo < 0 || crossTimeOne < 0) {
            // crossed in the past
            System.out.println("Hails crossed in the past");
            return false;
        }

        double crossX = two.start.x + crossTimeTwo * two.deltaX;
        double crossY = two.start.y + crossTimeTwo * two.deltaY;

        System.out.printf("Hailstones %s and %s cross at: x=%s, y=%s%n", one, two, crossX, crossY);

        return crossX >= INTERVAL_START && crossX <= INTERVAL_END && crossY >= INTERVAL_START && crossY <= INTERVAL_END;
    }

    private static boolean simplePathIntersectsInTestArea(Hailstone one, Hailstone two) {
        // one.x + t * one.deltaX = two.x + t * two.deltaX
        // t * one.deltaX - t * two.deltaX = two.x - one.x
        // t = (two.x - one.x) / (one.deltaX - two.deltaX)

        double zeroX;
        if (one.deltaX == two.deltaX) {
            // parallel on X axis
            zeroX = Double.NaN;
        } else {
            zeroX = (double) (two.start.x - one.start.x) / (one.deltaX - two.deltaX);
        }

        double zeroY;
        if (one.deltaY == two.deltaY) {
            // parallel on Y axis
            zeroY = Double.NaN;
        } else {
            zeroY = (double) (two.start.y - one.start.y) / (one.deltaY - two.deltaY);
        }

        return !Double.isNaN(zeroX) && !Double.isNaN(zeroY) && zeroX == zeroY && zeroX >= INTERVAL_START && zeroX < INTERVAL_END;
    }



    private record Hailstone(Position start, int deltaX, int deltaY, int deltaZ) {
        public static Hailstone fromInput(String line) {
            String[] components = line.split("@");

            String[] positions = components[0].split(",");
            long x = Long.parseLong(positions[0].trim());
            long y = Long.parseLong(positions[1].trim());
            long z = Long.parseLong(positions[2].trim());

            String[] velocities = components[1].split(",");
            int velocityX = Integer.parseInt(velocities[0].trim());
            int velocityY = Integer.parseInt(velocities[1].trim());
            int velocityZ = Integer.parseInt(velocities[2].trim());

            return new Hailstone(new Position(x, y, z), velocityX, velocityY, velocityZ);
        }

        public Position positionInTime(long time) {
            long newX = start.x + time * deltaX;
            long newY = start.y + time * deltaY;
            long newZ = start.z + time * deltaZ;

            return new Position(newX, newY, newZ);
        }
    }

    private record Position(long x, long y, long z) {
    }

    private record Tuple<T>(T one, T two) {}
}
