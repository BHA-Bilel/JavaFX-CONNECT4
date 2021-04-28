package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import game.GameApp;
import gfx.Assets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import room.JoinApp;
import room.RoomApp;
import shared.Game;
import shared.MainRequest;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class MainApp extends Application {

    public static String SERVER_IP;
    public static int MAIN_PORT;
    public static int GAME_PORT = -1;

    private boolean valid;
    public static final Game THIS_GAME = Game.CONNECT4;
    public static boolean in_main_menu = true;
    public Stage stage;
    private BorderPane root;
    private VBox center;
    private Runnable connectRunnable;
    private Alert initial_alert;
    public static Alert hold_alert;

    private JoinApp joinApp;
    public RoomApp roomApp;

    private double width_diff, height_diff;

    @Override
    public void start(Stage primaryStage) {
        Assets.init_scale();
        stage = primaryStage;
        hold_alert = new Alert(AlertType.INFORMATION);
        hold_alert.setTitle(THIS_GAME.toString());
        hold_alert.setHeaderText("Please hold for a moment...");
        hold_alert.setContentText("Thank you");
        initial_alert = new Alert(AlertType.INFORMATION);
        initial_alert.setTitle(THIS_GAME.toString());
        initial_alert.setHeaderText("Initializing game");
        initial_alert.setContentText("Connecting to server...");
        ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        initial_alert.getButtonTypes().setAll(cancel);
        initial_alert.show();
        readConfigFile();
        connectRunnable = () -> {
            try {
                Socket listen = new Socket();
                listen.connect(new InetSocketAddress(SERVER_IP, MAIN_PORT), 5000);
                boolean initial_reach = GAME_PORT == -1;
                try (DataOutputStream dataOut = new DataOutputStream(listen.getOutputStream());
                     DataInputStream dataIn = new DataInputStream(listen.getInputStream())) {
                    dataOut.writeInt(THIS_GAME.ordinal());
                    dataOut.flush();
                    try {
                        GAME_PORT = dataIn.readInt();
                    } catch (NullPointerException e) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle(THIS_GAME.toString());
                        alert.setHeaderText(THIS_GAME + " server is not available at the moment");
                        alert.setContentText("Please come back later");
                        alert.setOnHiding(value -> System.exit(0));
                        alert.show();
                    }
                } catch (IOException ignore) {
                }
                try {
                    listen.close();
                } catch (IOException ignore) {
                }
                if (initial_reach) {
                    Platform.runLater(() -> {
                        initial_alert.close();
                        createGUI();
                    });
                } else {
                    allow_user_interactions();
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    initial_alert.close();

                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle(THIS_GAME.toString());
                    alert.setHeaderText("Couldn't reach the server!");
                    alert.setContentText("Check your Internet connection, or choose one of the following");
                    alert.getDialogPane().setPrefWidth(500);
                    ButtonType retry = new ButtonType("Retry");
                    ButtonType enter_new = new ButtonType("Enter server IP/PORT");
                    ButtonType cancel1 = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(retry, enter_new, cancel1);
                    Optional<ButtonType> res = alert.showAndWait();
                    if (!res.isPresent() || res.get() == cancel1) {
                        System.exit(0);
                    }
                    if (res.get() == retry) {
                        initial_alert.show();
                        reachServer();
                    } else if (res.get() == enter_new) {
                        valid = false;
                        while (!valid) {
                            TextInputDialog ip_dialog = new TextInputDialog();
                            ip_dialog.setTitle("Home");
                            ip_dialog.setHeaderText("Enter a valid Server IP");
                            Optional<String> result = ip_dialog.showAndWait();
                            if (result.isPresent()) {
                                if (valid_ip(result.get())) {
                                    MainApp.SERVER_IP = result.get();
                                    valid = true;
                                }
                            } else {
                                System.exit(0);
                            }
                        }
                        valid = false;
                        while (!valid) {
                            TextInputDialog port_dialog = new TextInputDialog();
                            port_dialog.setTitle("Home");
                            port_dialog.setHeaderText("Enter a valid Server Port");
                            Optional<String> result = port_dialog.showAndWait();
                            if (result.isPresent()) {
                                try {
                                    MainApp.MAIN_PORT = Integer.parseInt(result.get());
                                    valid = true;
                                } catch (NumberFormatException ignore) {
                                }
                            } else {
                                System.exit(0);
                            }
                        }
                        update_Server_IpPort();
                        initial_alert.show();
                        reachServer();
                    }
                });
            }

        };
        reachServer();
    }

    public void reachServer() {
        Thread t = new Thread(connectRunnable);
        t.start();
    }

    private void readConfigFile() {
        Properties prop = new Properties();
        try (FileInputStream ip = new FileInputStream("src/shared/config.properties")) {
            prop.load(ip);
            MainApp.SERVER_IP = prop.getProperty("SERVER_IP");
            MainApp.MAIN_PORT = Integer.parseInt(prop.getProperty("PORT"));
        } catch (IOException ignore) {
        }
    }

    private void update_Server_IpPort() {
        Properties prop = new Properties();
        try {
            String fname = "src/shared/config.properties";
            FileInputStream ip = new FileInputStream(fname);
            prop.load(ip);
            prop.setProperty("SERVER_IP", MainApp.SERVER_IP);
            prop.setProperty("PORT", Integer.toString(MainApp.MAIN_PORT));
            prop.store(new FileOutputStream(fname), null);
        } catch (IOException ignore) {
        }
    }

    private boolean valid_ip(String ip) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?<!\\d|\\d\\.)"
                + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])" + "(?!\\d|\\.\\d)")
                .matcher(ip);
        String result = m.find() ? m.group() : null;
        return result != null;
    }

    public void createGUI() {
        root = new BorderPane();
        MenuBar top = createTopGUI();
        top.getTransforms().setAll(Assets.scale);
        center = createCenterGUI();
        center.getTransforms().setAll(Assets.scale);
        root.setTop(top);
        root.setCenter(center);
        Scene scene = new Scene(root);
        Path path = Paths.get("src/main/application.css");
        try {
            scene.getStylesheets().add(path.toUri().toURL().toExternalForm());
        } catch (MalformedURLException ignore) {
        }
        stage.setScene(scene);
        stage.show();
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            root.setMinWidth(stage.getWidth() * Assets.unscale_width - width_diff * Assets.unscale_width);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            root.setMinHeight(stage.getHeight() * Assets.unscale_height - height_diff * Assets.unscale_height);
        });
        Assets.mainApp_width = stage.getWidth();
        Assets.mainApp_height = stage.getHeight();
        width_diff = stage.getWidth() - scene.getWidth();
        height_diff = stage.getHeight() - scene.getHeight();
        update_width_height(Assets.mainApp_width, Assets.mainApp_height);
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public void update_width_height(double width, double height) {
        stage.sizeToScene();
        root.setMinWidth(width * Assets.unscale_width - width_diff * Assets.unscale_width);
        root.setMinHeight(height * Assets.unscale_height - height_diff * Assets.unscale_height);
        double stage_min_width = width * Assets.scale_width + width_diff * Assets.unscale_width;
        double stage_min_height = height * Assets.scale_height + height_diff * Assets.unscale_height;
        stage.setMinWidth(stage_min_width);
        stage.setMinHeight(stage_min_height);
        stage.setWidth(stage_min_width);
        stage.setHeight(stage_min_height);
        stage.centerOnScreen();
    }

    private MenuBar createTopGUI() {
        MenuBar top = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem about = new MenuItem("About Me");
        Menu exit = new Menu("Return to..");
        MenuItem return_room = new MenuItem("Return to Room (exit game)");
        MenuItem return_main = new MenuItem("Return Home (exit join/room)");
        MenuItem return_desktop = new MenuItem("Return to Desktop (exit application)");

        options.getItems().addAll(about);
        exit.getItems().addAll(return_room, return_main, return_desktop);
        top.getMenus().addAll(options, exit);
        about.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("BHA Bilel");
            alert.setHeaderText("Hello! my name is BENHADJ AMAR Bilel");
            alert.setContentText("Thanks for playing my game :)");
            ButtonType visit_linkedin = new ButtonType("Visit my LinkedIn profile");
            ButtonType visit_github = new ButtonType("Visit my Github profile");
            alert.getButtonTypes().addAll(visit_linkedin, visit_github);
            Optional<ButtonType> res = alert.showAndWait();
            boolean visit_li = res.get() == visit_linkedin, visit_gh = res.get() == visit_github;
            if (visit_li || visit_gh) {
                String url = visit_li ? "https://www.linkedin.com/in/bilel-bha/" : "https://github.com/BHA-Bilel";

                String myOS = System.getProperty("os.name").toLowerCase();
                try {
                    if (Desktop.isDesktopSupported()) { // Probably Windows
                        Desktop desktop = Desktop.getDesktop();
                        desktop.browse(new URI(url));
                    } else { // Definitely Non-windows
                        Runtime runtime = Runtime.getRuntime();
                        if (myOS.contains("mac")) { // Apples
                            runtime.exec("open " + url);
                        } else if (myOS.contains("nix") || myOS.contains("nux")) { // Linux flavours
                            runtime.exec("xdg-open " + url);
                        }
                    }
                } catch (IOException | URISyntaxException ignore) {
                }
            }
        });
        return_room.setOnAction(e -> {
            if (roomApp != null) {
                roomApp.endGame();
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(THIS_GAME.toString());
                alert.setHeaderText("You are not currently in a Game !");
                alert.show();
            }
        });
        return_desktop.setOnAction(e -> {
            if (roomApp != null) {
                roomApp.returnHome(false);
            } else if (joinApp != null) {
                joinApp.returnHome(false);
            }
            System.exit(0);
        });
        return_main.setOnAction(e -> {
            if (!in_main_menu) {
                prevent_user_interactions();
                if (roomApp != null) {
                    roomApp.returnHome(false);
                    roomApp = null;
                } else if (joinApp != null) {
                    joinApp.returnHome(false);
                    joinApp = null;
                }
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(THIS_GAME.toString());
                alert.setHeaderText("You are already Home !");
                alert.show();
            }
        });
        return top;
    }

    private VBox createCenterGUI() {
        VBox center = new VBox(50);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.CENTER);
        JFXTextField username = new JFXTextField();
        username.setPromptText("username");
        username.setPrefWidth(500);
        username.setMaxWidth(500);
        username.setPadding(new Insets(20, 0, 20, 0));

        HBox hb1 = new HBox(100);
        hb1.setPadding(new Insets(0, 50, 0, 50));
        hb1.setAlignment(Pos.CENTER);
        JFXButton host = new JFXButton("Host a Room");
        host.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        JFXButton join_rooms = new JFXButton("Join Public Rooms");
        join_rooms.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        host.setOnAction(e -> {
            if (!validUsername(username.getText()))
                return;
            HostRoom(username.getText());
        });

        join_rooms.setOnAction(e -> {
            if (!validUsername(username.getText()))
                return;
            JoinOnlineRooms(username.getText());
        });
        hb1.getChildren().addAll(host, join_rooms);

        HBox hb2 = new HBox(100);
        hb2.setPadding(new Insets(0, 50, 50, 50));
        hb2.setAlignment(Pos.CENTER);
        JFXTextField room_id = new JFXTextField();
        room_id.setPromptText("Room ID");

        room_id.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        room_id.setPrefColumnCount(5);
        JFXButton join_specific = new JFXButton("Join Room");
        join_specific.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        join_specific.setOnAction(e -> {
            if (!validUsername(username.getText()))
                return;
            int roomID = validRoomID(room_id.getText());
            if (roomID < 0)
                return;
            JoinSpecificRoom(username.getText(), roomID);
        });
        hb2.getChildren().addAll(room_id, join_specific);
        center.getChildren().addAll(username, hb1, hb2);
        return center;
    }

    public static boolean validUsername(String username) {
        if (username.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(THIS_GAME.toString());
            alert.setHeaderText("You must enter a username !");
            alert.setContentText("Enter a username to play");
            alert.show();
            return false;
        }
        return true;
    }

    private int validRoomID(String room_id) {
        int roomID = -1;
        if (room_id.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(THIS_GAME.toString());
            alert.setHeaderText("Empty room ID !");
            alert.setContentText("Enter a room ID to join a room");
            alert.show();
        } else {
            try {
                roomID = Integer.parseInt(room_id);
                if (roomID > 65535 || roomID < 49152) {
                    roomID = -1;
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle(THIS_GAME.toString());
                    alert.setHeaderText("Invalid room ID");
                    alert.setContentText("This room doesn't exist !");
                    alert.show();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(THIS_GAME.toString());
                alert.setHeaderText("Invalid room ID");
                alert.setContentText("This room doesn't exist !");
                alert.show();
            }
        }
        return roomID;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void HostRoom(String username) {
        prevent_user_interactions();
        Thread t = new Thread(() -> {
            try (Socket s = new Socket(SERVER_IP, GAME_PORT);
                 ObjectOutputStream objOut = new ObjectOutputStream(s.getOutputStream());
                 ObjectInputStream objIn = new ObjectInputStream(s.getInputStream())) {
                objOut.writeInt(MainRequest.HOST.ordinal());
                objOut.flush();
                int response = objIn.readInt();
                if (response > 0) {
                    try {
                        Socket roomSocket = new Socket();
                        roomSocket.connect(new InetSocketAddress(SERVER_IP, response), 5000);
                        roomApp = new RoomApp(this, null, roomSocket, response, username);
                        Platform.runLater(() -> setRoomApp(roomApp));
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle(THIS_GAME.toString());
                            alert.setHeaderText("Couldn't join the room");
                            alert.setContentText("Please check your Internet connection");
                            alert.setOnHidden(hidden -> reachServer());
                        });

                    }
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle(THIS_GAME.toString());
                        alert.setHeaderText("Couldn't join a Room");
                        alert.setContentText("Unfortunately, all rooms are taken. Please try again later");
                        alert.show();
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle(THIS_GAME.toString());
                    alert.setHeaderText("Couldn't connect to server");
                    alert.setContentText("Please check your Internet connection");
                    alert.show();
                    alert.setOnHidden(hidden -> reachServer());
                });
            }
        });
        t.start();
    }

    public void JoinOnlineRooms(String username) {
        prevent_user_interactions();
        Thread t = new Thread(() -> {
            try {
                Socket joinSocket = new Socket();
                joinSocket.connect(new InetSocketAddress(SERVER_IP, GAME_PORT), 5000);
                joinApp = new JoinApp(this, username, joinSocket);
                Platform.runLater(() -> setJoinApp(joinApp));
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle(THIS_GAME.toString());
                    alert.setHeaderText("Couldn't access the server");
                    alert.setContentText("Please check your Internet connection");
                    alert.show();
                    alert.setOnHidden(hidden -> reachServer());
                });
            }
        });
        t.start();
    }

    public void JoinSpecificRoom(String username, int roomID) {
        prevent_user_interactions();
        Socket roomSocket = new Socket();
        Thread t = new Thread(() -> {
            try {
                roomSocket.connect(new InetSocketAddress(SERVER_IP, roomID), 5000);
                roomApp = new RoomApp(this, null, roomSocket, roomID, username);

                Platform.runLater(() -> setRoomApp(roomApp));
            } catch (IOException e) {
                try {
                    roomSocket.close();
                } catch (IOException ignore) {
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle(THIS_GAME.toString());
                    alert.setHeaderText("Couldn't join the room");
                    alert.setContentText("It's either full or doesn't exist");
                    alert.show();
                    alert.setOnHidden(hidden -> reachServer());
                });
            }
        });
        t.start();
    }

    public void returnHomeApp(boolean exception) {
        in_main_menu = true;
        joinApp = null;
        roomApp = null;
        root.setCenter(center);
        update_width_height(Assets.mainApp_width, Assets.mainApp_height);
        if (exception) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(MainApp.THIS_GAME.toString());
            alert.setHeaderText("Something went wrong !");
            alert.setContentText("An error has occurred while communicating with the server");
            alert.show();
            alert.setOnHidden(hidden -> reachServer());
        } else {
            allow_user_interactions();
        }
    }


    public void setJoinApp(JoinApp joinApp) {
        in_main_menu = false;
        this.joinApp = joinApp;
        joinApp.getTransforms().setAll(Assets.scale);
        root.setCenter(joinApp);
        if (Assets.joinApp_width == 0) {
            stage.sizeToScene();
            Assets.joinApp_width = stage.getWidth();
            Assets.joinApp_height = stage.getHeight();
        }
        update_width_height(Assets.joinApp_width, Assets.joinApp_height);
        allow_user_interactions();
    }

    public void fitStageToScene() {
        stage.sizeToScene();
    }

    public void setRoomApp(RoomApp roomApp) {
        in_main_menu = false;
        this.roomApp = roomApp;
        roomApp.getTransforms().setAll(Assets.scale);
        root.setCenter(roomApp);
        if (Assets.roomApp_width == 0) {
            stage.sizeToScene();
            Assets.roomApp_width = stage.getWidth();
            Assets.roomApp_height = stage.getHeight();
        }
        update_width_height(Assets.roomApp_width, Assets.roomApp_height);
        allow_user_interactions();
    }

    public void setGameApp(GameApp gameApp) {
        gameApp.getTransforms().setAll(Assets.scale);
        root.setCenter(gameApp);
        if (Assets.gameApp_width == 0) {
            stage.sizeToScene();
            Assets.gameApp_width = stage.getWidth();
            Assets.gameApp_height = stage.getHeight();
        }
        update_width_height(Assets.gameApp_width, Assets.gameApp_height);
        allow_user_interactions();
    }

    public static void allow_user_interactions() {
        if (hold_alert.isShowing()) {
            hold_alert.setOnCloseRequest(e -> hold_alert.hide());
            Platform.runLater(() -> hold_alert.close());
        }
    }

    public static void prevent_user_interactions() {
        if (!hold_alert.isShowing()) {
            hold_alert.setOnCloseRequest(Event::consume);
            Platform.runLater(hold_alert::show);
        }
    }
}
