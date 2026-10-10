package castlecrashers;

import javafx.application.Application;
import javafx.stage.Stage;

// JavaFX starts here, then AppManager creates the window and opens the home screen.
public class Main extends Application {

    @Override
    public void start(Stage stage) {

        new AppManager(stage);
    }

    public static void main(String[] args) {

        launch(args);
    }
}
