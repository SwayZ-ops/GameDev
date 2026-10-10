package castlecrashers.ui;

import castlecrashers.*;
import castlecrashers.animation.SpriteAnimation;
import castlecrashers.character.CharacterClass;
import castlecrashers.sound.SoundManager;
import javafx.scene.image.Image;
import java.nio.file.*;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.*;

// Displays GameController's battle and connects input, sprite animation, sound, and the word panel.
public class GameView {
    private final AppManager appManager;
    private final GameController controller;
    private final StackPane root = new StackPane();
    private final Label[][] letterBoxes;
    private final Label message = HomeView.label("Choose thy word wisely…", "message");
    private final Label playerHealth = new Label(), enemyHealth = new Label(), scoreLabel = new Label();
    private final HealthBar playerBar = new HealthBar(), enemyBar = new HealthBar();
    private final SpriteAnimation hero, enemy;
    private final AttackBubble attackBubble = new AttackBubble();
    private final DoubleProperty heroPosition = new SimpleDoubleProperty(.28);
    private final DoubleProperty enemyPosition = new SimpleDoubleProperty(.72);
    private final List<Animation> animations = new ArrayList<>();
    private final Map<Character, Button> keys = new HashMap<>();
    private final Map<Character, Integer> known = new HashMap<>();
    private final HBox actions = new HBox(10);
    private final StringBuilder currentGuess = new StringBuilder();
    private final Image stageBackground;
    private final String backgroundFileName;
    private int currentRow, displayedEnemyHp = 100;
    private boolean busy, disposed;

    public GameView(AppManager manager, GameController controller) {
        this.appManager = manager;
        this.controller = controller;
        backgroundFileName = switch (controller.getLevel()) {
            case 1 -> "gate.png";
            case 2 -> "hallway.png";
            case 3 -> "prison.png";
            case 4 -> "treasury.png";
            case 5 -> "throne_room.png";
            default -> throw new IllegalArgumentException("Unknown stage");
        };
        stageBackground = loadStageBackground();
        letterBoxes = new Label[6][controller.getWordLength()];
        hero = new SpriteAnimation(controller.getPlayerColor(), controller.getPlayerClass(), false);
        enemy = new SpriteAnimation(controller.getEnemyColor(), controller.getEnemyClass(), true);
        if (controller.isFinalBoss())
            enemy.setEffect(new javafx.scene.effect.DropShadow(14, Color.CRIMSON));
        BorderPane layout = new BorderPane();
        layout.setPadding(Insets.EMPTY);
        root.getChildren().add(layout);
        HBox header = new HBox(12, HomeView.label(controller.getStageName(), "subtitle"),
                HomeView.label(
                        "Stage " + controller.getLevel() + "/5 · " + controller.getWordLength() + " letters",
                        "eyebrow"));
        Region headerGap = new Region();
        HBox.setHgrow(headerGap, Priority.ALWAYS);
        scoreLabel.setText("Score: " + controller.getScore());
        header.getChildren().addAll(headerGap, scoreLabel, HomeView.button("Home", appManager::showHome));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(8, 16, 4, 16));
        header.getStyleClass().add("battle-header");

        // Sprite positions follow the arena size, PaintScene follows the sprites and displays bg

        Pane arena = new Pane();
        arena.setMinHeight(250);
        arena.setPrefHeight(400);
        Canvas scenery = new Canvas();
        scenery.widthProperty().bind(arena.widthProperty());
        scenery.heightProperty().bind(arena.heightProperty());
        scenery.widthProperty().addListener((observable, oldValue, newValue) -> paintScene(scenery));
        scenery.heightProperty().addListener((observable, oldValue, newValue) -> paintScene(scenery));
        hero.layoutXProperty()
                .bind(arena.widthProperty().multiply(heroPosition).subtract(hero.getFitWidth() / 2));
        enemy.layoutXProperty()
                .bind(arena.widthProperty().multiply(enemyPosition).subtract(enemy.getFitWidth() / 2));
        // Position the feet on the foreground floor, accounting for sprite padding.
        var fighterCenter = controller.getLevel() == 3 ? arena.heightProperty().multiply(.70)
                : arena.heightProperty().multiply(.84).subtract(38);
        hero.layoutYProperty().bind(fighterCenter.subtract(hero.getFitWidth() / 2));
        enemy.layoutYProperty().bind(fighterCenter.subtract(enemy.getFitWidth() / 2));
        hero.setManaged(false);
        enemy.setManaged(false);
        attackBubble.resize(170, 50);
        attackBubble.layoutXProperty().bind(arena.widthProperty().multiply(enemyPosition).add(35));
        attackBubble.layoutYProperty()
                .bind(javafx.beans.binding.Bindings.max(140, fighterCenter.subtract(105)));
        arena.getChildren().addAll(scenery, hero, enemy, attackBubble);
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(arena.widthProperty());
        clip.heightProperty().bind(arena.heightProperty());
        arena.setClip(clip);

