package cz.schlosserovi.aoc.year2025;

import cz.schlosserovi.aoc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;

public class Day10 {
    static void main() {
        var lines = Utils.readLines("/2025/day-10-1");

        var one = 0L;

        nextMachine:
        for (var line : lines) {
            var split = line.split(" ");

            // parse target state in format '[...###.#.]'
            var targetState = new int[split[0].length() - 2];
            for (var i = 0; i < targetState.length; i++) {
                targetState[i] = split[0].charAt(i + 1) == '#' ? 1 : 0;
            }

            // parse buttons
            var buttons = new ArrayList<int[]>();
            for (var i = 1; i < split.length - 1; i++) {
                var button = new int[targetState.length];
                Arrays.fill(button, 0);

                var buttonInput = split[i].substring(1, split[i].length() - 1);
                for (var input : buttonInput.split(",")) {
                    button[Integer.parseInt(input)] = 1;
                }

                buttons.add(button);
            }

            // ignore joltage levels


            // try all combinations of buttons
            var combinations = new HashSet<Set<int[]>>();
            for (var button : buttons) {
                combinations.add(Set.of(button));
            }

            var initialSize = 0;
            while (combinations.size() != initialSize) {
                initialSize = combinations.size();

                for (var button : buttons) {
                    var newCombinations = new HashSet<Set<int[]>>();

                    for (var existing : combinations) {
                        var newCombination = new HashSet<>(existing);
                        if (newCombination.add(button)) {
                            newCombinations.add(newCombination);
                        }
                    }

                    combinations.addAll(newCombinations);
                }
            }

            var orderedCombinations = combinations.stream()
                .sorted(Comparator.comparingInt(Set::size))
                .toList();
            for (var combination : orderedCombinations) {
                var result = new int[targetState.length];
                Arrays.fill(result, 0);
                combination.forEach(button -> {
                        for (var i = 0; i < button.length; i++) {
                            result[i] = (result[i] + button[i]) % 2;
                        }
                    }
                );

                if (Arrays.equals(targetState, result)) {
                    one += combination.size();
                    continue nextMachine;
                }
            }
        }

        // 532
        System.out.println("Part one: " + one);

        var two = 0L;

        two = lines.stream()
            .mapToLong(line -> {
                var split = line.split(" ");

                var targetLength = split[0].length() - 2;
                // ignore target state

                // parse buttons
                var buttons = Arrays.stream(split, 1, split.length - 1)
                    .map(str -> str.substring(1, str.length() - 1))
                    .map(str -> {
                            var values = new int[targetLength];
                            Stream.of(str.split(","))
                                .map(Integer::parseInt)
                                .forEach(i -> values[i] = 1);
                            return values;
                        }
                    )
                    .toList();


                // joltage levels
                var joltageLevelsStr = split[split.length - 1];
                joltageLevelsStr = joltageLevelsStr.substring(1, joltageLevelsStr.length() - 1);

                var joltageLevelSplit = joltageLevelsStr.split(",");
                var joltageLevels = new int[targetLength];
                for (var i = 0; i < joltageLevels.length; i++) {
                    joltageLevels[i] = Short.parseShort(joltageLevelSplit[i]);
                }

                var lineNo = lines.indexOf(line) + 1;
                var partTwo = solveMatrix(joltageLevels, buttons);

                System.out.printf("%3s/%s ... lowest count: %d%n", lineNo, lines.size(), partTwo);
                return partTwo;
            })
            .sum();

        // 18387
        System.out.println("Part two: " + two);
    }

