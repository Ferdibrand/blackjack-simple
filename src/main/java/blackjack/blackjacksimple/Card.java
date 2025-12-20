package blackjack.blackjacksimple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    private String code;
    private String image;
    private String value;
    private String suit;
    private static String backImage = "https://www.deckofcardsapi.com/static/img/back.png";

    public static String getBackImage() {
        return backImage;
    }

    public String getCode() {
        return code;
    }

    public String getImage() {
        return image;
    }

    public String getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    public String toString() {
        return code;
    }
}

