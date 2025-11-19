import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private String messageType; // "PLACE_BETS", "PLAY_OR_FOLD", "GAME_RESULT"
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private int anteBet;
    private int pairPlusBet;
    private int playBet;
    private int totalWinnings;
    private String gameMessage;
    private int playerId;
    private boolean success;
    private String gameState;

    // Constructors
    public PokerInfo() {}

    public PokerInfo(String messageType) {
        this.messageType = messageType;
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
    }

    // Getters and setters
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public ArrayList<Card> getPlayerHand() { return playerHand; }
    public void setPlayerHand(ArrayList<Card> playerHand) { this.playerHand = playerHand; }

    public ArrayList<Card> getDealerHand() { return dealerHand; }
    public void setDealerHand(ArrayList<Card> dealerHand) { this.dealerHand = dealerHand; }

    public int getAnteBet() { return anteBet; }
    public void setAnteBet(int anteBet) { this.anteBet = anteBet; }

    public int getPairPlusBet() { return pairPlusBet; }
    public void setPairPlusBet(int pairPlusBet) { this.pairPlusBet = pairPlusBet; }

    public int getPlayBet() { return playBet; }
    public void setPlayBet(int playBet) { this.playBet = playBet; }

    public int getTotalWinnings() { return totalWinnings; }
    public void setTotalWinnings(int totalWinnings) { this.totalWinnings = totalWinnings; }

    public String getGameMessage() { return gameMessage; }
    public void setGameMessage(String gameMessage) { this.gameMessage = gameMessage; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getGameState() { return gameState; }
    public void setGameState(String gameState) { this.gameState = gameState; }
}