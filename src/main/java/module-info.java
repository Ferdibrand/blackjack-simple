module blackjack.blackjacksimple {
    requires javafx.controls;
    requires javafx.fxml;

    // Jackson modules
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    // HTTP client module
    requires java.net.http;

    // Open the package to both JavaFX FXML and Jackson for reflection
    opens blackjack.blackjacksimple to javafx.fxml, com.fasterxml.jackson.databind;

    exports blackjack.blackjacksimple;
}