    private static int solveMatrix(int[] targetState, List<int[]> buttons) {
        var width = buttons.size() + 1;
        var height = targetState.length;

        // construct matrix
        int[][] matrix = constructExtendedMatrix(targetState, buttons);
        //printExtendedMatrix(matrix);

        // get to reduced row echelon form
        // it's not truly an echelon form as the diagonal is not normalized to 1 because I'm using integers
        gaussJordanAlgorithm(matrix);
        //printExtendedMatrix(matrix);

        // construct results
        var results = new int[buttons.size()];
        var variables = new HashMap<Integer, Variable>();
        var resultCol = 0;
        for (var resultRow = 0; resultRow < height; resultRow++) {
            for (; resultCol < results.length; resultCol++) {
                if (matrix[resultRow][resultCol] != 0) {
                    break;
                }
            }

            if (resultCol == width - 1) {
                // empty row, continue
                continue;
            }

            var thisButtonIndex = resultCol;
            for (var i = resultCol + 1; i < width - 1; i++) {
                if (matrix[resultRow][i] != 0) {
                    var dependentButtonIndex = i;
                    var total = matrix[resultRow][width - 1];

                    var thisVariable = variables.computeIfAbsent(resultCol, buttonNumber ->
                        Variable.dependentVariable(buttonNumber, total, buttons.get(buttonNumber)));
                    var freeVariable = variables.computeIfAbsent(i, buttonNumber ->
                        Variable.freeVariable(buttonNumber, buttons.get(buttonNumber)));

                    thisVariable.addDependency(freeVariable);

                    var variableFactor = matrix[resultRow][i];
                    thisVariable.addVariableUseFunction((state, ignored) ->
                        state[thisButtonIndex] = state[thisButtonIndex] - variableFactor * state[dependentButtonIndex]);

                    // TODO add limits to free variables based on possible limits of base variable
                    // unfortunately, bounding the variables is problematic when there are more than one dependency
                    /*
                    if (variableFactor > 0) {
                        freeVariable.addUpperBoundCalculation(target -> total / variableFactor);
                    } else {
                        freeVariable.addLowerBoundCalculation(target -> total / variableFactor);
                    }
                     */
                }
            }

            var factor = matrix[resultRow][resultCol];

            if (variables.containsKey(resultCol)) {
                if (factor != 1) {
                    // divide any previous operations with a row factor
                    variables.get(resultCol).addVariableUseFunction((state, ignored) ->
                        state[thisButtonIndex] = state[thisButtonIndex] / factor);
                }
                // skip to the next column
                continue;
            }

            var value = matrix[resultRow][width - 1];
            if (value % factor != 0) {
                printExtendedMatrix(matrix);
                throw new ArithmeticException("Decimal in result");
            }
            results[resultCol] = value / factor;
        }

        // incomplete solution, use brute force to compute free variables
        if (!variables.isEmpty()) {
            printExtendedMatrix(matrix);
            var sortedVariables = variables.values().stream()
                .sorted(Comparator.comparingInt(Variable::dependencies))
                .toList();
            results = bruteForceTheFreeVariables(results, sortedVariables, targetState, buttons);
        }


        // test solution
        if (!testSolution(results, targetState, buttons)) {
            throw new ArithmeticException("Bad solution");
        }

        return IntStream.of(results).sum();

    }

    private static int[][] constructExtendedMatrix(int[] targetState, List<int[]> buttons) {
        int[][] matrix = new int[targetState.length][];
        for (var i = 0; i < matrix.length; i++) {
            matrix[i] = new int[buttons.size() + 1];

            for (var b = 0; b < buttons.size(); b++) {
                var button = buttons.get(b);
                matrix[i][b] = button[i];
            }
        }
        for (var i = 0; i < matrix.length; i++) {
            matrix[i][matrix[i].length - 1] = targetState[i];
        }

        return matrix;
    }

