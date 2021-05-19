package bg.connectFour.game;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Disc extends StackPane {
    private Circle circle;
    private static final double TILE_SIZE = 80;
    public int x;
    public int y;
    private String value;
    private Handler handler;

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

    public Disc(Handler handler, int x, int y) {
        Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
        rect.setFill(Paint.valueOf("00BFFF"));
        circle = new Circle(TILE_SIZE / 3);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.WHITE);
        setAlignment(Pos.CENTER);

        getChildren().addAll(rect, circle);
        this.handler = handler;
        this.x = x;
        this.y = y;
        value = "";
        setOnMouseClicked(event -> {
            if (!this.handler.getGame().isPlayable() || !this.handler.getGame().isYourTurn() || !value.isEmpty()
                    || !this.handler.getGame().isLegal(x, y) || event.getButton() != MouseButton.PRIMARY)
                return;
            play();
        });
    }

    public boolean NothingHappened(List<Combo> combos) {
        List<Disc> list = new ArrayList<>();
        boolean youWon = false;
        for (Combo c : combos) {
            if (c.isComplete()) {
                handler.getGame().setPlayable(false);
                youWon = c.getDiscs()[0].value == "red";
                if (youWon)
                    handler.getGame().parties_won++;
                else
                    handler.getGame().parties_lost++;
                for (int i = 0; i < c.getDiscs().length; i++) {
                    Disc item = c.getDiscs()[i];
                    if (!list.contains(item))
                        list.add(item);
                }
            }
        }
        if (!list.isEmpty())
            handler.getGame().blink(list, youWon);
        if (handler.getGame().isFull()) {
            handler.getGame().setPlayable(false);
            handler.getGame().startNewGame(
                    handler.getGame().getDrawCount() % 2 == (handler.getGame().getPlayerID() - 1));
            handler.getGame().drawCount++;
            handler.getGame().showResults();
        }
        return handler.getGame().isPlayable();
    }

    public void play() {
        if (handler.getGame().isYourTurn()) {
            value = "red";
            circle.setFill(Color.RED);
            handler.getGame().setYourTurn(false);
            handler.getGame().getCSC().sendCoor(x, y);
            if (NothingHappened(handler.getGame().getCombo())) {
                handler.getGame().waitForYourTurn();
            }
        } else {
            value = "yellow";
            circle.setFill(Color.YELLOW);
            if (NothingHappened(handler.getGame().getCombo())) {
                handler.getGame().setYourTurn(true);
            }
        }
    }

    // GETTERS SETTERS

    public String getValue() {
        return value;
    }

    public Circle getCircle() {
        return circle;
    }

}
