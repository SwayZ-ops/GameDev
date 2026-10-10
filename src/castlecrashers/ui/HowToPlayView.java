package castlecrashers.ui;

import castlecrashers.AppManager;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/** A book of letter-color examples, opened from the main menu. */
public final class HowToPlayView {
    private final StackPane root;

    public HowToPlayView(AppManager manager) {
        ImageView book = new ImageView(TinyUi.image("travel-book/open-book.png"));
        book.setViewport(new Rectangle2D(32, 84, 1420, 865));
        book.setFitWidth(860);
        book.setFitHeight(480);
        book.setSmooth(false);
        book.setMouseTransparent(true);
        Pane pages = new Pane(book);
        pages.setMinSize(860, 480);
        pages.setPrefSize(860, 480);
        pages.setMaxSize(860, 480);
        note(pages, "Letter clues", 85, 45, 270, 28);
        note(pages, "What they mean", 475, 45, 290, 28);
        note(pages, "Example answer: CASTLE", 85, 91, 290, 16);
        example(pages, "XYZ", "absent", 135);
        example(pages, "A", "present", 240);
        example(pages, "C", "correct", 345);
        note(pages, "GRAY\nThese letters are not in the word.", 475, 133, 290, 20);
        note(pages, "YELLOW\nIn the word, but in the wrong place.", 475, 238, 290, 20);
        note(pages, "GREEN\nIn the word and in the right place.", 475, 343, 290, 20);
        root = HomeView.screen(HomeView.label("How to Play", "title"), pages,
                HomeView.button("Back to menu", manager::showHome));
    }

    private static void note(Pane page, String text, double x, double y, double width, int size) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setPrefWidth(width);
        label.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: " + size
                + "px; -fx-text-fill: #493023;");
        label.relocate(x, y);
        page.getChildren().add(label);
    }

    private static void example(Pane page, String letters, String state, double y) {
        HBox tiles = new HBox(8);
        tiles.setAlignment(Pos.CENTER_LEFT);
        for (char letter : letters.toCharArray()) {
            Label tile = new Label(String.valueOf(letter));
            tile.getStyleClass().add(state);
            tile.setMinSize(58, 58);
            tile.setPrefSize(58, 58);
            tile.setAlignment(Pos.CENTER);
            tile.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-border-width: 2;");
            tiles.getChildren().add(tile);
        }
        tiles.relocate(115, y);
        page.getChildren().add(tiles);
    }

    public StackPane getRoot() {
        return root;
    }
}
