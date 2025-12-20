package blackjack.blackjacksimple;

import java.util.ArrayList;
import java.util.Map;
import static java.util.Map.entry;

public class Hand {
    private ArrayList<Card> cards;
    private boolean isHard;
    private int value;
    private boolean isBust;

    private static final Map<String, Integer> valuesDict = Map.ofEntries(
            entry("ACE", 1),
            entry("2", 2),
            entry("3", 3),
            entry("4", 4),
            entry("5", 5),
            entry("6", 6),
            entry("7", 7),
            entry("8", 8),
            entry("9", 9),
            entry("10", 10),
            entry("JACK", 10),
            entry("QUEEN", 10),
            entry("KING", 10)
    );

    public Hand() {
        cards = new ArrayList<>();
        value = 0;
        isHard = true;
        isBust = false;
    }

    public Hand(Card card) {
        cards = new ArrayList<>();
        add(card);
        checkValue();
    }

    public Hand(ArrayList<Card> cards) {
        this.cards = cards;
        checkValue();
    }

    public boolean getIsHard() {
        return isHard;
    }

    public int getValue() {
        return value;
    }

    public boolean getIsBust() {
        return isBust;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public String toString() {
        return cards + "\nvalue = " + value + "\nisHard = " + isHard + "\nisBust = " + isBust;
    }

    public void add(Card card) {
        cards.add(card);
        checkValue();
    }

    public void reset() {
        cards.clear();
        checkValue();
    }

    private void checkValue() {
        boolean containsAce = false;
        value = 0;
        isBust = false;
        isHard = true;
        for (Card card : cards) {
            value += valuesDict.get(card.getValue());
            if (card.getValue().equals("ACE")) {
                containsAce = true;
            }
        }

        if (value > 21) {
            isBust = true;
            value = 0;
            return;
        }

        if (containsAce && value <= 11) {
            value += 10;
            isHard = false;
        } else {
            isHard = true;
        }
    }
}
