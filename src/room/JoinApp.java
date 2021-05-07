package room;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gfx.Assets;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.MainApp;
import shared.MainRequest;
import shared.RoomInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JoinApp extends VBox {

    private final JoinClient joinClient;
    private FeaturingGP featuringGP;
    private JFXTextField username;
    private final MainApp mainApp;

    public JoinApp(MainApp mainApp, String username, Socket joinSocket) {
        super(20);
        this.mainApp = mainApp;
        createGUI(username);
        joinClient = new JoinClient(joinSocket);
    }

    private void createGUI(String username) {
        setAlignment(Pos.CENTER);
        this.username = new JFXTextField(username);
        this.username.setPromptText("username");
        this.username.setPrefWidth(Assets.scale_width(500));
        this.username.setMaxWidth(Assets.scale_width(500));

        featuringGP = new FeaturingGP();

        HBox bottom_hb = new HBox(100);
        HBox.setMargin(bottom_hb, new Insets(50, 50, 0, 50));
        JFXButton return_home = new JFXButton("Return Home");
        return_home.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        return_home.getStyleClass().add("kick");
        return_home.setOnAction(e -> returnHome(false));
        JFXButton refreshButton = new JFXButton("Refresh");
        refreshButton.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        refreshButton.setOnAction(e -> joinClient.askForMore());
        bottom_hb.getChildren().addAll(return_home, refreshButton);
        getChildren().addAll(this.username, featuringGP, bottom_hb);
    }

    public volatile boolean returned = false;

    public void closeJoinApp() {
        joinClient.closeConn();
        Platform.runLater(() -> getChildren().clear());
    }

    public synchronized void returnHome(boolean exception) {
        if (!returned) {
            MainApp.prevent_user_interactions();
            closeJoinApp();
            Platform.runLater(() -> mainApp.returnHomeApp(exception));
            returned = true;
        }
    }

    // networking
    class JoinClient {
        private Socket socket;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;

        public JoinClient(Socket joinSocket) {
            try {
                socket = joinSocket;
                socket.setSoTimeout(0);
                objOut = new ObjectOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());
                objOut.writeInt(MainRequest.JOIN.ordinal());
                objOut.flush();
                int list_size = objIn.readInt();
                List<RoomInfo> list = new ArrayList<>();
                for (int i = 0; i < list_size; i++) {
                    list.add((RoomInfo) objIn.readObject());
                }
                Platform.runLater(() -> featuringGP.refresh(list, true));
            } catch (IOException | ClassNotFoundException ex) {
                returnHome(true);
            }
        }

        private void askForMore() {
            MainApp.prevent_user_interactions();
            featuringGP.getChildren().clear();
            Thread t = new Thread(() -> {
                try {
                    objOut.writeBoolean(true);
                    objOut.flush();
                    int list_size = objIn.readInt();
                    List<RoomInfo> list = new ArrayList<>();
                    for (int i = 0; i < list_size; i++) {
                        list.add((RoomInfo) objIn.readObject());
                    }
                    Platform.runLater(() -> {
                        featuringGP.refresh(list, false);
                        MainApp.allow_user_interactions();
                    });
                } catch (IOException | ClassNotFoundException e) {
                    returnHome(true);
                }
            });
            t.start();
        }

        public void attemptJoin(int roomID, String username) {
            MainApp.prevent_user_interactions();
            Socket roomSocket = new Socket();
            Thread t = new Thread(() -> {
                try {
                    roomSocket.connect(new InetSocketAddress(MainApp.SERVER_IP, roomID), 5000);
                    RoomApp roomApp = new RoomApp(mainApp, JoinApp.this, roomSocket, roomID, username);
                    Platform.runLater(() -> mainApp.setRoomApp(roomApp));
                } catch (IOException e) {
                    try {
                        roomSocket.close();
                    } catch (IOException ignore) {
                    }
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle(MainApp.THIS_GAME.toString());
                        alert.setHeaderText("couldn't join the room !");
                        alert.setContentText("An error has occurred when joining the room");
                        alert.show();
                    });

                } finally {
                    MainApp.allow_user_interactions();
                }
            });
            t.start();
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

    // GUI
    class FeaturingGP extends GridPane {
        private final Label emptyLabel;

        public FeaturingGP() {
            GridPane.setMargin(this, new Insets(20, 0, 0, 0));
            setAlignment(Pos.CENTER);
            setHgap(10);
            setVgap(30);

            emptyLabel = new Label("No rooms are available at the moment,\nPlease come back later.");
            GridPane.setHalignment(emptyLabel, HPos.CENTER);
        }

        public void refresh(List<RoomInfo> infos, boolean first_time) {
            int row = 0, column = 0;
            for (RoomInfo info : infos) {
                Label room_label = new Label(info.host_name);
                Label room_players = new Label(info.room_players + " / " + MainApp.THIS_GAME.players + " players");
                JFXButton join = new JFXButton("Join");
                join.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
                join.setOnAction(e -> {
                    if (!MainApp.validUsername(username.getText()))
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
            if (!first_time) {
                mainApp.update_width_height(Assets.joinApp_width, Assets.joinApp_height);
            }
            if (infos.isEmpty()) {
                featuringGP.add(emptyLabel, 0, 0);
            }
        }
    }
}
