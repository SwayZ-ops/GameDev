package castlecrashers.animation;

import castlecrashers.character.*;
import javafx.animation.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.*;
import javafx.util.Duration;
import java.nio.file.*;
import java.util.*;

// GameView and the selection/dialogue screens use this ImageView to animate a character 
public final class SpriteAnimation extends ImageView {
    private static final Map<String, Image> CACHE = new HashMap<>();

    public enum State {
        IDLE, RUN, ATTACK
    }

    private final CharacterClass characterClass;
    private final Map<State, Image> sheets = new EnumMap<>(State.class);
    private Timeline frames;
    private int frame;
    private boolean dead;

    public SpriteAnimation(UnitColor color, CharacterClass characterClass, boolean faceLeft) {
        this.characterClass = characterClass;
        String folder = "Units/" + color.folder() + " Units/" + characterClass.folder + "/";
        sheets.put(State.IDLE, load(folder + characterClass.idle + ".png"));
        sheets.put(State.RUN, load(folder + characterClass.run + ".png"));
        sheets.put(State.ATTACK, load(folder + characterClass.attack + ".png"));
        setSmooth(false);
        setPreserveRatio(true);
        // Lancer is bigger so ya make it the same frame size
        setFitWidth(characterClass.frameSize * 1.1);
        faceLeft(faceLeft);
        idle();
    }

    // Reuse already used stuff before finding new 
   
    public static Image load(String relativePath) {
        Image cached = CACHE.get(relativePath);
        if (cached != null)
            return cached;
        String name = "images/tiny-swords/" + relativePath;
        var resource = SpriteAnimation.class.getResource("/" + name);
        String url = resource != null ? resource.toExternalForm()
                : Path.of("resources", name).toUri().toString();
        Image image = new Image(url, false);
        if (image.isError())
            throw new IllegalStateException("Missing asset: resources/" + name, image.getException());
        CACHE.put(relativePath, image);
        return image;
    }

    public void faceLeft(boolean left) {
        setScaleX(left ? -1 : 1);
    }

    public void idle() {
        play(State.IDLE, null);
    }

    public void run() {
        play(State.RUN, null);
    }

    public void attack(Runnable onFinished) {
        play(State.ATTACK, onFinished);
    }

    // Move the viewport across one sheet so the aniamtion will work well in frames of the screen
    
    private void play(State state, Runnable onFinished) {
        if (dead)
            return;
        stop();
        frame = 0;
        Image sheet = sheets.get(state);
        setImage(sheet);
        int size = characterClass.frameSize, count = (int) sheet.getWidth() / size;
        if (sheet.getHeight() != size || sheet.getWidth() % size != 0)
            throw new IllegalStateException("Unexpected sprite sheet dimensions");
        setViewport(new Rectangle2D(0, 0, size, size));
        frames = new Timeline(new KeyFrame(Duration.millis(state == State.ATTACK ? 80 : 100), e -> {
            frame = (frame + 1) % count;
            setViewport(new Rectangle2D(frame * size, 0, size, size));
        }));
        frames.setCycleCount(state == State.ATTACK ? count : Animation.INDEFINITE);
        if (state == State.ATTACK)
            frames.setOnFinished(e -> {
                idle();
                if (onFinished != null)
                    onFinished.run();
            });
        frames.play();
    }

    public void die() {
        dead = true;
        stop();
    }

    public void stop() {
        if (frames != null)
            frames.stop();
    }
}
