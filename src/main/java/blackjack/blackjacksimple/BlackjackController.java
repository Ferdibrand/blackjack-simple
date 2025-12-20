package blackjack.blackjacksimple;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BlackjackController {

    // Card Containers
    @FXML private HBox dealerCards;
    @FXML private HBox playerCards;

    // Action Buttons
    @FXML private Button hitButton;
    @FXML private Button standButton;

    // Betting Buttons
    @FXML private Button betButton;
    @FXML private Button bet1Button;
    @FXML private Button bet10Button;
    @FXML private Button bet100Button;
    @FXML private Button betN1Button;
    @FXML private Button betN10Button;
    @FXML private Button betN100Button;

    // Labels
    @FXML private Label betLabel;
    @FXML private Label accountLabel;

    // Game Logic
    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;

    private int currentBet;
    private int account;
    private boolean dealerCardHidden;
    private boolean playerBlackjack;
    private boolean dealerBlackjack;
    private boolean playerBust;
    private boolean dealerBust;

    private static final int STARTING_ACCOUNT = 100;
    private static final String DECK_ID = "68y294bmno1z";

    @FXML
    private void initialize() {
        // Initialize game state
        this.deck = new Deck(DECK_ID);
        this.account = STARTING_ACCOUNT;

        this.playerHand = new Hand();
        this.dealerHand = new Hand();

        updateAccount(account);
        initializeButtons();
        startBetting();
    }

    private void initializeButtons() {
        // Action buttons
        hitButton.setOnAction(e -> hit());
        standButton.setOnAction(e -> stand());

        // Betting buttons
        betButton.setOnAction(e -> bet(currentBet));
        bet1Button.setOnAction(e -> updateBet(1));
        bet10Button.setOnAction(e -> updateBet(10));
        bet100Button.setOnAction(e -> updateBet(100));
        betN1Button.setOnAction(e -> updateBet(-1));
        betN10Button.setOnAction(e -> updateBet(-10));
        betN100Button.setOnAction(e -> updateBet(-100));
    }

    private void disableBetButtons(boolean disabled) {
        betButton.setDisable(disabled);
        bet1Button.setDisable(disabled);
        bet10Button.setDisable(disabled);
        bet100Button.setDisable(disabled);
        betN1Button.setDisable(disabled);
        betN10Button.setDisable(disabled);
        betN100Button.setDisable(disabled);
    }

    private void disableActionButtons(boolean disabled) {
        hitButton.setDisable(disabled);
        standButton.setDisable(disabled);
    }

    private void startBetting() {
        disableBetButtons(false);
        currentBet = 0;
        updateBet(currentBet);
        disableActionButtons(true);
        clearHands();
    }

    private void updateBet(int amount) {
        currentBet += amount;
        if (currentBet < 0) currentBet = 0;
        if (currentBet > account) currentBet = account;
        betLabel.setText("Bet: " + currentBet);
    }

    private void updateAccount(double amount) {
        accountLabel.setText("Account: " + amount);
    }

    private void bet(int amount) {
        if (currentBet > 0 && account >= currentBet) {
            account -= currentBet;
            updateAccount(account);

            disableBetButtons(true);
            startPlayerPlay();
        }
    }

    private void startPlayerPlay() {
        dealerCardHidden = true;
        initialDeal();
        checkBlackjack();

        if (!playerBlackjack && !dealerBlackjack) {
            disableActionButtons(false);
        } else {
            updateHands();
        }
    }

    private void initialDeal() {
        deal(true, true);
        deal(false, false);
        deal(true, true);
        deal(false, true);
    }

    private void deal(boolean toPlayer, boolean faceUp) {
        Card card = deck.draw(1)[0];
        if (toPlayer) playerHand.add(card);
        else dealerHand.add(card);

        HBox targetBox = toPlayer ? playerCards : dealerCards;
        javafx.scene.image.Image image = faceUp ? new javafx.scene.image.Image(card.getImage())
                : new javafx.scene.image.Image(Card.getBackImage());
        targetBox.getChildren().add(new javafx.scene.image.ImageView(image));
    }

    private void checkBlackjack() {
        playerBlackjack = playerHand.getValue() == 21;
        dealerBlackjack = dealerHand.getValue() == 21;
    }

    private void checkBust() {
        playerBust = playerHand.getIsBust();
        dealerBust = dealerHand.getIsBust();
    }

    private void hit() {
        deal(true, true);
        checkBust();
        if (playerBust) {
            disableActionButtons(true);
            revealDealerHand();
        }
    }

    private void stand() {
        dealerCardHidden = false;
        updateHands();
        startDealerPlay();
    }

    private void startDealerPlay() {
        boolean playing = true;
        while (playing) {
            if (!dealerHand.getIsBust()) {
                int value = dealerHand.getValue();
                if (value < 17 || (value == 17 && !dealerHand.getIsHard())) {
                    deal(false, true);
                } else {
                    playing = false;
                }
            } else {
                playing = false;
            }
        }
    }

    private void updateHands() {
        clearHands();
        for (int i = 0; i < dealerHand.getCards().size(); i++) {
            Card card = dealerHand.getCards().get(i);
            boolean faceUp = !(i == 0 && dealerCardHidden);
            javafx.scene.image.Image image = faceUp ? new javafx.scene.image.Image(card.getImage())
                    : new javafx.scene.image.Image(Card.getBackImage());
            dealerCards.getChildren().add(new javafx.scene.image.ImageView(image));
        }
        for (Card card : playerHand.getCards()) {
            javafx.scene.image.Image image = new javafx.scene.image.Image(card.getImage());
            playerCards.getChildren().add(new javafx.scene.image.ImageView(image));
        }
    }

    private void clearHands() {
        dealerCards.getChildren().clear();
        playerCards.getChildren().clear();
    }

    private void revealDealerHand() {
        dealerCardHidden = false;
        updateHands();
    }
}