        playerBar.setPrefWidth(200);
        enemyBar.setPrefWidth(200);
        VBox playerInfo = new VBox(HomeView.label(controller.getPlayerName(), "subtitle"), playerBar,
                playerHealth);
        VBox enemyInfo = new VBox(HomeView.label(controller.getEnemyName(), "subtitle"), enemyBar,
                enemyHealth);
        playerInfo.getStyleClass().add("fighter-info");
        enemyInfo.getStyleClass().add("fighter-info");
        enemyInfo.setAlignment(Pos.CENTER_RIGHT);
        HBox playerCard = new HBox(8,
                TinyUi.portrait(controller.getPlayerColor(), controller.getPlayerClass()), playerInfo);
        HBox enemyCard = new HBox(8, enemyInfo,
                TinyUi.portrait(controller.getEnemyColor(), controller.getEnemyClass()));
        playerCard.setAlignment(Pos.CENTER_LEFT);
        enemyCard.setAlignment(Pos.CENTER_RIGHT);
        playerCard.getStyleClass().add("fighter-card");
        enemyCard.getStyleClass().add("fighter-card");
        Region healthGap = new Region();
        HBox.setHgrow(healthGap, Priority.ALWAYS);
        HBox health = new HBox(playerCard, healthGap, enemyCard);
        health.setPadding(new Insets(2, 16, 0, 16));
        VBox hud = new VBox(2, header, health);
        hud.setPickOnBounds(false);
        health.setPickOnBounds(false);
        StackPane upper = new StackPane(arena, hud);
        StackPane.setAlignment(hud, Pos.TOP_CENTER);
        hud.setMaxHeight(Region.USE_PREF_SIZE);
        layout.setCenter(upper);
        playerHealth.setText("120 / 120 HP · 0 / 6 mistakes");
        updateEnemyHp();

        // EEach row one guess displays the letters
        GridPane board = new GridPane();
        board.setHgap(4);
        board.setVgap(3);
        board.setAlignment(Pos.CENTER);
        int leadingColumns = controller.getWordLength() % 2;
        if (leadingColumns == 1) {
            Region blank = new Region();
            blank.setMinWidth(36);
            board.add(blank, 0, 0);
        }
        for (int r = 0; r < 6; r++)
            for (int c = 0; c < controller.getWordLength(); c++) {
                Label box = new Label();
                box.getStyleClass().add("tile");
                PixelLetters.apply(box, 1.5);
                letterBoxes[r][c] = box;
                board.add(box, c + leadingColumns, r);
                if (c == controller.getWordLength() / 2)
                    GridPane.setMargin(box, new Insets(0, 0, 0, 20));
            }
        VBox keyboard = new VBox(3);
        keyboard.setAlignment(Pos.CENTER);
        for (String letters : List.of("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")) {
            HBox line = new HBox(4);
            line.setAlignment(Pos.CENTER);
            if (letters.startsWith("Z")) {
                Button enter = HomeView.button("ENTER", () -> handleKey("ENTER"));
                enter.getStyleClass().add("key");
                PixelLetters.apply(enter, 1);
                line.getChildren().add(enter);
            }
            for (char letter : letters.toCharArray()) {
                Button key = HomeView.button("" + letter, () -> handleKey("" + letter));
                key.getStyleClass().add("key");
                PixelLetters.apply(key, 1);
                keys.put(letter, key);
                line.getChildren().add(key);
            }
            if (letters.startsWith("Z")) {
                Button delete = HomeView.button("DEL", () -> handleKey("BACK_SPACE"));
                delete.getStyleClass().add("key");
                PixelLetters.apply(delete, 1);
                line.getChildren().add(delete);
            }
            keyboard.getChildren().add(line);
        }
        Label hint = HomeView.label("ANCIENT INSCRIPTION: " + controller.getHint(), "hint");
        hint.setWrapText(true);
        hint.setMaxWidth(850);
        hint.setAlignment(Pos.CENTER);
        actions.setAlignment(Pos.CENTER);
        actions.setMinHeight(0);
        BookWordBoard boardFrame = new BookWordBoard(board, controller.getWordLength());
        StackPane keyboardFrame = new StackPane(keyboard);
        keyboardFrame.setBorder(TinyUi.wood(8));
        keyboardFrame.getStyleClass().add("keyboard-frame");
        keyboardFrame.setMaxWidth(Region.USE_PREF_SIZE);
        
        // WordPanel arranges BookWordBoard and the keyboard without handling any game rules
      
