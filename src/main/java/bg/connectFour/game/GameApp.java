package bg.connectFour.game;

import bg.connectFour.lang.Language;
import bg.connectFour.popup.MyAlert;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameApp extends GridPane {

    private int playerID;
    private final GameClient gameClient;
    private boolean yourTurn, playable = false;
    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private final Disc[][] board;
    private final List<Combo> combos = new ArrayList<>();
    private final String yourName, opName;
    public int parties_won, parties_lost, drawCount;
    private MyAlert results_alert;

    public GameApp(Socket gameSocket, String name, String opName) {
        this.yourName = name;
        this.opName = opName;
        gameClient = new GameClient(gameSocket);
        gameClient.handShake();
        board = new Disc[COLUMNS][ROWS];
        setAlignment(Pos.CENTER);
        createGUI();
    }

    private void createGUI() {
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Disc disk = new Disc(this, x, y);
                GridPane.setHalignment(disk, HPos.CENTER);
                GridPane.setFillHeight(disk, true);
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

    public void waitForYourTurn() {
        Thread t = new Thread(() -> {
            int[] coor = gameClient.receive();
            board[coor[0]][coor[1]].play();
        });
        t.start();
    }

    public void startNewGame(boolean youWon) {
        Platform.runLater(() -> {
            for (int x = 0; x < COLUMNS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    board[x][y].reset();
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
                if (board[x][y].getValue().equals(""))
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
                        new KeyFrame(Duration.seconds(1), new KeyValue(disc.getCircle().fillProperty(), Paint.valueOf("#FFD400"))));
            }
            timeLine.setAutoReverse(true);
            timeLine.setCycleCount(4);
            timeLine.play();
            timeLine.setOnFinished(e -> {
                showResults(false);
                startNewGame(youWon);
            });
        });
    }

    public void showResults(boolean shortcut) {
        if (results_alert != null && results_alert.isShowing())
            if (shortcut) return;
            else results_alert.close();
        Platform.runLater(() -> {
            String text = yourName + " : " + parties_won + "\n";
            text += opName + " : " + parties_lost + "\n";
            text += Language.DRAWS.get() + drawCount;
            if (results_alert == null) results_alert = new MyAlert(Alert.AlertType.INFORMATION, Language.GR_H, text);
            else results_alert.update(text);
            results_alert.show();
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

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public void sendCoor(int x, int y) {
        gameClient.sendCoor(x, y);
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
