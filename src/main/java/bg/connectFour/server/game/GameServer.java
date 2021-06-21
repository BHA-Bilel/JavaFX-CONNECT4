package bg.connectFour.server.game;

import bg.connectFour.server.room.RoomServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class GameServer {

    protected ServerSocket gameServer;
    protected ArrayList<Socket> sockets = new ArrayList<>();
    protected RoomServer room;

    public GameServer(RoomServer room) throws IOException {
        this.room = room;
        gameServer = new ServerSocket(0);
    }

    public Object[] getPort() {
        return new Object[]{gameServer.getLocalPort()};
    }

    public abstract void acceptConnection();

    public synchronized void closeGame() {
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException ignore) {
            }
        }
        try {
            gameServer.close();
        } catch (IOException ignore) {
        }
    }
}
