import java.io.Serializable;

public enum Suit implements Serializable {
    HEARTS, DIAMONDS, CLUBS, SPADES;

    @Override
    public String toString() {
        switch(this) {
            case HEARTS: return "Hearts";
            case DIAMONDS: return "Diamonds";
            case CLUBS: return "Clubs";
            case SPADES: return "Spades";
            default: return "";
        }
    }

    public String getSymbol() {
        switch(this) {
            case HEARTS: return "♥";
            case DIAMONDS: return "♦";
            case CLUBS: return "♣";
            case SPADES: return "♠";
            default: return "";
        }
    }
}