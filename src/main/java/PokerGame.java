import java.util.ArrayList;

public class PokerGame {
    private Deck deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private int anteBet;
    private int pairPlusBet;
    private int playBet;

    public PokerGame() {
        this.deck = new Deck();
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
        this.deck.shuffle();
    }

    public void placeBets(int ante, int pairPlus) {
        this.anteBet = ante;
        this.pairPlusBet = pairPlus;
        this.playBet = 0;
    }

    public void makePlayWager() {
        this.playBet = this.anteBet; // Play wager equals ante bet
    }

    public void dealHands() {
        playerHand.clear();
        dealerHand.clear();

        for (int i = 0; i < 3; i++) {
            playerHand.add(deck.drawCard());
            dealerHand.add(deck.drawCard());
        }

        // Set player cards face up, dealer cards face down
        for (Card card : playerHand) {
            card.setFaceUp(true);
        }
        for (Card card : dealerHand) {
            card.setFaceUp(false);
        }
    }

    public PokerInfo calculateResult() {
        PokerInfo result = new PokerInfo("GAME_RESULT");

        // Calculate Pair Plus winnings first (independent of dealer)
        int pairPlusWinnings = ThreeCardLogic.evalPPWinnings(playerHand, pairPlusBet);

        // Check if dealer qualifies
        boolean dealerQualifies = ThreeCardLogic.dealerQualifies(dealerHand);

        int mainGameWinnings = 0;
        String message = "";

        if (!dealerQualifies) {
            // Dealer doesn't qualify - return ante bet, play wager is push
            mainGameWinnings = anteBet; // Get ante back
            message = "Dealer does not qualify. Ante returned, play wager push.";
        } else {
            // Dealer qualifies - compare hands
            int comparison = ThreeCardLogic.compareHands(dealerHand, playerHand);

            if (comparison > 0) {
                // Player wins
                mainGameWinnings = (anteBet + playBet) * 2; // 1:1 payout on both bets
                message = "You beat the dealer!";
            } else if (comparison < 0) {
                // Dealer wins
                mainGameWinnings = 0; // Lose both ante and play
                message = "Dealer wins.";
            } else {
                // Tie - push both bets
                mainGameWinnings = anteBet + playBet;
                message = "Push! It's a tie.";
            }
        }

        // Set dealer cards face up for display
        for (Card card : dealerHand) {
            card.setFaceUp(true);
        }

        int totalWinnings = mainGameWinnings + pairPlusWinnings - (anteBet + pairPlusBet + playBet);

        result.setTotalWinnings(totalWinnings);
        result.setGameMessage(message + " Pair Plus: $" + pairPlusWinnings);
        result.setPlayerHand(playerHand);
        result.setDealerHand(dealerHand);

        return result;
    }

    // Getters
    public ArrayList<Card> getPlayerHand() { return playerHand; }
    public ArrayList<Card> getDealerHand() { return dealerHand; }
    public int getAnteBet() { return anteBet; }
    public int getPairPlusBet() { return pairPlusBet; }
    public int getPlayBet() { return playBet; }

    public void resetGame() {
        deck.resetDeck();
        playerHand.clear();
        dealerHand.clear();
    }
}
