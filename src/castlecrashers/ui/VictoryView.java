package castlecrashers.ui;

import castlecrashers.AppManager;
import castlecrashers.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/** Final celebration, using the same parchment artwork as the main menu. */
public final class VictoryView {
    private final StackPane root = new StackPane();

    public VictoryView(AppManager manager, GameController game) {
        root.setStyle("-fx-background-color: radial-gradient(center 50% 35%, radius 80%, #514030, #211d30 55%, #101521);");
        Label victory = text("Victory", 58, "#432330");
        victory.setStyle(victory.getStyle() + "-fx-font-style: italic; -fx-font-weight: bold;");
        StackPane heading = parchment(victory, 650, 155);
        Label caption = text("THE CASTLE IS YOURS", 16, "#e6c887");
        Label flourish = text("✦   ✦   ✦", 26, "#d8b36e");

        ImageView portrait = TinyUi.portrait(game.getPlayerColor(), game.getPlayerClass());
        portrait.setFitWidth(96);
        portrait.setFitHeight(96);
        Label name = text(game.getPlayerName(), 26, "#fff0cb");
        name.setWrapText(true);
        name.setMaxWidth(490);
        name.setAlignment(Pos.CENTER);
        Label detail = text("Five stages conquered. Every seal broken.", 17, "#d4c7b0");
        Label scoreCaption = text("FINAL SCORE", 14, "#e6c887");
        Label score = text(Integer.toString(game.getScore()), 52, "#ffe1a0");
        score.setId("victory-score");
        VBox trophy = new VBox(8, portrait, name, detail, scoreCaption, score);
        trophy.setAlignment(Pos.CENTER);
        trophy.setPadding(new Insets(20, 36, 20, 36));
        trophy.setMaxWidth(570);
        trophy.setStyle("-fx-background-color: #171923cc; -fx-border-color: #b79255; "
                + "-fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox actions = new HBox(22, action("Return home", manager::showHome), action("Quit", manager::exitGame));
        actions.setAlignment(Pos.CENTER);
        VBox content = new VBox(10, caption, heading, flourish, trophy, actions);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        root.getChildren().add(content);
    }

    private static Label text(String value, int size, String color) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: " + size + "px; -fx-text-fill: " + color + ";");
        return label;
    }

    private static StackPane parchment(javafx.scene.Node content, double width, double height) {
        ImageView scroll = new ImageView(TinyUi.image("wooden/menu-scroll.png"));
        scroll.setFitWidth(width);
        scroll.setFitHeight(height);
        scroll.setSmooth(true);
        scroll.setMouseTransparent(true);
        StackPane panel = new StackPane(scroll, content);
        if (content instanceof Button)
            panel.getStyleClass().add("scroll-action");
        panel.setMinSize(width, height);
        panel.setMaxSize(width, height);
        return panel;
    }

    // Real buttons preserve keyboard activation while their parchment supplies the decoration.
    private static StackPane action(String title, Runnable run) {
        Button button = new Button(title);
        button.setMinSize(240, 72);
        button.setStyle("-fx-background-color: transparent; -fx-font-family: 'Georgia'; "
                + "-fx-font-size: 20px; -fx-font-style: italic; -fx-text-fill: #432330;");
        button.setOnAction(event -> run.run());
        return parchment(button, 240, 72);
    }

    public StackPane getRoot() {
        return root;
    }
}
