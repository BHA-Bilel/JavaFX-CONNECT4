package gfx;

import javafx.scene.transform.Scale;

import java.awt.*;

public class Assets {


    private static double width, height;
    public static double scale_width, scale_height, unscale_width, unscale_height;
    public static Scale scale;

    public static double mainApp_width = 0, mainApp_height = 0;
    public static double joinApp_width = 0, joinApp_height = 0;
    public static double roomApp_width = 0, roomApp_height = 0;
    public static double gameApp_width = 0, gameApp_height = 0;

    public static void init_scale() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.getWidth();
        height = screenSize.getHeight();
        scale_width = scale_width(1);
        scale_height = scale_height(1);
        unscale_width = unscale_width(1);
        unscale_height = unscale_height(1);
        scale = new Scale(scale_width, scale_height, 0, 0);
    }

    public static double scale_width(double old_width) {
        return old_width * (width / 1920.0);
    }

    public static double scale_height(double old_height) {
        return old_height * (height / 1080.0);
    }

    public static double unscale_width(double old_width) {
        return old_width * (1920.0 / width);
    }

    public static double unscale_height(double old_height) {
        return old_height * (1080.0 / height);
    }

}