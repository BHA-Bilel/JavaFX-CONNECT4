package chat;

import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import room.RoomApp;
import shared.ChatMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatApp {
    private final ScrollPane sp;
    private final VBox content;
    private final JFXTextField textField;
    private final RoomApp room;
    private final ChatClient chatClient;
    private Stage stage;
    private ChatMsgGUI latest;
    private int unread_messages;

    public ChatApp(RoomApp room, Socket chatSocket) {
        this.room = room;
        chatClient = new ChatClient(chatSocket);
        chatClient.startChat();

        VBox root = new VBox(5);
        sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content = new VBox();
        sp.setContent(content);
        sp.vvalueProperty().bind(content.heightProperty());
        sp.prefHeightProperty().bind(root.heightProperty());
        textField = new JFXTextField();

        textField.setOnKeyPressed((ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER) && !textField.getText().isEmpty()) {
                ChatMsg msg = new ChatMsg(room.id, textField.getText());
                addMessage(msg);
                chatClient.send(msg);
                textField.setText("");
            }
        });
        root.getChildren().addAll(sp, textField);
        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());
        Platform.runLater(() -> {
            stage = new Stage();
            stage.setScene(scene);
        });
    }

    public void showChat() {
        Platform.runLater(() -> {
            if (!stage.isShowing()) {
                stage.show();
                unread_messages = 0;
            }
        });
    }

    public void addMessage(ChatMsg message) {
        Platform.runLater(() -> {
            ChatMsgGUI msg = new ChatMsgGUI(message);
            content.getChildren().add(msg);
            if (!stage.isShowing()) {
                unread_messages++;
                if (unread_messages % 5 == 0)
                    room.showNotification("You have " + unread_messages + " unread messages, open chat to read them !", "info");
            }
        });
    }

    class ChatMsgGUI extends HBox {
        int from;
        Label nameLabel;
        Label content;

        public ChatMsgGUI(ChatMsg msg) {
            this.from = msg.from;
            String name = room.getName(msg.from);
            boolean latest_width = false;
            if (latest != null) {
                if (latest.nameLabel.getText().replace(" : ", "").equals(name)) {
                    nameLabel = new Label("");
                    latest_width = true;
                } else {
                    latest = this;
                    nameLabel = new Label(name + " : ");
                }
            } else {
                latest = this;
                nameLabel = new Label(name + " : ");
            }
            nameLabel.getStyleClass().add("name");
            nameLabel.setMinWidth(Region.USE_PREF_SIZE);
            content = new Label(msg.content);
            content.setWrapText(true);
            content.getStyleClass().add("content");
            setAlignment(Pos.TOP_LEFT);
            maxWidthProperty().bind(sp.widthProperty());
            getChildren().addAll(nameLabel, content);
            if (latest_width) {
                nameLabel.setMinWidth(latest.nameLabel.getWidth());
            }
        }
    }

    class ChatClient {

        private ObjectOutputStream objOut;
        private ObjectInputStream objIn;
        private final Socket chatSocket;
        private Thread recieveThread;
        private volatile boolean closing_chat = false;

        public ChatClient(Socket chatSocket) {
            this.chatSocket = chatSocket;
            try {
                this.chatSocket.setSoTimeout(0);
                objOut = new ObjectOutputStream(this.chatSocket.getOutputStream());
                objIn = new ObjectInputStream(this.chatSocket.getInputStream());
            } catch (IOException e) {
                room.returnHome(true);
            }
        }

        public void startChat() {
            recieveThread = new Thread(() -> {
                try {
                    while (true) {
                        addMessage((ChatMsg) objIn.readObject());
                    }
                } catch (ClassNotFoundException | IOException e) {
                    if (!closing_chat)
                        room.returnHome(true);
                }
            });
            recieveThread.start();
        }

        public void closeConn() {
            closing_chat = true;
            recieveThread.interrupt();
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
                room.returnHome(true);
            }
        }
    }

    public void closeChatApp() {
        chatClient.closeConn();
        Platform.runLater(() -> stage.close());
    }

}
