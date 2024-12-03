package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DayElevenPartTwo {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DayTen.class.getResourceAsStream("/2023/day-11"))))) {

            List<Galaxy> galaxies = new ArrayList<>();
            int sizeX = 0;
            int sizeY = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '#') {
                        galaxies.add(new Galaxy(i, sizeY));
                    }
                }

                sizeX = Math.max(sizeX, line.length());
                sizeY++;
            }

            // apply expansion
            for (int i = sizeX - 1; i >= 0; i--) {
                final int x = i;
                if (galaxies.stream().noneMatch(galaxy -> galaxy.getX() == x)) {
                    galaxies.stream()
                            .filter(galaxy -> galaxy.getX() > x)
                            .forEach(Galaxy::expandX);
                }
            }
            for (int i = sizeY - 1; i >= 0; i--) {
                final int y = i;
                if (galaxies.stream().noneMatch(galaxy -> galaxy.getY() == y)) {
                    galaxies.stream()
                            .filter(galaxy -> galaxy.getY() > y)
                            .forEach(Galaxy::expandY);
                }
            }

            // part one
            long one = 0;
            for (int i = 0; i < galaxies.size(); i++) {
                for (int j = i + 1; j < galaxies.size(); j++) {
                    one += galaxies.get(i).distance(galaxies.get(j));
                }
            }

            System.out.println("Final value: " + one);
            System.out.println("Final value (part two): " + 0);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static class Galaxy {
        private long x;
        private long y;

        private Galaxy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void expandX() {
            this.x += 999_999;
        }

        public void expandY() {
            this.y += 999_999;
        }

        public long getX() {
            return x;
        }

        public long getY() {
            return y;
        }

        public long distance(Galaxy other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }
    }
}
