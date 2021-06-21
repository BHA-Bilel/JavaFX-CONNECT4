package bg.connectFour.room;

import bg.connectFour.MainApp;
import bg.connectFour.lang.Language;
import bg.connectFour.popup.MyAlert;
import bg.connectFour.server.local.LocalClient;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import shared.MainRequest;
import shared.LocalRoomInfo;
import shared.OnlineRoomInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class JoinApp extends VBox {

    private final JoinClient joinClient;
    private Map<String, LocalRoomInfo> rooms;
    private FeaturingGP featuringGP;
    private JFXTextField username;
    private final MainApp mainApp;
    private final Map<Integer, KeyCode> keycodes;
    private JFXButton refreshButton, nextButton, return_home;
    private volatile boolean returned = false;
    private volatile boolean join_shortcuts_activated = false;

    public JoinApp(MainApp mainApp, String username, Socket joinSocket) {
        super(20);
        this.mainApp = mainApp;
        keycodes = new HashMap<>();
        keycodes.put(0, KeyCode.NUMPAD1);
        keycodes.put(1, KeyCode.NUMPAD2);
        keycodes.put(2, KeyCode.NUMPAD3);
        keycodes.put(3, KeyCode.NUMPAD4);
        keycodes.put(4, KeyCode.NUMPAD5);
        createGUI(username);
        joinClient = new JoinClient(joinSocket);
    }

    public JoinApp(MainApp mainApp, String username, Map<String, LocalRoomInfo> rooms) {
        super(20);
        this.mainApp = mainApp;
        this.rooms = rooms;
        keycodes = new HashMap<>();
        keycodes.put(0, KeyCode.NUMPAD1);
        keycodes.put(1, KeyCode.NUMPAD2);
        keycodes.put(2, KeyCode.NUMPAD3);
        keycodes.put(3, KeyCode.NUMPAD4);
        keycodes.put(4, KeyCode.NUMPAD5);
        createGUI(username);
        show_next_local_rooms();
        joinClient = new JoinClient();
    }

    private void createGUI(String username) {
        setAlignment(Pos.CENTER);
        this.username = new JFXTextField();
        this.username.promptTextProperty().bind(Language.USERNAME);
        this.username.setText(username);
        this.username.maxWidthProperty().bind(MainApp.stage.widthProperty().divide(3));
        this.username.setMinSize(JFXTextField.USE_PREF_SIZE, JFXTextField.USE_PREF_SIZE);
        VBox username_vb = new VBox();
        username_vb.setAlignment(Pos.CENTER);
        username_vb.getChildren().add(this.username);

        featuringGP = new FeaturingGP();

        HBox bottom_hb = new HBox();
        bottom_hb.spacingProperty().bind(MainApp.spacingProperty);
        bottom_hb.setAlignment(Pos.CENTER);
        return_home = new JFXButton();
        return_home.textProperty().bind(Language.RET_HOME);
        return_home.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        return_home.getStyleClass().add("kick");
        return_home.setOnAction(e -> returnHome(false));
        refreshButton = new JFXButton();
        refreshButton.textProperty().bind(Language.REFRESH);
        refreshButton.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        refreshButton.setOnAction(e -> {
            MyAlert.show_alert("HOLD");
            removeJoinShortcuts();
            featuringGP.getChildren().clear();
            if (MainApp.online.isSelected()) {
                joinClient.askForMore();
            } else {
                searchLocalRooms();
            }
        });
        bottom_hb.getChildren().addAll(return_home, refreshButton);
        if (MainApp.local.isSelected()) {
            nextButton = new JFXButton();
            nextButton.getStyleClass().add("take-place");
            nextButton.textProperty().bind(Language.NEXT);
            nextButton.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
            nextButton.setOnAction(e -> {
                MyAlert.show_alert("HOLD");
                removeJoinShortcuts();
                featuringGP.getChildren().clear();
                show_next_local_rooms();
            });
            nextButton.setVisible(false);
            bottom_hb.getChildren().addAll(nextButton);
        }

        getChildren().addAll(username_vb, featuringGP, bottom_hb);
    }

    private void searchLocalRooms() {
        Thread local_search = new Thread(() -> {
            rooms = LocalClient.send_join_req();
            Platform.runLater(this::show_next_local_rooms);
        });
        local_search.start();
    }

    private void show_next_local_rooms() {
        int i = 0;
        Iterator<Map.Entry<String, LocalRoomInfo>> iterator = rooms.entrySet().iterator();
        Map<String, LocalRoomInfo> temp = new HashMap<>();
        while (i < 5 && iterator.hasNext()) {
            Map.Entry<String, LocalRoomInfo> entry = iterator.next();
            temp.put(entry.getKey(), entry.getValue());
            iterator.remove();
            i++;
        }
        if (rooms.size() > 0) {
            nextButton.setVisible(true);
            add_next_shortcut();
        } else {
            nextButton.setVisible(false);
            remove_next_shortcut();
        }
        featuringGP.refresh(temp);
        MyAlert.hide_alert("HOLD");
    }

    public void closeJoinApp() {
        remove_shortcuts();
        if (MainApp.online.isSelected())
            joinClient.closeConn();
        Platform.runLater(() -> getChildren().clear());
    }

    public synchronized void returnHome(boolean exception) {
        if (returned) return;
        returned = true;
        MyAlert.show_alert("HOLD");
        closeJoinApp();
        Platform.runLater(() -> mainApp.returnHomeApp(exception));

    }

    public void setup_shortcuts() {
        if (join_shortcuts_activated) return;
        join_shortcuts_activated = true;
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), return_home::fire);
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), refreshButton::fire);
    }

    public void add_next_shortcut() {
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), nextButton::fire);
    }

    private void remove_next_shortcut() {
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
    }

    private void removeJoinShortcuts() {
        keycodes.forEach((key, value) -> MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(value, KeyCombination.CONTROL_DOWN)));
    }

    public void remove_shortcuts() {
        if (!join_shortcuts_activated) return;
        join_shortcuts_activated = false;
        removeJoinShortcuts();
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
    }

    class JoinClient {

        private Socket socket;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;

        public JoinClient() {
        }

        public JoinClient(Socket joinSocket) {
            try {
                socket = joinSocket;
                socket.setSoTimeout(0);
                objOut = new ObjectOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());
                objOut.writeInt(MainRequest.JOIN.ordinal());
                objOut.flush();
                int list_size = objIn.readInt();
                List<OnlineRoomInfo> list = new ArrayList<>();
                for (int i = 0; i < list_size; i++) {
                    list.add((OnlineRoomInfo) objIn.readObject());
                }
                Platform.runLater(() -> featuringGP.refresh(list));
            } catch (IOException | ClassNotFoundException ex) {
                returnHome(true);
            }
        }

        private void askForMore() {
            Thread online_search = new Thread(() -> {
                try {
                    objOut.writeBoolean(true);
                    objOut.flush();
                    int list_size = objIn.readInt();
                    List<OnlineRoomInfo> list = new ArrayList<>();
                    for (int i = 0; i < list_size; i++) {
                        list.add((OnlineRoomInfo) objIn.readObject());
                    }
                    Platform.runLater(() -> {
                        featuringGP.refresh(list);
                        MyAlert.hide_alert("HOLD");
                    });
                } catch (IOException | ClassNotFoundException e) {
                    returnHome(true);
                }
            });
            online_search.start();
        }

        public void attemptJoin(int roomID, String username) {
            MyAlert.show_alert("HOLD");
            Socket roomSocket = new Socket();
            Thread online_join = new Thread(() -> {
                try {
                    roomSocket.connect(new InetSocketAddress(MainApp.ONLINE_IP, roomID), MainApp.ONLINE_TIMEOUT);
                    RoomApp roomApp = new RoomApp(mainApp, JoinApp.this, roomSocket, String.valueOf(roomID), username);
                    Platform.runLater(() -> mainApp.setRoomApp(roomApp));
                } catch (IOException e) {
                    try {
                        roomSocket.close();
                    } catch (IOException ignore) {
                    }
                    Platform.runLater(() -> {
                        Alert alert = new MyAlert(AlertType.WARNING, Language.ROOM_H, Language.JOIN_ERROR);
                        alert.show();
                    });
                } finally {
                    MyAlert.hide_alert("HOLD");
                }
            });
            online_join.start();
        }

        public void attemptJoin(String ip, int roomID, String username) {
            MyAlert.show_alert("HOLD");
            Socket roomSocket = new Socket();
            Thread local_join = new Thread(() -> {
                try {
                    roomSocket.connect(new InetSocketAddress(ip, roomID), MainApp.LOCAL_TIMEOUT);
                    RoomApp roomApp = new RoomApp(mainApp, JoinApp.this, ip, roomSocket, username);
                    Platform.runLater(() -> mainApp.setRoomApp(roomApp));
                } catch (IOException e) {
                    try {
                        roomSocket.close();
                    } catch (IOException ignore) {
                    }
                    Platform.runLater(() -> {
                        Alert alert = new MyAlert(AlertType.WARNING, Language.ROOM_H, Language.JOIN_ERROR);
                        alert.show();
                    });
                } finally {
                    MyAlert.hide_alert("HOLD");
                }
            });
            local_join.start();
        }

        private void closeConn() {
            try {
                objOut.writeBoolean(false);
                objOut.flush();
            } catch (IOException ignore) {
            } finally {
                try {
                    objIn.close();
                    objOut.close();
                    socket.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    class FeaturingGP extends GridPane {
        private final Label emptyLabel;

        public FeaturingGP() {
            spacingProperty().bind(MainApp.spacingProperty);
            styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));
            setAlignment(Pos.CENTER);
            hgapProperty().bind(MainApp.spacingProperty);
            vgapProperty().bind(MainApp.spacingProperty);

            emptyLabel = new Label();
            emptyLabel.textProperty().bind(Language.NO_ROOMS);
            GridPane.setHalignment(emptyLabel, HPos.CENTER);
        }

        public void refresh(List<OnlineRoomInfo> infos) {
            int row = 0, column = 0;
            for (OnlineRoomInfo info : infos) {
                Label room_label = new Label(info.host_name);
                Label room_players = new Label(info.room_players + " / " + MainApp.BG_GAME.players + Language.PLAYERS.getValue());
                JFXButton join = new JFXButton();
                join.textProperty().bind(Language.JOIN);
                MainApp.stage.getScene().getAccelerators().put(
                        new KeyCodeCombination(keycodes.get(row), KeyCombination.CONTROL_DOWN), join::fire);
                join.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
                join.setOnAction(e -> {
                    if (!MainApp.valid_username(username.getText()))
                        return;
                    joinClient.attemptJoin(info.room_id, username.getText());
                });
                GridPane.setHalignment(room_label, HPos.CENTER);
                featuringGP.add(room_label, column, row);
                column++;

                GridPane.setHalignment(room_players, HPos.CENTER);
                featuringGP.add(room_players, column, row);
                column++;

                GridPane.setHalignment(join, HPos.CENTER);
                featuringGP.add(join, column, row);
                column = 0;
                row++;
            }
            if (infos.isEmpty()) {
                featuringGP.add(emptyLabel, 0, 2);
            }
        }

        public void refresh(Map<String, LocalRoomInfo> infos) {
            int row = 0, column = 0;
            for (Map.Entry<String, LocalRoomInfo> entry : infos.entrySet()) {
                Label room_label = new Label(entry.getValue().host_name);
                Label room_players = new Label(entry.getValue().room_players + " / " + MainApp.BG_GAME.players + Language.PLAYERS.getValue());
                JFXButton join = new JFXButton();
                join.textProperty().bind(Language.JOIN);
                MainApp.stage.getScene().getAccelerators().put(
                        new KeyCodeCombination(keycodes.get(row), KeyCombination.CONTROL_DOWN), join::fire);
                join.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
                join.setOnAction(e -> {
                    if (!MainApp.valid_username(username.getText()))
                        return;
                    joinClient.attemptJoin(entry.getKey().substring(1), entry.getValue().room_id, username.getText());
                });
                GridPane.setHalignment(room_label, HPos.CENTER);
                featuringGP.add(room_label, column, row);
                column++;

                GridPane.setHalignment(room_players, HPos.CENTER);
                featuringGP.add(room_players, column, row);
                column++;

                GridPane.setHalignment(join, HPos.CENTER);
                featuringGP.add(join, column, row);
                column = 0;
                row++;
            }
            if (infos.isEmpty() && !featuringGP.getChildren().contains(emptyLabel)) {
                featuringGP.add(emptyLabel, 0, 2);
            }
        }
    }

}
