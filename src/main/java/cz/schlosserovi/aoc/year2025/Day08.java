package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Day08 {
    static void main(String... args) {
        var lines = Utils.readLines("/2025/day-08-1");
        var iterations = 1000;

        var one = 0L;

        var junctionBoxes = new ArrayList<JunctionBox>();
        var junctionBoxDistances = new ArrayList<JunctionBoxDistance>();
        for (var line : lines) {
            var junctionBox = new JunctionBox(line);
            for (var otherBox : junctionBoxes) {
                junctionBoxDistances.add(new JunctionBoxDistance(junctionBox, otherBox, distance(junctionBox, otherBox)));
            }
            junctionBoxes.add(junctionBox);
        }

        junctionBoxDistances.sort(Comparator.comparingDouble(JunctionBoxDistance::distance));

        var boxGroups = new ArrayList<Set<JunctionBox>>();
        for (var box : junctionBoxes) {
            boxGroups.add(new HashSet<>(Set.of(box)));
        }

        var iteration = 0;
        for (; iteration < iterations; iteration++) {
            var boxA = junctionBoxDistances.get(iteration).one();
            var boxB = junctionBoxDistances.get(iteration).two();

            Set<JunctionBox> groupA = null;
            Set<JunctionBox> groupB = null;
            for (var existingGroup : boxGroups) {
                if (existingGroup.contains(boxA)) {
                    groupA = existingGroup;
                }
                if (existingGroup.contains(boxB)) {
                    groupB = existingGroup;
                }
            }

            if (groupA == null || groupB == null) {
                throw new IllegalArgumentException("Invalid input");
            }

            if (groupA != groupB) {
                boxGroups.remove(groupB);
                groupA.addAll(groupB);
            }
        }

        one = boxGroups.stream()
            .map(Set::size)
            .sorted(Comparator.reverseOrder())
            .mapToLong(Integer::intValue)
            .limit(3)
            .reduce(1, (a, b) -> a * b);

        // 103488
        System.out.println("Part one: " + one);

        var two = 0L;

        while (boxGroups.size() > 1) {
            var boxA = junctionBoxDistances.get(iteration).one();
            var boxB = junctionBoxDistances.get(iteration).two();

            Set<JunctionBox> groupA = null;
            Set<JunctionBox> groupB = null;
            for (var existingGroup : boxGroups) {
                if (existingGroup.contains(boxA)) {
                    groupA = existingGroup;
                }
                if (existingGroup.contains(boxB)) {
                    groupB = existingGroup;
                }
            }

            if (groupA == null || groupB == null) {
                throw new IllegalArgumentException("Invalid input");
            }

            if (groupA != groupB) {
                boxGroups.remove(groupB);
                groupA.addAll(groupB);
            }

            iteration++;
        }

        // take previous iteration
        two = (long) junctionBoxDistances.get(iteration - 1).one().x() * (long) junctionBoxDistances.get(iteration - 1).two().x();

        // 8759985540
        System.out.println("Part two: " + two);
    }

    private static double distance(JunctionBox one, JunctionBox two) {
        return sqrt(pow(one.x() - two.x(), 2) + pow(one.y() - two.y(), 2) + pow(one.z() - two.z(), 2));
    }

    private record JunctionBox(int x, int y, int z) {
        public JunctionBox(String input) {
            var split = input.split(",");
            this(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
    }

    private record JunctionBoxDistance(JunctionBox one,  JunctionBox two, double distance) {}
}