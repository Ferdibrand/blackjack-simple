package blackjack.blackjacksimple;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class BlackjackApplication extends Application {
    private static final String APP_TITLE = "Bodacious Blackjack";

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/blackjack/blackjacksimple/blackjack-view.fxml")));

        Scene scene = new Scene(root, 900, 500);

        stage.setScene(scene);
        stage.setTitle(APP_TITLE);
        stage.show();
    }
}