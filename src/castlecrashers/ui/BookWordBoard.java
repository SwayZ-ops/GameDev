package castlecrashers.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

//PLaces the rows
public final class BookWordBoard extends StackPane {
    // GameView supplies all the grids for guessing 
    
    public BookWordBoard(GridPane board, int letters) {
        double width = Math.max(440, letters * 40 + 110);
        ImageView book = new ImageView(TinyUi.image("travel-book/open-book.png"));
        // Formatting of the book
        book.setViewport(new Rectangle2D(32, 84, 1420, 865));
        book.setFitWidth(width);
        book.setFitHeight(240);
        book.setSmooth(false);
        book.setMouseTransparent(true);
        getStyleClass().add("book-board");
        setMinSize(width, 240);
        setPrefSize(width, 240);
        setMaxSize(width, 240);
        getChildren().addAll(book, board);
    }
}
