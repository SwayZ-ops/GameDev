package castlecrashers.ui;

import castlecrashers.AppManager;
import javafx.scene.layout.StackPane;

// AppManager opens this after six mistakes; its buttons call back into the manager.
public class GameOverView {
    private final StackPane root;

    public GameOverView(AppManager manager) {
        root = HomeView.screen(HomeView.label("GAME OVER", "title"),
                HomeView.label(manager.getDefeatText(), "subtitle"),
                HomeView.button("Retry stage", manager::showGame), HomeView.button("Home", manager::showHome),
                HomeView.button("Exit", manager::exitGame));
    }

    public StackPane getRoot() {
        return root;
    }
}
