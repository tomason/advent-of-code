package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.Arrays;

public class Day04 {
    public static void main(String... args) {
        var lines = Utils.readLines("/2025/day-04-1");

        var height = lines.size();
        var width = lines.getFirst().length();
        char[][] map = new char[height][];

        for (var row = 0; row < height; row++) {
            map[row] = lines.get(row).toCharArray();
        }

        var one = 0L;
        for (var row = 0; row < height; row++) {
            for (var col = 0; col < width; col++) {
                if (map[row][col] == '@') {
                    // count adjacent rolls
                    var adjacent =
                        (row - 1 >= 0 && col - 1 >= 0 && map[row - 1][col - 1] == '@' ? 1 : 0) +
                            (row - 1 >= 0 && map[row - 1][col] == '@' ? 1 : 0) +
                            (row - 1 >= 0 && col + 1 < width && map[row - 1][col + 1] == '@' ? 1 : 0) +
                            (col - 1 >= 0 && map[row][col - 1] == '@' ? 1 : 0) +
                            (col + 1 < width && map[row][col + 1] == '@' ? 1 : 0) +
                            (row + 1 < height && col - 1 >= 0 && map[row + 1][col - 1] == '@' ? 1 : 0) +
                            (row + 1 < height && map[row + 1][col] == '@' ? 1 : 0) +
                            (row + 1 < height && col + 1 < width && map[row + 1][col + 1] == '@' ? 1 : 0);
                    if (adjacent < 4) {
                        one++;
                    }
                }
            }
        }

        // 1346
        System.out.println("Part one: " + one);

        var two = 0L;

        int removed;
        do {
            // make a copy of a map
            var newMap = new char[height][];
            for (var row = 0; row < height; row++) {
                newMap[row] = Arrays.copyOf(map[row], width);
            }

            removed = 0;
            for (var row = 0; row < height; row++) {
                for (var col = 0; col < width; col++) {
                    if (map[row][col] == '@') {
                        // count adjacent rolls
                        var adjacent =
                            (row - 1 >= 0 && col - 1 >= 0 && map[row - 1][col - 1] == '@' ? 1 : 0) +
                                (row - 1 >= 0 && map[row - 1][col] == '@' ? 1 : 0) +
                                (row - 1 >= 0 && col + 1 < width && map[row - 1][col + 1] == '@' ? 1 : 0) +
                                (col - 1 >= 0 && map[row][col - 1] == '@' ? 1 : 0) +
                                (col + 1 < width && map[row][col + 1] == '@' ? 1 : 0) +
                                (row + 1 < height && col - 1 >= 0 && map[row + 1][col - 1] == '@' ? 1 : 0) +
                                (row + 1 < height && map[row + 1][col] == '@' ? 1 : 0) +
                                (row + 1 < height && col + 1 < width && map[row + 1][col + 1] == '@' ? 1 : 0);
                        if (adjacent < 4) {
                            removed++;
                            newMap[row][col] = 'x';
                        }
                    }
                }
            }

            two += removed;

            // update the map
            map = newMap;
        } while (removed > 0);

        // 8493
        System.out.println("Part two: " + two);
    }


}