import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ThreeCardLogic {

    // Hand rankings constants
    public static final int HIGH_CARD = 0;
    public static final int PAIR = 1;
    public static final int FLUSH = 2;
    public static final int STRAIGHT = 3;
    public static final int THREE_OF_A_KIND = 4;
    public static final int STRAIGHT_FLUSH = 5;

    // Evaluate hand and return ranking
    public static int evalHand(ArrayList<Card> hand) {
        if (hand.size() != 3) return HIGH_CARD;

        boolean isFlush = isFlush(hand);
        boolean isStraight = isStraight(hand);
        boolean isThreeOfAKind = isThreeOfAKind(hand);
        boolean isPair = isPair(hand);

        if (isStraight && isFlush) return STRAIGHT_FLUSH;
        if (isThreeOfAKind) return THREE_OF_A_KIND;
        if (isStraight) return STRAIGHT;
        if (isFlush) return FLUSH;
        if (isPair) return PAIR;

        return HIGH_CARD;
    }

    // Calculate Pair Plus winnings
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        if (bet == 0) return 0;

        int handRank = evalHand(hand);

        switch (handRank) {
            case STRAIGHT_FLUSH: return bet * 40;
            case THREE_OF_A_KIND: return bet * 30;
            case STRAIGHT: return bet * 6;
            case FLUSH: return bet * 3;
            case PAIR: return bet * 1;
            default: return 0;
        }
    }

    // Compare dealer and player hands
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int dealerRank = evalHand(dealer);
        int playerRank = evalHand(player);

        // Compare by hand ranking first
        if (playerRank > dealerRank) return 1; // Player wins
        if (playerRank < dealerRank) return -1; // Dealer wins

        // If same rank, compare high cards
        return compareSameRankHands(dealer, player, dealerRank);
    }

    // Check if dealer qualifies (Queen high or better)
    public static boolean dealerQualifies(ArrayList<Card> hand) {
        int rank = evalHand(hand);
        if (rank >= PAIR) return true; // Pair or better qualifies

        // For high card, check if highest card is Queen or better
        ArrayList<Card> sorted = new ArrayList<>(hand);
        sorted.sort(Comparator.comparingInt(Card::getValue).reversed());

        // Adjust Ace value (Ace can be high)
        int highCard = sorted.get(0).getValue();
        if (highCard == 1) return true; // Ace is always qualifying
        return highCard >= 12; // Queen = 12, King = 13
    }

    // Helper methods
    private static boolean isFlush(ArrayList<Card> hand) {
        Suit firstSuit = hand.get(0).getSuit();
        return hand.stream().allMatch(card -> card.getSuit() == firstSuit);
    }

    private static boolean isStraight(ArrayList<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getValue());
        }
        Collections.sort(values);

        // Check for normal straight
        if (values.get(0) + 1 == values.get(1) && values.get(1) + 1 == values.get(2)) {
            return true;
        }

        // Check for Ace-low straight (A-2-3)
        if (values.contains(1) && values.contains(2) && values.contains(3)) {
            return true;
        }

        return false;
    }

    private static boolean isThreeOfAKind(ArrayList<Card> hand) {
        int firstValue = hand.get(0).getValue();
        return hand.stream().allMatch(card -> card.getValue() == firstValue);
    }

    private static boolean isPair(ArrayList<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getValue());
        }

        return values.stream().distinct().count() == 2;
    }

    private static int compareSameRankHands(ArrayList<Card> dealer, ArrayList<Card> player, int rank) {
        // For simplicity in this implementation
        // In a full implementation, you'd compare high cards appropriately
        ArrayList<Integer> dealerValues = getSortedValues(dealer);
        ArrayList<Integer> playerValues = getSortedValues(player);

        for (int i = 2; i >= 0; i--) {
            if (playerValues.get(i) > dealerValues.get(i)) return 1;
            if (playerValues.get(i) < dealerValues.get(i)) return -1;
        }

        return 0; // Tie
    }

    private static ArrayList<Integer> getSortedValues(ArrayList<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            int value = card.getValue();
            // Treat Ace as high for comparison
            if (value == 1) value = 14;
            values.add(value);
        }
        Collections.sort(values);
        return values;
    }
}