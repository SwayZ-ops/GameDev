package castlecrashers.ui;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

//Uses the original A–Z pixels from the the sprite sheet
public final class PixelLetters {
    private static final int CELL = 32;
    private static final String[] ROWS = { "           A  B C ", "D E F G H I J K L ", "M N   O P Q R S T ",
            "U V W X Y Z       " };

    private PixelLetters() {
    }

    // GameView attaches this to tiles and keys so changing their text also changes how they look.

    public static void apply(Labeled label, double scale) {
        label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        label.textProperty().addListener((observable, previous, text) -> update(label, text, scale));
        update(label, label.getText(), scale);
    }

    private static void update(Labeled label, String text, double scale) {
        HBox letters = new HBox(2);
        letters.setAlignment(Pos.CENTER);
        letters.setMouseTransparent(true);
        if (text != null) {
            for (char letter : text.toCharArray()) {
                ImageView glyph = glyph(letter, scale);
                if (glyph != null)
                    letters.getChildren().add(glyph);
            }
        }
        label.setGraphic(letters);
        label.setAccessibleText(text == null ? "" : text);
    }

    // Find the letter's atlas cell and trim transparent padding before displaying the pixels
   
    private static ImageView glyph(char letter, double scale) {
        Image sheet = TinyUi.image("travel-book/letter-sheet.png");
        for (int row = 0; row < ROWS.length; row++) {
            int column = ROWS[row].indexOf(letter);
            if (column < 0 || letter == ' ')
                continue;
            int left = CELL, top = CELL, right = -1, bottom = -1;
            for (int y = 0; y < CELL; y++) {
                for (int x = 0; x < CELL; x++) {
                    if ((sheet.getPixelReader().getArgb(column * CELL + x, row * CELL + y) >>> 24) > 0) {
                        left = Math.min(left, x);
                        top = Math.min(top, y);
                        right = Math.max(right, x);
                        bottom = Math.max(bottom, y);
                    }
                }
            }
            if (right < left)
                throw new IllegalStateException("Missing letter: " + letter);
            ImageView image = new ImageView(sheet);
            image.setViewport(new Rectangle2D(column * CELL + left, row * CELL + top, right - left + 1,
                    bottom - top + 1));
            image.setFitWidth((right - left + 1) * scale);
            image.setFitHeight((bottom - top + 1) * scale);
            image.setSmooth(false);
            return image;
        }
        return null;
    }
}
