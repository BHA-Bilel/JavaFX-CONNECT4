package bg.connectFour.popup;

import bg.connectFour.MainApp;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceDialog;

public class MyChoiceDialog {

    public static void setupChoiceDialog(ChoiceDialog<String> choiceDialog, StringProperty title, StringProperty header, StringProperty content) {
        choiceDialog.initOwner(MainApp.stage);
        choiceDialog.getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        choiceDialog.getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        choiceDialog.titleProperty().bind(title);
        choiceDialog.headerTextProperty().bind(header);
        choiceDialog.contentTextProperty().bind(content);
        choiceDialog.getDialogPane().getStylesheets().add(
                MyChoiceDialog.class.getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

}
