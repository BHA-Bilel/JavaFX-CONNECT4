package bg.connectFour.server.room;

import bg.connectFour.MainApp;
import bg.connectFour.server.chat.ChatServer;
import bg.connectFour.server.game.GameServer;
import bg.connectFour.server.game.CONNECT4_GameServer;
import bg.connectFour.server.local.LocalClient;
import javafx.application.Platform;
import shared.RoomComm;
import shared.LocalRoomInfo;
import shared.RoomMsg;
import shared.RoomPosition;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RoomServer {

    private static Map<Integer, Client> clients;
    private ServerSocket roomServer;
    private final ChatServer chatServer;
    private GameServer gameServer;
    private int playerID = 1;
    private volatile boolean roomClosed = false;
    private final ReentrantLock place_mutex = new ReentrantLock();
    protected final ReentrantLock name_mutex = new ReentrantLock();
    private RoomPosition current_pos;
    protected RoomMsg game_started;
    private final Runnable connectRunnable;
    private Thread connectThread;
    private static int room_port;
    protected static volatile boolean migration_finished = false;

    public RoomServer() throws IOException {
        clients = new ConcurrentHashMap<>();
        roomServer = new ServerSocket(0);
        room_port = roomServer.getLocalPort();
        chatServer = new ChatServer();
        connectRunnable = () -> {
            try {
                while (clients.size() < MainApp.BG_GAME.players) {
                    Socket client_socket = roomServer.accept();
                    Client new_client = new Client(RoomServer.this, client_socket);
                    place_mutex.lock();
                    new_client.position = getEmptyPosition();
                    new_client.id = playerID;
                    playerID++;
                    int players_before_you = clients.size();
                    clients.put(new_client.id, new_client);
                    place_mutex.unlock();
                    new_client.handShake(chatServer.getPort(), players_before_you);
                    MeetNewClient(new_client);
                    chatServer.acceptNewclient(new_client.id);
                    new_client.start();
                    LocalClient.hosting(true);
                }
                try {
                    roomServer.close();
                } catch (IOException ignore) {
                }
            } catch (IOException e) {
                closeRoom();
            } finally {
                LocalClient.hosting(false);
            }
        };
        waitForPlayers(true);
    }

    protected RoomPosition getEmptyPosition() {
        List<RoomPosition> avail_pos = new ArrayList<>(Arrays.asList(RoomPosition.values()));
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            Client c = IdClientEntry.getValue();
            avail_pos.remove(c.position);
        }
        return avail_pos.get(0);
    }

    public int getPort() {
        return room_port;
    }

    public static LocalRoomInfo getHostRoomInfo() throws SocketException {
        return new LocalRoomInfo(LocalClient.get_lan_ip(), room_port);
    }

    public static LocalRoomInfo getJoinRoomInfo() {
        Client host = getHost();
        if (host != null)
            return new LocalRoomInfo(room_port, host.name, clients.size());
        else return null;
    }

    public void waitForPlayers(boolean initial_wait) {
        if (connectThread == null || !connectThread.isAlive()) {
            try {
                if (!initial_wait) {
                    roomServer.close();
                    roomServer = new ServerSocket(room_port);
                    LocalClient.hosting(true);
                }
                connectThread = new Thread(connectRunnable);
                connectThread.start();
            } catch (IOException e) {
                closeRoom();
            }
        }
    }

    public void startGame() throws IOException {
        gameServer = new CONNECT4_GameServer(RoomServer.this);
        Object[] adt_data = gameServer.getPort();
        current_pos = RoomPosition.BOTTOM;
        game_started = new RoomMsg(RoomComm.GAME_STARTED, adt_data);
        gameServer.acceptConnection();
    }

    private void MeetNewClient(Client new_client) {
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            Client existing_client = IdClientEntry.getValue();
            if (new_client.id != existing_client.id) {
                existing_client.meet(new_client);
            }
        }
    }

    protected void migration_server_created(Client new_host, RoomMsg msg) {
        diffuseClientMsg(new_host, msg);
        closeRoom();
        RoomServer.migration_finished = true;
    }

    public void at_migration_finish(MainApp mainApp, boolean exception) {
        new Thread(() -> {
            while (!migration_finished) Thread.onSpinWait();
            migration_finished = false;
            Platform.runLater(() -> mainApp.returnHomeApp(exception));
        }).start();
    }

    public synchronized void closeRoom() {
        if (roomClosed) return;
        roomClosed = true;
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            IdClientEntry.getValue().closeConnection();
        }
        try {
            roomServer.close();
        } catch (IOException ignore) {
        }
        if (chatServer != null)
            chatServer.closeChat();
        if (gameServer != null)
            gameServer.closeGame();
    }

    public void diffuseClientMsg(Client client, RoomMsg msg) {
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            Client c = IdClientEntry.getValue();
            if (!c.equals(client))
                c.sendMsg(msg);
        }
    }

    public boolean all_clients_left() {
        return clients.isEmpty();
    }

    public void clientLeft(Client left) {
        Client host = getHost();
        Client removed = clients.remove(left.id);
        if (removed == null) return;
        if (all_clients_left()) {
            closeRoom();
        } else {
            left.closeConnection();
            RoomMsg msg = new RoomMsg(left.id, RoomComm.LEFT);
            diffuseMsg(msg);
            if (gameServer != null) {
                endGame();
            }
            if (removed.id != host.id) waitForPlayers(false);
        }
    }

    public void clientKicked(int id) {
        Client kicked = clients.remove(id);
        if (kicked == null) return;
        kicked.closeConnection();
        if (gameServer != null) {
            endGame();
        }
        waitForPlayers(false);
    }

    public int getUniqueName(int id, String nameToLowerCase, int duplicates) {
        String search = nameToLowerCase;
        if (duplicates > 0) {
            search += " " + duplicates;
        }
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            Client client = IdClientEntry.getValue();
            if (client.id != id && client.name.toLowerCase().equals(search)) {
                return getUniqueName(id, nameToLowerCase, duplicates + 1);
            }
        }
        return duplicates;
    }

    @Override
    public boolean equals(Object obj) {
        if (RoomServer.this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        RoomServer room = (RoomServer) obj;
        return room.getPort() == this.getPort();
    }

    public void diffuseMsg(RoomMsg msg) {
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            IdClientEntry.getValue().sendMsg(msg);
        }
    }

    public static Client getHost() {
        Iterator<Entry<Integer, Client>> itr = clients.entrySet().iterator();
        Client host = null;
        while (itr.hasNext()) {
            Client potentialHost = itr.next().getValue();
            if (host == null || potentialHost.id < host.id) {
                host = potentialHost;
            }
        }
        return host;
    }

    protected void take_empty_place(Client client, Object[] adt_data) {
        RoomPosition new_pos = RoomPosition.values()[(int) adt_data[0]];
        place_mutex.lock();
        for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
            Client c = IdClientEntry.getValue();
            if (c.position == new_pos) {
                place_mutex.unlock();
                return;
            }
        }
        client.position = new_pos;
        RoomMsg msg = new RoomMsg(client.id, RoomComm.TOOK_EMPTY_PLACE, adt_data);
        diffuseMsg(msg);
        place_mutex.unlock();
    }

    public void NotifyNextPlayer() {
        Client curr_client = null;
        while (curr_client == null && current_pos != null) {
            for (Entry<Integer, Client> IdClientEntry : clients.entrySet()) {
                Client client = IdClientEntry.getValue();
                if (client.position == current_pos) {
                    curr_client = client;
                    break;
                }
            }
            current_pos = current_pos.nextPlayerToNotify();
        }
        if (curr_client != null)
            curr_client.sendMsg(game_started);
        else
            endGame();
    }

    public synchronized void endGame() {
        if (gameServer == null) return;
        RoomMsg msg = new RoomMsg(RoomComm.GAME_ENDED);
        diffuseMsg(msg);
        gameServer.closeGame();
        gameServer = null;
        waitForPlayers(false);
    }

}
