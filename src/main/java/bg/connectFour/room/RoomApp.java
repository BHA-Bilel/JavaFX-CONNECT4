package bg.connectFour.room;

import bg.connectFour.MainApp;
import bg.connectFour.chat.ChatApp;
import bg.connectFour.game.GameApp;
import bg.connectFour.lang.Language;
import bg.connectFour.popup.MyAlert;
import bg.connectFour.popup.MyChoiceDialog;
import bg.connectFour.popup.MyTextInputDialog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import shared.RoomComm;
import shared.LocalRoomInfo;
import shared.RoomMsg;
import shared.RoomPosition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class RoomApp extends VBox {

    private RoomClient roomClient;
    private PlayersGP playersGP;

    public int id;
    private String host_ip, name;
    private boolean youAreHost, isPublic;
    private volatile boolean returned = false;

    private ChatApp chatApp;
    private GameApp gameApp;
    private JFXButton privacyButton, start_game, kick, copy, return_home, open_chat, change_name;
    private final Map<RoomPosition, JFXButton> empty_places_buttons;
    private JoinApp joinApp;
    private JFXCheckBox notifications;
    private VBox hostVBox;
    private final MainApp mainApp;
    private final Semaphore events_mutex = new Semaphore(1, true);
    private volatile boolean room_shortcuts_activated = false;

    // online
    public RoomApp(MainApp mainApp, JoinApp joinApp, Socket roomSocket, String room_id, String name) {
        super(20);
        this.mainApp = mainApp;
        this.name = name;
        this.joinApp = joinApp;
        empty_places_buttons = new HashMap<>();
        roomClient = new RoomClient(roomSocket);
        createGUI(room_id);
        roomClient.handShakeRun(joinApp != null);
    }

    // local
    public RoomApp(MainApp mainApp, JoinApp joinApp, String host_ip, Socket roomSocket, String name) {
        super(20);
        this.host_ip = host_ip;
        this.mainApp = mainApp;
        this.name = name;
        this.joinApp = joinApp;
        empty_places_buttons = new HashMap<>();
        roomClient = new RoomClient(roomSocket);
        createGUI(null);
        roomClient.handShakeRun(joinApp != null);
    }

    private void createGUI(String room_id) {
        setAlignment(Pos.CENTER);
        HBox topHBox = createTopHBox();
        playersGP = new PlayersGP();
        HBox middleHBox = createMiddleHBox(room_id);
        hostVBox = createHostVBox();
        getChildren().addAll(topHBox, playersGP, middleHBox, hostVBox);
    }

    private HBox createTopHBox() {
        HBox topHBox = new HBox();
        topHBox.spacingProperty().bind(MainApp.spacingProperty);
        topHBox.styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));
        topHBox.setAlignment(Pos.CENTER);
        change_name = new JFXButton();
        change_name.textProperty().bind(Language.CH_NAME);
        change_name.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        change_name.setOnAction(e -> {
            TextInputDialog ch_name_dialog = new MyTextInputDialog(Language.ROOM, Language.ENTER_UN, name);
            Optional<String> result = ch_name_dialog.showAndWait();
            if (result.isPresent()) {
                if (name.equals(result.get())) {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.NAME_H, Language.NAME_C);
                    alert.show();
                } else if (MainApp.valid_username(result.get())) {
                    roomClient.request_change_name(result.get());
                }
            }
        });
        topHBox.getChildren().add(change_name);

        open_chat = new JFXButton();
        open_chat.textProperty().bind(Language.OPEN_CH);
        open_chat.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        open_chat.setOnAction(e -> chatApp.showChat());
        topHBox.getChildren().add(open_chat);
        return topHBox;
    }

    private HBox createMiddleHBox(String room_id_str) {
        HBox middleHBox = new HBox();
        middleHBox.spacingProperty().bind(MainApp.spacingProperty);
        middleHBox.setAlignment(Pos.CENTER);

        return_home = new JFXButton();
        return_home.textProperty().bind(Language.RET_HOME);
        return_home.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        return_home.getStyleClass().add("kick");
        return_home.setOnAction(e -> returnHome(false));

        notifications = new JFXCheckBox();
        notifications.textProperty().bind(Language.NOTIFICATIONS);
        notifications.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
        notifications.setSelected(true);
        if (MainApp.online.isSelected()) {
            HBox room_hb = create_room_hb(room_id_str);
            middleHBox.getChildren().addAll(return_home, room_hb, notifications);
        } else
            middleHBox.getChildren().addAll(return_home, notifications);
        return middleHBox;
    }

    private HBox create_room_hb(String room_id_str) {
        HBox room_hb = new HBox();
        room_hb.spacingProperty().bind(MainApp.spacingProperty);
        room_hb.setAlignment(Pos.CENTER);

        JFXTextField room_id = new JFXTextField(room_id_str);
        room_id.setMinSize(JFXTextField.USE_PREF_SIZE, JFXTextField.USE_PREF_SIZE);
        room_id.setEditable(false);
        room_id.setPrefColumnCount(4);
        copy = new JFXButton();
        copy.textProperty().bind(Language.COPY);
        copy.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        copy.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(room_id.getText());
            clipboard.setContent(content);
        });
        room_hb.getChildren().addAll(room_id, copy);
        room_hb.visibleProperty().bind(MainApp.online.selectedProperty());
        return room_hb;
    }

    private VBox createHostVBox() {
        VBox hostVBox = new VBox();
        hostVBox.setAlignment(Pos.CENTER);
        hostVBox.styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));
        hostVBox.setDisable(true);
        Label host_privileges = new Label();
        host_privileges.textProperty().bind(Language.HOST_PRIV);
        host_privileges.translateXProperty().bind(MainApp.stage.widthProperty().divide(6).negate());
        HBox hostHBox = new HBox();
        hostHBox.spacingProperty().bind(MainApp.spacingProperty);
        hostHBox.styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));
        hostHBox.spacingProperty().bind(MainApp.spacingProperty);
        hostHBox.setAlignment(Pos.CENTER);
        HBox privacy_hb = null;
        if (MainApp.online.isSelected()) privacy_hb = create_privacy_hb();
        kick = new JFXButton();
        kick.textProperty().bind(Language.KICK_PLAYER);
        kick.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        kick.getStyleClass().add("kick");
        kick.setOnAction(e -> {
            new ArrayList<>();
            HashMap<String, Integer> map = playersGP.getOtherPlayers();
            if (map.isEmpty()) {
                Alert alert = new MyAlert(AlertType.ERROR, Language.KICK_H1, Language.KICK_C1);
                alert.show();
                return;
            }
            Map.Entry<String, Integer> entry = map.entrySet().iterator().next();
            ChoiceDialog<String> dialog = new ChoiceDialog<>(entry.getKey(), map.keySet());
            MyChoiceDialog.setupChoiceDialog(dialog, Language.KICK_PLAYER, Language.KICK_H2, Language.KICK_C2);
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(res -> roomClient.request_kick(map.get(res)));
        });

        start_game = new JFXButton();
        start_game.textProperty().bind(Language.START_GAME);
        start_game.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        start_game.setOnAction(e -> {
            if (playersGP.canStartGame())
                roomClient.startGame();
        });
        if (MainApp.online.isSelected()) hostHBox.getChildren().addAll(privacy_hb, kick, start_game);
        else hostHBox.getChildren().addAll(kick, start_game);

        hostVBox.getChildren().addAll(host_privileges, hostHBox);
        return hostVBox;
    }

    private HBox create_privacy_hb() {
        HBox privacy_hb = new HBox();
        privacy_hb.spacingProperty().bind(MainApp.spacingProperty);
        privacy_hb.setAlignment(Pos.CENTER);
        Label privacy_label = new Label();
        privacy_label.textProperty().bind(Language.ROOM_PR);
        privacy_label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        privacyButton = new JFXButton();
        privacyButton.textProperty().bind(Language.PRIVATE);
        privacyButton.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        privacyButton.setOnAction(e -> roomClient.change_privacy());
        privacy_hb.getChildren().addAll(privacy_label, privacyButton);
        privacy_hb.visibleProperty().bind(MainApp.online.selectedProperty());
        return privacy_hb;
    }

    private void addPlayer(boolean from_handshake, int id, String player_name, RoomPosition position) {
        boolean isYou = id == this.id;
        try {
            events_mutex.acquire();
            playersGP.addNewPlayerToPosition(id, player_name, position, isYou);
            events_mutex.release();
        } catch (InterruptedException e) {
            returnHome(true);
            return;
        }
        if (!from_handshake) {
            playersGP.resetReadyStatus();
            showNotification(MainApp.stage, player_name + Language.JOINED_NOTIF.getValue(), "info");
        }
    }

    public void showNotification(Stage stage, String text, String notif_type) {
        Platform.runLater(() -> {
            if (notifications.isSelected()) {
                Notifications notif = Notifications.create()
                        .title(MainApp.GAME_NAME.getValue()).text(text)
                        .owner(MainApp.stage).onAction(e -> {
                            if (stage.isIconified()) stage.setIconified(false);
                            else if (!stage.isShowing()) stage.show();
                            else stage.requestFocus();
                        });
                switch (notif_type) {
                    case "info": {
                        notif.showInformation();
                        break;
                    }
                    case "warning": {
                        notif.showWarning();
                        break;
                    }
                    case "error": {
                        notif.showError();
                        break;
                    }
                    case "confirm": {
                        notif.showConfirm();
                        break;
                    }
                }
            }
        });
    }

    public void updateChatTheme() {
        chatApp.updateTheme();
    }

    public void viewScore() {
        if (gameApp == null) return;
        gameApp.showResults(true);
    }

    class RoomClient extends Thread {

        private Socket socket;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;

        public RoomClient(Socket roomSocket) {
            try {
                socket = roomSocket;
                socket.setSoTimeout(0);
                objOut = new ObjectOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());

            } catch (IOException ex) {
                returnHome(true);
            }
        }

        public void startChat(String ip, int chatPort) {
            Thread local_chat = new Thread(() -> {
                try {
                    Socket chatSocket = new Socket();
                    chatSocket.connect(new InetSocketAddress(ip, chatPort), MainApp.LOCAL_TIMEOUT);
                    chatApp = new ChatApp(RoomApp.this, chatSocket);
                } catch (IOException e) {
                    returnHome(true);
                }
            });
            local_chat.start();
        }

        public void startChat(int chatPort) {
            Thread online_chat = new Thread(() -> {
                try {
                    Socket chatSocket = new Socket();
                    chatSocket.connect(new InetSocketAddress(MainApp.ONLINE_IP, chatPort), MainApp.ONLINE_TIMEOUT);
                    chatApp = new ChatApp(RoomApp.this, chatSocket);
                } catch (IOException e) {
                    returnHome(true);
                }
            });
            online_chat.start();
        }

        public void handShakeRun(boolean featuring_join) {
            if (MainApp.online.isSelected()) {
                Thread online_thread = new Thread(() -> {
                    try {
                        objOut.writeBoolean(featuring_join);
                        objOut.flush();
                        if (!featuring_join) {
                            objOut.writeInt(MainApp.BG_GAME.ordinal());
                            objOut.flush();
                        }
                        boolean denied_access = objIn.readBoolean();
                        if (denied_access) {
                            if (featuring_join)
                                returnJoin();
                            else {
                                returnHome(false);
                                Platform.runLater(() -> {
                                    Alert alert = new MyAlert(AlertType.ERROR, Language.CNT_ACCESS_H, Language.CNT_ACCESS_C);
                                    alert.show();
                                });
                            }
                            return;
                        } else if (featuring_join) {
                            discardJoin();
                        }
                        objOut.writeUTF(name);
                        objOut.flush();
                        id = objIn.readInt();
                        int chatPort = objIn.readInt();
                        int playersBeforeYou = objIn.readInt();
                        setRoomPrivacy(objIn.readBoolean());
                        RoomPosition position = RoomPosition.values()[objIn.readInt()];
                        name = objIn.readUTF();
                        if (playersBeforeYou == 0) {
                            promote();
                        }
                        startChat(chatPort);
                        Platform.runLater(() -> addPlayer(true, id, name, position));
                        for (int i = 0; i < playersBeforeYou; i++) {
                            RoomMsg msg = (RoomMsg) objIn.readObject();
                            Platform.runLater(() -> addPlayer(true, msg.from, (String) msg.adt_data[0], RoomPosition.values()[(int) msg.adt_data[1]]));
                        }
                        roomClient.start();
                    } catch (IOException | ClassNotFoundException e) {
                        returnHome(true);
                    }
                });
                online_thread.start();
            } else {
                Thread local_thread = new Thread(() -> {
                    try {
                        objOut.writeUTF(name);
                        objOut.flush();
                        id = objIn.readInt();
                        int chatPort = objIn.readInt();
                        int playersBeforeYou = objIn.readInt();
                        RoomPosition position = RoomPosition.values()[objIn.readInt()];
                        name = objIn.readUTF();
                        if (playersBeforeYou == 0) {
                            promote();
                        }
                        startChat(host_ip, chatPort);
                        Platform.runLater(() -> addPlayer(true, id, name, position));
                        for (int i = 0; i < playersBeforeYou; i++) {
                            RoomMsg msg = (RoomMsg) objIn.readObject();
                            Platform.runLater(() -> addPlayer(true, msg.from, (String) msg.adt_data[0], RoomPosition.values()[(int) msg.adt_data[1]]));
                        }
                        roomClient.start();
                    } catch (IOException | ClassNotFoundException e) {
                        returnHome(true);
                    }
                });
                local_thread.start();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    RoomMsg msg = (RoomMsg) objIn.readObject();
                    switch (RoomComm.values()[msg.comm]) {
                        case GAME_STARTING: {
                            MyAlert.show_alert("HOLD");
                            break;
                        }
                        case GAME_STARTED: {
                            Socket gameSocket = new Socket();
                            if (MainApp.online.isSelected())
                                gameSocket.connect(new InetSocketAddress(MainApp.ONLINE_IP, (int) msg.adt_data[0]), MainApp.ONLINE_TIMEOUT);
                            else
                                gameSocket.connect(new InetSocketAddress(host_ip, (int) msg.adt_data[0]), MainApp.LOCAL_TIMEOUT);
                            gameApp = new GameApp(gameSocket, name, playersGP.getOpponentName());
                            Platform.runLater(() -> mainApp.setGameApp(gameApp));
                            break;
                        }
                        case GAME_ENDED: {
                            if (gameApp != null) {
                                gameApp.showResults(false);
                                gameApp.closeGameApp();
                                gameApp = null;
                                Platform.runLater(() -> {
                                    mainApp.disable_game_menu();
                                    mainApp.setRoomApp(RoomApp.this);
                                    Alert alert = new MyAlert(AlertType.INFORMATION, Language.GE_H, Language.GE_C);
                                    alert.show();
                                    playersGP.resetReadyStatus();
                                });
                            }
                            break;
                        }

                        case TOOK_EMPTY_PLACE: {
                            Platform.runLater(() -> took_empty_place(msg.from, RoomPosition.values()[(int) msg.adt_data[0]]));
                            break;
                        }
                        case JOINED: {
                            Platform.runLater(() -> addPlayer(false, msg.from, (String) msg.adt_data[0], RoomPosition.values()[(int) msg.adt_data[1]]));
                            break;
                        }
                        case LEFT: {
                            Platform.runLater(() -> removePlayer(msg.from, false));
                            break;
                        }
                        case KICKED: {
                            Platform.runLater(() -> removePlayer(msg.from, true));
                            break;
                        }
                        case NOT_READY: {
                            Platform.runLater(() -> playersGP.setReady(msg.from, false));
                            break;
                        }
                        case READY: {
                            Platform.runLater(() -> playersGP.setReady(msg.from, true));
                            break;
                        }
                        case CHANGED_NAME: {
                            Platform.runLater(() -> playersGP.changeName(msg.from, (String) msg.adt_data[0]));
                            break;
                        }
                        case GONE_PUBLIC: {
                            Platform.runLater(() -> setRoomPrivacy(true));
                            break;
                        }
                        case GONE_PRIVATE: {
                            Platform.runLater(() -> setRoomPrivacy(false));
                            break;
                        }
                        case MIGRATION: {
                            mainApp.migrate_new_host((LocalRoomInfo) msg.adt_data[0]);
                            return;
                        }
                        default: {
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                returnHome(true);
            }
        }

        private void closeConn() {
            try {
                objIn.close();
                objOut.close();
                socket.close();
            } catch (IOException ignore) {
            }
        }

        public void change_privacy() {
            try {
                RoomMsg msg = new RoomMsg(isPublic ? RoomComm.GO_PRIVATE : RoomComm.GO_PUBLIC);
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void sendReady(boolean ready) {
            try {
                RoomMsg msg = new RoomMsg(id, ready ? RoomComm.READY : RoomComm.NOT_READY);
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void sendNewRoomServer(LocalRoomInfo localRoomInfo) {
            try {
                RoomMsg msg = new RoomMsg(id, RoomComm.MIGRATION, new Object[]{localRoomInfo});
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void startGame() {
            try {
                RoomMsg msg = new RoomMsg(RoomComm.START_GAME);
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void endGame() {
            try {
                RoomMsg msg = new RoomMsg(RoomComm.END_GAME);
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void request_change_name(String new_name) {
            RoomMsg msg = new RoomMsg(id, RoomComm.REQUEST_CHANGE_NAME, new Object[]{new_name});
            try {
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void request_kick(int id) {
            RoomMsg msg = new RoomMsg(RoomComm.REQUEST_KICK, new Object[]{id});
            try {
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }

        public void take_empty_place(RoomPosition position) {
            RoomMsg msg = new RoomMsg(id, RoomComm.TAKE_EMPTY_PLACE, new Object[]{position.ordinal()});
            try {
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                returnHome(true);
            }
        }
    }

    public void send_new_room_server(LocalRoomInfo room_info) {
        roomClient.sendNewRoomServer(room_info);
    }

    private void promote() {
        youAreHost = true;
        hostVBox.setDisable(false);
        if (MainApp.roomServer == null && MainApp.local.isSelected()) {
            mainApp.start_migration();
        }
    }

    public void endGame() {
        roomClient.endGame();
    }

    public void setup_shortcuts() {
        if (room_shortcuts_activated) return;
        room_shortcuts_activated = true;
        for (Map.Entry<RoomPosition, JFXButton> k : empty_places_buttons.entrySet()) {
            switch (k.getKey()) {
                case BOTTOM:
                    MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN), k.getValue()::fire);
                    break;
                case RIGHT:
                    MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN), k.getValue()::fire);
                    break;
                case TOP:
                    MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN), k.getValue()::fire);
                    break;
                case LEFT:
                    MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN), k.getValue()::fire);
                    break;
            }
        }
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), change_name::fire);
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), open_chat::fire);
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), return_home::fire);
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN), () -> notifications.setSelected(!notifications.isSelected()));
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN), kick::fire);
        MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), start_game::fire);
        if (MainApp.online.isSelected()) {
            MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), copy::fire);
            MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), privacyButton::fire);
        }
    }

    public void partial_shortcuts_remove() {
        if (!room_shortcuts_activated) return;
        room_shortcuts_activated = false;
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN));
    }

    public void remove_shortcuts() {
        if (!room_shortcuts_activated) return;
        room_shortcuts_activated = false;
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN));
        MainApp.stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
    }

    public void closeRoomApp() {
        remove_shortcuts();
        if (roomClient != null) {
            roomClient.closeConn();
            roomClient = null;
        }
        if (gameApp != null) {
            gameApp.closeGameApp();
            gameApp = null;
        }
        if (chatApp != null) {
            chatApp.closeChatApp();
            chatApp = null;
        }
        Platform.runLater(() -> getChildren().clear());
    }

    public void discardOldRoom() {
        returned = true;
        closeRoomApp();
    }

    public synchronized void returnHome(boolean exception) {
        if (returned) return;
        returned = true;
        MyAlert.show_alert("HOLD");
        closeRoomApp();
        if (MainApp.roomServer != null && !MainApp.roomServer.all_clients_left())
            MainApp.roomServer.at_migration_finish(mainApp, exception);
        else Platform.runLater(() -> mainApp.returnHomeApp(exception));
    }

    public void discardJoin() {
        joinApp.closeJoinApp();
        joinApp = null;
        setup_shortcuts();
    }

    public void returnJoin() {
        roomClient.closeConn();
        remove_shortcuts();
        Platform.runLater(() -> {
            getChildren().clear();
            mainApp.setJoinApp(joinApp);
            Alert alert = new MyAlert(AlertType.WARNING, Language.ROOM_H, Language.GONE_PRIVATE);
            alert.show();
        });
    }

    private void took_empty_place(int id, RoomPosition taken_position) {
        try {
            events_mutex.acquire();
            PlayerGUI player = playersGP.get(id);
            playersGP.putPlayerInPosition(taken_position, player);
            events_mutex.release();
        } catch (InterruptedException e) {
            returnHome(true);
        }
    }

    public void setRoomPrivacy(boolean isPublic) {
        this.isPublic = isPublic;
        if (isPublic) {
            privacyButton.textProperty().bind(Language.PUBLIC);
            privacyButton.getStyleClass().remove("private");
            privacyButton.getStyleClass().add("public");
        } else {
            privacyButton.textProperty().bind(Language.PRIVATE);
            privacyButton.getStyleClass().remove("public");
            privacyButton.getStyleClass().add("private");
        }
    }

    public void removePlayer(int id, boolean isKicked) {
        String username;
        try {
            events_mutex.acquire();
            username = playersGP.remove(id);
            events_mutex.release();
        } catch (InterruptedException e) {
            returnHome(true);
            return;
        }
        if (!isKicked) {
            checkHost();
        }
        playersGP.resetReadyStatus();
        showNotification(MainApp.stage, isKicked ? Language.kicked_notif(username, id == this.id) : username + Language.LEFT_NOTIF.getValue(), "error");
    }

    private void checkHost() {
        if (!youAreHost && playersGP.isFirst()) {
            hostVBox.setDisable(false);
            promote();
        }
    }

    public String getName(int from) {
        return playersGP.getName(from);
    }

    private class PlayersGP extends GridPane {
        private final Map<Integer, PlayerGUI> players = new ConcurrentHashMap<>();
        private final PlayerGUI right, left, top, bottom;

        private PlayersGP() {
            setAlignment(Pos.CENTER);
            hgapProperty().bind(MainApp.spacingProperty);
            vgapProperty().bind(MainApp.spacingProperty);

            right = new PlayerGUI(RoomPosition.RIGHT);
            left = new PlayerGUI(RoomPosition.LEFT);
            top = new PlayerGUI(RoomPosition.TOP);
            bottom = new PlayerGUI(RoomPosition.BOTTOM);

            GridPane.setHalignment(right, HPos.CENTER);
            GridPane.setHalignment(left, HPos.CENTER);
            GridPane.setHalignment(top, HPos.CENTER);
            GridPane.setHalignment(bottom, HPos.CENTER);

            add(top, 1, 0);
            add(left, 0, 1);
            add(right, 2, 1);
            add(bottom, 1, 2);
        }

        private void changeName(int from, String new_name) {
            PlayerGUI set = players.get(from);
            if (set != null) {
                if (from == id)
                    name = new_name;
                set.setName(new_name);
            }
        }

        private boolean canStartGame() {
            int enough_players = MainApp.BG_GAME.players;
            if (players.size() >= enough_players && allReady()) {
                return true;
            } else {
                Alert alert = new MyAlert(AlertType.ERROR, Language.CNT_SG_H, players.size() == enough_players ? Language.CNT_SG_C1 : Language.CNT_SG_C2);
                alert.show();
                return false;
            }
        }

        private boolean allReady() {
            for (Map.Entry<Integer, PlayerGUI> IdClientEntry : players.entrySet()) {
                if (!IdClientEntry.getValue().ready.isSelected())
                    return false;
            }
            return true;
        }

        private void addNewPlayerToPosition(int id, String player_name, RoomPosition position, boolean isYou) {
            PlayerGUI player = null;
            switch (position) {
                case BOTTOM: {
                    player = bottom;
                    break;
                }
                case LEFT: {
                    player = left;
                    break;
                }
                case TOP: {
                    player = top;
                    break;
                }
                case RIGHT: {
                    player = right;
                    break;
                }
            }
            player.addPlayer(id, player_name, isYou);
            players.put(player.id, player);
        }

        private void putPlayerInPosition(RoomPosition taken_position, PlayerGUI player) {
            if (player != null) {
                Object[] player_info = emptyPosition(player.position);
                if (player_info != null)
                    addExistingPlayerToPosition(taken_position, (int) player_info[0], (String) player_info[1], (boolean) player_info[2]);
            }
        }

        private void addExistingPlayerToPosition(RoomPosition position, int id, String player_name, boolean isReady) {
            PlayerGUI empty_position = null;
            switch (position) {
                case BOTTOM: {
                    empty_position = bottom;
                    break;
                }
                case LEFT: {
                    empty_position = left;
                    break;
                }
                case TOP: {
                    empty_position = top;
                    break;
                }
                case RIGHT: {
                    empty_position = right;
                    break;
                }
            }
            empty_position.addExistingPlayer(id, player_name, isReady);
            players.put(empty_position.id, empty_position);
        }

        private Object[] emptyPosition(RoomPosition position) {
            switch (position) {
                case BOTTOM: {
                    return bottom.removePlayer();
                }
                case LEFT: {
                    return left.removePlayer();
                }
                case TOP: {
                    return top.removePlayer();
                }
                case RIGHT: {
                    return right.removePlayer();
                }
                default: {
                    return null;
                }
            }
        }

        private boolean isFirst() {
            int lowest_id = Integer.MAX_VALUE;
            for (Map.Entry<Integer, PlayerGUI> IdClientEntry : players.entrySet()) {
                PlayerGUI player = IdClientEntry.getValue();
                if (player.id < lowest_id)
                    lowest_id = player.id;
            }
            return lowest_id == id;
        }

        private void setReady(int id, boolean isReady) {
            PlayerGUI set = players.get(id);

            if (set != null) {
                set.setReady(isReady);
            }
        }

        private PlayerGUI get(int id) {
            return players.get(id);
        }

        private String remove(int id) {
            PlayerGUI player = players.remove(id);
            if (player != null) {
                Object[] info = emptyPosition(player.position);
                return (String) (info != null ? info[1] : null);
            }
            return null;
        }

        private String getName(int from) {
            PlayerGUI player = players.get(from);
            if (player != null)
                return player.name.getText();

            return null;
        }

        public HashMap<String, Integer> getOtherPlayers() {
            HashMap<String, Integer> map = new HashMap<>();
            for (Map.Entry<Integer, PlayerGUI> IdClientEntry : players.entrySet()) {
                PlayerGUI player = IdClientEntry.getValue();
                if (player.id != id)
                    map.put(player.name.getText(), player.id);
            }
            return map;
        }

        public void resetReadyStatus() {
            for (Map.Entry<Integer, PlayerGUI> IdClientEntry : players.entrySet()) {
                PlayerGUI player = IdClientEntry.getValue();
                player.setReady(false);
            }
        }

        public String getOpponentName() {
            for (Map.Entry<Integer, PlayerGUI> IdClientEntry : players.entrySet()) {
                PlayerGUI player = IdClientEntry.getValue();
                if (player.id != id)
                    return player.name.getText();
            }
            return null;
        }
    }

    private class PlayerGUI extends HBox {
        private int id;
        private Label name;
        private JFXCheckBox ready;
        private final RoomPosition position;
        private final JFXButton take_place;

        private PlayerGUI(RoomPosition position) {
            super(20);
            id = 0;
            name = null;
            ready = null;
            setAlignment(Pos.CENTER);
            this.position = position;
            take_place = new JFXButton();
            take_place.textProperty().bind(Language.TAKE_PLACE);
            take_place.getStyleClass().add("take-place");
            empty_places_buttons.put(position, take_place);
            take_place.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
            take_place.setOnAction(e -> roomClient.take_empty_place(position));
            getChildren().add(take_place);
        }

        private Object[] removePlayer() {
            if (name != null) {
                getChildren().clear();
                getChildren().add(take_place);
                Object[] info = new Object[]{
                        id, name.getText(), ready.isSelected()
                };
                id = 0;
                name = null;
                ready = null;
                return info;
            } else {
                return null;
            }
        }

        private void addPlayer(int id, String player_name, boolean isYou) {
            this.id = id;
            this.name = new Label(player_name);
            ready = new JFXCheckBox();
            ready.textProperty().bind(Language.READY);
            ready.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
            if (isYou) {
                MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), () -> ready.setSelected(!ready.isSelected()));
                ready.selectedProperty()
                        .addListener((observable, oldValue, newValue) -> roomClient.sendReady(newValue));
            } else {
                ready.setDisable(true);
            }
            getChildren().clear();
            getChildren().addAll(name, ready);
        }

        private void addExistingPlayer(int id, String player_name, boolean isReady) {
            this.id = id;
            this.name = new Label(player_name);
            ready = new JFXCheckBox();
            ready.textProperty().bind(Language.READY);
            ready.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
            ready.setSelected(isReady);
            if (id == RoomApp.this.id) {
                MainApp.stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), () -> ready.setSelected(!ready.isSelected()));
                ready.selectedProperty()
                        .addListener((observable, oldValue, newValue) -> roomClient.sendReady(newValue));
            } else {
                ready.setDisable(true);
            }
            getChildren().clear();
            getChildren().addAll(name, ready);
        }

        private void setReady(boolean isReady) {
            ready.setSelected(isReady);
        }

        private void setName(String new_name) {
            name.setText(new_name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (this.getClass() != obj.getClass())
                return false;
            PlayerGUI player = (PlayerGUI) obj;
            return player.id == this.id;
        }
    }

}
