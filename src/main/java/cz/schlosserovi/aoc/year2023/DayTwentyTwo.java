package cz.schlosserovi.aoc.year2023;

import cz.schlosserovi.aoc.Utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DayTwentyTwo {
    private static final int MAP_SIZE = 30;

    public static void main(String[] args) {
        List<String> input = Utils.readLines("2023/day-22");

        Brick[][] bricks = new Brick[MAP_SIZE][];
        for (int i = 0; i < bricks.length; i++) {
            bricks[i] = new Brick[MAP_SIZE];
        }

        Collection<Brick> landedBricks = input.stream()
                // parse input
                .map(Brick::fromInput)
                // sort by Z position
                .sorted(Comparator.comparingInt(b -> b.startZ))
                // place on the map
                .map(brick -> {
                    // first find out the height where the brick lands
                    int maxHeight = 0;
                    Collection<Brick> supportingBricks = new HashSet<>();

                    for (int x = brick.startX; x <= brick.endX; x++) {
                        for (int y = brick.startY; y <= brick.endY; y++) {
                            if (bricks[x][y] != null) {
                                Brick supportingBrick = bricks[x][y];
                                if (supportingBrick.endZ > maxHeight) {
                                    // there is a higher supporting brick
                                    maxHeight = supportingBrick.endZ;
                                    supportingBricks.clear();
                                    supportingBricks.add(supportingBrick);
                                } else if (supportingBrick.endZ == maxHeight) {
                                    // additional support
                                    supportingBricks.add(supportingBrick);
                                }
                            }
                        }
                    }

                    // now we have the supporting bricks, let's update the position
                    Brick updatedBrick = new Brick(brick, maxHeight + 1);
                    updatedBrick.supportingBricks.addAll(supportingBricks);
                    supportingBricks.forEach(support -> support.supportedBricks.add(updatedBrick));

                    // store the updated brick on top of the stack
                    for (int x = updatedBrick.startX; x <= updatedBrick.endX; x++) {
                        for (int y = updatedBrick.startY; y <= updatedBrick.endY; y++) {
                            bricks[x][y] = updatedBrick;
                        }
                    }

                    return updatedBrick;
                }).toList();

        long one = landedBricks.stream()
                .peek(System.out::println)
                // only care about bricks not supporting other bricks
                .filter(brick -> brick.supportedBricks.size() == 0 || brick.supportedBricks.stream().allMatch(supportedBrick -> supportedBrick.supportingBricks.size() > 1))
                .count();

        System.out.println("Result of part one: " + one);

        Map<Brick, Collection<Brick>> fallingBricksMap = new HashMap<>();
        landedBricks.stream()
                .sorted(Comparator.comparingInt(Brick::endZ).reversed())
                .forEach(brick -> collectFallingBricks(brick, fallingBricksMap));
        long two = fallingBricksMap.values().stream().mapToLong(Collection::size).sum();
        System.out.println("Result of part two: " + two);
    }

    private static void collectFallingBricks(Brick toDisintegrate, Map<Brick, Collection<Brick>> solutionCache) {
        Collection<Brick> result = new HashSet<>();

        Collection<Brick> alreadyFalling = new HashSet<>();
        alreadyFalling.add(toDisintegrate);

        for (Brick next : toDisintegrate.supportedBricks) {
            result.addAll(collectFurtherFallingBricks(next, alreadyFalling, solutionCache));
        }

        solutionCache.put(toDisintegrate, result);
    }

    private static Collection<Brick> collectFurtherFallingBricks(Brick toCheck, Collection<Brick> alreadyFalling, Map<Brick, Collection<Brick>> solutionCache) {

        Collection<Brick> result = new HashSet<>();

        // decide whether current brick will fall
        if (toCheck.supportingBricks.isEmpty() || alreadyFalling.containsAll(toCheck.supportingBricks)) {
            result.add(toCheck);
            alreadyFalling.add(toCheck);

            // count the bricks falling above
            for (Brick next : toCheck.supportedBricks) {
                result.addAll(collectFurtherFallingBricks(next, alreadyFalling, solutionCache));
            }
        }

        return result;
    }


    private record Brick(int startX, int startY, int startZ, int endX, int endY, int endZ, Set<Brick> supportedBricks,
                         Set<Brick> supportingBricks) {
        public static Brick fromInput(String line) {
            String[] coordinates = line.split("~");

            // starting positions
            String[] startingPositions = coordinates[0].split(",");
            int startX = Integer.parseInt(startingPositions[0]);
            int startY = Integer.parseInt(startingPositions[1]);
            int startZ = Integer.parseInt(startingPositions[2]);

            // ending positions
            String[] endingPositions = coordinates[1].split(",");
            int endX = Integer.parseInt(endingPositions[0]);
            int endY = Integer.parseInt(endingPositions[1]);
            int endZ = Integer.parseInt(endingPositions[2]);

            return new Brick(startX, startY, startZ, endX, endY, endZ);
        }

        private Brick(int startX, int startY, int startZ, int endX, int endY, int endZ) {
            this(startX, startY, startZ, endX, endY, endZ, new HashSet<>(), new HashSet<>());
        }

        public Brick(Brick original, int newStartZ) {
            this(original.startX, original.startY, newStartZ, original.endX, original.endY,
                    newStartZ + original.endZ - original.startZ, // count the final position on Z axis based on original size
                    original.supportedBricks, original.supportingBricks);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("Brick[")
                    .append("position=")
                    .append(startX).append(',').append(startY).append(',').append(startZ).append('~')
                    .append(endX).append(',').append(endY).append(',').append(endZ).append(';')
                    .append("supportedBy=").append(supportingBricks.size()).append(',')
                    .append("supports=").append(supportedBricks.size())
                    .append("]");

            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Brick brick = (Brick) o;
            return startX == brick.startX && startY == brick.startY && startZ == brick.startZ && endX == brick.endX && endY == brick.endY && endZ == brick.endZ;
        }

        @Override
        public int hashCode() {
            return Objects.hash(startX, startY, startZ, endX, endY, endZ);
        }
    }
}
