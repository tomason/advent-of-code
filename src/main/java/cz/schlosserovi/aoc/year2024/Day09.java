package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day09 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-09-1");

        String dense = lines.get(0);
        int[] denseSizes = dense.chars()
                .map(c -> c - '0')
                .toArray();

        Map<Integer, FilesystemBlock> initialFilesystemMap = new HashMap<>();
        int fsMapIndex = 0;
        for (int i = 0; i < denseSizes.length; i++) {
            FilesystemBlock block;
            if (i % 2 == 0) {
                block = new File(i / 2, denseSizes[i]);
            } else {
                block = new FreeSpace(denseSizes[i]);
            }
            for (int j = 0; j < block.size; j++) {
                initialFilesystemMap.put(fsMapIndex++, block);
            }
        }

        long starOne = 0L;

        /*
        my first try, algorithmically nice, but does not help with second case

        int head = 0;
        int tail = (dense.length() % 2 == 0 ? (dense.length() - 2) : (dense.length() - 1)) + 2;

        int compactedIndex = 0;

        int remainderBack = 0;

        while (head < tail) {
            int size = denseSizes[head];
            if (head % 2 == 0) {
                // file
                for (int i = 0; i < size; i++) {
                    starOne += (long) compactedIndex * (head / 2);
                    compactedIndex++;
                }
            } else {
                // free space
                for (int i = 0; i < size; i++) {
                    if (remainderBack <= 0) {
                        tail = tail - 2; // skip free spaces from back
                        if (tail < head) {
                            // overflown, no more files to move
                            break;
                        }
                        remainderBack = denseSizes[tail];
                    }

                    starOne += (long) compactedIndex * (tail / 2);
                    remainderBack--;
                    compactedIndex++;
                }
            }
            head++;
        }

        while (remainderBack > 0) {
            starOne += (long) compactedIndex * (tail / 2);
            remainderBack--;
            compactedIndex++;
        }
        */

        starOne = calculateChecksum(compactFragment(initialFilesystemMap));

        // 6384282079460
        System.out.println("Star one: " + starOne);

        long starTwo = 0L;

        starTwo = calculateChecksum(compactWholeFiles(initialFilesystemMap));

        // 6408966547049
        System.out.println("Star two: " + starTwo);

    }

    private static Map<Integer, FilesystemBlock> compactFragment(Map<Integer, FilesystemBlock> loose) {
        Map<Integer, FilesystemBlock> result = new HashMap<>();

        int head = 0;
        int tail = loose.size() - 1;

        while (head < tail) {
            FilesystemBlock block = loose.get(head);
            if (block instanceof File) {
                result.put(head, block);
            } else {
                while (loose.get(tail) instanceof FreeSpace) {
                    tail--;
                }
                if (tail < head) {
                    break;
                }
                result.put(head, loose.get(tail--));
            }

            head++;
        }

        return result;
    }

    private static Map<Integer, FilesystemBlock> compactWholeFiles(Map<Integer, FilesystemBlock> loose) {
        Map<Integer, FilesystemBlock> result = new HashMap<>(loose);

        int tail = result.size();

        while (--tail > 0) {
            FilesystemBlock blockToMove = result.get(tail);
            // roll to the beginning of a file
            tail -= blockToMove.size - 1;

            if (blockToMove instanceof FreeSpace) {
                // do not move free blocks
                continue;
            }

            // find first spot to the left that has suitable size
            for (int head = 0; head < tail; ) {
                FilesystemBlock potentialSpace = result.get(head);
                if (potentialSpace instanceof FreeSpace && potentialSpace.size >= blockToMove.size) {
                    FreeSpace newFreeSpace = new FreeSpace(blockToMove.size);
                    // move it here
                    for (int i = 0; i < blockToMove.size; i++) {
                        result.put(head + i, blockToMove);
                        result.put(tail + i, newFreeSpace);
                    }
                    // any space left?
                    if (potentialSpace.size > blockToMove.size) {
                        FreeSpace smallerFreeSpace = new FreeSpace(potentialSpace.size - blockToMove.size);
                        for (int i = 0; i < smallerFreeSpace.size; i++) {
                            result.put(head + blockToMove.size + i, smallerFreeSpace);
                        }
                    }
                    break;
                } else {
                    // try next block
                    head += potentialSpace.size;
                }
            }
        }

        return result;
    }

    private static long calculateChecksum(Map<Integer, FilesystemBlock> filesystemMap) {
        return filesystemMap.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof File)
                .mapToLong(entry -> (long) entry.getKey() * ((File)entry.getValue()).id)
                .sum();
    }

    private static class FilesystemBlock {
        public final int size;

        public FilesystemBlock(int size) {
            this.size = size;
        }
    }

    private static class File extends FilesystemBlock {
        public final int id;
        public File(int id, int size) {
            super(size);
            this.id = id;
        }
    }

    private static class FreeSpace extends FilesystemBlock {
        public FreeSpace(int size) {
            super(size);
        }
    }
}
