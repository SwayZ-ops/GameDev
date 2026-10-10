package castlecrashers;

import castlecrashers.character.*;
import castlecrashers.ui.*;
import castlecrashers.sound.SoundManager;

import javafx.scene.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

// Main creates this manager then connects it to GameController
public class AppManager {
    private final Stage stage;
    private final Scene scene;
    private final StackPane screens = new StackPane();
    private SequentialTransition screenFade;
    private GameView game;
    private HomeView home;
    private String playerName = "";
    private CharacterSelectView selection;
    private CutsceneView dialogue;
    private GameController controller;
    private UnitColor color = UnitColor.BLUE;
    private CharacterClass characterClass = CharacterClass.WARRIOR;

    public AppManager(Stage stage) {
        this.stage = stage;
        scene = new Scene(screens, 1100, 820);
        screens.setStyle("-fx-background-color: #111019;");
        var css = AppManager.class.getResource("/game.css");
        if (css == null) {
            java.nio.file.Path file = java.nio.file.Path.of("resources/game.css");
            scene.getStylesheets().add(file.toUri().toString());
        } else
            scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Castle Crashers");
        stage.setMinWidth(960);
        stage.setMinHeight(760);
        stage.setResizable(true);
        stage.setOnHidden(e -> dispose());
        showHome();
        stage.show();
    }

    // Stop the previous screen's animations before attaching the next screen and the loaded custom cursor
    
    private void show(Parent root) {
        if (home != null) {
            home.stopBackground();
            home = null;
        }
        if (game != null) {
            game.dispose();
            game = null;
        }
        if (selection != null) {
            selection.dispose();
            selection = null;
        }
        if (dialogue != null) {
            dialogue.dispose();
            dialogue = null;
        }
        // Keep a dark backing visible while the outgoing and incoming screens fade.
        if (screenFade != null)
            screenFade.stop();
        Node previous = screens.getChildren().isEmpty() ? null
                : screens.getChildren().get(screens.getChildren().size() - 1);
        screens.getChildren().clear();
        if (previous != null) {
            previous.setDisable(true);
            screens.getChildren().add(previous);
        }
        root.setOpacity(0);
        root.setDisable(true);
        screens.getChildren().add(root);
        GameCursor.apply(root);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(240), root);
        fadeIn.setToValue(1);
        screenFade = new SequentialTransition();
        if (previous != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), previous);
            fadeOut.setToValue(0);
            screenFade.getChildren().add(fadeOut);
        }
        screenFade.getChildren().add(fadeIn);
        screenFade.setOnFinished(event -> {
            screens.getChildren().setAll(root);
            root.setDisable(false);
            Node nameInput = root.lookup("#player-name");
            if (nameInput != null) nameInput.requestFocus();
            else root.requestFocus();
            screenFade = null;
        });
        screenFade.play();
    }

    public void showHome() {
        HomeView next = new HomeView(this);
        show(next.getRoot());
        home = next;
        SoundManager.playMenuMusic();
    }

    public void showColorSelection() {
        SoundManager.playSelectionMusic();
        CharacterSelectView next = new CharacterSelectView(this, false, color);
        show(next.getRoot());
        selection = next;
    }

    // The guide uses the shared screen fade and keeps the menu music playing.
    public void showHowToPlay() {
        show(new HowToPlayView(this).getRoot());
    }

    // CharacterSelectView sends the selected color here, then NameEntryView gets the name input.

    public void chooseColor(UnitColor color) {
        this.color = color;
        showNameEntry();
    }

    public void showNameEntry() {
        SoundManager.playSelectionMusic();
        show(new NameEntryView(this, playerName).getRoot());
    }

    public void chooseName(String name) {
        playerName = name.strip();
        showClassSelection();
    }

    public void showClassSelection() {
        SoundManager.playSelectionMusic();
        CharacterSelectView next = new CharacterSelectView(this, true, color);
        show(next.getRoot());
        selection = next;
    }

    // Finish character setup and create the controller that keeps progress across all stages so what color picked will be the same.
   
    public void chooseClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
        controller = new GameController(color, characterClass, playerName);
        showGame();
    }

    // CutsceneView calls beginBattle after its last line or when the player skips the dialogue.
    
    public void showGame() {
        SoundManager.stopMusic();
        if (controller == null)
            controller = new GameController(color, characterClass, playerName);
        else if (controller.isFinished() && !controller.isSolved())
            controller.retryLevel();
        CutsceneView next = new CutsceneView(controller, this::beginBattle, this::showHome);
        show(next.getRoot());
        dialogue = next;
    }

    private void beginBattle() {
        SoundManager.playBattleMusic(controller.getLevel());
        GameView next = new GameView(this, controller);
        show(next.getRoot());
        game = next;
    }

    // GameView's Next stage button advances the controller or opens the victory.
    
    public void nextLevel() {
        if (controller.getLevel() == 5) {
            showVictory();
            return;
        }
        controller.nextLevel();
        showGame();
    }

    public void showGameOver() {
        show(new GameOverView(this).getRoot());
        SoundManager.playGameOverMusic();
    }

    public String getDefeatText() {
        return controller == null ? "Six mistakes. The castle stands."
                : "The word was " + controller.getAnswer() + ". Score: " + controller.getScore();
    }

    private void showVictory() {
        show(new VictoryView(this, controller).getRoot());
        SoundManager.playVictoryMusic();
    }

    public void dispose() {
        if (screenFade != null) {
            screenFade.stop();
            screenFade = null;
        }
        if (home != null)
            home.stopBackground();
        if (game != null)
            game.dispose();
        if (selection != null)
            selection.dispose();
        if (dialogue != null)
            dialogue.dispose();
        SoundManager.stopMusic();
    }

    public void exitGame() {
        dispose();
        stage.close();
    }
}
