package bg.connectFour.server.room;

import shared.RoomComm;
import shared.RoomMsg;
import shared.RoomPosition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread {
    protected int id;
    protected String name;
    protected RoomPosition position;

    private final Socket socket;
    private final ObjectInputStream objIn;
    private final ObjectOutputStream objOut;
    private final RoomServer room;
    private volatile boolean closed = false;

    public Client(RoomServer room, Socket socket) throws IOException {
        this.room = room;
        this.socket = socket;
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    public void handShake(int chatPort, int players_count) {
        try {
            objOut.writeInt(id);
            objOut.writeInt(chatPort);
            objOut.writeInt(players_count);
            objOut.writeInt(position.ordinal());
            objOut.flush();
            name = objIn.readUTF();
            room.name_mutex.lock();
            int dup = room.getUniqueName(id, name.toLowerCase(), 0);
            if (dup > 0)
                name += " " + dup;
            room.name_mutex.unlock();

            objOut.writeUTF(name);
            objOut.flush();
        } catch (IOException e) {
            room.clientLeft(this);
        }
    }

    public void meet(Client newClient) {
        RoomMsg msg = new RoomMsg(newClient.id, RoomComm.JOINED, new Object[]{newClient.name, newClient.position.ordinal()});
        sendMsg(msg);
        msg = new RoomMsg(id, RoomComm.JOINED, new Object[]{name, position.ordinal()});
        newClient.sendMsg(msg);
    }

    public void sendMsg(RoomMsg msg) {
        try {
            objOut.writeObject(msg);
            objOut.flush();
        } catch (IOException e) {
            room.clientLeft(this);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                RoomMsg msg = (RoomMsg) objIn.readObject();
                RoomMsg resp;
                switch (RoomComm.values()[msg.comm]) {
                    case START_GAME: {
                        resp = new RoomMsg(RoomComm.GAME_STARTING);
                        room.diffuseMsg(resp);
                        room.startGame();
                        break;
                    }
                    case REQUEST_CHANGE_NAME: {
                        room.name_mutex.lock();
                        int dup = room.getUniqueName(id, ((String) msg.adt_data[0]).toLowerCase(), 0);
                        if (dup > 0)
                            name = msg.adt_data[0] + " " + dup;
                        else
                            name = (String) msg.adt_data[0];
                        resp = new RoomMsg(id, RoomComm.CHANGED_NAME, new Object[]{name});
                        room.diffuseMsg(resp);
                        room.name_mutex.unlock();
                        break;
                    }
                    case REQUEST_KICK: {
                        resp = new RoomMsg((int) msg.adt_data[0], RoomComm.KICKED);
                        room.diffuseMsg(resp);
                        room.clientKicked((int) msg.adt_data[0]);
                        break;
                    }
                    case END_GAME: {
                        room.endGame();
                        break;
                    }
                    case TAKE_EMPTY_PLACE: {
                        room.take_empty_place(this, msg.adt_data);
                        break;
                    }
                    case MIGRATION: {
                        room.migration_server_created(this, msg);
                        return;
                    }
                    default: {
                        room.diffuseClientMsg(this, msg);
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            room.clientLeft(this);
        }
    }

    public synchronized void closeConnection() {
        if (closed) return;
        closed = true;
        try {
            objIn.close();
            objOut.close();
            socket.close();
        } catch (IOException ignore) {
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Client client = (Client) obj;
        return client.id == this.id;
    }

}
