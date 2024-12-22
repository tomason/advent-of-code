package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Day12 {

    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-12-1");

        Map<Plot, Character> gardenMap = new HashMap<>();
        int maxRow = lines.size();
        int maxCol = lines.stream().map(String::length).max(Integer::compare).orElseThrow();

        for (int row = 0; row < maxRow; row++) {
            String line = lines.get(row);
            for (int col = 0; col < maxCol; col++) {
                gardenMap.put(new Plot(row, col), line.charAt(col));
            }
        }

        long starOne = 0L;

        final Set<Plot> toVisit = new HashSet<>(gardenMap.keySet());

        List<Set<Plot>> knownAreas = new ArrayList<>();

        while (!toVisit.isEmpty()) {
            Plot startingPlot = toVisit.iterator().next();
            Character plant = gardenMap.get(startingPlot);

            Set<Plot> stillToCheck = new HashSet<>();
            Set<Plot> areaCovered = new HashSet<>();
            stillToCheck.add(startingPlot);

            int perimeter = 0;
            int area = 0;

            while (!stillToCheck.isEmpty()) {
                Plot current = stillToCheck.iterator().next();
                stillToCheck.remove(current);
                toVisit.remove(current);
                areaCovered.add(current);
                area++;

                Plot[] plotsToCheck = new Plot[]{
                        new Plot(current.row - 1, current.col),
                        new Plot(current.row + 1, current.col),
                        new Plot(current.row, current.col - 1),
                        new Plot(current.row, current.col + 1)
                };
                for (Plot toCheck : plotsToCheck) {
                    if (Objects.equals(gardenMap.get(toCheck), plant)) {
                        // same plant, expand if not yet visited
                        if (!areaCovered.contains(toCheck)) {
                            stillToCheck.add(toCheck);
                        }
                    } else {
                        // different plant or free space, add fence
                        perimeter++;
                    }
                }
            }

            starOne += (long) area * perimeter;
            knownAreas.add(areaCovered);
        }

        // 1371306
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;


        for (Set<Plot> area : knownAreas) {
            int corners = 0;

            for (Plot plot : area) {
                Plot up = new Plot(plot.row - 1, plot.col);
                Plot down = new Plot(plot.row + 1, plot.col);
                Plot left = new Plot(plot.row, plot.col - 1);
                Plot right = new Plot(plot.row, plot.col + 1);

                if (
                        (area.contains(up) && area.contains(down)) ||   // vertical line
                        (area.contains(left) && area.contains(right))   // horizontal line
                ) {
                    // no outer corners
                } else if (
                        (area.contains(right) && area.contains(down)) || // top left
                        (area.contains(left) && area.contains(down)) ||  // top right
                        (area.contains(left) && area.contains(up)) ||    // bottom right
                        (area.contains(right) && area.contains(up))      // bottom left
                ) {
                    // one corner
                    corners++;
                } else if (!area.contains(up) && !area.contains(down) && !area.contains(left) && !area.contains(right)) {
                    // single cell, four corners
                    corners += 4;
                } else {
                    // two corners - cell sticking out of the area
                    corners += 2;
                }
                // internal corners
                if (area.contains(right) && area.contains(down) && !area.contains(new Plot(plot.row + 1, plot.col + 1))) {
                    corners++;
                }
                if (area.contains(left) && area.contains(down) && !area.contains(new Plot(plot.row + 1, plot.col - 1))) {
                    corners++;
                }
                if (area.contains(right) && area.contains(up) && !area.contains(new Plot(plot.row - 1, plot.col + 1))) {
                    corners++;
                }
                if (area.contains(left) && area.contains(up) && !area.contains(new Plot(plot.row - 1, plot.col - 1))) {
                    corners++;
                }
            }

            starTwo += (long) corners * area.size();
        }

        // 805880
        System.out.println("Star two: " + starTwo);

    }

    private record Plot(int row, int col) {
    }
}
