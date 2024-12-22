package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.List;

public class Day04 {
    public static void main(String... args) {
        List<String> input = Utils.readLines("/2024/day-04-1");
        char[][] inputMap = new char[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            inputMap[i] = line.toCharArray();
        }

        long starOne = 0;

        for (int row = 0; row < inputMap.length; row++) {
            for (int col = 0; col < inputMap[row].length; col++) {
                starOne += countXmas(inputMap, row, col);
            }
        }


        // 2642
        System.out.println("Star one: " + starOne);

        long starTwo = 0;

        for (int row = 1; row < inputMap.length - 1; row++) {
            for (int col = 1; col < inputMap[row].length - 1; col++) {
                starTwo += countCross(inputMap, row, col);
            }
        }

        // 1974
        System.out.println("Star two: " + starTwo);

    }

    private static int countXmas(char[][] inputMap, int row, int col) {
        if (row < 0 || row >= inputMap.length) {
            return 0;
        }
        if (col < 0 || col >= inputMap[row].length) {
            return 0;
        }
        if (inputMap[row][col] != 'X') {
            return 0;
        }

        String newWord = "MAS";
        return
                countXmas(inputMap, row - 1, col - 1, newWord, -1, -1) +
                countXmas(inputMap, row - 1, col, newWord, -1, 0) +
                countXmas(inputMap, row - 1, col + 1, newWord, -1, 1) +
                countXmas(inputMap, row, col - 1, newWord, 0, -1) +
                countXmas(inputMap, row, col + 1, newWord, 0, 1) +
                countXmas(inputMap, row + 1, col - 1, newWord, 1, -1) +
                countXmas(inputMap, row + 1, col, newWord, 1, 0) +
                countXmas(inputMap, row + 1, col + 1, newWord, 1, 1);
    }

    private static int countXmas(char[][] inputMap, int row, int col, String word, int deltaRow, int deltaCol) {
        if (word.isEmpty()) {
            return 1;
        }
        if (row < 0 || row >= inputMap.length) {
            return 0;
        }
        if (col < 0 || col >= inputMap[row].length) {
            return 0;
        }
        if (inputMap[row][col] != word.charAt(0)) {
            return 0;
        }

        return countXmas(inputMap, row + deltaRow, col + deltaCol, word.substring(1), deltaRow, deltaCol);
    }

    private static int countCross(char[][] inputMap, int row, int col) {
        if (row <= 0 || row >= inputMap.length - 1) {
            return 0;
        }
        if (col <= 0 || col >= inputMap[row].length - 1) {
            return 0;
        }
        if (inputMap[row][col] != 'A') {
            return 0;
        }

        if (
                (
                    inputMap[row - 1][col - 1] == 'M' && inputMap[row + 1][col + 1] == 'S' ||
                    inputMap[row - 1][col - 1] == 'S' && inputMap[row + 1][col + 1] == 'M'
                ) &&
                (
                    inputMap[row - 1][col + 1] == 'M' && inputMap[row + 1][col - 1] == 'S' ||
                    inputMap[row - 1][col + 1] == 'S' && inputMap[row + 1][col - 1] == 'M'
                )
        ) {
            return 1;
        }

        return 0;
    }
}
