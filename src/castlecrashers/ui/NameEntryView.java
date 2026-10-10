package castlecrashers.ui;

import castlecrashers.AppManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;

// Collects the name shown in battle and story dialogue. 
public final class NameEntryView {
    private final StackPane root;

    // Sends the name before opening classes
    
    public NameEntryView(AppManager manager, String previousName) {
        TextField input = new TextField(previousName);
        input.setId("player-name");
        input.setPromptText("Your name");
        input.setMaxWidth(340);
        input.getStyleClass().add("name-input");
        input.setTextFormatter(new TextFormatter<String>(
                change -> change.getControlNewText().length() <= 24 ? change : null));

        Label guidance = HomeView.label("Enter a name (1–24 characters).", "muted");
        Runnable proceed = () -> {
            if (input.getText().isBlank()) {
                guidance.setText("Please enter your name first.");
                input.requestFocus();
                return;
            }
            manager.chooseName(input.getText().strip());
        };
        Button next = HomeView.button("Choose class", proceed);
        input.setOnAction(event -> proceed.run());
        root = HomeView.screen(HomeView.label("What is your name?", "title"), input, guidance, next,
                HomeView.button("Back", manager::showColorSelection));
        Platform.runLater(input::requestFocus);
    }

    public StackPane getRoot() {
        return root;
    }
}
