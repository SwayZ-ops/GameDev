package castlecrashers.ui;

import castlecrashers.AppManager;
import castlecrashers.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/** Shows a clear, atmospheric end-of-stage defeat and its recovery actions. */
public final class GameOverView {
    private final StackPane root = new StackPane();

    public GameOverView(AppManager manager, GameController game) {
        root.setStyle("-fx-background-color: radial-gradient(center 50% 36%, radius 82%, #493037, #251c29 57%, #11151e);");

        Label caption = text("YOUR JOURNEY ENDS HERE", 15, "#dca7a0");
        Label headingText = text("Defeat", 56, "#432330");
        headingText.setStyle(headingText.getStyle() + "-fx-font-style: italic; -fx-font-weight: bold;");
        StackPane heading = parchment(headingText, 620, 145);

        Label chapter = text("" + game.getStageName().toUpperCase()
                + "   /   STAGE " + game.getLevel() + " OF 5", 15, "#e3c99d");
        chapter.setStyle(chapter.getStyle() + "-fx-font-weight: bold; -fx-letter-spacing: 1px;");

        ImageView portrait = TinyUi.portrait(game.getPlayerColor(), game.getPlayerClass());
        portrait.setFitWidth(96);
        portrait.setFitHeight(96);
        Label name = text(game.getPlayerName(), 25, "#fff0cb");
        Label summary = text("The seal held this time. Regroup and try again.", 16, "#d0c3b8");
        summary.setWrapText(true);
        summary.setMaxWidth(440);
        summary.setAlignment(Pos.CENTER);

        Label wordCaption = text("THE WORD YOU WERE SEEKING", 13, "#d5b77c");
        Label answer = text(game.getAnswer(), 34, "#ffe2a1");
        answer.setId("defeat-answer");
        Label scoreCaption = text("TOTAL SCORE", 12, "#d5b77c");
        Label score = text(Integer.toString(game.getScore()), 30, "#fff0cb");
        score.setId("defeat-score");

        VBox details = new VBox(8, portrait, name, summary, wordCaption, answer, scoreCaption, score);
        details.setAlignment(Pos.CENTER);
        details.setPadding(new Insets(18, 34, 18, 34));
        details.setMaxWidth(520);
        details.setStyle("-fx-background-color: #171923dc; -fx-border-color: #9c675b; "
                + "-fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox primaryActions = new HBox(18,
                action("Retry stage", manager::showGame, 240, 72),
                action("Return home", manager::showHome, 240, 72));
        primaryActions.setAlignment(Pos.CENTER);
        StackPane quit = action("Quit", manager::exitGame, 200, 60);

        VBox content = new VBox(9, caption, heading, chapter, details, primaryActions, quit);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(18));
        root.getChildren().add(content);
    }

    private static Label text(String value, int size, String color) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: " + size
                + "px; -fx-text-fill: " + color + ";");
        return label;
    }

    private static StackPane parchment(Node content, double width, double height) {
        ImageView scroll = new ImageView(TinyUi.image("wooden/menu-scroll.png"));
        scroll.setFitWidth(width);
        scroll.setFitHeight(height);
        scroll.setSmooth(true);
        scroll.setMouseTransparent(true);
        StackPane panel = new StackPane(scroll, content);
        panel.setMinSize(width, height);
        panel.setMaxSize(width, height);
        return panel;
    }

    private static StackPane action(String title, Runnable run, double width, double height) {
        Button button = new Button(title);
        button.setMinSize(width, height);
        button.setPrefSize(width, height);
        button.setMaxSize(width, height);
        button.setStyle("-fx-background-color: transparent; -fx-font-family: 'Georgia'; "
                + "-fx-font-size: 19px; -fx-font-style: italic; -fx-text-fill: #432330;");
        button.setOnAction(event -> run.run());
        StackPane panel = parchment(button, width, height);
        panel.getStyleClass().add("scroll-action");
        return panel;
    }

    public StackPane getRoot() {
        return root;
    }
}
