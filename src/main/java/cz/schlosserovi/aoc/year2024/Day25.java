package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Day25 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-25-1");

        List<int[]> keys = new ArrayList<>();
        List<int[]> locks = new ArrayList<>();

        for (int startRow = 0; startRow < lines.size(); startRow += 8) {
            String line = lines.get(startRow);

            int[] heights = new int[] {0, 0, 0, 0, 0};

            if ("#####".equals(line)) {
                // parse lock
                for (int lockRow = startRow + 1; lockRow < startRow + 6; lockRow++) {
                    String lockLine = lines.get(lockRow);
                    for (int i = 0; i < heights.length; i++) {
                        if (lockLine.charAt(i) == '#') {
                            heights[i] = heights[i] + 1;
                        }
                    }
                }
                locks.add(heights);
            } else if (".....".equals(line)) {
                // parse key
                for (int keyRow = startRow + 5; keyRow > startRow; keyRow--) {
                    String keyLine = lines.get(keyRow);
                    for (int i = 0; i < heights.length; i++) {
                        if (keyLine.charAt(i) == '#') {
                            heights[i] = heights[i] + 1;
                        }
                    }
                }
                keys.add(heights);
            } else {
                throw new IllegalStateException("Unknown first line: " + line);
            }
        }

        long starOne = 0L;

        for (int[] key : keys) {
            lockLoop: for (int[] lock : locks) {
                for (int i = 0; i < 5; i++) {
                    if (key[i] + lock[i] > 5) {
                        continue lockLoop;
                    }
                }
                starOne++;
            }
        }

        // 2840
        System.out.println("Star one: " + starOne);
    }


}
