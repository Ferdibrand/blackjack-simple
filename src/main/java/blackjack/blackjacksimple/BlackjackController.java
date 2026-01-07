package blackjack.blackjacksimple;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class BlackjackController {

    @FXML private HBox dealerCards;
    @FXML private HBox playerCards;

    @FXML private Button hitButton;
    @FXML private Button standButton;

    @FXML private Button betButton;
    @FXML private Button bet1Button;
    @FXML private Button bet10Button;
    @FXML private Button bet100Button;
    @FXML private Button betN1Button;
    @FXML private Button betN10Button;
    @FXML private Button betN100Button;

    @FXML private Label betLabel;
    @FXML private Label accountLabel;
    @FXML private Label statusLabel;

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
    private static final String DECK_ID = "i64fx0203vrm";
    private static final double BLACKJACK_PAYOUT = 3.0/2;

    @FXML
    private void initialize() {
        deck = new Deck(DECK_ID);
        account = STARTING_ACCOUNT;
        playerHand = new Hand();
        dealerHand = new Hand();
        updateAccount(account);
        setupButtons();
        startBetting();
    }

    private void setupButtons() {
        hitButton.setOnAction(e -> hit());
        standButton.setOnAction(e -> stand());

        betButton.setOnAction(e -> bet(currentBet));
        bet1Button.setOnAction(e -> changeBet(1));
        bet10Button.setOnAction(e -> changeBet(10));
        bet100Button.setOnAction(e -> changeBet(100));
        betN1Button.setOnAction(e -> changeBet(-1));
        betN10Button.setOnAction(e -> changeBet(-10));
        betN100Button.setOnAction(e -> changeBet(-100));
    }

    private void disableBetButtons(boolean disable) {
        betButton.setDisable(disable);
        bet1Button.setDisable(disable);
        bet10Button.setDisable(disable);
        bet100Button.setDisable(disable);
        betN1Button.setDisable(disable);
        betN10Button.setDisable(disable);
        betN100Button.setDisable(disable);
    }

    private void disableActionButtons(boolean disable) {
        hitButton.setDisable(disable);
        standButton.setDisable(disable);
    }

    private void startBetting() {
        disableBetButtons(false);
        resetHands();
        changeBet(-currentBet);
        currentBet = 0;
        disableActionButtons(true);
    }

    private void changeBet(int amount) {
        currentBet += amount;
        if (currentBet < 0) currentBet = 0;
        if (currentBet > account) currentBet = account;
        betLabel.setText("Bet: " + currentBet);
    }

    private void updateAccount(double value) {
        accountLabel.setText("Account: " + value);
    }

    private void updateStatusLabel(String str) {
        statusLabel.setText(str);
    }

    private void bet(int amount) {
        clearHands();

        if (currentBet <= 0 || account < currentBet) {
            return;
        }

        account -= currentBet;
        updateAccount(account);
        disableBetButtons(true);
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        dealerCardHidden = true;
        initialDeal();
        checkBlackjacks();
        if (!playerBlackjack && !dealerBlackjack) {
            disableActionButtons(false);
            updateStatusLabel("Time to play");
        } else {
            refreshHands();
            startResolution();
        }
    }

    private void initialDeal() {
        dealCard(true, true);
        dealCard(false, false);
        dealCard(true, true);
        dealCard(false, true);
    }

    private void dealCard(boolean toPlayer, boolean faceUp) {
        Card card = deck.draw(1)[0];
        HBox targetBox;

        if (toPlayer) {
            playerHand.add(card);
            targetBox = playerCards;
        } else {
            dealerHand.add(card);
            targetBox = dealerCards;
        }

        Image image;
        if (faceUp) {
            image = new Image(card.getImage());
        } else {
            image = new Image(Card.getBackImage());
        }

        ImageView imageView = resizeCardImage(new ImageView(image));
        targetBox.getChildren().add(imageView);
    }

    private ImageView resizeCardImage(ImageView image) {
        image.setPreserveRatio(true);
        image.setFitHeight(100);
        return image;
    }


    private void checkBlackjacks() {
        playerBlackjack = playerHand.getValue() == 21;
        dealerBlackjack = dealerHand.getValue() == 21;
    }

    private void checkBusts() {
        playerBust = playerHand.getIsBust();
        dealerBust = dealerHand.getIsBust();
    }

    private void hit() {
        dealCard(true, true);
        checkBusts();
        if (playerBust) {
            disableActionButtons(true);
            revealDealerHand();
            startResolution();
        }
    }

    private void stand() {
        dealerCardHidden = false;
        disableActionButtons(true);
        refreshHands();
        playDealerTurn();
    }

    private void playDealerTurn() {
        while (!dealerBust) {
            int value = dealerHand.getValue();
            if (value < 17 || (value == 17 && !dealerHand.getIsHard())) {
                dealCard(false, true);
            } else {
                break;
            }
            checkBusts();
        }
        refreshHands();
        startResolution();
    }

    public void refreshHands() {
        clearHands();

        for (int i = 0; i < dealerHand.getCards().size(); i++) {
            Image image;
            Card card = dealerHand.getCards().get(i);
            if (i == 0 && dealerCardHidden) {
                image = new Image(Card.getBackImage());
            } else {
                image = new Image(card.getImage());
            }
            dealerCards.getChildren().add(resizeCardImage(new ImageView(image)));
        }

        for (Card card : playerHand.getCards()) {
            playerCards.getChildren().add(resizeCardImage(new ImageView(new Image(card.getImage()))));
        }
    }

    public void clearHands() {
        dealerCards.getChildren().clear();
        playerCards.getChildren().clear();
    }

    public void revealDealerHand() {
        dealerCardHidden = false;
        refreshHands();
    }

    public void startResolution() {
        disableActionButtons(true);
        resolveBets();

        if (account == 0) {
            triggerEnding();
        } else {
            startBetting();
        }
    }

    public void checkShuffle() {
        if (deck.getNumCardsRemaining() < 22) {
            deck.shuffle();
        }
    }

    private void resolveBets() {
        checkBusts();

        if (playerBlackjack) {
            if (dealerBlackjack) {
                account += currentBet;
                updateStatusLabel("You both got blackjack, Push.");
            } else {
                account += (int) (currentBet + currentBet * BLACKJACK_PAYOUT);
                updateStatusLabel("You got blackjack, You win.");
            }
        } else {
            if (dealerBlackjack) {
                updateStatusLabel("Dealer got blackjack, You lose.");
            } else if (playerBust) {
                updateStatusLabel("You busted.");
            } else if (dealerBust || playerHand.getValue() > dealerHand.getValue()) {
                account += currentBet * 2;
                if (dealerBust) {
                    updateStatusLabel("Dealer busted. You win.");
                } else {
                    updateStatusLabel("You win.");
                }
            } else if (playerHand.getValue() < dealerHand.getValue()) {
                updateStatusLabel("You lose.");
            } else {
                account += currentBet;
                updateStatusLabel("Push.");
            }
        }

        updateAccount(account);// <-- Must update the account display
    }


    private void resetHands() {
        playerHand.reset();
        dealerHand.reset();
    }

    private void triggerEnding() {
        updateStatusLabel("Oh no, you can't play anymore because you ran out of money, so sad.");
    }
}
