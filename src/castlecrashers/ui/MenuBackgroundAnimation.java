package castlecrashers.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class MenuBackgroundAnimation extends ImageView {

    private static final int FRAME_COUNT = 41;
    private static final int FRAME_DELAY_MS = 80;

    private final List<Image> frames = new ArrayList<>();
    private final Timeline animation;

    private int currentFrame = 0;

    // HomeView owns this frame animation and stops it through AppManager when
    // leaving the menu.
    public MenuBackgroundAnimation() {
        setPreserveRatio(false);
        setSmooth(false);
        setMouseTransparent(true);

        for (int number = 1; number <= FRAME_COUNT; number++) {
            frames.add(loadFrame(number));
        }

        setImage(frames.get(0));

        animation = new Timeline(new KeyFrame(Duration.millis(FRAME_DELAY_MS), event -> {
            currentFrame = (currentFrame + 1) % frames.size();

            setImage(frames.get(currentFrame));
        }));

        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    private Image loadFrame(int number) {
        String fileName = String.format("frame%03d.png", number);

        String resourcePath = "/images/menu/" + fileName;

        URL resource = MenuBackgroundAnimation.class.getResource(resourcePath);

        String imageUrl;

        if (resource != null) {
            imageUrl = resource.toExternalForm();
        } else {
            File file = new File("resources/images/menu/" + fileName);

            if (!file.isFile()) {
                throw new IllegalStateException("Missing menu frame: " + file.getAbsolutePath());
            }

            imageUrl = file.toURI().toString();
        }

        Image image = new Image(imageUrl, false);

        if (image.isError()) {
            throw new IllegalStateException("Could not load menu frame: " + fileName, image.getException());
        }

        return image;
    }

    public void stopAnimation() {
        animation.stop();
    }

    public void startAnimation() {
        animation.play();
    }
}
