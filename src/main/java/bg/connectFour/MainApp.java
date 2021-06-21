package bg.connectFour;

import bg.connectFour.game.GameApp;
import bg.connectFour.gfx.Assets;
import bg.connectFour.lang.LANGNAME;
import bg.connectFour.lang.Language;
import bg.connectFour.popup.MyAlert;
import bg.connectFour.popup.MyTextInputDialog;
import bg.connectFour.room.JoinApp;
import bg.connectFour.room.RoomApp;
import bg.connectFour.server.local.LocalClient;
import bg.connectFour.server.room.RoomServer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import shared.Game;
import shared.LocalRoomInfo;
import shared.MainRequest;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class MainApp extends Application {

    public static final String CURRENT_VERSION = "1.0";
    private static Path PROP_PATH, UN_PATH;
    public static RoomServer roomServer;
    private Runnable online_setup_runnable;
    public static String ONLINE_IP;
    public static String MULTICAST_IP;
    public static int ONLINE_PORT, ONLINE_GAME_PORT, ONLINE_TIMEOUT, LOCAL_TIMEOUT, JOINERS_PORT, HOSTERS_PORT;

    public static final String[] themes = new String[]{"/main_light.css", "/main_dark.css"};
    public static File[] notif_themes = new File[2];
    public static String USERNAME, PREF_MODE, CURRENT_THEME;

    public static Stage stage;
    public static final Game BG_GAME = Game.CONNECT4;
    public static StringProperty GAME_NAME;
    public static JFXRadioButton online, local;
    public static DoubleProperty fontProperty, spacingProperty, paddingProperty;

    private BorderPane root;
    private VBox center;
    private JFXTextField username, room_id;
    private Menu game_menu;
    private JFXButton host, join_rooms, join_specific;

    private volatile boolean main_shortcuts_activated = false;
    private JoinApp joinApp;
    public RoomApp roomApp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        Assets.init_resolution();
        Language.init();
        init_properties();
        online_setup_runnable = () -> {
            try (Socket listen = new Socket()) {
                listen.connect(new InetSocketAddress(ONLINE_IP, ONLINE_PORT), ONLINE_TIMEOUT);
                DataOutputStream dataOut = new DataOutputStream(listen.getOutputStream());
                DataInputStream dataIn = new DataInputStream(listen.getInputStream());
                dataOut.writeInt(BG_GAME.ordinal());
                dataOut.flush();
                ONLINE_GAME_PORT = dataIn.readInt();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    MyAlert.hide_alert("ONLINE");
                    Alert alert = new MyAlert(AlertType.WARNING, Language.ONLINE_SETUP_FAIL_H, Language.ONLINE_SETUP_FAIL_C);
                    ButtonType retry = new ButtonType(Language.RETRY.getValue());
                    ButtonType cancel = new ButtonType(Language.CANCEL.getValue(), ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(retry, cancel);
                    Optional<ButtonType> res = alert.showAndWait();
                    if (res.isEmpty() || res.get() == cancel) {
                        online.setSelected(false);
                    } else if (res.get() == retry) {
                        setup_online_multiplayer();
                    }
                });
                return;
            }
            if (ONLINE_GAME_PORT < 0) {
                Platform.runLater(() -> {
                    MyAlert.hide_alert("ONLINE");
                    Alert alert = new MyAlert(AlertType.INFORMATION, Language.unav(), Language.GS_NA_C);
                    alert.show();
                });
            } else {
                Platform.runLater(() -> {
                    enable_gui();
                    MyAlert.hide_alert("ONLINE");
                    add_join_shortcut();
                    Notifications notif = Notifications.create()
                            .title(MainApp.GAME_NAME.getValue()).text(Language.ONLINE_SETUP_SUCCESS.getValue())
                            .owner(stage).hideCloseButton().onAction(e -> {
                                if (stage.isIconified()) stage.setIconified(false);
                                else if (!stage.isShowing()) stage.show();
                                else stage.requestFocus();
                            });
                    notif.show();
                });
            }
        };
        GAME_NAME = new SimpleStringProperty();
        GAME_NAME.bind(Language.CONNECT4);
        stage.titleProperty().bind(GAME_NAME);
        stage.setWidth(Assets.width / 2);
        stage.setHeight(Assets.height / 2);
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> {
            release_ressources();
            System.exit(0);
        });
        createGUI();
        post_initialization();
    }

    private void post_initialization() {
        setup_notif_css();
        MyAlert.post_init();
        if (isFirstTime()) {
            disable_gui();
            Language.first_timer();
            MyAlert.first_timer(this, 1);
        } else {
            Notifications notif = Notifications.create()
                    .title(MainApp.GAME_NAME.getValue()).text(Language.WELCOME_BACK.getValue())
                    .owner(stage).hideCloseButton().onAction(e -> {
                        if (stage.isIconified()) stage.setIconified(false);
                        else if (!stage.isShowing()) stage.show();
                        else stage.requestFocus();
                    });
            notif.show();
            setup_pref_mode();
        }
    }

    public void setup_pref_mode() {
        if (PREF_MODE.equals("LOCAL")) {
            local.setSelected(true);
        } else if (PREF_MODE.equals("ONLINE")) {
            online.setSelected(true);
        } else {
            disable_gui();
        }
    }

    private void disable_gui() {
        host.setDisable(true);
        join_rooms.setDisable(true);
        join_specific.setDisable(true);
        room_id.setDisable(true);
    }

    private void enable_gui() {
        host.setDisable(false);
        join_rooms.setDisable(false);
        join_specific.setDisable(false);
        room_id.setDisable(false);
    }

    private void setup_online_multiplayer() {
        MyAlert.show_alert("ONLINE");
        reachServer();
    }

    private void setup_local_multiplayer() {
        MyAlert.show_alert("LOCAL");
        Thread local_setup = new Thread(() -> {
            boolean success = LocalClient.init_local(MULTICAST_IP, JOINERS_PORT, HOSTERS_PORT);
            Platform.runLater(() -> {
                MyAlert.hide_alert("LOCAL");
                if (success) {
                    enable_gui();
                    Notifications notif = Notifications.create()
                            .title(MainApp.GAME_NAME.getValue()).text(Language.LOCAL_SETUP_SUCCESS.getValue())
                            .owner(stage).hideCloseButton().onAction(e -> {
                                if (stage.isIconified()) stage.setIconified(false);
                                else if (!stage.isShowing()) stage.show();
                                else stage.requestFocus();
                            });
                    notif.show();
                } else {
                    Alert alert = new MyAlert(AlertType.WARNING, Language.LOCAL_SETUP_FAIL_H, Language.LOCAL_SETUP_FAIL_C);
                    ButtonType retry = new ButtonType(Language.RETRY.getValue());
                    ButtonType cancel = new ButtonType(Language.CANCEL.getValue(), ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(retry, cancel);
                    Optional<ButtonType> res = alert.showAndWait();
                    if (res.isEmpty() || res.get() == cancel) {
                        local.setSelected(false);
                    } else if (res.get() == retry) {
                        MyAlert.show_alert("LOCAL");
                        setup_local_multiplayer();
                    }
                }
            });
        });
        local_setup.start();
    }

    public void reachServer() {
        Thread online_seup = new Thread(online_setup_runnable);
        online_seup.start();
    }

    private void init_properties() {
        PROP_PATH = Paths.get(System.getProperty("user.home"), ".bg", "." + BG_GAME, "/config.properties");
        UN_PATH = Paths.get(System.getProperty("user.home"), ".bg", "." + BG_GAME, "/username.txt");
        try {
            Files.createDirectories(PROP_PATH.getParent());
        } catch (IOException ignore) {
        }
        if (!Files.exists(PROP_PATH)) {
            create_prop_file();
        }
        if (!Files.exists(UN_PATH)) {
            create_un_file();
        }
        get_properties();
    }

    private void get_properties() {
        Properties prop = new Properties();
        try (InputStream ip = new FileInputStream(PROP_PATH.toFile())) {
            prop.load(ip);
            ONLINE_IP = prop.getProperty("ONLINE_IP");
            ONLINE_PORT = Integer.parseInt(prop.getProperty("ONLINE_PORT"));
            MULTICAST_IP = prop.getProperty("MULTICAST_IP");
            JOINERS_PORT = Integer.parseInt(prop.getProperty("JOINERS_PORT"));
            HOSTERS_PORT = Integer.parseInt(prop.getProperty("HOSTERS_PORT"));
            ONLINE_TIMEOUT = Integer.parseInt(prop.getProperty("ONLINE_TIMEOUT"));
            LOCAL_TIMEOUT = Integer.parseInt(prop.getProperty("LOCAL_TIMEOUT"));
            CURRENT_THEME = prop.getProperty("THEME");
            Language.load_lang(LANGNAME.valueOf(prop.getProperty("LANG")));
            PREF_MODE = prop.getProperty("PREF_MODE");
            USERNAME = Files.readString(UN_PATH.toFile().toPath(), StandardCharsets.UTF_8);
            check_props_integrity();
        } catch (IOException | NumberFormatException e) {
            reset_properties(false, 0);
        }
    }

    private void check_props_integrity() {
        boolean valid = valid_ip(ONLINE_IP);
        if (!valid) {
            ONLINE_IP = update_property("ONLINE_IP", get_default_property("ONLINE_IP"));
        }
        valid = valid_timeout(String.valueOf(ONLINE_TIMEOUT));
        if (!valid) {
            ONLINE_TIMEOUT = Integer.parseInt(update_property("ONLINE_TIMEOUT", get_default_property("ONLINE_TIMEOUT")));
        }
        valid = valid_timeout(String.valueOf(LOCAL_TIMEOUT));
        if (!valid) {
            LOCAL_TIMEOUT = Integer.parseInt(update_property("LOCAL_TIMEOUT", get_default_property("LOCAL_TIMEOUT")));
        }
        valid = valid_theme();
        if (!valid) {
            CURRENT_THEME = update_property("THEME", get_default_property("THEME"));
        }
        valid = valid_pref_mode();
        if (!valid) {
            PREF_MODE = update_property("PREF_MODE", get_default_property("PREF_MODE"));
        }
        int valid_int;
        valid_int = valid_mc_ip(MULTICAST_IP);
        if (valid_int < 0) {
            MULTICAST_IP = update_property("MULTICAST_IP", get_default_property("MULTICAST_IP"));
        }
        valid_int = valid_port(String.valueOf(ONLINE_PORT));
        if (valid_int < 0) {
            ONLINE_PORT = Integer.parseInt(update_property("ONLINE_PORT", get_default_property("ONLINE_PORT")));
        }
        valid_int = valid_port(String.valueOf(JOINERS_PORT));
        if (valid_int < 0) {
            JOINERS_PORT = Integer.parseInt(update_property("JOINERS_PORT", get_default_property("JOINERS_PORT")));
        }
        valid_int = valid_port(String.valueOf(HOSTERS_PORT));
        if (valid_int < 0) {
            HOSTERS_PORT = Integer.parseInt(update_property("HOSTERS_PORT", get_default_property("HOSTERS_PORT")));
        }
    }

    private int in_default_settings() {
        Properties prop = new Properties();
        boolean local = false, online = false;
        try (InputStream ip = MainApp.class.getResourceAsStream("/def_config.properties")) {
            prop.load(ip);
            if (!ONLINE_IP.equals(prop.getProperty("ONLINE_IP"))) online = true;
            if (ONLINE_PORT != Integer.parseInt(prop.getProperty("ONLINE_PORT"))) online = true;
            if (!MULTICAST_IP.equals(prop.getProperty("MULTICAST_IP"))) local = true;
            if (JOINERS_PORT != Integer.parseInt(prop.getProperty("JOINERS_PORT"))) local = true;
            if (HOSTERS_PORT != Integer.parseInt(prop.getProperty("HOSTERS_PORT"))) local = true;
            if (ONLINE_TIMEOUT != Integer.parseInt(prop.getProperty("ONLINE_TIMEOUT"))) online = true;
            if (LOCAL_TIMEOUT != Integer.parseInt(prop.getProperty("LOCAL_TIMEOUT"))) local = true;
        } catch (IOException e) {
            return 3;
        }
        if (local && online) return 3;
        else if (local) return 2;
        else if (online) return 1;
        else return 0;
    }

    private void reset_properties(boolean restore, int change) {
        restore_default_prop();
        update_property("FIRST_TIME", "false");
        if (restore) {
            update_property("THEME", CURRENT_THEME);
            update_property("LANG", Language.LANG_NAME.toString());
            update_property("PREF_MODE", PREF_MODE);
        }
        get_properties();
        if ((change == 3 || change == 1) && online.isSelected()) {
            setup_online_multiplayer();
        }
        if ((change == 3 || change == 2) && local.isSelected()) {
            LocalClient.stop_local();
            setup_local_multiplayer();
        }
    }

    private void restore_default_prop() {
        Properties def_prop = new Properties();
        try (InputStream def = MainApp.class.getResourceAsStream("/def_config.properties")) {
            def_prop.load(def);
            Properties curr_prop = new Properties();
            try (InputStream curr = new FileInputStream(PROP_PATH.toFile())) {
                curr_prop.load(curr);
                Set<String> keys = def_prop.stringPropertyNames();
                for (String key : keys) {
                    curr_prop.setProperty(key, def_prop.getProperty(key));
                }
                curr_prop.store(new OutputStreamWriter(new FileOutputStream(PROP_PATH.toFile())), null);
            }
        } catch (IOException ignore) {
        }
    }

    private void create_prop_file() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("def_config.properties")) {
            Files.copy(stream, PROP_PATH);
        } catch (IOException ignore) {
        }
    }

    private void create_un_file() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("def_un.txt")) {
            Files.copy(stream, UN_PATH);
        } catch (IOException ignore) {
        }
    }

    private boolean isFirstTime() {
        Properties prop = new Properties();
        try (InputStream ip = new FileInputStream(PROP_PATH.toFile())) {
            prop.load(ip);
            return prop.getProperty("FIRST_TIME").equals("true");
        } catch (IOException ignore) {
        }
        return false;
    }

    private static void save_new_username(String username) {
        USERNAME = username;
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(UN_PATH.toFile()), StandardCharsets.UTF_8)) {
            writer.write(username);
        } catch (IOException ignore) {
        }
    }

    public String update_property(String key, String value) {
        Properties prop = new Properties();
        try (InputStream ip = new FileInputStream(PROP_PATH.toFile())) {
            prop.load(ip);
            prop.setProperty(key, value);
            prop.store(new OutputStreamWriter(new FileOutputStream(PROP_PATH.toFile())), null);
        } catch (IOException ignore) {
        }
        return value;
    }

    private String get_default_property(String key) {
        Properties prop = new Properties();
        try (InputStream ip = new FileInputStream(PROP_PATH.toFile())) {
            prop.load(ip);
            return prop.getProperty(key);
        } catch (IOException ignore) {
        }
        return null;
    }

    public void update_theme(String NEW_THEME, File notif_temp) {
        CURRENT_THEME = NEW_THEME;
        update_property("THEME", NEW_THEME);
        stage.getScene().getStylesheets().clear();
        stage.getScene().getStylesheets().add(MainApp.class.getResource(CURRENT_THEME).toExternalForm());
        stage.getScene().getStylesheets().add(notif_temp.toURI().toString());

        if (roomApp != null) {
            roomApp.updateChatTheme();
        }
        MyAlert.update_alerts_theme();
    }

    public static boolean valid_username(String username) {
        if (username.isEmpty()) {
            Alert alert = new MyAlert(AlertType.ERROR, Language.UN_H1, Language.UN_C1);
            alert.show();
            return false;
        } else if (username.length() > 20) {
            Alert alert = new MyAlert(AlertType.ERROR, Language.UN_H2, Language.UN_C2);
            alert.show();
            return false;
        }
        if (!username.equals(USERNAME)) {
            save_new_username(username);
        }
        return true;
    }

    private boolean valid_ip(String ip) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?<!\\d|\\d\\.)"
                + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])" + "(?!\\d|\\.\\d)")
                .matcher(ip);
        String result = m.find() ? m.group() : null;
        return result != null;
    }


    private int valid_mc_ip(String ip) {
        boolean valid_ip = valid_ip(ip);
        if (!valid_ip) return -1;
        int first = Integer.parseInt(ip.split("\\.")[0]);
        valid_ip = first >= 224 && first <= 239;
        return valid_ip ? 1 : -2;
    }

    private int valid_port(String port) {
        int roomID;
        try {
            roomID = Integer.parseInt(port);
            if (roomID <= 0 || roomID > 65535) {
                return -1;
            }
        } catch (NumberFormatException ex) {
            return -2;
        }
        return roomID;
    }

    private boolean valid_timeout(String timeout) {
        try {
            int time = Integer.parseInt(timeout);
            return time >= 100 && time < 10000;
        } catch (NumberFormatException ignore) {
        }
        return false;
    }

    private boolean valid_theme() {
        for (String theme : themes) {
            if (CURRENT_THEME.equals(theme)) return true;
        }
        return false;
    }

    private boolean valid_pref_mode() {
        return PREF_MODE.equals("NONE") || PREF_MODE.equals("ONLINE") || PREF_MODE.equals("LOCAL");
    }

    public void createGUI() {
        root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        fontProperty = new SimpleDoubleProperty();
        spacingProperty = new SimpleDoubleProperty();
        paddingProperty = new SimpleDoubleProperty();
        fontProperty.bind(scene.widthProperty().add(scene.heightProperty()).divide(100));
        spacingProperty.bind(scene.widthProperty().add(scene.heightProperty()).divide(100));
        paddingProperty.bind(scene.widthProperty().add(scene.heightProperty()).divide(100));
        root.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontProperty.asString()));
        MenuBar top = createTopGUI();
        center = createCenterGUI();
        root.setTop(top);
        root.setCenter(center);
        root.requestFocus();
        scene.getStylesheets().add(MainApp.class.getResource(CURRENT_THEME).toExternalForm());
        scene.setOnMousePressed(e -> root.requestFocus());
        setup_shotcuts();
        stage.show();
    }

    private void setup_notif_css() {
        // workaround because controlsfx notifications don't have any properties to bind
        int curr_theme = 0, i = 0;
        for (String css : themes) {
            if (CURRENT_THEME.equals(css)) curr_theme = i;

            StringBuilder text = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getClassLoader().getResourceAsStream(css.replace("/main", "notif"))))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }
            } catch (IOException ignore) {
            }
            String notif_css = text.toString();
            notif_css = notif_css.replaceAll("-fx-font-size:;", "-fx-font-size: " + fontProperty.intValue() + "px;");
            File temp = new File(css.replace("main", "notif"));
            temp.delete();
            try {
                temp.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
                bw.write(notif_css);
                bw.close();
                notif_themes[i] = temp;
                i++;
            } catch (IOException ignore) {
            }
        }
        stage.getScene().getStylesheets().add(notif_themes[curr_theme].toURI().toString());
    }

    private MenuBar createTopGUI() {
        MenuBar top = new MenuBar();

        Menu window = new Menu();
        window.textProperty().bind(Language.WINDOW);
        MenuItem switch_fs = new MenuItem();
        switch_fs.textProperty().bind(Language.SWITCH_FS);
        switch_fs.setAccelerator(KeyCombination.keyCombination("F11"));
        MenuItem switch_theme = new MenuItem();
        switch_theme.textProperty().bind(Language.SWITCH_THEME);
        switch_theme.setAccelerator(KeyCombination.keyCombination("F2"));

        Menu shortcuts = new Menu();
        shortcuts.textProperty().bind(Language.SHORTCUTS);
        MenuItem general_shortcuts = new MenuItem();
        general_shortcuts.textProperty().bind(Language.GENERAL);
        MenuItem main_shortcuts = new MenuItem();
        main_shortcuts.textProperty().bind(Language.MAIN);
        MenuItem join_shortcuts = new MenuItem();
        join_shortcuts.textProperty().bind(Language.JOIN);
        MenuItem room_shortcuts = new MenuItem();
        room_shortcuts.textProperty().bind(Language.ROOM);
        MenuItem game_shortcuts = new MenuItem();
        game_shortcuts.textProperty().bind(Language.GAME);

        Menu network = new Menu();
        network.textProperty().bind(Language.NETWORK);
        Menu online_net = new Menu();
        online_net.textProperty().bind(Language.ONLINE);
        MenuItem ch_on_ip = new MenuItem();
        ch_on_ip.textProperty().bind(Language.CH_ON_IP);
        MenuItem ch_on_port = new MenuItem();
        ch_on_port.textProperty().bind(Language.CH_ON_PORT);
        MenuItem ch_on_to = new MenuItem();
        ch_on_to.textProperty().bind(Language.CH_ON_TO);
        Menu local_net = new Menu();
        local_net.textProperty().bind(Language.LOCAL);
        MenuItem ch_mc_ip = new MenuItem();
        ch_mc_ip.textProperty().bind(Language.CH_MC_IP);
        MenuItem ch_jn_port = new MenuItem();
        ch_jn_port.textProperty().bind(Language.CH_JN_PORT);
        MenuItem ch_hs_port = new MenuItem();
        ch_hs_port.textProperty().bind(Language.CH_HS_PORT);
        MenuItem ch_lc_to = new MenuItem();
        ch_lc_to.textProperty().bind(Language.CH_LC_TO);
        MenuItem restore_defaults = new MenuItem();
        restore_defaults.textProperty().bind(Language.RESTORE);

        Menu language = new Menu();
        language.textProperty().bind(Language.LANGUAGE);
        MenuItem english = new MenuItem();
        english.textProperty().bind(Language.ENGLISH);
        MenuItem french = new MenuItem();
        french.textProperty().bind(Language.FRENCH);
        MenuItem arabic = new MenuItem();
        arabic.textProperty().bind(Language.ARABIC);

        Menu download_update = new Menu();
        download_update.textProperty().bind(Language.DOWNLOAD_UPDATE);
        MenuItem du_xo = new MenuItem();
        du_xo.textProperty().bind(Language.XO);
        MenuItem du_checkers = new MenuItem();
        du_checkers.textProperty().bind(Language.CHECKERS);
        MenuItem du_chess = new MenuItem();
        du_chess.textProperty().bind(Language.CHESS);
        MenuItem du_connect4 = new MenuItem();
        du_connect4.textProperty().bind(Language.CONNECT4);
        MenuItem du_dominoes = new MenuItem();
        du_dominoes.textProperty().bind(Language.DOMINOS);
        MenuItem du_coinche = new MenuItem();
        du_coinche.textProperty().bind(Language.COINCHE);

        Menu about_menu = new Menu();
        about_menu.textProperty().bind(Language.ABOUT);
        MenuItem help = new MenuItem();
        help.textProperty().bind(Language.HELP);
        help.setAccelerator(KeyCombination.keyCombination("F1"));
        MenuItem about = new MenuItem();
        about.textProperty().bind(Language.ABOUT_ME);
        MenuItem feedback = new MenuItem();
        feedback.textProperty().bind(Language.FEEDBACK);
        MenuItem copyright = new MenuItem();
        copyright.textProperty().bind(Language.COPYRIGHT);

        game_menu = new Menu();
        game_menu.textProperty().bind(Language.GAME);
        game_menu.setDisable(true);
        MenuItem view_score = new MenuItem();
        view_score.setAccelerator(KeyCombination.keyCombination("ctrl+v"));
        view_score.textProperty().bind(Language.VIEW_SC);
        MenuItem end_game = new MenuItem();
        end_game.setAccelerator(KeyCombination.keyCombination("ctrl+e"));
        end_game.textProperty().bind(Language.END_GAME);

        shortcuts.getItems().addAll(general_shortcuts, main_shortcuts, join_shortcuts, room_shortcuts, game_shortcuts);
        window.getItems().addAll(switch_fs, switch_theme, shortcuts);
        online_net.getItems().addAll(ch_on_ip, ch_on_port, ch_on_to);
        local_net.getItems().addAll(ch_mc_ip, ch_jn_port, ch_hs_port, ch_lc_to);
        network.getItems().addAll(online_net, local_net, restore_defaults);
        language.getItems().addAll(english, french, arabic);
        download_update.getItems().addAll(du_xo, du_checkers, du_chess, du_connect4, du_dominoes, du_coinche);
        about_menu.getItems().addAll(about, help, feedback, copyright);
        game_menu.getItems().addAll(view_score, end_game);
        top.getMenus().addAll(window, network, language, download_update, about_menu, game_menu);

        switch_fs.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));

        switch_theme.setOnAction(e -> {
            if (CURRENT_THEME.equals(themes[0])) update_theme(themes[1], notif_themes[1]);
            else update_theme(themes[0], notif_themes[0]);
        });

        general_shortcuts.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.GEN_SH_T, Language.GEN_SH_H, Language.GEN_SH_C);
            alert.show();
        });

        main_shortcuts.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.M_SH_T, Language.M_SH_H, Language.M_SH_C);
            alert.show();
        });

        join_shortcuts.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.J_SH_T, Language.J_SH_H, Language.J_SH_C);
            alert.show();
        });

        room_shortcuts.setOnAction(e -> {
            Alert first_five = new MyAlert(AlertType.INFORMATION, Language.R_SH_T, Language.R_SH_H1, Language.R_SH_C1);
            ButtonType next_five = new ButtonType(Language.NEXT.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            first_five.getButtonTypes().setAll(next_five, ok);
            Optional<ButtonType> result = first_five.showAndWait();
            if (result.isPresent() && result.get().equals(next_five)) {
                Alert alert = new MyAlert(AlertType.INFORMATION, Language.R_SH_T, Language.R_SH_H2, Language.R_SH_C2);
                alert.show();
            }
        });

        game_shortcuts.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.G_SH_T, Language.G_SH_H, Language.G_SH_C);
            alert.show();
        });

        ch_on_ip.setOnAction(e -> {
            TextInputDialog ip_dialog = new MyTextInputDialog(Language.CH_ON_IP_H, ONLINE_IP);
            Optional<String> result = ip_dialog.showAndWait();
            if (result.isPresent()) {
                if (valid_ip(result.get())) {
                    ONLINE_IP = update_property("ONLINE_IP", result.get());
                    if (online.isSelected()) {
                        setup_online_multiplayer();
                    }
                } else {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.INV_IP_H, Language.INV_IP_C);
                    alert.show();
                }
            }
        });

        ch_on_port.setOnAction(e -> {
            TextInputDialog port_dialog = new MyTextInputDialog(Language.CH_PORT_H, String.valueOf(ONLINE_PORT));
            Optional<String> result = port_dialog.showAndWait();
            if (result.isPresent()) {
                if (valid_port(result.get()) < 0) {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.INV_PORT_H, Language.INV_PORT_C);
                    alert.show();
                } else {
                    ONLINE_PORT = Integer.parseInt(update_property("ONLINE_PORT", result.get()));
                    if (online.isSelected()) {
                        setup_online_multiplayer();
                    }
                }
            }
        });

        ch_on_to.setOnAction(e -> {
            TextInputDialog timeout_dialog = new MyTextInputDialog(Language.CH_TO_H, String.valueOf(ONLINE_TIMEOUT));
            Optional<String> result = timeout_dialog.showAndWait();
            if (result.isPresent()) {
                if (valid_timeout(result.get())) {
                    ONLINE_TIMEOUT = Integer.parseInt(update_property("ONLINE_TIMEOUT", result.get()));
                } else {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.INV_TO_H, Language.INV_TO_C);
                    alert.show();
                }
            }
        });

        ch_mc_ip.setOnAction(e -> {
            TextInputDialog ip_dialog = new MyTextInputDialog(Language.CH_MC_IP_H, MULTICAST_IP);
            Optional<String> result = ip_dialog.showAndWait();
            if (result.isPresent()) {
                int valid = valid_mc_ip(result.get());
                switch (valid) {
                    case -1: {
                        Alert alert = new MyAlert(AlertType.ERROR, Language.INV_IP_H, Language.INV_IP_C);
                        alert.show();
                        return;
                    }
                    case -2: {
                        Alert alert = new MyAlert(AlertType.ERROR, Language.INV_MC_IP_H, Language.INV_MC_IP_C);
                        alert.show();
                        return;
                    }
                    default: {
                        MULTICAST_IP = update_property("MULTICAST_IP", result.get());
                        if (local.isSelected()) {
                            LocalClient.stop_local();
                            setup_local_multiplayer();
                        }
                    }
                }
            }
        });

        ch_jn_port.setOnAction(e -> {
            TextInputDialog port_dialog = new MyTextInputDialog(Language.CH_PORT_H, String.valueOf(JOINERS_PORT));
            Optional<String> result = port_dialog.showAndWait();
            if (result.isPresent()) {
                if (valid_port(result.get()) < 0) {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.INV_PORT_H, Language.INV_PORT_C);
                    alert.show();
                } else {
                    if (String.valueOf(HOSTERS_PORT).equals(result.get())) {
                        Alert alert = new MyAlert(AlertType.ERROR, Language.SAME_JH_H, Language.SAME_JH_C1);
                        alert.show();
                    } else {
                        JOINERS_PORT = Integer.parseInt(update_property("JOINERS_PORT", result.get()));
                        if (local.isSelected()) {
                            LocalClient.stop_local();
                            setup_local_multiplayer();
                        }
                    }
                }
            }
        });

        ch_hs_port.setOnAction(e -> {
            TextInputDialog port_dialog = new MyTextInputDialog(Language.CH_PORT_H, String.valueOf(HOSTERS_PORT));
            Optional<String> result = port_dialog.showAndWait();
            if (result.isPresent()) {
                if (valid_port(result.get()) < 0) {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.INV_PORT_H, Language.INV_PORT_C);
                    alert.show();
                } else {
                    if (String.valueOf(JOINERS_PORT).equals(result.get())) {
                        Alert alert = new MyAlert(AlertType.ERROR, Language.SAME_JH_H, Language.SAME_JH_C2);
                        alert.show();
                    } else {
                        HOSTERS_PORT = Integer.parseInt(update_property("HOSTERS_PORT", result.get()));
                        if (local.isSelected()) {
                            LocalClient.stop_local();
                            setup_local_multiplayer();
                        }
                    }
                }
            }
        });

        ch_lc_to.setOnAction(e -> {
            TextInputDialog timeout_dialog = new MyTextInputDialog(Language.CH_TO_H, String.valueOf(LOCAL_TIMEOUT));
            Optional<String> result = timeout_dialog.showAndWait();
            if (result.isPresent()) {
                if (valid_timeout(result.get())) {
                    LOCAL_TIMEOUT = Integer.parseInt(update_property("LOCAL_TIMEOUT", result.get()));
                } else {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.INV_TO_H, Language.INV_TO_C);
                    alert.show();
                }
            }
        });

        restore_defaults.setOnAction(e -> {
            int change = in_default_settings();
            if (change == 0) {
                Alert alert = new MyAlert(AlertType.INFORMATION, Language.ALREADY_DEFAULT_H, Language.ALREADY_DEFAULT_C);
                alert.show();
                return;
            }
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.RESTORE_H, Language.RESTORE_C);
            ButtonType restore = new ButtonType(Language.RESTORE_BT.getValue());
            ButtonType cancel = new ButtonType(Language.CANCEL.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(restore, cancel);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == restore) {
                MyAlert.show_alert("HOLD");
                reset_properties(true, change);
                MyAlert.hide_alert("HOLD");
            }
        });

        english.setOnAction(e -> {
            Language.load_lang(LANGNAME.ENGLISH);
            update_property("LANG", "ENGLISH");
        });

        french.setOnAction(e -> {
            Language.load_lang(LANGNAME.FRENCH);
            update_property("LANG", "FRENCH");
        });

        arabic.setOnAction(e -> {
            Language.load_lang(LANGNAME.ARABIC);
            update_property("LANG", "ARABIC");
        });

        du_xo.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.UPDATE, Language.DU_XO_H, Language.DU_XO_C);
            ButtonType du_button = new ButtonType(Language.DOWNLOAD_NOW.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(du_button, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == du_button)
                open_url("https://github.com/BHA-Bilel/JavaFX-XO#setup");
        });

        du_checkers.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.DOWNLOAD, Language.DU_CHECKERS_H, Language.DU_CHECKERS_C);
            alert.initOwner(stage);
            ButtonType du_button = new ButtonType(Language.DOWNLOAD_NOW.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(du_button, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == du_button)
                open_url("https://github.com/BHA-Bilel/JavaFX-CHECKERS#setup");
        });

        du_chess.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.DOWNLOAD, Language.DU_CHESS_H, Language.DU_CHESS_C);
            ButtonType du_button = new ButtonType(Language.DOWNLOAD_NOW.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(du_button, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == du_button)
                open_url("https://github.com/BHA-Bilel/JavaFX-CHESS#setup");
        });

        du_connect4.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.DOWNLOAD, Language.DU_CONNECT4_H, Language.DU_CONNECT4_C);
            ButtonType du_button = new ButtonType(Language.CHECK.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(du_button, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == du_button)
                open_url("https://github.com/BHA-Bilel/JavaFX-CONNECT4#setup");
        });

        du_dominoes.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.DOWNLOAD, Language.DU_DOMINOS_H, Language.DU_DOMINOS_C);
            ButtonType du_button = new ButtonType(Language.DOWNLOAD_NOW.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(du_button, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == du_button)
                open_url("https://github.com/BHA-Bilel/JavaFX-DOMINOS#setup");
        });

        du_coinche.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.DOWNLOAD, Language.DU_COINCHE_H, Language.DU_COINCHE_C);
            ButtonType du_button = new ButtonType(Language.DOWNLOAD_NOW.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(du_button, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == du_button)
                open_url("https://github.com/BHA-Bilel/JavaFX-COINCHE#setup");
        });

        about.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.ABOUT_T, Language.ABOUT_H, Language.ABOUT_C);
            ButtonType visit_gh = new ButtonType(Language.VISIT_GH.getValue());
            ButtonType visit_li = new ButtonType(Language.VISIT_LI.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(visit_gh, visit_li, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent()) {
                boolean visit_linkedin = res.get() == visit_li, visit_github = res.get() == visit_gh;
                if (visit_linkedin || visit_github) {
                    String url = visit_linkedin ? "https://www.linkedin.com/in/bilel-bha/" : "https://github.com/BHA-Bilel";
                    open_url(url);
                }
            }
        });

        help.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.HELP, Language.HELP_H, Language.HELP_C);
            ButtonType get_help = new ButtonType(Language.HELP_BT.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(get_help, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == get_help)
                open_url("https://github.com/BHA-Bilel/JavaFX-CONNECT4#how-to-play");
        });

        feedback.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.FEEDBACK, Language.FEEDBACK_H, Language.FEEDBACK_C);
            ButtonType give_feedback = new ButtonType(Language.FEEDBACK_BT.getValue());
            ButtonType ok = new ButtonType(Language.OK.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(give_feedback, ok);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == give_feedback)
                open_url("https://gist.github.com/BHA-Bilel/77892e183cce8aaf2408e365a66f21ad");
        });

        copyright.setOnAction(e -> {
            Alert alert = new MyAlert(AlertType.INFORMATION, Language.COPYRIGHT_T, Language.COPYRIGHT_H, Language.COPYRIGHT_C);
            ButtonType open_project = new ButtonType(Language.COPYRIGHT_BT.getValue());
            ButtonType accept = new ButtonType(Language.ACCEPT.getValue(), ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(open_project, accept);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == open_project) {
                open_url("https://github.com/BHA-Bilel/JavaFX-DOMINOS");
            }
        });

        view_score.setOnAction(e -> {
            if (roomApp == null) return;
            roomApp.viewScore();
        });

        end_game.setOnAction(e -> {
            if (roomApp == null) return;
            roomApp.endGame();
        });

        return top;
    }

    private void open_url(String url) {
        String myOS = System.getProperty("os.name").toLowerCase();
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            } else {
                Runtime runtime = Runtime.getRuntime();
                if (myOS.contains("mac")) {
                    runtime.exec("open " + url);
                } else if (myOS.contains("nix") || myOS.contains("nux")) {
                    runtime.exec("xdg-open " + url);
                }
            }
        } catch (IOException | URISyntaxException ignore) {
        }
    }

    private VBox createCenterGUI() {
        VBox center = new VBox();
        center.spacingProperty().bind(spacingProperty);
        center.styleProperty().bind(Bindings.concat("-fx-padding: ", paddingProperty.asString()));
        center.setAlignment(Pos.CENTER);
        username = new JFXTextField();
        username.promptTextProperty().bind(Language.USERNAME);
        username.setText(USERNAME);
        username.maxWidthProperty().bind(stage.widthProperty().divide(3));
        VBox username_vb = new VBox();
        username_vb.setAlignment(Pos.CENTER);
        username_vb.getChildren().add(username);
        HBox top_hbox = createTopHBox();
        VBox toggle_vb = create_toggle();
        HBox hb2 = createBottomHBox();
        hb2.visibleProperty().bind(online.selectedProperty());
        center.getChildren().addAll(username_vb, top_hbox, toggle_vb, hb2);
        return center;
    }

    private HBox createTopHBox() {
        HBox hb = new HBox();
        hb.spacingProperty().bind(spacingProperty);
        hb.styleProperty().bind(Bindings.concat("-fx-padding: ", paddingProperty.asString()));
        hb.setAlignment(Pos.CENTER);

        host = new JFXButton();
        host.textProperty().bind(Language.HOST);
        host.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        join_rooms = new JFXButton();
        join_rooms.textProperty().bind(Language.JOIN_PUB);
        join_rooms.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);

        host.setOnAction(e -> {
            if (!valid_username(username.getText()))
                return;
            HostRoom();
        });

        join_rooms.setOnAction(e -> {
            if (!valid_username(username.getText()))
                return;
            JoinPublicRooms();
        });

        hb.getChildren().addAll(host, join_rooms);
        return hb;
    }

    public VBox create_toggle() {
        VBox mode_vb = new VBox();
        mode_vb.spacingProperty().bind(spacingProperty);
        mode_vb.styleProperty().bind(Bindings.concat("-fx-padding: ", paddingProperty.asString()));
        mode_vb.setAlignment(Pos.CENTER);
        local = new JFXRadioButton();
        local.textProperty().bind(Language.LOCAL);
        local.setMinSize(JFXRadioButton.USE_PREF_SIZE, JFXRadioButton.USE_PREF_SIZE);
        online = new JFXRadioButton();
        online.textProperty().bind(Language.ONLINE);
        online.setMinSize(JFXRadioButton.USE_PREF_SIZE, JFXRadioButton.USE_PREF_SIZE);

        ToggleGroup toggleGroup = new ToggleGroup();

        local.setToggleGroup(toggleGroup);
        online.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (oldVal == local) {
                LocalClient.stop_local();
            }
            if (newVal == online) {
                setup_online_multiplayer();
            } else if (newVal == local) {
                if (oldVal == online)
                    remove_join_shortcut();
                setup_local_multiplayer();
            } else {
                disable_gui();
            }
        });
        mode_vb.getChildren().addAll(local, online);
        return mode_vb;
    }

    private HBox createBottomHBox() {
        HBox hb = new HBox();
        hb.spacingProperty().bind(spacingProperty);
        hb.styleProperty().bind(Bindings.concat("-fx-padding: ", paddingProperty.asString()));
        hb.setAlignment(Pos.CENTER);

        room_id = new JFXTextField();
        room_id.setPrefColumnCount(5);
        room_id.promptTextProperty().bind(Language.ROOM_ID);
        room_id.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);

        join_specific = new JFXButton();
        join_specific.textProperty().bind(Language.JOIN_SPEC);
        join_specific.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);

        room_id.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                try {
                    String data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if (valid_port(data) > 0)
                        room_id.setText(data);
                } catch (UnsupportedFlavorException | IOException ignore) {
                }
            }
        });

        join_specific.setOnAction(e -> {
            if (!valid_username(username.getText()))
                return;
            if (room_id.getText().isEmpty()) {
                Alert alert = new MyAlert(AlertType.ERROR, Language.RID_H1, Language.RID_C1);
                alert.show();
                return;
            }
            int roomID = valid_port(room_id.getText());
            switch (roomID) {
                case -2:
                case -3: {
                    Alert alert = new MyAlert(AlertType.ERROR, Language.RID_H2, Language.RID_C2);
                    alert.show();
                    return;
                }
                default: {
                    JoinSpecificRoom(String.valueOf(roomID));
                }
            }
        });
        hb.getChildren().addAll(room_id, join_specific);
        return hb;
    }

    public void start_migration() {
        MyAlert.show_alert("MIGRATION");
        Thread local_host = new Thread(() -> {
            try {
                roomServer = new RoomServer();
                Socket roomSocket = new Socket();
                roomSocket.connect(new InetSocketAddress("0.0.0.0", roomServer.getPort()), LOCAL_TIMEOUT);
                roomApp.send_new_room_server(RoomServer.getHostRoomInfo());
                roomApp.discardOldRoom();
                RoomApp roomApp = new RoomApp(this, null, "0.0.0.0", roomSocket, USERNAME);
                Platform.runLater(() -> setRoomApp(roomApp));
            } catch (IOException e) {
                roomServer = null;
                Platform.runLater(() -> {
                    Alert alert = new MyAlert(AlertType.WARNING, Language.HOST_H, Language.CHK_LOCAL);
                    alert.show();
                });
            } finally {
                MyAlert.hide_alert("MIGRATION");
            }
        });
        local_host.start();
    }

    public void migrate_new_host(LocalRoomInfo localRoomInfo) {
        MyAlert.show_alert("MIGRATION");
        Thread local_join = new Thread(() -> {
            boolean connected = false;
            for (String ip : localRoomInfo.ip) {
                try {
                    Socket roomSocket = new Socket();
                    roomSocket.connect(new InetSocketAddress(ip, localRoomInfo.room_id), LOCAL_TIMEOUT);
                    roomApp.discardOldRoom();
                    RoomApp roomApp = new RoomApp(this, null, ip, roomSocket, USERNAME);
                    Platform.runLater(() -> setRoomApp(roomApp));
                    connected = true;
                    break;
                } catch (IOException ignore) {
                }
            }
            if (connected) {
                MyAlert.hide_alert("MIGRATION");
            } else {
                roomServer = null;
                Platform.runLater(() -> {
                    Alert alert = new MyAlert(AlertType.WARNING, Language.HOST_H, Language.CHK_LOCAL);
                    alert.show();
                });
            }
        });
        local_join.start();
    }

    public void HostRoom() {
        MyAlert.show_alert("HOLD");
        if (online.isSelected()) {
            Thread online_host = new Thread(() -> {
                try (Socket s = new Socket(ONLINE_IP, ONLINE_GAME_PORT);
                     ObjectOutputStream objOut = new ObjectOutputStream(s.getOutputStream());
                     ObjectInputStream objIn = new ObjectInputStream(s.getInputStream())) {
                    objOut.writeInt(MainRequest.HOST.ordinal());
                    objOut.flush();
                    int response = objIn.readInt();
                    if (response > 0) {
                        try {
                            Socket roomSocket = new Socket();
                            roomSocket.connect(new InetSocketAddress(ONLINE_IP, response), ONLINE_TIMEOUT);
                            roomApp = new RoomApp(this, null, roomSocket, String.valueOf(response), username.getText());
                            Platform.runLater(() -> setRoomApp(roomApp));
                        } catch (IOException e) {
                            Platform.runLater(() -> {
                                MyAlert.hide_alert("HOLD");
                                Alert alert = new MyAlert(AlertType.WARNING, Language.HOST_H, Language.CHK_INTERNET);
                                alert.setOnHidden(hidden -> setup_online_multiplayer());
                                alert.show();
                            });
                        }
                    } else {
                        Platform.runLater(() -> {
                            MyAlert.hide_alert("HOLD");
                            Alert alert = new MyAlert(AlertType.WARNING, Language.HOST_H, Language.ALL_TAKEN);
                            alert.show();
                        });
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        MyAlert.hide_alert("HOLD");
                        Alert alert = new MyAlert(AlertType.WARNING, Language.COMM_ERROR_H, Language.CHK_INTERNET);
                        alert.setOnHidden(hidden -> setup_online_multiplayer());
                        alert.show();
                    });
                }
            });
            online_host.start();
        } else {
            Thread local_host = new Thread(() -> {
                try {
                    roomServer = new RoomServer();
                    Socket roomSocket = new Socket();
                    roomSocket.connect(new InetSocketAddress("0.0.0.0", roomServer.getPort()), LOCAL_TIMEOUT);
                    roomApp = new RoomApp(this, null, "0.0.0.0", roomSocket, username.getText());
                    Platform.runLater(() -> setRoomApp(roomApp));
                } catch (IOException e) {
                    roomServer = null;
                    Platform.runLater(() -> {
                        MyAlert.hide_alert("HOLD");
                        Alert alert = new MyAlert(AlertType.WARNING, Language.HOST_H, Language.CHK_LOCAL);
                        alert.show();
                    });
                }
            });
            local_host.start();
        }
    }

    public void JoinSpecificRoom(String roomID) {
        MyAlert.show_alert("HOLD");
        Socket roomSocket = new Socket();
        Thread online_join = new Thread(() -> {
            try {
                roomSocket.connect(new InetSocketAddress(ONLINE_IP, Integer.parseInt(roomID)), ONLINE_TIMEOUT);
                roomApp = new RoomApp(this, null, roomSocket, roomID, username.getText());
                Platform.runLater(() -> setRoomApp(roomApp));
            } catch (IOException e) {
                try {
                    roomSocket.close();
                } catch (IOException ignore) {
                }
                Platform.runLater(() -> {
                    MyAlert.hide_alert("HOLD");
                    Alert alert = new MyAlert(AlertType.WARNING, Language.ROOM_H, Language.ROOM_C);
                    alert.setOnHidden(hidden -> setup_online_multiplayer());
                    alert.show();
                });
            }
        });
        online_join.start();
    }

    public void JoinPublicRooms() {
        MyAlert.show_alert("HOLD");
        if (online.isSelected()) {
            Thread online_join = new Thread(() -> {
                try {
                    Socket joinSocket = new Socket();
                    joinSocket.connect(new InetSocketAddress(ONLINE_IP, ONLINE_GAME_PORT), ONLINE_TIMEOUT);
                    joinApp = new JoinApp(this, username.getText(), joinSocket);
                    Platform.runLater(() -> setJoinApp(joinApp));
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        MyAlert.hide_alert("HOLD");
                        Alert alert = new MyAlert(AlertType.WARNING, Language.COMM_ERROR_H, Language.CHK_INTERNET);
                        alert.setOnHidden(hidden -> setup_online_multiplayer());
                        alert.show();
                    });
                }
            });
            online_join.start();
        } else {
            Thread local_join = new Thread(() -> {
                Map<String, LocalRoomInfo> rooms = LocalClient.send_join_req();
                joinApp = new JoinApp(this, username.getText(), rooms);
                Platform.runLater(() -> setJoinApp(joinApp));
            });
            local_join.start();
        }
    }

    private void release_ressources() {
        if (joinApp != null) {
            joinApp.closeJoinApp();
        }
        if (roomApp != null) {
            roomApp.discardOldRoom();
        }
    }

    public void returnHomeApp(boolean exception) {
        if (roomServer != null) {
            roomServer.closeRoom();
            roomServer = null;
        }
        joinApp = null;
        roomApp = null;
        username.setText(USERNAME);
        root.setCenter(center);
        setup_shotcuts();
        root.requestFocus();
        MyAlert.hide_alert("HOLD");
        if (exception) {
            Alert alert = new MyAlert(AlertType.WARNING, Language.COMM_ERROR_H, Language.COMM_ERROR_C);
            alert.setOnHidden(hidden -> {
                if (online.isSelected()) setup_online_multiplayer();
            });
            alert.show();
        }
    }

    private void setup_shotcuts() {
        if (main_shortcuts_activated) return;
        main_shortcuts_activated = true;
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), host::fire);
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN), join_rooms::fire);
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), () -> online.setSelected(true));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN), () -> local.setSelected(true));
    }

    private void add_join_shortcut() {
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), join_specific::fire);
    }

    private void remove_join_shortcut() {
        stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
    }

    private void remove_shortcuts() {
        if (!main_shortcuts_activated) return;
        main_shortcuts_activated = false;
        stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN));
        stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        stage.getScene().getAccelerators().remove(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
    }

    public void setJoinApp(JoinApp joinApp) {
        this.joinApp = joinApp;
        root.setCenter(joinApp);
        stage.getScene().getStylesheets().clear();
        stage.getScene().getStylesheets().add(MainApp.class.getResource(CURRENT_THEME).toExternalForm());
        remove_shortcuts();
        joinApp.setup_shortcuts();
        root.requestFocus();
        MyAlert.hide_alert("HOLD");
    }

    public void setRoomApp(RoomApp roomApp) {
        if (online.isSelected()) {
            update_property("PREF_MODE", "ONLINE");
        } else {
            update_property("PREF_MODE", "LOCAL");
        }
        this.roomApp = roomApp;
        root.setCenter(roomApp);
        remove_shortcuts();
        roomApp.setup_shortcuts();
        root.requestFocus();
        MyAlert.hide_alert("HOLD");
    }

    public void disable_game_menu() {
        game_menu.setDisable(true);
    }

    public void setGameApp(GameApp gameApp) {
        root.setCenter(gameApp);
        roomApp.partial_shortcuts_remove();
        game_menu.setDisable(false);
        MyAlert.hide_alert("HOLD");
    }

}
