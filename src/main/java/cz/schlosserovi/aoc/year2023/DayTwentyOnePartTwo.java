package cz.schlosserovi.aoc.year2023;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DayTwentyOnePartTwo {
    private static final int ITERATIONS2 = 26501365;

    private static final int[] baseSeriesValues = new int[]{4, 9, 16, 24, 34, 47, 58, 76, 86, 108, 125, 152, 170, 196, 213, 248, 270, 317, 338, 387, 413, 463, 494, 553, 581, 643, 675, 740, 772, 839, 872, 939, 975, 1052, 1093, 1163, 1229, 1291, 1363, 1423, 1505, 1562, 1655, 1708, 1806, 1872, 1963, 2032, 2135, 2203, 2310, 2373, 2487, 2542, 2662, 2731, 2854, 2928, 3051, 3133, 3255, 3376, 3512, 3642, 3776, 3902, 4032, 4154, 4245, 4359, 4444, 4556, 4637, 4749, 4824, 4938, 4991, 5110, 5172, 5283, 5337, 5452, 5507, 5617, 5659, 5769, 5808, 5920, 5953, 6064, 6096, 6196, 6228, 6322, 6352, 6447, 6463, 6559, 6575, 6664, 6674, 6767, 6768, 6863, 6862, 6957, 6942, 7037, 7015, 7106, 7087, 7166, 7150, 7225, 7213, 7281, 7264, 7324, 7308, 7361, 7347, 7393, 7376, 7418, 7398, 7435, 7412, 7446};
    private static final int[] allSidesValues = new int[]{4, 12, 24, 40, 60, 84, 110, 142, 172, 213, 248, 296, 335, 394, 437, 506, 555, 630, 687, 772, 835, 923, 990, 1088, 1158, 1270, 1354, 1461, 1544, 1656, 1739, 1877, 1967, 2111, 2208, 2356, 2465, 2612, 2716, 2876, 2999, 3175, 3297, 3473, 3606, 3789, 3925, 4120, 4264, 4467, 4594, 4815, 4959, 5175, 5312, 5543, 5712, 5937, 6117, 6353, 6526, 6759, 6950, 7193, 7398, 7661, 7868, 8123, 8313, 8560, 8753, 9013, 9198, 9473, 9666, 9914, 10105, 10364, 10557, 10798, 10979, 11249, 11430, 11719, 11889, 12178, 12352, 12633, 12808, 13108, 13269, 13558, 13719, 14010, 14164, 14454, 14605, 14890, 15040, 15338, 15477, 15752, 15935, 16197, 16395, 16645, 16850, 17095, 17310, 17538, 17764, 18013, 18212, 18454, 18675, 18909, 19127, 19347, 19568, 19769, 19994, 20218, 20440, 20672, 20884, 21126, 21328, 21640, 21862, 22184, 22394, 22704, 22906, 23208, 23332, 23618, 23730, 24012, 24116, 24398, 24490, 24776, 24824, 25120, 25186, 25466, 25516, 25804, 25856, 26134, 26160, 26438, 26458, 26740, 26748, 27028, 27034, 27292, 27298, 27544, 27546, 27794, 27768, 28018, 27992, 28228, 28190, 28434, 28378, 28626, 28566, 28814, 28726, 28974, 28872, 29112, 29016, 29232, 29142, 29350, 29268, 29462, 29370, 29548, 29458, 29622, 29536, 29686, 29594, 29736, 29638, 29770, 29666, 29792};
    private static final int[] allDiagonalsValues = new int[]{4, 8, 15, 23, 32, 43, 57, 74, 89, 111, 125, 155, 169, 206, 225, 266, 280, 328, 341, 400, 411, 476, 487, 551, 574, 643, 671, 738, 778, 842, 878, 948, 993, 1064, 1120, 1186, 1249, 1321, 1379, 1458, 1520, 1602, 1669, 1751, 1825, 1905, 1988, 2071, 2157, 2239, 2323, 2416, 2500, 2589, 2682, 2770, 2877, 2962, 3077, 3167, 3285, 3376, 3540, 3643, 3808, 3909, 4076, 4181, 4352, 4424, 4592, 4661, 4842, 4908, 5097, 5160, 5346, 5413, 5613, 5692, 5894, 5988, 6185, 6276, 6485, 6578, 6791, 6881, 7101, 7202, 7424, 7527, 7734, 7852, 8045, 8171, 8378, 8510, 8719, 8850, 9060, 9201, 9413, 9552, 9771, 9922, 10158, 10303, 10536, 10687, 10919, 11069, 11315, 11467, 11717, 11841, 12110, 12251, 12512, 12646, 12916, 13075, 13338, 13502, 13778, 13931, 14200, 14367, 14642, 14819, 15110, 15285, 15564, 15718, 15986, 16140, 16416, 16561, 16847, 17001, 17256, 17401, 17662, 17808, 18052, 18187, 18451, 18581, 18852, 18972, 19241, 19360, 19620, 19735, 20005, 20109, 20365, 20465, 20720, 20813, 21065, 21154, 21401, 21486, 21736, 21805, 22039, 22127, 22356, 22453, 22672, 22766, 22983, 23076, 23280, 23379, 23591, 23670, 23872, 23961, 24156, 24238, 24424, 24502, 24677, 24753, 24937, 25007, 25194, 25254, 25443, 25494, 25714, 25771, 25992, 26039, 26252, 26295, 26504, 26508, 26709, 26707, 26906, 26900, 27099, 27087, 27288, 27254, 27460, 27435, 27633, 27600, 27802, 27770, 27967, 27922, 28119, 28071, 28270, 28216, 28414, 28359, 28546, 28491, 28672, 28615, 28797, 28726, 28909, 28838, 29014, 28937, 29117, 29031, 29213, 29125, 29307, 29205, 29387, 29278, 29456, 29350, 29516, 29413, 29575, 29476, 29631, 29527, 29674, 29571, 29711, 29610, 29743, 29639, 29768, 29661, 29785, 29675, 29796};

    private static final String BASE = "BASE";

    public static void main(String... args) {
        long result = 0L;


        Map<String, AtomicInteger> baseInProgress = new ConcurrentHashMap<>();
        Map<Integer, AtomicInteger> sidesInProgress = new ConcurrentHashMap<>();
        Map<Integer, AtomicInteger> diagonalsInProgress = new ConcurrentHashMap<>();
        baseInProgress.put(BASE, new AtomicInteger());

        long[] oscillating = new long[2];

        AtomicInteger sides = new AtomicInteger();
        AtomicInteger diagonals = new AtomicInteger();

        for (int iteration = 0; iteration < ITERATIONS2; iteration++) {
            // to be used in lambda
            final int i = iteration;

            // count the result
            result = oscillating[i % 2] +
                    // count diagonals first
                    diagonalsInProgress.entrySet().stream()
                            .mapToLong(entry -> {
                                int turn = entry.getValue().getAndIncrement();

                                // diagonal series
                                if (turn < 0) {
                                    return 0;
                                } else if (turn < allDiagonalsValues.length) {
                                    return allDiagonalsValues[turn];
                                } else {
                                    oscillating[i % 2] += 4 * 7421;
                                    oscillating[(i + 1) % 2] += 4 * 7450;
                                    diagonalsInProgress.remove(entry.getKey());
                                    return 4 * 7421;
                                }
                            }).sum() +
                    // then sides
                    sidesInProgress.entrySet().stream()
                            .mapToLong(entry -> {
                                int turn = entry.getValue().getAndIncrement();

                                if (turn == 65) {
                                    diagonalsInProgress.put(diagonals.incrementAndGet(), new AtomicInteger());
                                    for (int additional = 1; additional < entry.getKey(); additional++) {
                                        // add extra diagonal series
                                        diagonalsInProgress.put(diagonals.incrementAndGet(), new AtomicInteger());
                                    }
                                }

                                // start a new side
                                if (turn == 131) {
                                    sidesInProgress.put(sides.incrementAndGet(), new AtomicInteger());
                                }

                                // side series
                                if (turn < allSidesValues.length) {
                                    return allSidesValues[turn];
                                } else {
                                    oscillating[i % 2] += 4 * 7421;
                                    oscillating[(i + 1) % 2] += 4 * 7450;
                                    sidesInProgress.remove(entry.getKey());
                                    return 4 * 7421;
                                }
                            }).sum() +

                    // then base
                    baseInProgress.entrySet().stream()
                            .mapToLong(entry -> {
                                int turn = entry.getValue().getAndIncrement();

                                // add side series
                                if (turn == 64) {
                                    sidesInProgress.put(sides.incrementAndGet(), new AtomicInteger());
                                }

                                // base series
                                if (turn < baseSeriesValues.length) {
                                    return baseSeriesValues[turn];
                                } else {
                                    // just add to the oscillating part and remove from "inProgress"
                                    oscillating[i % 2] += 7421;
                                    oscillating[(i + 1) % 2] += 7450;
                                    baseInProgress.remove(entry.getKey());
                                    return 7421;
                                }
                            }).sum();
            if (i < 2000) {
                System.out.printf("%8d,%20d%n", i + 1, result);
            } else {
                System.out.print(".");
            }
        }

        System.out.println();
        System.out.println("Done: " + result);
    }
}
