package castlecrashers.ui;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

//Taunts
public final class AttackBubble extends VBox {
    private static final String[] TAUNTS = { "Futile.", "Pathetic.", "Too slow.", "Is that all?", "Kneel.",
            "You have failed." };
    private final Label text = new Label();
    private final PauseTransition timeout = new PauseTransition(Duration.seconds(1.8));

    public AttackBubble() {
        setId("enemy-attack-bubble");
        setManaged(false);
        setMouseTransparent(true);
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(170);
        text.getStyleClass().add("attack-bubble");
        Polygon tail = new Polygon(0, 0, 14, 0, 7, 9);
        tail.setFill(Color.web("#f4dfb5"));
        getChildren().addAll(text, tail);
        setVisible(false);
        timeout.setOnFinished(event -> setVisible(false));
    }

    // GameView calls this at the start of an enemy attack then hides after timer ends.
    
    public void showTaunt(int mistake) {
        text.setText(TAUNTS[Math.floorMod(mistake - 1, TAUNTS.length)]);
        setVisible(true);
        timeout.playFromStart();
    }

    public void dispose() {
        timeout.stop();
        setVisible(false);
    }
}
