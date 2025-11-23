import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ThreeCardLogicTest {

    private ArrayList<Card> straightFlushHand;
    private ArrayList<Card> threeOfAKindHand;
    private ArrayList<Card> straightHand;
    private ArrayList<Card> flushHand;
    private ArrayList<Card> pairHand;
    private ArrayList<Card> highCardHand;

    @BeforeEach
    void setUp() {
        // Straight Flush: 10, 9, 8 of Hearts
        straightFlushHand = new ArrayList<>();
        straightFlushHand.add(new Card(Suit.HEARTS, 10));
        straightFlushHand.add(new Card(Suit.HEARTS, 9));
        straightFlushHand.add(new Card(Suit.HEARTS, 8));

        // Three of a Kind: 7, 7, 7
        threeOfAKindHand = new ArrayList<>();
        threeOfAKindHand.add(new Card(Suit.HEARTS, 7));
        threeOfAKindHand.add(new Card(Suit.DIAMONDS, 7));
        threeOfAKindHand.add(new Card(Suit.CLUBS, 7));

        // Straight: 6, 5, 4 (mixed suits)
        straightHand = new ArrayList<>();
        straightHand.add(new Card(Suit.HEARTS, 6));
        straightHand.add(new Card(Suit.DIAMONDS, 5));
        straightHand.add(new Card(Suit.CLUBS, 4));

        // Flush: K, 9, 7 of Spades
        flushHand = new ArrayList<>();
        flushHand.add(new Card(Suit.SPADES, 13)); // King
        flushHand.add(new Card(Suit.SPADES, 9));
        flushHand.add(new Card(Suit.SPADES, 7));

        // Pair: K, K, 9
        pairHand = new ArrayList<>();
        pairHand.add(new Card(Suit.HEARTS, 13)); // King
        pairHand.add(new Card(Suit.DIAMONDS, 13)); // King
        pairHand.add(new Card(Suit.CLUBS, 9));

        // High Card: A, K, 7 (no pair, no flush, no straight)
        highCardHand = new ArrayList<>();
        highCardHand.add(new Card(Suit.HEARTS, 1));  // Ace
        highCardHand.add(new Card(Suit.DIAMONDS, 13)); // King
        highCardHand.add(new Card(Suit.CLUBS, 7));
    }

    // ===================== evalHand TESTS =====================

    @Test
    void testEvalHand_StraightFlush() {
        int result = ThreeCardLogic.evalHand(straightFlushHand);
        assertEquals(ThreeCardLogic.STRAIGHT_FLUSH, result,
                "Straight Flush should return STRAIGHT_FLUSH constant");
    }

    @Test
    void testEvalHand_ThreeOfAKind() {
        int result = ThreeCardLogic.evalHand(threeOfAKindHand);
        assertEquals(ThreeCardLogic.THREE_OF_A_KIND, result,
                "Three of a Kind should return THREE_OF_A_KIND constant");
    }

    @Test
    void testEvalHand_Straight() {
        int result = ThreeCardLogic.evalHand(straightHand);
        assertEquals(ThreeCardLogic.STRAIGHT, result,
                "Straight should return STRAIGHT constant");
    }

    @Test
    void testEvalHand_Flush() {
        int result = ThreeCardLogic.evalHand(flushHand);
        assertEquals(ThreeCardLogic.FLUSH, result,
                "Flush should return FLUSH constant");
    }

    @Test
    void testEvalHand_Pair() {
        int result = ThreeCardLogic.evalHand(pairHand);
        assertEquals(ThreeCardLogic.PAIR, result,
                "Pair should return PAIR constant");
    }

    @Test
    void testEvalHand_HighCard() {
        int result = ThreeCardLogic.evalHand(highCardHand);
        assertEquals(ThreeCardLogic.HIGH_CARD, result,
                "High Card should return HIGH_CARD constant");
    }

    @Test
    void testEvalHand_AceLowStraight() {
        // Ace-low straight: A, 2, 3
        ArrayList<Card> aceLowStraight = new ArrayList<>();
        aceLowStraight.add(new Card(Suit.HEARTS, 1));  // Ace
        aceLowStraight.add(new Card(Suit.DIAMONDS, 2));
        aceLowStraight.add(new Card(Suit.CLUBS, 3));

        int result = ThreeCardLogic.evalHand(aceLowStraight);
        assertEquals(ThreeCardLogic.STRAIGHT, result,
                "Ace-low straight (A-2-3) should be recognized as Straight");
    }

    @Test
    void testEvalHand_InvalidHandSize() {
        ArrayList<Card> invalidHand = new ArrayList<>();
        invalidHand.add(new Card(Suit.HEARTS, 10));
        invalidHand.add(new Card(Suit.HEARTS, 9));
        // Only 2 cards - should return HIGH_CARD

        int result = ThreeCardLogic.evalHand(invalidHand);
        assertEquals(ThreeCardLogic.HIGH_CARD, result,
                "Invalid hand size should return HIGH_CARD");
    }

    // ===================== evalPPWinnings TESTS =====================

    @Test
    void testEvalPPWinnings_StraightFlush() {
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(straightFlushHand, bet);
        assertEquals(400, winnings,
                "Straight Flush should pay 40:1 (10 * 40 = 400)");
    }

    @Test
    void testEvalPPWinnings_ThreeOfAKind() {
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(threeOfAKindHand, bet);
        assertEquals(300, winnings,
                "Three of a Kind should pay 30:1 (10 * 30 = 300)");
    }

    @Test
    void testEvalPPWinnings_Straight() {
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(straightHand, bet);
        assertEquals(60, winnings,
                "Straight should pay 6:1 (10 * 6 = 60)");
    }

    @Test
    void testEvalPPWinnings_Flush() {
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(flushHand, bet);
        assertEquals(30, winnings,
                "Flush should pay 3:1 (10 * 3 = 30)");
    }

    @Test
    void testEvalPPWinnings_Pair() {
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(pairHand, bet);
        assertEquals(10, winnings,
                "Pair should pay 1:1 (10 * 1 = 10)");
    }

    @Test
    void testEvalPPWinnings_HighCard() {
        int bet = 10;
        int winnings = ThreeCardLogic.evalPPWinnings(highCardHand, bet);
        assertEquals(0, winnings,
                "High Card should pay 0 (lose the bet)");
    }

    @Test
    void testEvalPPWinnings_ZeroBet() {
        int winnings = ThreeCardLogic.evalPPWinnings(straightFlushHand, 0);
        assertEquals(0, winnings,
                "Zero bet should return 0 winnings regardless of hand");
    }

    // ===================== compareHands TESTS =====================

    @Test
    void testCompareHands_PlayerWinsWithBetterHand() {
        // Player has Straight Flush, Dealer has Three of a Kind
        int result = ThreeCardLogic.compareHands(threeOfAKindHand, straightFlushHand);
        assertEquals(1, result,
                "Player should win when they have a better hand (Straight Flush vs Three of a Kind)");
    }

    @Test
    void testCompareHands_DealerWinsWithBetterHand() {
        // Dealer has Straight Flush, Player has Three of a Kind
        int result = ThreeCardLogic.compareHands(straightFlushHand, threeOfAKindHand);
        assertEquals(-1, result,
                "Dealer should win when they have a better hand (Straight Flush vs Three of a Kind)");
    }

    @Test
    void testCompareHands_SameHandRankPlayerWinsWithHigherCards() {
        // Both have Flush, but Player has higher cards
        ArrayList<Card> dealerFlush = new ArrayList<>();
        dealerFlush.add(new Card(Suit.HEARTS, 10));
        dealerFlush.add(new Card(Suit.HEARTS, 8));
        dealerFlush.add(new Card(Suit.HEARTS, 6));

        ArrayList<Card> playerFlush = new ArrayList<>();
        playerFlush.add(new Card(Suit.SPADES, 12)); // Queen
        playerFlush.add(new Card(Suit.SPADES, 9));
        playerFlush.add(new Card(Suit.SPADES, 7));

        int result = ThreeCardLogic.compareHands(dealerFlush, playerFlush);
        assertEquals(1, result,
                "Player should win with higher cards when both have same hand rank (Flush)");
    }

    @Test
    void testCompareHands_Tie() {
        // Both have exactly the same hand (same rank and same high cards)
        ArrayList<Card> identicalHand1 = new ArrayList<>();
        identicalHand1.add(new Card(Suit.HEARTS, 10));
        identicalHand1.add(new Card(Suit.HEARTS, 9));
        identicalHand1.add(new Card(Suit.HEARTS, 8));

        ArrayList<Card> identicalHand2 = new ArrayList<>();
        identicalHand2.add(new Card(Suit.SPADES, 10));
        identicalHand2.add(new Card(Suit.SPADES, 9));
        identicalHand2.add(new Card(Suit.SPADES, 8));

        int result = ThreeCardLogic.compareHands(identicalHand1, identicalHand2);
        assertEquals(0, result,
                "Should return 0 for a tie when hands are identical in rank and high cards");
    }

    @Test
    void testCompareHands_PairVsHighCard() {
        int result = ThreeCardLogic.compareHands(pairHand, highCardHand);
        assertEquals(-1, result,
                "Player should win with Pair vs Dealer's High Card");
    }

    // ===================== dealerQualifies TESTS =====================

    @Test
    void testDealerQualifies_WithPair() {
        boolean qualifies = ThreeCardLogic.dealerQualifies(pairHand);
        assertTrue(qualifies,
                "Dealer should qualify with a Pair");
    }

    @Test
    void testDealerQualifies_WithQueenHigh() {
        // Queen high hand
        ArrayList<Card> queenHigh = new ArrayList<>();
        queenHigh.add(new Card(Suit.HEARTS, 12)); // Queen
        queenHigh.add(new Card(Suit.DIAMONDS, 8));
        queenHigh.add(new Card(Suit.CLUBS, 5));

        boolean qualifies = ThreeCardLogic.dealerQualifies(queenHigh);
        assertTrue(qualifies,
                "Dealer should qualify with Queen high");
    }

    @Test
    void testDealerQualifies_WithKingHigh() {
        // King high hand
        ArrayList<Card> kingHigh = new ArrayList<>();
        kingHigh.add(new Card(Suit.HEARTS, 13)); // King
        kingHigh.add(new Card(Suit.DIAMONDS, 7));
        kingHigh.add(new Card(Suit.CLUBS, 4));

        boolean qualifies = ThreeCardLogic.dealerQualifies(kingHigh);
        assertTrue(qualifies,
                "Dealer should qualify with King high");
    }

    @Test
    void testDealerQualifies_WithAceHigh() {
        // Ace high hand
        ArrayList<Card> aceHigh = new ArrayList<>();
        aceHigh.add(new Card(Suit.HEARTS, 1));  // Ace
        aceHigh.add(new Card(Suit.DIAMONDS, 8));
        aceHigh.add(new Card(Suit.CLUBS, 5));

        boolean qualifies = ThreeCardLogic.dealerQualifies(aceHigh);
        assertTrue(qualifies,
                "Dealer should qualify with Ace high");
    }

    @Test
    void testDealerQualifies_WithJackHigh() {
        // Jack high hand (should NOT qualify)
        ArrayList<Card> jackHigh = new ArrayList<>();
        jackHigh.add(new Card(Suit.HEARTS, 11)); // Jack
        jackHigh.add(new Card(Suit.DIAMONDS, 9));
        jackHigh.add(new Card(Suit.CLUBS, 7));

        boolean qualifies = ThreeCardLogic.dealerQualifies(jackHigh);
        assertFalse(qualifies,
                "Dealer should NOT qualify with Jack high (needs Queen or better)");
    }

    @Test
    void testDealerQualifies_WithTenHigh() {
        // Ten high hand (should NOT qualify)
        ArrayList<Card> tenHigh = new ArrayList<>();
        tenHigh.add(new Card(Suit.HEARTS, 10));
        tenHigh.add(new Card(Suit.DIAMONDS, 8));
        tenHigh.add(new Card(Suit.CLUBS, 6));

        boolean qualifies = ThreeCardLogic.dealerQualifies(tenHigh);
        assertFalse(qualifies,
                "Dealer should NOT qualify with Ten high (needs Queen or better)");
    }

    @Test
    void testDealerQualifies_WithFlush() {
        boolean qualifies = ThreeCardLogic.dealerQualifies(flushHand);
        assertTrue(qualifies,
                "Dealer should qualify with Flush (any hand better than High Card qualifies)");
    }

    @Test
    void testDealerQualifies_WithStraight() {
        boolean qualifies = ThreeCardLogic.dealerQualifies(straightHand);
        assertTrue(qualifies,
                "Dealer should qualify with Straight (any hand better than High Card qualifies)");
    }
}