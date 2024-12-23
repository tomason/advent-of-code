package cz.schlosserovi.aoc.year2024;

import cz.schlosserovi.aoc.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day23 {
    public static void main(String... args) {
        List<String> lines = Utils.readLines("/2024/day-23-1");

        Map<String, Set<String>> connectionMap = new HashMap<>();

        for (String line : lines) {
            String[] computers = line.split("-");

            String computerA = computers[0];
            Set<String> networkA = connectionMap.computeIfAbsent(computerA, ignored -> new HashSet<>());

            String computerB = computers[1];
            Set<String> networkB = connectionMap.computeIfAbsent(computerB, ignored -> new HashSet<>());


            networkA.add(computerB);
            networkB.add(computerA);
        }

        long starOne = 0L;

        Set<Set<String>> computerTriplets = new HashSet<>();
        for (String one : connectionMap.keySet()) {
            for (String two : connectionMap.get(one)) {
                if (Objects.equals(one, two)) {
                    continue;
                }
                for (String three : connectionMap.get(two)) {
                    if (Objects.equals(one, three) || Objects.equals(two, three) || !connectionMap.get(one).contains(three)) {
                        continue;
                    }
                    computerTriplets.add(Set.of(one, two, three));
                }
            }
        }

        for (Set<String> triplet : computerTriplets) {
            for (String computer : triplet) {
                if (computer.startsWith("t")) {
                    starOne++;
                    break;
                }
            }
        }

        // 1215
        System.out.println("Star one: " + starOne);

        String starTwo = "";

        Set<Set<String>> connectedSets = new HashSet<>();

        for (String initial : connectionMap.keySet()) {
            connectedSets.add(Set.of(initial));
        }

        // the stopping condition should be better, for the puzzle, I expect there will be exactly one solution
        while (connectedSets.size() > 1) {
            Set<Set<String>> largerNetworks = new HashSet<>();
            for (Set<String> currentNetwork : connectedSets) {
                largerNetworks.addAll(extendConnectionNetwork(connectionMap, currentNetwork));
            }

            connectedSets = largerNetworks;
        }

        starTwo = connectedSets.stream()
                .flatMap(Set::stream)
                .sorted()
                .collect(Collectors.joining(","));

        // bm,by,dv,ep,ia,ja,jb,ks,lv,ol,oy,uz,yt
        System.out.println("Star two: " + starTwo);
    }

    private static Set<Set<String>> extendConnectionNetwork(Map<String, Set<String>> connectionMap, Set<String> currentNetwork) {
        return connectionMap.entrySet().stream()
                // any computer that connects to all the already connected ones
                .filter(entry -> entry.getValue().containsAll(currentNetwork))
                .map(entry -> Stream.concat(currentNetwork.stream(), Stream.of(entry.getKey())).collect(Collectors.toSet()))
                .collect(Collectors.toSet());
    }
}
