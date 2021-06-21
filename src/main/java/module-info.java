module MainModule {
    requires javafx.controls;
    requires com.jfoenix;
    requires org.controlsfx.controls;
    requires java.desktop;

    exports shared;
    exports bg.connectFour.server.local;
    exports bg.connectFour.server.room;
    exports bg.connectFour.lang;
    exports bg.connectFour.room;
    exports bg.connectFour.game;
    exports bg.connectFour;
}