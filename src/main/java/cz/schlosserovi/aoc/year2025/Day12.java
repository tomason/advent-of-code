package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day12 {
    static void main() {
        var lines = Utils.readLines("/2025/day-12-1");

        var allPackages = new HashMap<Integer, Set<Package>>();
        var areas = new ArrayList<Area>();

        Integer packageIndex = null;
        List<String> packageLines = null;
        for (var line : lines) {
            if (line.matches("^\\d:$")) {
                packageIndex = Integer.parseInt(line.substring(0, line.length() - 1));
                packageLines = new ArrayList<>();
                continue;
            }
            if (packageIndex != null && packageLines != null) {
                if (line.isEmpty()) {
                    // end of shape
                    allPackages.put(packageIndex, generatePackageVariants(packageLines));
                    packageIndex = null;
                    packageLines = null;
                } else {
                    packageLines.add(line);
                }
                continue;
            }

            // area line
            var sizeSplit = line.substring(0, line.indexOf(':')).split("x");
            var width = Integer.parseInt(sizeSplit[0]);
            var height = Integer.parseInt(sizeSplit[1]);
            var expectedPackages = Stream.of(line.substring(line.indexOf(':') + 1).split(" "))
                .filter(str -> !str.isEmpty())
                .map(Integer::parseInt)
                .toList();

            areas.add(new Area(height, width, expectedPackages));
        }

        var one = 0L;

        one = areas.stream()
            //.peek(System.out::println)
            .map(problem -> canFit(problem, allPackages))
            //.peek(System.out::println)
            .filter(Boolean::booleanValue)
            .count();

        // 21139440284
        System.out.println("Part one: " + one);
    }

    private static Set<Package> generatePackageVariants(List<String> inputLines) {
        char[][] baseShape = new char[inputLines.size()][];

        for (var i = 0; i < inputLines.size(); i++) {
            baseShape[i] = inputLines.get(i).toCharArray();
        }

        Set<Package> variants = new HashSet<>();
        variants.add(new Package(baseShape));

        var lastShape = baseShape;

        // rotate 90 degrees 3 times to get rotations
        for (int i = 0; i < 3; i++) {
            var height = lastShape.length;
            var width = lastShape[0].length;

            var newShape = new char[width][];
            for (int a = 0; a < width; a++) {
                newShape[a] = new char[height];
                for (int b = 0; b < height; b++) {
                    newShape[a][b] = lastShape[b][a];
                }
            }

            variants.add(new Package(newShape));
            lastShape = newShape;
        }

        // flip horizontally
        {
            var height = lastShape.length;
            var width = lastShape[0].length;

            var newShape = new char[height][];
            for (int a = 0; a < height; a++) {
                newShape[a] = new char[width];
                for (int b = 0; b < width; b++) {
                    newShape[a][b] = lastShape[(height - 1) - a][(width - 1) - b];
                }
            }

            variants.add(new Package(newShape));
            lastShape = newShape;
        }

        // rotate flipped shape 3 times to get all flipped rotations
        for (int i = 0; i < 3; i++) {
            var height = lastShape.length;
            var width = lastShape[0].length;

            var newShape = new char[width][];
            for (int a = 0; a < width; a++) {
                newShape[a] = new char[height];
                for (int b = 0; b < height; b++) {
                    newShape[a][b] = lastShape[b][a];
                }
            }

            variants.add(new Package(newShape));
            lastShape = newShape;
        }

        return variants;
    }

    private record Package(char[][] shape, int height, int width) {
        public Package(char[][] shape) {
            var height = shape.length;
            var width = shape[0].length;

            this(shape, height, width);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Package aPackage)) return false;
            return Objects.deepEquals(shape, aPackage.shape);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(shape);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (var row : shape) {
                stringBuilder.append(row).append("\n");
            }

            return stringBuilder.toString();
        }
    }

    private static class Area {
        private final char[][] area;
        private final int height;
        private final int width;
        private final int[] requiredPackages;

        public Area(int height, int width, List<Integer> expectedPackages) {
            this.height = height;
            this.width = width;
            this.requiredPackages = IntStream.range(0, expectedPackages.size())
                .mapToObj(idx -> Map.entry(idx, expectedPackages.get(idx)))
                .filter(entry -> entry.getValue() > 0)
                .flatMapToInt(entry -> {
                    int[] arr = new int[entry.getValue()];
                    Arrays.fill(arr, entry.getKey());
                    return IntStream.of(arr);
                })
                .toArray();


            area = new char[height][];
            for (var i = 0; i < height; i++) {
                area[i] = new char[width];
                for (var j = 0; j < width; j++) {
                    area[i][j] = '.';
                }
            }
        }

        public boolean canFit(Package p, int row, int col) {
            for (var i = 0; i < p.height; i++) {
                for (var j = 0; j < p.width; j++) {
                    if (p.shape[i][j] == '#' && area[row + i][col + j] == '#') {
                        return false;
                    }
                }
            }

            return true;
        }

        public void place(Package p, int row, int col) {
            for (var i = 0; i < p.height; i++) {
                for (var j = 0; j < p.width; j++) {
                    if (p.shape[i][j] == '#') {
                        area[row + i][col + j] = '#';
                    }
                }
            }
        }

        public void remove(Package p, int row, int col) {
            for (var i = 0; i < p.height; i++) {
                for (var j = 0; j < p.width; j++) {
                    if (p.shape[i][j] == '#') {
                        area[row + i][col + j] = '.';
                    }
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            stringBuilder.append(Arrays.toString(requiredPackages)).append("\n");
            for (var row : area) {
                stringBuilder.append(row).append("\n");
            }

            return stringBuilder.toString();
        }
    }


    private static boolean canFit(Area area, Map<Integer, Set<Package>> packageVariants) {
        return canFit(area, 0, packageVariants, new HashMap<>());
    }

    private static boolean canFit(Area area, int requiredIdx, Map<Integer, Set<Package>> packageVariants, Map<Integer, Boolean> cache) {
        var hash = area.hashCode();

        if (cache.containsKey(hash)) {
            return cache.get(hash);
        }

        if (requiredIdx >= area.requiredPackages.length) {
            cache.put(hash, true);
            return true;
        }

        var packageIdx = area.requiredPackages[requiredIdx];
        for (var packageVariant : packageVariants.get(packageIdx)) {
            for (var row = 0; row < area.height - packageVariant.height + 1; row++) {
                for (var col = 0; col < area.width - packageVariant.width + 1; col++) {
                    if (area.canFit(packageVariant, row, col)) {
                        area.place(packageVariant, row, col);

                        if (canFit(area, requiredIdx + 1, packageVariants, cache)) {
                            cache.put(hash, true);
                            return true;
                        }

                        area.remove(packageVariant, row, col);
                    }
                }
            }
        }

        cache.put(hash, false);
        return false;
    }
}