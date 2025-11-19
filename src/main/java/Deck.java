import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;
    private int currentIndex;

    public Deck() {
        cards = new ArrayList<>();
        currentIndex = 0;
        initializeDeck();
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (int value = 1; value <= 13; value++) {
                cards.add(new Card(suit, value));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
        currentIndex = 0;
    }

    public Card drawCard() {
        if (currentIndex >= cards.size()) {
            resetDeck();
            shuffle();
        }
        return cards.get(currentIndex++);
    }

    public void resetDeck() {
        currentIndex = 0;
        shuffle();
    }

    public int cardsRemaining() {
        return cards.size() - currentIndex;
    }
}