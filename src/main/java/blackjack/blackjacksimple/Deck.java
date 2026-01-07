package blackjack.blackjacksimple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Deck {
    private String deckID = "450ean90wc5d";
    private int numCardsTotal;
    private int numCardsRemaining;
    private int numDecks = 6;
    private static final ObjectMapper mapper = new ObjectMapper();

    public Deck() {
        try {
            String buildURL =
                    "https://www.deckofcardsapi.com/api/deck/new/shuffle/?deck_count=" + numDecks;

            String deckAsJson = connect(buildURL);
            if (deckAsJson == null) {
                throw new IllegalStateException("Deck API returned null response");
            }

            JsonNode deckAsNode = mapper.readTree(deckAsJson);

            this.deckID = deckAsNode.path("deck_id").asText();
            this.numCardsRemaining = deckAsNode.path("remaining").asInt();
            this.numCardsTotal = this.numCardsRemaining;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create new deck", e);
        }
    }

    public Deck(String deckID) {
        this.deckID = deckID;

        try {
            String buildURL =
                    "https://www.deckofcardsapi.com/api/deck/" + deckID + "/shuffle/";

            String deckAsJson = connect(buildURL);
            if (deckAsJson == null) {
                throw new IllegalStateException("Deck API returned null response");
            }

            JsonNode deckAsNode = mapper.readTree(deckAsJson);

            this.deckID = deckAsNode.path("deck_id").asText();
            this.numCardsRemaining = deckAsNode.path("remaining").asInt();
            this.numCardsTotal = this.numCardsRemaining;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create new deck", e);
        }

    }

    public String getDeckID() {
        return deckID;
    }

    public int getNumCardsTotal() {
        return numCardsTotal;
    }

    public int getNumCardsRemaining() {
        return numCardsRemaining;
    }

    public void shuffle() {
        String buildURL = "https://www.deckofcardsapi.com/api/deck/" + deckID + "/shuffle/";
        numCardsRemaining = numCardsTotal;

        connect(buildURL);
    }

    public Card[] draw(int numCards) {
        try {
            String buildURL =
                    "https://www.deckofcardsapi.com/api/deck/" + deckID + "/draw/?count=" + numCards;

            String drawJson = connect(buildURL);
            if (drawJson == null) {
                throw new IllegalStateException("Null response from deck API");
            }

            JsonNode drawNode = mapper.readTree(drawJson);
            numCardsRemaining = drawNode.path("remaining").asInt();

            JsonNode cardsNodes = drawNode.path("cards");
            Card[] cards = new Card[numCards];

            for (int i = 0; i < numCards; i++) {
                cards[i] = mapper.treeToValue(cardsNodes.get(i), Card.class);
            }

            return cards;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse deck API response", e);
        }
    }

    public static String connect(String url) {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
