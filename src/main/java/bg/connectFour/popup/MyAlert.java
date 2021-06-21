package bg.connectFour.popup;

import bg.connectFour.MainApp;
import bg.connectFour.lang.LANGNAME;
import bg.connectFour.lang.Language;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MyAlert extends Alert {

    private static Map<String, MyAlert> alerts;

    public static void post_init() {
        MyAlert online_setup_alert = new MyAlert(AlertType.INFORMATION, Language.ONLINE_SETUP_H, Language.ONLINE_SETUP_C);
        MyAlert local_setup_alert = new MyAlert(AlertType.INFORMATION, Language.LOCAL_SETUP_H, Language.LOCAL_SETUP_C);
        MyAlert hold_alert = new MyAlert(AlertType.INFORMATION, Language.HOLD_H, Language.HOLD_C);
        MyAlert migration_alert = new MyAlert(AlertType.INFORMATION, Language.MIGRATION_H1, Language.MIGRATION_C1);

        alerts = new HashMap<>();
        alerts.put("ONLINE", online_setup_alert);
        alerts.put("LOCAL", local_setup_alert);
        alerts.put("HOLD", hold_alert);
        alerts.put("MIGRATION", migration_alert);

        for (Alert alert : alerts.values()) {
            alert.getButtonTypes().removeIf(node -> true);
        }
    }

    public static void update_alerts_theme() {
        for (Alert alert : alerts.values()) {
            alert.getDialogPane().getStylesheets().clear();
            alert.getDialogPane().getStylesheets().add(
                    MyAlert.class.getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
        }
    }

    public static void show_alert(String type) {
        MyAlert alert = alerts.get(type);
        if (Platform.isFxApplicationThread()) {
            Window window = alert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(Event::consume);
            alert.show();
        } else {
            Platform.runLater(() -> {
                Window window = alert.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(Event::consume);
                alert.show();
            });
        }
    }

    public static void hide_alert(String type) {
        MyAlert alert = alerts.get(type);
        if (Platform.isFxApplicationThread()) {
            Window window = alert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            window.hide();
        } else {
            Platform.runLater(() -> {
                Window window = alert.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(event -> window.hide());
                window.hide();
            });
        }
    }

    public MyAlert(AlertType alertType, StringProperty header, String content) {
        super(alertType);
        initOwner(MainApp.stage);
        getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        titleProperty().bind(MainApp.GAME_NAME);
        headerTextProperty().bind(header);
        setContentText(content);
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

    public MyAlert(AlertType alertType, StringProperty header, StringProperty content) {
        super(alertType);
        initOwner(MainApp.stage);
        getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        titleProperty().bind(MainApp.GAME_NAME);
        headerTextProperty().bind(header);
        contentTextProperty().bind(content);
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

    public MyAlert(AlertType alertType, StringProperty title, StringProperty header, StringProperty content) {
        super(alertType);
        initOwner(MainApp.stage);
        getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        titleProperty().bind(title);
        headerTextProperty().bind(header);
        contentTextProperty().bind(content);
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

    public MyAlert(AlertType alertType, Stage stage, StringProperty title, StringProperty header, StringProperty content) {
        super(alertType);
        initOwner(stage);
        getDialogPane().lookup(".dialog-pane:header *.header-panel")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.8).asString())
        );
        getDialogPane().lookup(".dialog-pane > *.label.content")
                .styleProperty().bind(
                Bindings.concat(" -fx-font-size: ", MainApp.fontProperty.multiply(.6).asString())
        );
        titleProperty().bind(title);
        headerTextProperty().bind(header);
        contentTextProperty().bind(content);
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

    public void update(String content) {
        setContentText(content);
        getDialogPane().getStylesheets().clear();
        getDialogPane().getStylesheets().add(
                getClass().getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
    }

    public static void first_timer(MainApp mainApp, int welcome_phase) {
        Alert skip_alert = new MyAlert(AlertType.WARNING, Language.SKIP_H, Language.SKIP_C);
        ButtonType go_back = new ButtonType(Language.GO_BACK.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType skip = new ButtonType(Language.SKIP_BT.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
        skip_alert.getButtonTypes().setAll(go_back, skip);

        ButtonType previous = new ButtonType(Language.PREVIOUS.getValue());
        ButtonType next = new ButtonType(Language.NEXT.getValue());
        ButtonType cancel = new ButtonType(Language.CANCEL.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);

        switch (welcome_phase) {
            case 1: {
                Alert lang_alert = new MyAlert(AlertType.CONFIRMATION, Language.LANG_H, Language.LANG_C);
                ButtonType english = new ButtonType(Language.ENGLISH.getValue());
                ButtonType french = new ButtonType(Language.FRENCH.getValue());
                ButtonType arabic = new ButtonType(Language.ARABIC.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
                lang_alert.getButtonTypes().setAll(english, french, arabic, cancel);
                Optional<ButtonType> res = lang_alert.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 1, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == english) {
                    Language.load_lang(LANGNAME.ENGLISH);
                } else if (res.get() == french) {
                    Language.load_lang(LANGNAME.FRENCH);
                } else if (res.get() == arabic) {
                    Language.load_lang(LANGNAME.ARABIC);
                }
                skip_alert = new MyAlert(AlertType.WARNING, Language.SKIP_H, Language.SKIP_C);
                go_back = new ButtonType(Language.GO_BACK.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
                skip = new ButtonType(Language.SKIP_BT.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
                skip_alert.getButtonTypes().setAll(go_back, skip);
                previous = new ButtonType(Language.PREVIOUS.getValue());
                next = new ButtonType(Language.NEXT.getValue());
                cancel = new ButtonType(Language.CANCEL.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
            }
            case 2: {
                Alert theme_alert = new MyAlert(AlertType.CONFIRMATION, Language.THEME_H, Language.THEME_C);
                ButtonType light = new ButtonType(Language.LIGHT.getValue());
                ButtonType dark = new ButtonType(Language.DARK.getValue());
                theme_alert.getButtonTypes().setAll(previous, light, dark, cancel);
                Optional<ButtonType> res = theme_alert.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 2, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == light) {
                    mainApp.update_theme(MainApp.themes[0], MainApp.notif_themes[0]);
                } else if (res.get() == dark) {
                    mainApp.update_theme(MainApp.themes[1], MainApp.notif_themes[1]);
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 1);
                    return;
                }
                skip_alert.getDialogPane().getStylesheets().clear();
                skip_alert.getDialogPane().getStylesheets().add(
                        MyAlert.class.getResource(MainApp.CURRENT_THEME.replace("/main_", "/alert_")).toExternalForm());
            }
            case 3: {
                Alert welcome_alert = new MyAlert(AlertType.INFORMATION, Language.WELCOME_H, Language.WELCOME_C);
                welcome_alert.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = welcome_alert.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 3, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 2);
                    return;
                }
            }
            case 4: {
                Alert general_shortcuts = new MyAlert(AlertType.INFORMATION, Language.GEN_SH_T, Language.GEN_SH_H, Language.GEN_SH_C);
                general_shortcuts.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = general_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 4, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 3);
                    return;
                }
            }
            case 5: {
                Alert main_shortcuts = new MyAlert(AlertType.INFORMATION, Language.M_SH_T, Language.M_SH_H, Language.M_SH_C);
                main_shortcuts.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = main_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 5, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 4);
                    return;
                }
            }
            case 6: {
                Alert join_shortcuts = new MyAlert(AlertType.INFORMATION, Language.J_SH_T, Language.J_SH_H, Language.J_SH_C);
                join_shortcuts.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = join_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 6, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 5);
                    return;
                }
            }
            case 7: {
                Alert room1_shortcuts = new MyAlert(AlertType.INFORMATION, Language.R_SH_T, Language.R_SH_H1, Language.R_SH_C1);
                room1_shortcuts.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = room1_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 7, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 6);
                    return;
                }
            }
            case 8: {
                Alert room2_shortcuts = new MyAlert(AlertType.INFORMATION, Language.R_SH_T, Language.R_SH_H2, Language.R_SH_C2);
                room2_shortcuts.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = room2_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 8, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 7);
                    return;
                }
            }
            case 9: {
                Alert game_shortcuts = new MyAlert(AlertType.INFORMATION, Language.G_SH_T, Language.G_SH_H, Language.G_SH_C);
                game_shortcuts.getButtonTypes().setAll(previous, next, cancel);
                Optional<ButtonType> res = game_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == cancel) {
                    show_skip_alert(mainApp, 9, skip_alert, go_back, skip);
                    return;
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 8);
                    return;
                }
            }
            case 10: {
                Alert final_shortcuts = new MyAlert(AlertType.INFORMATION, Language.SHORTCUTS, Language.FINAL_PHASE_H, Language.FINAL_PHASE_C);
                ButtonType done = new ButtonType(Language.DONE.getValue(), ButtonBar.ButtonData.CANCEL_CLOSE);
                final_shortcuts.getButtonTypes().setAll(previous, done);
                Optional<ButtonType> res = final_shortcuts.showAndWait();
                if (res.isEmpty() || res.get() == done) {
                    mainApp.update_property("FIRST_TIME", "false");
                } else if (res.get() == previous) {
                    MyAlert.first_timer(mainApp, 9);
                }
            }
        }
    }

    private static void show_skip_alert(MainApp mainApp, int welcome_phase, Alert skip_alert, ButtonType go_back, ButtonType skip) {
        Optional<ButtonType> res;
        res = skip_alert.showAndWait();
        if (res.isEmpty() || res.get() == skip) {
            mainApp.update_property("FIRST_TIME", "false");
        } else if (res.get() == go_back) {
            first_timer(mainApp, welcome_phase);
        }
    }

}