    private static void gaussJordanAlgorithm(int[][] matrix) {
        var height = matrix.length;
        var width = matrix[0].length;

        var pivotRow = 0;
        for (var pivotCol = 0; pivotCol < width - 1; pivotCol++) {
            // find first row that has nonzero value on the required place
            var selectedRow = pivotRow;
            for (var row = pivotRow; row < height; row++) {
                if (matrix[row][pivotCol] != 0 && (matrix[selectedRow][pivotCol] == 0 || abs(matrix[row][pivotCol]) < abs(matrix[selectedRow][pivotCol]))) {
                    selectedRow = row;
                }
            }

            if (selectedRow >= height || matrix[selectedRow][pivotCol] == 0) {
                // no variables below, try moving the column to the end
                continue;
            }

            // swap selected row to top
            if (selectedRow != pivotRow) {
                var temp = matrix[pivotRow];
                matrix[pivotRow] = matrix[selectedRow];
                matrix[selectedRow] = temp;
            }

            // normalize to 1 only if all following numbers can be divided by the pivot
            var pivotFactor = matrix[pivotRow][pivotCol];
            if (pivotFactor != 1) {
                var divisible = true;
                for (var i = 0; i < width; i++) {
                    if (matrix[pivotRow][i] % pivotFactor != 0) {
                        divisible = false;
                        break;
                    }
                }
                if (!divisible && pivotFactor < 0) {
                    // not divisible, but negative factor, just switch signs
                    divisible = true;
                    pivotFactor = -1;
                }
                if (divisible) {
                    for (var i = 0; i < width; i++) {
                        matrix[pivotRow][i] = matrix[pivotRow][i] / pivotFactor;
                    }
                }
                // refresh pivot factor
                pivotFactor = matrix[pivotRow][pivotCol];
            }

            // eliminate above and below diagonal
            for (var row = 0; row < height; row++) {
                var rowFactor = matrix[row][pivotCol];
                if (row == pivotRow || rowFactor == 0) {
                    continue;
                }
                for (var i = 0; i < width; i++) {
                    matrix[row][i] = pivotFactor * matrix[row][i] - rowFactor * matrix[pivotRow][i];
                }
            }


            pivotRow++;
        }

        for (var row = pivotRow; row < height; row++) {
            // verify any spare rows are zeroed out
            if (matrix[row][width - 1] != 0) {
                throw new ArithmeticException("Spare rows bellow diagonal give result other than zero");
            }
        }
    }

    private static int[] bruteForceTheFreeVariables(int[] currentResult, List<Variable> variables, int[] targetState, List<int[]> buttons) {
        var simplifiedTargetState = Arrays.copyOf(targetState, targetState.length);
        for (var i = 0; i < currentResult.length; i++) {
            var button = buttons.get(i);
            for (var j = 0; j < targetState.length; j++) {
                simplifiedTargetState[j] -= button[j] * currentResult[i];
            }
        }

        var variableValues = new HashSet<int[]>();
        variableValues.add(new int[0]);

        for (var variable : variables) {
            var low = variable.calculateLowerBound(simplifiedTargetState);
            var high = variable.calculateUpperBound(simplifiedTargetState);

            var oldValues = new HashSet<>(variableValues);
            variableValues.clear();
            if (variable.dependencies() > 0) {
                // generated value, just append 0 and ignore
                for (var old : oldValues) {
                    var newValues = IntStream.concat(IntStream.of(old), IntStream.of(0)).toArray();
                    variableValues.add(newValues);
                }
            } else {
                for (var j = low; j <= high; j++) {
                    for (var old : oldValues) {
                        var newValues = IntStream.concat(IntStream.of(old), IntStream.of(j)).toArray();
                        variableValues.add(newValues);
                    }
                }
            }
        }

        int[] bestResult = null;
        var bestScore = Integer.MAX_VALUE;
        for (var values : variableValues) {
            var result = currentResult;
            for (var i = 0; i < variables.size(); i++) {
                result = variables.get(i).calculateGivenValue(result, values[i]);
            }
            var score = IntStream.of(result).sum();
            if (score < bestScore && testSolution(result, targetState, buttons)) {
                bestResult = result;
                bestScore = score;
            }
        }

        return bestResult;
    }

