package castlecrashers.ui;

import castlecrashers.AppManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeView {

    private final StackPane root;
    private final MenuBackgroundAnimation background;

    // MenuBackgroundAnimation supplies the moving backdrop; the buttons call AppManager
    
    public HomeView(AppManager appManager) {

        root = new StackPane();
        background = new MenuBackgroundAnimation();
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        Region darkOverlay = new Region();
        darkOverlay.setStyle("-fx-background-color: rgba(5, 12, 25, 0.42);");
        darkOverlay.setMouseTransparent(true);
        // The generated scroll includes the title's mystical calligraphy.
        ImageView title = new ImageView(TinyUi.image("wooden/title-scroll.png"));
        title.setFitWidth(720);
        title.setPreserveRatio(true);
        title.setSmooth(true);
        title.setAccessibleText("Castle Crashers");
        VBox menu = new VBox(24, title,
                woodenBoard(boardButton("START", appManager::showColorSelection)),
                woodenBoard(boardButton("HOW TO PLAY", appManager::showHowToPlay)),
                woodenBoard(boardButton("QUIT", appManager::exitGame)));
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(30));
        root.getChildren().addAll(background, darkOverlay, menu);
    }

    private static StackPane woodenBoard(Node content) {
        ImageView board = new ImageView(TinyUi.image("wooden/menu-scroll.png"));
        board.setFitWidth(376);
        board.setFitHeight(90);
        board.setSmooth(true);
        board.setMouseTransparent(true);
        StackPane panel = new StackPane(board, content);
        panel.getStyleClass().add("scroll-action");
        panel.setMinSize(376, 90);
        panel.setPrefSize(376, 90);
        panel.setMaxSize(376, 90);
        return panel;
    }

    private static Button boardButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMinSize(376, 90);
        button.setPrefSize(376, 90);
        button.setMaxSize(376, 90);
        button.setStyle("-fx-background-color: transparent; -fx-border-width: 0; "
                + "-fx-font-family: 'Georgia'; -fx-font-style: italic; "
                + "-fx-font-size: 28px; -fx-text-fill: #432330;");
        button.setOnAction(event -> action.run());
        return button;
    }

    public static Label label(String text, String style) {
        Label label = new Label(text);
        label.getStyleClass().add(style);
        return label;
    }

    // Other views reuse this for resuability
  
    public static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setOnAction(event -> action.run());
        TinyUi.styleButton(button);
        return button;
    }

    public static StackPane screen(Node... children) {
        VBox box = new VBox(18, children);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        return new StackPane(box);
    }

    public StackPane getRoot() {
        return root;
    }

    public void stopBackground() {
        background.stopAnimation();
    }
}
