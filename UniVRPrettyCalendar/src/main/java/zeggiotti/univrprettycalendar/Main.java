package zeggiotti.univrprettycalendar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    protected static File calendar;

    @Override
    public void start(Stage stage) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Apri Calendario");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Calendari (*.ics)", "*.ics");
        fileChooser.getExtensionFilters().add(extFilter);
        calendar = fileChooser.showOpenDialog(stage);

        if(calendar == null)
            System.exit(0);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Main.class.getResource("styles/root.css").toExternalForm());

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}