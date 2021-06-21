package bg.connectFour.gfx;

import java.awt.*;

public class Assets {

    public static double height, width;

    public static void init_resolution() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        height = screenSize.getHeight();
        width = screenSize.getWidth();
    }
}