package bg.connectFour.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class GameApp extends GridPane {
    private int playerID;
    private final GameClient gameClient;

    private final Handler handler;
    private boolean yourTurn, playable = false;
    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private Disc[][] board;
    private final List<Combo> combos = new ArrayList<>();
    private final String yourName, opName;
    public int parties_won, parties_lost, drawCount;

    public GameApp(Socket gameSocket, String name, String opName) {
        this.yourName = name;
        this.opName = opName;
        gameClient = new GameClient(gameSocket);
        gameClient.handShake();
//        setPrefSize(700, 600);
        handler = new Handler(this);
        getChildren().clear();
        getChildren().add(board());
        board = new Disc[COLUMNS][ROWS];
        setAlignment(Pos.CENTER);
//        setHgap(10);
//        setVgap(10);
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Disc disk = new Disc(handler, x, y);
                GridPane.setHalignment(disk, HPos.CENTER);
                add(disk, x, y);
                board[x][y] = disk;
            }
        }

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 6; i++) {
                combos.add(new Combo(new Disc[]{board[j][i], board[j + 1][i], board[j + 2][i], board[j + 3][i]}));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                combos.add(new Combo(new Disc[]{board[j][i], board[j][i + 1], board[j][i + 2], board[j][i + 3]}));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 3; j < 7; j++) {
                combos.add(new Combo(
                        new Disc[]{board[j][i], board[j - 1][i + 1], board[j - 2][i + 2], board[j - 3][i + 3]}));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 3; j > -1; j--) {
                combos.add(new Combo(
                        new Disc[]{board[j][i], board[j + 1][i + 1], board[j + 2][i + 2], board[j + 3][i + 3]}));
            }
        }
    }

    private Shape board() {
        Shape shape = new Rectangle();

        Light.Distant light = new Light.Distant();
        light.setAzimuth(45.0);
        light.setElevation(30.0);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);

        shape.setEffect(lighting);
        shape.setFill(Color.BLUE);

        return shape;
    }

    public void waitForYourTurn() {
        Thread t = new Thread(() -> {
            int[] coor = gameClient.receive();
            board[coor[0]][coor[1]].play();
        });
        t.start();
    }

    public void startNewGame(boolean youWon) {
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().add(board());
            board = new Disc[COLUMNS][ROWS];
            combos.clear();
            for (int x = 0; x < COLUMNS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    Disc circle = new Disc(handler, x, y);
                    GridPane.setHalignment(circle, HPos.CENTER);
                    add(circle, x, y);
                    board[x][y] = circle;
                }
            }

            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < 6; i++) {
                    combos.add(new Combo(
                            new Disc[]{board[j][i], board[j + 1][i], board[j + 2][i], board[j + 3][i]}));
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    combos.add(new Combo(
                            new Disc[]{board[j][i], board[j][i + 1], board[j][i + 2], board[j][i + 3]}));
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 3; j < 7; j++) {
                    combos.add(new Combo(new Disc[]{board[j][i], board[j - 1][i + 1], board[j - 2][i + 2],
                            board[j - 3][i + 3]}));
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 3; j > -1; j--) {
                    combos.add(new Combo(new Disc[]{board[j][i], board[j + 1][i + 1], board[j + 2][i + 2],
                            board[j + 3][i + 3]}));
                }
            }
            if (!youWon) {
                waitForYourTurn();
            }
            setYourTurn(youWon);
            setPlayable(true);
        });
    }

    public boolean isFull() {
        for (int x = COLUMNS - 1; x >= 0; x--) {
            for (int y = ROWS - 1; y >= 0; y--) {
                if (board[x][y].getValue() == "")
                    return false;
            }
        }
        return true;
    }

    public boolean isLegal(int x, int y) {
        return y == 5 || !board[x][y + 1].getValue().isEmpty();
    }

    public void blink(List<Disc> Discs, boolean youWon) {
        Platform.runLater(() -> {
            Timeline timeLine = new Timeline();
            for (Disc disc : Discs) {
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1), new KeyValue(disc.getCircle().fillProperty(), Color.GREEN)));
            }
            timeLine.setAutoReverse(true);
            timeLine.setCycleCount(4);
            timeLine.play();
            timeLine.setOnFinished(e -> {
                handler.getGame().showResults();
                startNewGame(youWon);
            });
        });
    }

    public void showResults() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game");
            alert.setHeaderText("Results");
            String text = yourName + " : " + parties_won + "\n";
            text += opName + " : " + parties_lost + "\n";
            text += "Draws : " + drawCount;
            alert.setContentText(text);
            alert.show();
        });
    }

    public synchronized void closeGameApp() {
        gameClient.closeConn();
        Platform.runLater(() -> getChildren().clear());
    }

    class GameClient {
        private Socket gameSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public GameClient(Socket gameSocket) {
            try {
                this.gameSocket = gameSocket;
                dataOut = new DataOutputStream(this.gameSocket.getOutputStream());
                dataIn = new DataInputStream(this.gameSocket.getInputStream());
            } catch (IOException ignore) {
            }
        }

        public void closeConn() {
            try {
                dataOut.close();
                dataIn.close();
                gameSocket.close();
            } catch (IOException ignore) {
            }
        }

        public void handShake() {
            try {
                playerID = dataIn.readInt();
                if (playerID == 1) {
                    yourTurn = true;
                    Thread t = new Thread(() -> {
                        try {
                            playable = dataIn.readBoolean();
                        } catch (IOException ignore) {
                        }
                    });
                    t.start();

                } else {
                    yourTurn = false;
                    playable = true;
                    waitForYourTurn();
                }
            } catch (IOException ignore) {
            }
        }

        public void sendCoor(int x, int y) {
            try {
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.flush();
            } catch (IOException ignore) {
            }
        }

        public int[] receive() {
            int[] coor = new int[2];
            try {
                coor[0] = dataIn.readInt();
                coor[1] = dataIn.readInt();
            } catch (IOException ignore) {
            }
            return coor;
        }

    }

    // GETTERS SETTERS

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public GameClient getCSC() {
        return gameClient;
    }

    public List<Combo> getCombo() {
        return combos;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public int getPlayerID() {
        return playerID;
    }

}
