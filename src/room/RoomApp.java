package room;

import chat.ChatApp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import game.GameApp;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.MainApp;
import org.controlsfx.control.Notifications;
import shared.Game;
import shared.RoomComm;
import shared.RoomMsg;
import shared.RoomPosition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class RoomApp extends VBox {

    private final RoomClient roomClient;
    private PlayersGP playersGP;

    public int id;
    private String name;
    private boolean youAreHost, isPublic;
    public volatile boolean returned = false;

    private ChatApp chatApp;
    private GameApp gameApp;
    private JFXButton privacyButton;
    private JoinApp joinApp;
    private JFXCheckBox notifications;
    private VBox hostVBox;
    private final MainApp mainApp;
    private final Semaphore events_mutex = new Semaphore(1, true);

    public RoomApp(MainApp mainApp, JoinApp joinApp, Socket roomSocket, int room_port, String name) {
        super(20);
        this.mainApp = mainApp;
        this.name = name;
        this.joinApp = joinApp;

        roomClient = new RoomClient(roomSocket);
        createGUI(room_port);
        roomClient.handShakeRun(joinApp != null);
    }

    private void createGUI(int port) {
        setAlignment(Pos.CENTER);
        HBox topHBox = createTopHBox();
        playersGP = new PlayersGP();
        HBox middleHBox = createMiddleHBox(port);
        hostVBox = createHostVBox();
        getChildren().addAll(topHBox, playersGP, middleHBox, hostVBox);
    }

    private HBox createTopHBox() {
        HBox topHBox = new HBox(100);
        topHBox.setAlignment(Pos.CENTER);
        HBox.setMargin(topHBox, new Insets(20, 0, 20, 0));

        JFXButton change_name = new JFXButton("Change name");
        change_name.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        change_name.setOnAction(e -> {
            TextInputDialog ip_dialog = new TextInputDialog();
            ip_dialog.setTitle("Room");
            ip_dialog.setHeaderText("Enter a valid username");
            Optional<String> result = ip_dialog.showAndWait();
            if (result.isPresent()) {
                if (MainApp.validUsername(result.get())) {
                    roomClient.request_change_name(result.get());
                }
            }
        });
        topHBox.getChildren().add(change_name);
        JFXButton open_chat = new JFXButton("Open chat");
        open_chat.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        open_chat.setOnAction(e -> chatApp.showChat());
        topHBox.getChildren().add(open_chat);
        return topHBox;
    }

    private HBox createMiddleHBox(int port) {
        HBox middleHBox = new HBox(100);
        middleHBox.setAlignment(Pos.CENTER);

        JFXButton return_home = new JFXButton("Return Home");
        return_home.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        return_home.getStyleClass().add("kick");
        return_home.setOnAction(e -> returnHome(false));

        notifications = new JFXCheckBox("Notifications");
        notifications.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
        notifications.setSelected(true);

        HBox room_hb = new HBox(20);
        room_hb.setAlignment(Pos.CENTER);

        JFXTextField room_id = new JFXTextField(port + "");
        room_id.setMinSize(JFXTextField.USE_PREF_SIZE, JFXTextField.USE_PREF_SIZE);
        room_id.setEditable(false);
        room_id.setPrefColumnCount(4);
        JFXButton copy = new JFXButton("Copy");
        copy.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        copy.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(room_id.getText());
            clipboard.setContent(content);
        });
        room_hb.getChildren().addAll(room_id, copy);
        middleHBox.getChildren().addAll(return_home, room_hb, notifications);
        middleHBox.setMinWidth(middleHBox.getWidth());
        return middleHBox;
    }


    private VBox createHostVBox() {
        VBox hostVBox = new VBox();
        VBox.setMargin(hostVBox, new Insets(20));
        hostVBox.setMaxWidth(750);
        hostVBox.setDisable(true);
        Label host_privileges = new Label("Host Privileges");

        HBox hostHBox = new HBox(100);
        HBox.setMargin(hostHBox, new Insets(20, 0, 20, 0));
        hostHBox.setAlignment(Pos.CENTER);

        HBox privacy_hb = new HBox(20);
        privacy_hb.setAlignment(Pos.CENTER);
        Label privacy_label = new Label("Room Privacy");
        privacy_label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        privacyButton = new JFXButton("Private");
        privacyButton.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        privacyButton.setOnAction(e -> roomClient.change_privacy());
        privacy_hb.getChildren().addAll(privacy_label, privacyButton);

        JFXButton kick = new JFXButton("Kick A Player");
        kick.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        kick.getStyleClass().add("kick");
        kick.setOnAction(e -> {
            new ArrayList<>();
            HashMap<String, Integer> map = playersGP.getOtherPlayers();
            if (map.isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(MainApp.THIS_GAME.toString());
                alert.setHeaderText("You can't kick anyone");
                alert.setContentText("You're the only one in the room !");
                alert.show();
                return;
            }
            Map.Entry<String, Integer> entry = map.entrySet().iterator().next();
            ChoiceDialog<String> dialog = new ChoiceDialog<>(entry.getKey(), map.keySet());
            dialog.setTitle("Kick");
            dialog.setHeaderText("Select the player you want to kick from the room");
            dialog.setContentText("He could still rejoin the room with the Room ID");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(res -> roomClient.request_kick(map.get(res)));
        });

        JFXButton start_game = new JFXButton("Start Game");
        start_game.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        start_game.setOnAction(e -> {
            if (playersGP.canStartGame())
                roomClient.startGame();
        });

        hostHBox.getChildren().addAll(privacy_hb, kick, start_game);
        hostVBox.getChildren().addAll(host_privileges, hostHBox);
        return hostVBox;
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
            showNotification(player_name + " joined the room", "info");
        }
    }

    public void showNotification(String text, String notif_type) {
        Platform.runLater(() -> {
            if (notifications.isSelected()) {
                Notifications notif = Notifications.create().title("Room").text(text)
                        .hideAfter(Duration.seconds(3)).position(Pos.BOTTOM_RIGHT);
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

    // CLIENT CONNECTION
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

        public void startChat(int chatPort) {
            Thread t = new Thread(() -> {
                try {
                    Socket chatSocket = new Socket();
                    chatSocket.connect(new InetSocketAddress(MainApp.SERVER_IP, chatPort), 5000);
                    chatApp = new ChatApp(RoomApp.this, chatSocket);
                } catch (IOException e) {
                    returnHome(true);
                }
            });
            t.start();
        }

        public void handShakeRun(boolean featuring_join) {
            Thread t = new Thread(() -> {
                try {
                    objOut.writeBoolean(featuring_join);
                    objOut.flush();
                    if (!featuring_join) {
                        objOut.writeInt(MainApp.THIS_GAME.ordinal());
                        objOut.flush();
                    }
                    boolean denied_access = objIn.readBoolean();
                    if (denied_access) {
                        if (featuring_join)
                            returnJoin();
                        else {
                            returnHome(false);
                            Platform.runLater(() -> {
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle(MainApp.THIS_GAME.toString());
                                alert.setHeaderText("You can't access this room");
                                alert.setContentText("The room you're trying to access is meant for another game");
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
                    name = objIn.readUTF(); // after checking for dup names
                    if (playersBeforeYou == 0) {// You are the first to enter the room (You created the room)
                        promote();
                    }
                    startChat(chatPort);
                    Platform.runLater(() -> addPlayer(true, id, name, position));
                    int i = 0;
                    List<RoomMsg> queued = new ArrayList<>();
                    while (i < playersBeforeYou) {
                        RoomMsg msg = (RoomMsg) objIn.readObject();
                        if (msg.comm == RoomComm.JOINED.ordinal()) {
                            Platform.runLater(() -> addPlayer(true, msg.from, (String) msg.adt_data[0], RoomPosition.values()[(int) msg.adt_data[1]]));
                            i++;
                        } else {
                            queued.add(msg);
                        }
                    }
                    for (RoomMsg msg : queued) {
                        roomClient.handle_message(msg);
                    }
                    roomClient.start();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    returnHome(true);
                }
            });
            t.start();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    RoomMsg msg = (RoomMsg) objIn.readObject();
                    handle_message(msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                returnHome(true);
            }
        }

        private void handle_message(RoomMsg msg) throws IOException {
            switch (RoomComm.values()[msg.comm]) {
                case GAME_STARTING: {
                    MainApp.prevent_user_interactions();
                    break;
                }
                case GAME_STARTED: {
                    Socket gameSocket = new Socket();
                    gameSocket.connect(new InetSocketAddress(MainApp.SERVER_IP, (int) msg.adt_data[0]), 5000);
                    gameApp = new GameApp(gameSocket, name, playersGP.getOpponentName());
                    Platform.runLater(() -> mainApp.setGameApp(gameApp));
                    break;
                }
                case GAME_ENDED: {
                    if (gameApp != null) {
                        gameApp.closeGameApp();
                        gameApp = null;
                        Platform.runLater(() -> {
                            mainApp.setRoomApp(RoomApp.this);
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle(MainApp.THIS_GAME.toString());
                            alert.setHeaderText("The game has ended !");
                            alert.setContentText("An event has led the game to finish");
                            alert.show();
                            playersGP.resetReadyStatus();
                        });
                    }
                    break;
                }
                // server sent events
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
                default: {
                }
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

        // client sent events: begin

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
        // client sent events: end
    }

    private void promote() {
        youAreHost = true;
        hostVBox.setDisable(false);
    }

    public void endGame() {
        if (gameApp != null) {
            roomClient.endGame();
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(MainApp.THIS_GAME.toString());
            alert.setHeaderText("The game has not yet started");
            alert.setContentText("You are already in the room !");
            alert.show();
        }
    }

    public void closeRoomApp() {
        if (roomClient != null)
            roomClient.closeConn();
        if (gameApp != null)
            gameApp.closeGameApp();
        if (chatApp != null)
            chatApp.closeChatApp();
        Platform.runLater(() -> getChildren().clear());
    }

    public synchronized void returnHome(boolean exception) {
        if (!returned) {
            MainApp.prevent_user_interactions();
            closeRoomApp();
            Platform.runLater(() -> mainApp.returnHomeApp(exception));
            returned = true;
        }
    }

    public void discardJoin() {
        joinApp.closeJoinApp();
        joinApp = null;
    }

    public void returnJoin() {
        roomClient.closeConn();
        Platform.runLater(() -> {
            getChildren().clear();
            mainApp.setJoinApp(joinApp);
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(MainApp.THIS_GAME.toString());
            alert.setHeaderText("Couldn't join the room");
            alert.setContentText("This room has gone private");
            alert.show();
        });
    }

    // server sent events: begin

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
            privacyButton.setText("Public");
            privacyButton.getStyleClass().remove("private");
            privacyButton.getStyleClass().add("public");
        } else {
            privacyButton.setText("Private");
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
        showNotification(username + (isKicked ? " was kicked" : " left the room"), "error");
    }

    // server sent events: end

    private void checkHost() {
        if (!youAreHost && playersGP.isFirst()) {
            hostVBox.setDisable(false);
            promote();
        }
    }

    private class PlayersGP extends GridPane {
        private final Map<Integer, PlayerGUI> players = new ConcurrentHashMap<>();
        private final PlayerGUI right, left, top, bottom;

        private PlayersGP() {
            setAlignment(Pos.CENTER);
            setHgap(20);
            setVgap(150);
            setMaxWidth(750);
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
            int enough_players;
            if (MainApp.THIS_GAME == Game.DOMINOS) {
                enough_players = 2;
            } else {
                enough_players = MainApp.THIS_GAME.players;
            }
            if (players.size() >= enough_players && allReady()) {
                return true;
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(MainApp.THIS_GAME.toString());
                alert.setHeaderText("You can't start the game yet!");
                alert.setContentText(players.size() == enough_players ? "Not all players are ready"
                        : "Please wait for more players to join");
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
            take_place = new JFXButton("Take place");
            take_place.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
            take_place.setOnAction(e -> roomClient.take_empty_place(position));
            getChildren().add(take_place);
        }

        private Object[] removePlayer() {
            if (name != null) { // a player actually exists in this position
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
            ready = new JFXCheckBox("Ready");
            ready.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
            if (isYou) {
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
            ready = new JFXCheckBox("Ready");
            ready.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
            ready.setSelected(isReady);
            if (id == RoomApp.this.id) {
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

    public String getName(int from) {
        return playersGP.getName(from);
    }
}
