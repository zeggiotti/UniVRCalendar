package zeggiotti.univrprettycalendar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import prettifier.Event;
import prettifier.Prettifier;

import java.io.IOException;

public class EventPane extends AnchorPane {

    @FXML
    private TextField nameField;

    @FXML
    private Label locationLabel, aulaLabel;

    @FXML
    private Button removeButton;

    private final Event event;
    private final Prettifier prettifier;

    public EventPane(Event event, Prettifier prettifier) {
        this.event = event;
        this.prettifier = prettifier;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/event-pane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        nameField.setText(event.getName());
        locationLabel.setText(event.getLocation());
        if(this.event.getLocation().isBlank())
            aulaLabel.setVisible(false);
    }

    public Event getEvent() {
        return event;
    }

    public String getName() {
        return nameField.getText();
    }

    @FXML
    protected void onRemoveButtonClicked() {
        this.prettifier.removeEvent(this.event);
        ((VBox) this.getParent()).getChildren().remove(this);
    }

}
