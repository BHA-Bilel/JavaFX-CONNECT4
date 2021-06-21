package bg.connectFour.chat;

import bg.connectFour.MainApp;
import bg.connectFour.gfx.Assets;
import bg.connectFour.lang.Language;
import bg.connectFour.popup.MyAlert;
import bg.connectFour.room.RoomApp;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.ChatMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatApp {

    private Stage stage;
    private final RoomApp room;
    private final ScrollPane sp;
    private final VBox content;
    private final JFXTextField textField;
    private int unread_messages, latest_from = -1;
    private final ChatClient chatClient;

    public ChatApp(RoomApp room, Socket chatSocket) {
        this.room = room;
        chatClient = new ChatClient(chatSocket);
        chatClient.startChat();

        VBox root = new VBox(5);
        root.styleProperty().bind(Bindings.concat("-fx-font-size: ", MainApp.fontProperty.asString()));
        Scene scene = new Scene(root, Assets.width * .3, Assets.height * .7);
        scene.getStylesheets().add(ChatApp.class.getResource(MainApp.CURRENT_THEME.replace("/main_", "/chat_")).toExternalForm());
        scene.addEventFilter(KeyEvent.KEY_PRESSED,
                e -> {
                    if (e.getCode() != KeyCode.ESCAPE) return;
                    stage.close();
                });

        sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content = new VBox();
        sp.setContent(content);
        sp.vvalueProperty().bind(content.heightProperty());
        sp.prefHeightProperty().bind(root.heightProperty());
        textField = new JFXTextField();
        textField.setOnKeyPressed((ke) -> {
            if (!ke.getCode().equals(KeyCode.ENTER)) return;
            if (!valid_msg(textField.getText())) return;
            ChatMsg msg = new ChatMsg(room.id, textField.getText());
            addMessage(msg);
            chatClient.send(msg);
            textField.setText("");
        });
        root.getChildren().addAll(sp, textField);

        Platform.runLater(() -> {
            stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initOwner(MainApp.stage);
        });
    }

    private boolean valid_msg(String msg) {
        if (msg.isEmpty()) {
            Alert alert = new MyAlert(Alert.AlertType.ERROR, stage, Language.CHAT_T, Language.CHAT_H1, Language.CHAT_C1);
            alert.show();
            return false;
        } else if (msg.length() > 50) {
            Alert alert = new MyAlert(Alert.AlertType.ERROR, stage, Language.CHAT_T, Language.CHAT_H2, Language.CHAT_C2);
            alert.show();
            return false;
        }
        return true;
    }

    public void showChat() {
        Platform.runLater(() -> {
            if (!stage.isShowing()) {
                stage.show();
                unread_messages = 0;
            } else {
                stage.requestFocus();
            }
        });
    }

    public void addMessage(ChatMsg message) {
        Platform.runLater(() -> {
            ChatMsgGUI msg = new ChatMsgGUI(message);
            content.getChildren().add(msg);
            if (stage.isShowing()) return;
            unread_messages++;
            if (unread_messages % 5 == 0)
                room.showNotification(stage, Language.unread_msg(unread_messages), "info");
        });
    }

    public void updateTheme() {
        stage.getScene().getStylesheets().clear();
        stage.getScene().getStylesheets().add(MainApp.class.getResource(MainApp.CURRENT_THEME.replace("/main_", "/chat_")).toExternalForm());
    }

    class ChatMsgGUI extends HBox {

        int from;
        Label nameLabel;
        Label content;

        public ChatMsgGUI(ChatMsg msg) {
            this.from = msg.from;
            String name = room.getName(msg.from);
            nameLabel = new Label(name + " : ");
            nameLabel.getStyleClass().add("name");
            nameLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            if (latest_from == msg.from) {
                nameLabel.setVisible(false);
            } else {
                latest_from = msg.from;
            }
            content = new Label(msg.content);
            content.setWrapText(true);
            content.getStyleClass().add("content");
            setAlignment(Pos.TOP_LEFT);
            maxWidthProperty().bind(sp.widthProperty());
            getChildren().addAll(nameLabel, content);
        }
    }

    class ChatClient {

        private final Socket chatSocket;
        private ObjectOutputStream objOut;
        private ObjectInputStream objIn;
        private volatile boolean closing_chat = false;

        public ChatClient(Socket chatSocket) {
            this.chatSocket = chatSocket;
            try {
                this.chatSocket.setSoTimeout(0);
                objOut = new ObjectOutputStream(this.chatSocket.getOutputStream());
                objIn = new ObjectInputStream(this.chatSocket.getInputStream());
            } catch (IOException e) {
                if (closing_chat) return;
                room.returnHome(true);
            }
        }

        public void startChat() {
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        ChatMsg msg = (ChatMsg) objIn.readObject();
                        addMessage(msg);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    if (closing_chat) return;
                    room.returnHome(true);
                }
            });
            receiveThread.start();
        }

        public void closeConn() {
            closing_chat = true;
            try {
                objIn.close();
                objOut.close();
                chatSocket.close();
            } catch (IOException ignore) {
            }
        }

        public void send(ChatMsg msg) {
            try {
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                if (closing_chat) return;
                room.returnHome(true);
            }
        }
    }

    public void closeChatApp() {
        chatClient.closeConn();
        Platform.runLater(stage::close);
    }

}
