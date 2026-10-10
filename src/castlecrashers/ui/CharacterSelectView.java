package castlecrashers.ui;

import castlecrashers.AppManager;
import castlecrashers.character.*;
import castlecrashers.animation.SpriteAnimation;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import java.util.*;

public final class CharacterSelectView {
    private final StackPane root;
    private final List<SpriteAnimation> sprites = new ArrayList<>();

    // AppManager reuses this screen for color selection and then for class.
    
    public CharacterSelectView(AppManager manager, boolean chooseClass, UnitColor selected) {
        HBox cards = new HBox(10);
        cards.setAlignment(Pos.CENTER);
        if (chooseClass) {
            for (CharacterClass type : CharacterClass.values())
                cards.getChildren()
                        .add(card(selected, type, type.toString(), () -> manager.chooseClass(type)));
        } else {
            for (UnitColor color : UnitColor.values())
                cards.getChildren().add(bannerCard(color,
                        () -> manager.chooseColor(color)));
        }
        var subtitle = HomeView.label(selected + " faction", "subtitle");
        subtitle.setVisible(chooseClass);
        subtitle.setManaged(chooseClass);
        root = HomeView
                .screen(HomeView.label(chooseClass ? "Choose your class" : "Choose your color", "title"),
                        subtitle,
                        cards,
                        HomeView.button("Back", chooseClass ? manager::showNameEntry : manager::showHome));
    }

    // Color choices use the supplied faction banners; class choices still show animations.
    private VBox bannerCard(UnitColor color, Runnable choose) {
        StackPane portrait = new StackPane(TinyUi.banner(color));
        portrait.setMinSize(160, 210);
        portrait.setPrefSize(160, 210);
        portrait.setMaxSize(160, 210);
        javafx.scene.control.Button button = HomeView.button(color.toString(), choose);
        button.setMinWidth(120);
        portrait.setOnMouseClicked(event -> button.fire());
        VBox card = new VBox(8, portrait, button);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private VBox card(UnitColor color, CharacterClass type, String text, Runnable choose) {
        SpriteAnimation sprite = new SpriteAnimation(color, type, false);
        sprites.add(sprite);
        // DImensions for Lancer since it big
       
        StackPane portrait = new StackPane(sprite);
        portrait.setMinSize(160, 210);
        portrait.setPrefSize(160, 210);
        portrait.setMaxSize(160, 210);
        VBox card = new VBox(8, portrait, HomeView.button(text, choose));
        card.setAlignment(Pos.CENTER);
        return card;
    }

    public StackPane getRoot() {
        return root;
    }

    public void dispose() {
        sprites.forEach(SpriteAnimation::stop);
    }
}