    private static boolean testSolution(int[] potentialSolution, int[] targetState, List<int[]> buttons) {
        var counters = new int[targetState.length];
        for (var b = 0; b < buttons.size(); b++) {
            var button = buttons.get(b);
            var clicks = potentialSolution[b];
            if (clicks < 0) {
                return false;
            }
            for (var i = 0; i < button.length; i++) {
                counters[i] = counters[i] + clicks * button[i];
            }
        }

        return Arrays.equals(counters, targetState);
    }

    private static void printExtendedMatrix(int[][] extendedMatrix) {
        StringBuilder stringBuilder = new StringBuilder();

        for (var row : extendedMatrix) {
            for (var c = 0; c < row.length; c++) {
                if (c > 0) {
                    stringBuilder.append(" ");
                }
                if (c == row.length - 1) {
                    stringBuilder.append("| ");
                }
                stringBuilder.append(row[c]);

            }
            stringBuilder.append("\n");
        }

        System.out.println(stringBuilder);
    }

    private static class Variable {
        private final int buttonIndex;
        private final Collection<Function<int[], Integer>> lowerLimits = new ArrayList<>();
        private final Collection<Function<int[], Integer>> upperLimits = new ArrayList<>();
        private final List<BiConsumer<int[], Integer>> variableUses = new ArrayList<>();
        private final Set<Variable> dependencies = new HashSet<>();

        public static Variable freeVariable(int buttonIndex, int[] button) {
            Variable result = new Variable(buttonIndex);

            // default lower limit is 0 (no negative button presses possible
            result.addLowerBoundCalculation(partialResults -> 0);

            // default upper limit is the maximum number a button can be pressed
            result.addUpperBoundCalculation(partialResults -> IntStream.range(0, button.length)
                .filter(idx -> button[idx] == 1)
                .map(idx -> partialResults[idx])
                .min().orElse(Integer.MAX_VALUE));

            // by default set the value of this variable
            result.addVariableUseFunction((state, value) -> state[buttonIndex] = value);

            return result;
        }

        public static Variable dependentVariable(int buttonIndex, int initialValue, int[] button) {
            Variable result = new Variable(buttonIndex);

            // default lower limit is 0 (no negative button presses possible
            result.addLowerBoundCalculation(partialResults -> 0);

            // default upper limit is the maximum number a button can be pressed
            result.addUpperBoundCalculation(partialResults -> IntStream.range(0, button.length)
                .filter(idx -> button[idx] == 1)
                .map(idx -> partialResults[idx])
                .min().orElse(Integer.MAX_VALUE));

            // set initial value
            result.addVariableUseFunction((state, ignored) -> state[buttonIndex] = initialValue);

            return result;
        }

        private Variable(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        public void addLowerBoundCalculation(Function<int[], Integer> limitFunction) {
            lowerLimits.add(limitFunction);
        }

        public void addUpperBoundCalculation(Function<int[], Integer> limitFunction) {
            upperLimits.add(limitFunction);
        }

        public void addVariableUseFunction(BiConsumer<int[], Integer> useFunction) {
            variableUses.add(useFunction);
        }

        public int calculateLowerBound(int[] targetStates) {
            return lowerLimits.stream()
                .mapToInt(function -> function.apply(targetStates))
                .max()
                .orElse(0);
        }

        public int calculateUpperBound(int[] targetStates) {
            return upperLimits.stream()
                .mapToInt(function -> function.apply(targetStates))
                .min()
                .orElse(Integer.MAX_VALUE);
        }

        public int[] calculateGivenValue(int[] currentState, int variableValue) {
            var result = Arrays.copyOf(currentState, currentState.length);

            variableUses.forEach(useFunction -> useFunction.accept(result, variableValue));

            return result;
        }

        public void addDependency(Variable variable) {
            dependencies.add(variable);
        }

        public int dependencies() {
            return dependencies.size();
        }

        @Override
        public String toString() {
            return "Variable{" +
                "buttonIndex=" + buttonIndex +
                '}';
        }
    }
}