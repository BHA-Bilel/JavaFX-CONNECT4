package bg.connectFour.popup;

import bg.connectFour.MainApp;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class MyTextInputDialog extends TextInputDialog {
    public MyTextInputDialog(StringProperty header, String content) {
        initOwner(MainApp.stage);
        getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        setHeaderText(header.getValue());
        ((TextField) getDialogPane().lookup(".text-field")).setText(content);
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

    public MyTextInputDialog(StringProperty title, StringProperty header, String content) {
        initOwner(MainApp.stage);
        getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        setTitle(title.getValue());
        setHeaderText(header.getValue());
        ((TextField) getDialogPane().lookup(".text-field")).setText(content);
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }
}
