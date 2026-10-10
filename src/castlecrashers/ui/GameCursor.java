package castlecrashers.ui;

import javafx.collections.ListChangeListener;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/** Keeps the supplied cursor on controls and on newly added screen content. */
public final class GameCursor {
    private static final ImageCursor CURSOR = createCursor();
    private static final String INSTALLED = "castlecrashers.customCursor";

    private GameCursor() {
    }

    private static ImageCursor createCursor() {
        Image original = TinyUi.image("travel-book/cursor.png");
        WritableImage enlarged = new WritableImage((int) original.getWidth() * 2,
                (int) original.getHeight() * 2);
        for (int y = 0; y < enlarged.getHeight(); y++) {
            for (int x = 0; x < enlarged.getWidth(); x++) {
                enlarged.getPixelWriter().setArgb(x, y, original.getPixelReader().getArgb(x / 2, y / 2));
            }
        }
        return new ImageCursor(enlarged, 0, 0);
    }

    // AppManager applies the cursor to each new screen, including controls added
    // later.
    public static void apply(Node node) {
        if (node.getProperties().putIfAbsent(INSTALLED, Boolean.TRUE) != null)
            return;
        node.cursorProperty().bind(new javafx.beans.property.SimpleObjectProperty<>(CURSOR));
        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(GameCursor::apply);
            parent.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) change -> {
                while (change.next()) {
                    if (change.wasAdded())
                        change.getAddedSubList().forEach(GameCursor::apply);
                }
            });
        }
    }
}
