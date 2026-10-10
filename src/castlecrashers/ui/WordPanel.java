package castlecrashers.ui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

//Book left letters right
public final class WordPanel extends Region {
    private final Label hint;
    private final Label message;
    private final BookWordBoard book;
    private final StackPane keyboard;
    private final HBox actions;
    private final Group bookGroup;
    private final Group keyboardGroup;
    private final Scale bookScale = new Scale();
    private final Scale keyboardScale = new Scale();

    public WordPanel(Label hint, BookWordBoard book, Label message, StackPane keyboard, HBox actions) {
        this.hint = hint;
        this.book = book;
        this.message = message;
        this.keyboard = keyboard;
        this.actions = actions;
        bookGroup = new Group(book);
        keyboardGroup = new Group(keyboard);
        bookGroup.getTransforms().add(bookScale);
        keyboardGroup.getTransforms().add(keyboardScale);
        hint.setWrapText(true);
        hint.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        message.setWrapText(true);
        message.setAlignment(javafx.geometry.Pos.CENTER);
        getChildren().addAll(hint, bookGroup, message, keyboardGroup, actions);
        setBorder(TinyUi.wood());
        getStyleClass().add("word-panel");
        setBackground(new Background(new BackgroundImage(TinyUi.image("Wood Table/WoodTable_Slots.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(192, 192, false, false, false, false))));
        setMinHeight(280);
    }

    @Override
    // GameView supplies the nodes, resize the grid even if the window resizes
  
    protected void layoutChildren() {
        double x = getInsets().getLeft() + 8;
        double y = getInsets().getTop() + 4;
        double width = Math.max(1, getWidth() - x - getInsets().getRight() - 8);
        double height = Math.max(1, getHeight() - y - getInsets().getBottom() - 4);
        double gap = 24;
        double leftWidth = (width - gap) * .42;
        double rightWidth = width - gap - leftWidth;
        double rightX = x + leftWidth + gap;

        double hintHeight = Math.min(54, hint.prefHeight(leftWidth));
        hint.resizeRelocate(x, y, leftWidth, hintHeight);
        double bookWidth = book.prefWidth(-1);
        double bookHeight = book.prefHeight(-1);
        book.resize(bookWidth, bookHeight);
        double scale = Math.min(leftWidth / bookWidth, Math.max(1, height - hintHeight - 8) / bookHeight);
        bookScale.setX(scale);
        bookScale.setY(scale);
        bookGroup.setLayoutX(x + (leftWidth - bookWidth * scale) / 2);
        bookGroup.setLayoutY(y + hintHeight + 8 + (height - hintHeight - 8 - bookHeight * scale) / 2);

        double keyWidth = keyboard.prefWidth(-1);
        double keyHeight = keyboard.prefHeight(keyWidth);
        keyboard.resize(keyWidth, keyHeight);
        double messageHeight = Math.max(22, message.prefHeight(rightWidth));
        double actionHeight = actions.getChildren().isEmpty() ? 0 : actions.prefHeight(rightWidth) + 8;
        double keyScale = Math.min(rightWidth / keyWidth,
                Math.max(1, height - messageHeight - actionHeight - 16) / keyHeight);
        keyboardScale.setX(keyScale);
        keyboardScale.setY(keyScale);
        double totalHeight = messageHeight + 8 + keyHeight * keyScale + actionHeight;
        double top = y + Math.max(0, (height - totalHeight) / 2);
        message.resizeRelocate(rightX, top, rightWidth, messageHeight);
        keyboardGroup.setLayoutX(rightX + (rightWidth - keyWidth * keyScale) / 2);
        keyboardGroup.setLayoutY(top + messageHeight + 8);
        actions.resizeRelocate(rightX, top + messageHeight + 16 + keyHeight * keyScale, rightWidth,
                Math.max(0, actionHeight - 8));
    }
}
