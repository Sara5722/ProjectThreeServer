import java.io.Serializable;

public class Card implements Serializable {
    private Suit suit;
    private int value;
    private boolean faceUp;
    private String imagePath;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
        this.faceUp = false;
        this.imagePath = "";
    }

    // Getters and setters
    public Suit getSuit() { return suit; }
    public int getValue() { return value; }
    public boolean isFaceUp() { return faceUp; }
    public void setFaceUp(boolean faceUp) { this.faceUp = faceUp; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        String valueName = getValueName();
        return valueName + " of " + suit.toString();
    }

    private String getValueName() {
        switch(value) {
            case 1: return "Ace";
            case 11: return "Jack";
            case 12: return "Queen";
            case 13: return "King";
            default: return String.valueOf(value);
        }
    }
}