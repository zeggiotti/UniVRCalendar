package zeggiotti.univrprettycalendar;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import prettifier.Event;
import prettifier.Prettifier;

import java.io.File;
import java.util.Optional;

public class MainController {

    @FXML
    private VBox eventsBox;

    @FXML
    private CheckBox locationCheckBox;

    private Prettifier prettifier;

    @FXML
    public void initialize() {
        prettifier = new Prettifier(Main.calendar);
        for(Event event : prettifier.getOldEvents()){
            eventsBox.getChildren().add(new EventPane(event,  prettifier));
        }
    }

    @FXML
    protected void onSaveBtnClicked() {
        for(Node node : eventsBox.getChildren()){
            EventPane eventPane = (EventPane) node;
            Event oldEvent = eventPane.getEvent();
            Event newEvent = new Event(eventPane.getName(), oldEvent.getLocation());
            prettifier.setBinding(oldEvent, newEvent);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Apri Calendario");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Calendari (*.ics)", "*.ics");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("*.ics");
        File output = fileChooser.showSaveDialog((Stage) eventsBox.getScene().getWindow());

        if(output == null)
            return;

        prettifier.save(output);

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Email");
        textInputDialog.setHeaderText("Puoi inserire il tuo indirizzo\nper ricevere il calendario via email.");
        textInputDialog.getDialogPane().getStylesheets().add(
                Main.class.getResource("styles/dialog.css").toExternalForm()
        );

        Optional<String> result = textInputDialog.showAndWait();

        result.ifPresent(address -> {
            prettifier.sendCalendarViaEmail(address, output);
        });

        ((Stage) eventsBox.getScene().getWindow()).close();
    }

    @FXML
    protected void onLocationCheckBoxClicked() {
        prettifier.writeLocation(locationCheckBox.isSelected());
    }



}