package bg.connectFour.game;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Disc extends StackPane {
    private final Circle circle;
    public int x;
    public int y;
    private String value;
    private final GameApp gameApp;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Disc disc = (Disc) obj;
        return disc.x == x && disc.y == y;
    }

    public Disc(GameApp gameApp, int x, int y) {
        this.gameApp = gameApp;
        this.x = x;
        this.y = y;
        value = "";
        Region border = new Region();
        border.setBackground(new Background(new BackgroundFill(Paint.valueOf("#0089C0"), null, null)));
        border.prefWidthProperty().bind(gameApp.heightProperty().divide(7));
        border.prefHeightProperty().bind(gameApp.heightProperty().divide(6));
        circle = new Circle();
        circle.radiusProperty().bind(border.heightProperty().divide(3));
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.WHITE);
        setAlignment(Pos.CENTER);
        getChildren().addAll(border, circle);
        setOnMouseClicked(event -> {
            if (!gameApp.isPlayable() || !gameApp.isYourTurn() || !value.isEmpty()
                    || !gameApp.isLegal(x, y) || event.getButton() != MouseButton.PRIMARY)
                return;
            play();
        });
    }

    public boolean NothingHappened(List<Combo> combos) {
        List<Disc> list = new ArrayList<>();
        boolean youWon = false;
        for (Combo c : combos) {
            if (c.isComplete()) {
                gameApp.setPlayable(false);
                youWon = c.getDiscs()[0].value.equals("red");
                if (youWon)
                    gameApp.parties_won++;
                else
                    gameApp.parties_lost++;
                for (int i = 0; i < c.getDiscs().length; i++) {
                    Disc item = c.getDiscs()[i];
                    if (!list.contains(item))
                        list.add(item);
                }
            }
        }
        if (!list.isEmpty())
            gameApp.blink(list, youWon);
        if (gameApp.isFull()) {
            gameApp.setPlayable(false);
            gameApp.startNewGame(
                    gameApp.getDrawCount() % 2 == (gameApp.getPlayerID() - 1));
            gameApp.drawCount++;
            gameApp.showResults(false);
        }
        return gameApp.isPlayable();
    }

    public void play() {
        if (gameApp.isYourTurn()) {
            value = "red";
            circle.setFill(Paint.valueOf("#00A468"));
            gameApp.setYourTurn(false);
            gameApp.sendCoor(x, y);
            if (NothingHappened(gameApp.getCombo())) {
                gameApp.waitForYourTurn();
            }
        } else {
            value = "yellow";
            circle.setFill(Paint.valueOf("#C50030"));
            if (NothingHappened(gameApp.getCombo())) {
                gameApp.setYourTurn(true);
            }
        }
    }

    public void reset() {
        value = "";
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.WHITE);
    }

    public String getValue() {
        return value;
    }

    public Circle getCircle() {
        return circle;
    }

}
