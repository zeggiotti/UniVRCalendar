module zeggiotti.univrprettycalendar {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;


    opens zeggiotti.univrprettycalendar to javafx.fxml;
    exports zeggiotti.univrprettycalendar;
}