        WordPanel lower = new WordPanel(hint, boardFrame, message, keyboardFrame, actions);
        lower.prefHeightProperty().bind(root.heightProperty().multiply(.43));
        layout.setBottom(lower);
        root.setFocusTraversable(true);
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() || e.isAltDown() || e.isMetaDown())
                return;
            String key = e.getCode().name();
            if (key.matches("[A-Z]") || key.equals("ENTER") || key.equals("BACK_SPACE")) {
                handleKey(key);
                e.consume();
            }
        });
    }

    // Physical keys and on-screen buttons share this input handler and pause during battle anim
 
    private void handleKey(String key) {
        if (busy || disposed || controller.isFinished())
            return;
        if (key.equals("ENTER")) {
            submitWord();
            return;
        }
        if (key.equals("BACK_SPACE")) {
            if (currentGuess.length() > 0)
                currentGuess.deleteCharAt(currentGuess.length() - 1);
        } else if (key.matches("[A-Z]") && currentGuess.length() < controller.getWordLength()) {
            currentGuess.append(key);
            SoundManager.playTilePlacement();
        }
        for (int c = 0; c < controller.getWordLength(); c++)
            letterBoxes[currentRow][c].setText(c < currentGuess.length() ? "" + currentGuess.charAt(c) : "");
    }

    // Ask GameController to score the word then animate the stored results one letter at a time
   
    private void submitWord() {
        if (currentGuess.length() != controller.getWordLength()) {
            message.setText("Complete all " + controller.getWordLength() + " letters first.");
            return;
        }
        busy = true;
        SoundManager.playWordSubmit();
        String guess = currentGuess.toString();
        int[] results = controller.calculateResults(guess);
        boolean solved = controller.submitGuess(guess);
        for (Label tile : letterBoxes[currentRow])
            tile.setText("");
        message.setText("Your champion advances…");
        move(hero, heroPosition, .60, false, () -> reveal(guess, results, solved, 0));
    }

    //The next tile waits for the full attack sheet, so animations never overlap.
     
    private void reveal(String guess, int[] results, boolean solved, int column) {
        if (disposed)
            return;
        if (column == results.length) {
            if (Arrays.stream(results).noneMatch(mark -> mark == 2)) {
                message.setText("The attack finds no opening.");
                playAttackSound(controller.getPlayerClass());
                hero.attack(() -> returnHero(solved));
            } else
                returnHero(solved);
            return;
        }
        int result = results[column];
        char letter = guess.charAt(column);
        Label tile = letterBoxes[currentRow][column];
        tile.setText("" + letter);
        String style = new String[] { "absent", "present", "correct" }[result];
        tile.getStyleClass().add(style);
        if (!known.containsKey(letter) || result > known.get(letter)) {
            known.put(letter, result);
            keys.get(letter).getStyleClass().removeAll("absent", "present", "correct");
            keys.get(letter).getStyleClass().add(style);
        }

        if (result == 2) {
            message.setText("Correct letter — attack!");
            playAttackSound(controller.getPlayerClass());
            hero.attack(() -> {
                if (disposed)
                    return;
                displayedEnemyHp = solved && column == results.length - 1 ? 0
                        : Math.max(1, displayedEnemyHp - 10);
                updateEnemyHp();
                recoil(enemy);
                later(160, () -> reveal(guess, results, solved, column + 1));
            });
        } else
            later(240, () -> reveal(guess, results, solved, column + 1));
    }

    private void returnHero(boolean solved) {
        if (disposed)
            return;
        if (solved) {
            displayedEnemyHp = 0;
            updateEnemyHp();
            die(enemy);
        }
        move(hero, heroPosition, .28, true, () -> {
            hero.faceLeft(false);
            if (solved) {
                int points = controller.awardScore();
                scoreLabel.setText("Score: " + controller.getScore());
                message.setText(
                        "Correct! +" + points + " points. " + controller.getStageName() + " cleared.");
                later(400,
                        () -> actions.getChildren()
                                .setAll(HomeView.button(controller.getLevel() == 5 ? "Victory" : "Next stage",
                                        appManager::nextLevel)));
            } else
                enemyTurn();
        });
    }

    // wrong word triggers the enemy's approach, AttackBubble, attack, and return to starting point

    private void enemyTurn() {
        message.setText("Wrong word — your rival advances…");
        move(enemy, enemyPosition, .40, true, () -> {
            attackBubble.showTaunt(controller.getMistakes());
            playAttackSound(controller.getEnemyClass());
            enemy.attack(() -> {
                if (disposed)
                    return;
                playerBar.setProgress(controller.getPlayerHp() / 120.0);
                playerHealth.setText(controller.getPlayerHp() + " / 120 HP · " + controller.getMistakes()
                        + " / 6 mistakes");
                recoil(hero);
                if (controller.isFinished())
                    die(hero);
                move(enemy, enemyPosition, .72, false, () -> {
                    enemy.faceLeft(true);
                    if (controller.isFinished()) {
                        message.setText("Six mistakes. The word was " + controller.getAnswer() + ".");
                        later(600, appManager::showGameOver);
                    } else {
                        currentRow++;
                        currentGuess.setLength(0);
                        busy = false;
                        message.setText(
                                (6 - controller.getMistakes()) + " guesses remain. Choose thy word wisely…");
                        root.requestFocus();
                    }
                });
            });
        });
    }

    private void move(SpriteAnimation sprite, DoubleProperty position, double destination, boolean left,
            Runnable then) {
        if (disposed)
            return;
        sprite.faceLeft(left);
        sprite.run();
        Timeline movement = new Timeline(
                new KeyFrame(Duration.millis(850), new KeyValue(position, destination, Interpolator.LINEAR)));
        animations.add(movement);
        movement.setOnFinished(e -> {
            if (!disposed) {
                sprite.idle();
                then.run();
            }
        });
        movement.play();
    }

    // Schedule the next animation step without blocking JavaFX input or drawing.
    private void later(int millis, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.millis(millis));
        animations.add(pause);
        pause.setOnFinished(e -> {
            if (!disposed)
                action.run();
        });
        pause.play();
    }

    private void recoil(SpriteAnimation sprite) {
        TranslateTransition hit = new TranslateTransition(Duration.millis(70), sprite);
        hit.setFromX(0);
        hit.setToX(sprite == hero ? -8 : 8);
        hit.setCycleCount(2);
        hit.setAutoReverse(true);
        animations.add(hit);
        hit.play();
    }

    // SoundManager plays the shared death effect enemy death also fades the battle end.
    
    private void die(SpriteAnimation sprite) {
        SoundManager.playDeath();
        if (sprite == enemy)
            SoundManager.fadeOutAndStop(.9);
        sprite.die();
        FadeTransition fade = new FadeTransition(Duration.millis(450), sprite);
        fade.setToValue(0);
        animations.add(fade);
        fade.play();
    }

    private void playAttackSound(CharacterClass characterClass) {
        switch (characterClass) {
            case LANCER -> SoundManager.playSpearAttack();
            case MONK -> SoundManager.playMonkAttack();
            case ARCHER -> SoundManager.playArrowHit();
            case WARRIOR -> SoundManager.playSwordClash();
        }
    }

    private void updateEnemyHp() {
        enemyBar.setProgress(displayedEnemyHp / 100.0);
        enemyHealth.setText(displayedEnemyHp + " / 100 HP");
    }

    public StackPane getRoot() {
        return root;
    }

    // AppManager calls this when leaving the battle so old animations cannot update

    public void dispose() {
        disposed = true;
        attackBubble.dispose();
        animations.forEach(Animation::stop);
        hero.stop();
        enemy.stop();
    }

    private Image loadStageBackground() {
        String relative = "images/backgrounds/" + backgroundFileName;
        try {
            var resource = GameView.class.getResource("/" + relative);
            Path file = Path.of("resources", relative);
            if (resource == null && !Files.isRegularFile(file))
                return null;
            Image image = new Image(resource != null ? resource.toExternalForm() : file.toUri().toString(),
                    false);
            if (image.isError()) {
                System.err.println(
                        "Cannot load background " + backgroundFileName + ": " + image.getException());
                return null;
            }
            return image;
        } catch (RuntimeException error) {
            System.err.println("Cannot load background " + backgroundFileName + ": " + error.getMessage());
            return null;
        }
    }

    //HElps formatting bg
    private void paintScene(Canvas canvas) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        if (w <= 0 || h <= 0)
            return;
        g.clearRect(0, 0, w, h);
        g.setFill(Color.web("#252b3e"));
        g.fillRect(0, 0, w, h);
        if (stageBackground == null) {
            g.setFill(Color.LIGHTGRAY);
            g.fillText("Background missing: " + backgroundFileName, 16, 26);
            return;
        }
        double iw = stageBackground.getWidth(), ih = stageBackground.getHeight();
        double scale = Math.max(w / iw, h / ih);
        double sw = Math.min(iw, w / scale), sh = Math.min(ih, h / scale);
        g.setImageSmoothing(true);
        double floorFocus = switch (controller.getLevel()) {
            case 1 -> .64;
            case 2 -> .58;
            case 4 -> .62;
            case 5 -> .64;
            default -> 0;
        };
        // Put the chosen source floor line at the fighters' on-screen foot line.
        double cropY = Math.max(0, Math.min(ih - sh, ih * floorFocus - sh * .84));
        g.drawImage(stageBackground, (iw - sw) / 2, cropY, sw, sh, 0, 0, w, h);
    }
}
