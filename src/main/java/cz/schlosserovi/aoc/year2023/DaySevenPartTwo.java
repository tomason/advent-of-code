package cz.schlosserovi.aoc.year2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class DaySevenPartTwo {
    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DaySevenPartTwo.class.getResourceAsStream("/2023/day-7"))))) {
            final SortedSet<Hand> hands = new TreeSet<>();

            String line;
            while ((line = reader.readLine()) != null) {
                hands.add(parseLine(line));
            }

            // part two
            int two = 0;
            int rank = 0;
            for (Hand hand : hands) {
                rank++;
                two += rank * hand.bid;
            }

            System.out.println("Final value (part two): " + two);
        } catch (IOException ex) {
            System.out.println("Could not load input " + ex);
        }
    }

    private static Hand parseLine(String line) {
        List<Card> cards = new ArrayList<>();
        int bid = 0;

        boolean parsingCards = true;
        for (char character : line.toCharArray()) {
            if (character == ' ') {
                parsingCards = false;
            } else if (parsingCards) {
                cards.add(Card.parse(character));
            } else {
                bid = bid * 10 + Character.digit(character, 10);
            }
        }

        return new Hand(cards.toArray(new Card[0]), bid);
    }

    private record Hand(Card[] cards, int bid, HandType type) implements Comparable<Hand> {
        public Hand(Card[] cards, int bid) {
            this(cards, bid, recognizeType(cards));
        }

        private static HandType recognizeType(Card[] cards) {
            Map<Card, Integer> labeledCards = new HashMap<>();

            for (Card card : cards) {
                labeledCards.merge(card, 1, Integer::sum);
            }

            int jokerCount = labeledCards.getOrDefault(Card.J, 0);

            if (labeledCards.size() == 1) {
                return HandType.FIVE_OF_A_KIND;
            }
            if (labeledCards.size() == 2) {
                if (jokerCount == 0) {
                    if (labeledCards.values().stream().mapToInt(Integer::intValue).max().orElseThrow() == 4) {
                        return HandType.FOUR_OF_A_KIND;
                    } else {
                        return HandType.FULL_HOUSE;
                    }
                } else {
                    return HandType.FIVE_OF_A_KIND;
                }
            }
            if (labeledCards.size() == 3) {
                if (jokerCount == 0) {
                    if (labeledCards.values().stream().mapToInt(Integer::intValue).max().orElseThrow() == 3) {
                        return HandType.THREE_OF_A_KIND;
                    } else {
                        return HandType.TWO_PAIR;
                    }
                } else if (jokerCount == 1) {
                    if (labeledCards.values().stream().mapToInt(Integer::intValue).max().orElseThrow() == 3) {
                        // 1 joker, 3 of a kind, one card
                        return HandType.FOUR_OF_A_KIND;
                    } else {
                        // 1 joker, two pairs
                        return HandType.FULL_HOUSE;
                    }
                } else if (jokerCount == 2) {
                    // 2 jokers, pair, one card
                    return HandType.FOUR_OF_A_KIND;
                } else {
                    // 3 jokers, two distinct cards
                    return HandType.FOUR_OF_A_KIND;
                }
            }
            if (labeledCards.size() == 4) {
                if (jokerCount == 0) {
                    return HandType.ONE_PAIR;
                } else if (jokerCount == 1) {
                    // 1 joker, one pair, two distinct cards
                    return HandType.THREE_OF_A_KIND;
                } else {
                    // 2 joker, three distinct cards
                    return HandType.THREE_OF_A_KIND;
                }
            }
            if (labeledCards.size() == 5) {
                if (jokerCount == 0) {
                    return HandType.HIGH_CARD;
                } else {
                    return HandType.ONE_PAIR;
                }
            }
            return null;
        }

        @Override
        public int compareTo(Hand o) {
            int result = type.compareTo(o.type);

            int cardIndex = 0;
            while (result == 0 && cardIndex < 5) {
                result = cards[cardIndex].compareTo(o.cards[cardIndex]);
                cardIndex++;
            }

            return result;
        }
    }

    private enum Card {
        J, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, T, Q, K, A;

        public static Card parse(char input) {
            return switch (input) {
                case 'A' -> Card.A;
                case 'K' -> Card.K;
                case 'Q' -> Card.Q;
                case 'J' -> Card.J;
                case 'T' -> Card.T;
                case '9' -> Card.NINE;
                case '8' -> Card.EIGHT;
                case '7' -> Card.SEVEN;
                case '6' -> Card.SIX;
                case '5' -> Card.FIVE;
                case '4' -> Card.FOUR;
                case '3' -> Card.THREE;
                case '2' -> Card.TWO;
                default -> throw new IllegalArgumentException("Unknown card " + input);
            };
        }
    }

    private enum HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND
    }
}
