package castlecrashers.ui;

import castlecrashers.GameController;
import castlecrashers.animation.SpriteAnimation;
import castlecrashers.data.DialogueRepository;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.List;


public class CutsceneView {
    private final StackPane root = new StackPane();
    private final HBox conversation = new HBox(24);
    private final StackPane portrait = new StackPane();
    private final VBox dialogueBox;
    private final Label name = HomeView.label("", "dialogue-name");
    private final Label text = HomeView.label("", "dialogue-text");
    private final Button next;
    private final SpriteAnimation hero, enemy;
    private final List<DialogueRepository.Line> lines;
    private final GameController controller;
    private final Runnable onComplete;
    private Timeline typewriter;
    private int index, character;
    private boolean disposed, completed;

    public CutsceneView(GameController controller, Runnable onComplete, Runnable onHome) {
        this.controller = controller;
        this.onComplete = onComplete;
        lines = DialogueRepository.beforeBattle(controller);
        hero = new SpriteAnimation(controller.getPlayerColor(), controller.getPlayerClass(), false);
        enemy = new SpriteAnimation(controller.getEnemyColor(), controller.getEnemyClass(), true);

        if (controller.isFinalBoss())
            enemy.setEffect(new DropShadow(18, Color.CRIMSON));
        portrait.setMinSize(270, 290);
        portrait.setPrefSize(270, 290);
        portrait.setMaxSize(270, 290);
        portrait.getStyleClass().add("dialogue-portrait");
        name.setWrapText(true);
        text.setWrapText(true);
        text.setMinHeight(135);
        text.setMaxWidth(Double.MAX_VALUE);
        text.setAlignment(Pos.TOP_LEFT);
        next = HomeView.button("Continue", this::advance);
        next.setId("dialogue-next");
        HBox buttons = new HBox(12, new Region(), next);
        buttons.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(buttons.getChildren().get(0), Priority.ALWAYS);
        dialogueBox = new VBox(20, name, text, buttons);
        dialogueBox.setPadding(new Insets(26));
        dialogueBox.getStyleClass().add("dialogue-box");
        dialogueBox.setMinWidth(0);
        dialogueBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialogueBox, Priority.ALWAYS);
        conversation.setAlignment(Pos.CENTER);
        conversation.setMaxWidth(1050);
        Button skip = HomeView.button("Skip dialogue", this::finish);
        skip.setId("dialogue-skip");
        HBox navigation = new HBox(16, HomeView.button("Home", onHome), skip);
        navigation.setAlignment(Pos.CENTER);
        VBox layout = new VBox(28, conversation, navigation);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        root.getChildren().add(layout);
        root.setFocusTraversable(true);
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                advance();
                event.consume();
            }
        });
        showLine();
    }

    // DialogueRepository identifies the speaker; SpriteAnimation supplies the portratis for hero and enemy.
  
    private void showLine() {
        if (disposed)
            return;
        if (typewriter != null)
            typewriter.stop();
        var line = lines.get(index);
        hero.stop();
        enemy.stop();
        SpriteAnimation speaker = line.hero() ? hero : enemy;
        speaker.idle();
        portrait.getChildren().setAll(speaker);

        conversation.getChildren().setAll(line.hero() ? new javafx.scene.Node[] { portrait, dialogueBox }
                : new javafx.scene.Node[] { dialogueBox, portrait });
        name.setText(line.hero() ? controller.getPlayerName() : controller.getEnemyName());
        name.setStyle(line.hero() ? "-fx-text-fill: #8ecaff;" : "-fx-text-fill: #f19a9a;");
        text.setText("");
        character = 0;
        next.setText("Reveal text");
        typewriter = new Timeline(new KeyFrame(Duration.millis(24), e -> {
            character++;
            text.setText(line.text().substring(0, character));
        }));
        typewriter.setCycleCount(line.text().length());
        typewriter.setOnFinished(e -> updateButton());
        typewriter.play();
    }

    private void updateButton() {
        next.setText(index == lines.size() - 1 ? "Begin fight" : "Continue");
    }

    // Button and enter for next dialogue
  
    private void advance() {
        if (disposed || completed)
            return;
        if (character < lines.get(index).text().length()) {
            typewriter.stop();
            character = lines.get(index).text().length();
            text.setText(lines.get(index).text());
            updateButton();
        } else if (++index == lines.size())
            finish();
        else
            showLine();
    }

    // Stop portrait/text animations before calling AppManager's beginBattle
    
    private void finish() {
        if (disposed || completed)
            return;
        completed = true;
        dispose();
        onComplete.run();
    }

    public StackPane getRoot() {
        return root;
    }

    public void dispose() {
        disposed = true;
        if (typewriter != null)
            typewriter.stop();
        hero.stop();
        enemy.stop();
    }
}
