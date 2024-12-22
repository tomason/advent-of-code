package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-17-1");

        int registryA = -1;
        int registryB = -1;
        int registryC = -1;
        int[] program = null;
        String programString = null;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String content = line.substring(line.indexOf(':') + 1).trim();
            switch (i) {
                case 0 -> registryA = Integer.parseInt(content);
                case 1 -> registryB = Integer.parseInt(content);
                case 2 -> registryC = Integer.parseInt(content);
                case 4 -> {
                    programString = content;
                    program = Stream.of(content.split(",")).mapToInt(Integer::parseInt).toArray();
                }
            }
        }

        String starOne = runComputer(registryA, registryB, registryC, program);

        // 2,1,4,7,6,0,3,1,4
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        // the program processes 3 bits at a time to produce a number
        // working backwards in the program, shifting by 3 bits every time
        // gives gradually the number required

        for (int programIndex = 0; programIndex < program.length; programIndex++) {
            String expectedOutput = Arrays.stream(program, program.length - 1 - programIndex, program.length)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(","));

            // shift by 3 bits
            starTwo = starTwo * 8;

            for (long i = 0; i < 8; i++) {
                long regA = starTwo + i;

                if (Objects.equals(expectedOutput, runComputer(regA, registryB, registryC, program))) {
                    starTwo += i;
                    break;
                }
            }
        }

        // do a test
        String output = runComputer(starTwo, registryB, registryC, program);
        System.out.println(output);
        if (!Objects.equals(programString, runComputer(starTwo, registryB, registryC, program))) {
            throw new RuntimeException("Bad guess");
        }

        // 266932601404433
        System.out.println("Star two: " + starTwo);

    }

    private static String runComputer(long registryA, long registryB, long registryC, int[] program) {
        StringBuilder output = new StringBuilder();
        int instructionPointer = 0;

        while (instructionPointer < program.length) {
            int instruction = program[instructionPointer];
            int operand = program[instructionPointer + 1];

            long comboValue = switch (operand) {
                case 0, 1, 2, 3 -> operand;
                case 4 -> registryA;
                case 5 -> registryB;
                case 6 -> registryC;
                default -> -1;
            };

            switch (instruction) {
                case 0: // adv - division
                    registryA = registryA / power(2, comboValue);
                    break;
                case 1: // bxl - bitwise XOR
                    registryB = registryB ^ operand;
                    break;
                case 2: // bst - modulo 8
                    registryB = (int) (comboValue % 8);
                    break;
                case 3: // jnz - conditional jump
                    if (registryA != 0) {
                        instructionPointer = operand;
                        continue;
                    }
                    break;
                case 4: // bxc - bitwise XOR
                    registryB = registryB ^ registryC;
                    break;
                case 5: // out - modulo 8 and print
                    if (!output.isEmpty()) {
                        output.append(',');
                    }
                    output.append(comboValue % 8);
                    break;
                case 6: // bdv
                    registryB = registryA / power(2, comboValue);
                    break;
                case 7: // cdv
                    registryC = registryA / power(2, comboValue);
                    break;
            }

            // else jump two instructions
            instructionPointer += 2;
        }

        return output.toString();
    }

    private static boolean runComputerIfItOutputsItsProgram(long registryA, long registryB, long registryC, int[] program) {
        int programPointer = 0;
        int instructionPointer = 0;

        while (instructionPointer < program.length) {
            int instruction = program[instructionPointer];
            int operand = program[instructionPointer + 1];

            long comboValue = switch (operand) {
                case 0, 1, 2, 3 -> operand;
                case 4 -> registryA;
                case 5 -> registryB;
                case 6 -> registryC;
                default -> -1;
            };

            switch (instruction) {
                case 0: // adv - division
                    registryA = registryA / power(2, comboValue);
                    break;
                case 1: // bxl - bitwise XOR
                    registryB = registryB ^ operand;
                    break;
                case 2: // bst - modulo 8
                    registryB = (int) (comboValue % 8);
                    break;
                case 3: // jnz - conditional jump
                    if (registryA != 0) {
                        instructionPointer = operand;
                        continue;
                    }
                    break;
                case 4: // bxc - bitwise XOR
                    registryB = registryB ^ registryC;
                    break;
                case 5: // out - modulo 8 and print
                    int output = (int) comboValue % 8;

                    if (programPointer >= program.length || program[programPointer] != output) {
                        return false;
                    } else {
                        programPointer++;
                    }
                    break;
                case 6: // bdv
                    registryB = registryA / power(2, comboValue);
                    break;
                case 7: // cdv
                    registryC = registryA / power(2, comboValue);
                    break;
            }

            // else jump two instructions
            instructionPointer += 2;
        }

        return programPointer == program.length;
    }

    private static long power(long base, long exp) {
        if (exp == 0) {
            return 1;
        }
        if (exp == 1) {
            return base;
        }
        long powerHalf = power(base, exp / 2);
        if (exp % 2 == 0) {
            return powerHalf * powerHalf;
        } else {
            return base * powerHalf * powerHalf;
        }
    }
}
