package bg.connectFour.server.chat;

import shared.ChatMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final ServerSocket chatServer;
    private final List<ChatClient> clients;
    private volatile boolean closed = false;

    public ChatServer() throws IOException {
        chatServer = new ServerSocket(0);
        clients = new ArrayList<>();
    }

    public void acceptNewclient(int id) {
        try {
            Socket chatSocket = chatServer.accept();
            ChatClient client = new ChatClient(id, chatSocket);
            client.start();
            clients.add(client);
        } catch (IOException ignore) {
        }
    }

    class ChatClient extends Thread {

        private final int id;
        private Socket chatSocket;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;

        public ChatClient(int id, Socket chatSocket) {
            this.id = id;
            try {
                this.chatSocket = chatSocket;
                objOut = new ObjectOutputStream(chatSocket.getOutputStream());
                objIn = new ObjectInputStream(chatSocket.getInputStream());
            } catch (IOException e) {
                closeConn();
            }

        }

        @Override
        public void run() {
            try {
                while (true) {
                    ChatMsg msg = (ChatMsg) objIn.readObject();
                    diffuseClientMsg(this, msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                closeConn();
            }
        }

        private synchronized void closeConn() {
            if (closed) return;
            closed = true;
            try {
                objOut.close();
                objIn.close();
                chatSocket.close();
            } catch (IOException ignore) {
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (ChatClient.this == obj)
                return true;
            if (obj == null)
                return false;
            if (ChatClient.this.getClass() != obj.getClass())
                return false;
            ChatClient client = (ChatClient) obj;
            return client.id == ChatClient.this.id;
        }

        public void sendMsg(ChatMsg msg) {
            try {
                objOut.writeObject(msg);
                objOut.flush();
            } catch (IOException e) {
                closeConn();
            }
        }
    }

    public int getPort() {
        return chatServer.getLocalPort();
    }

    public void diffuseClientMsg(ChatClient client, ChatMsg msg) {
        for (ChatClient c : clients) {
            if (c != client)
                c.sendMsg(msg);
        }
    }

    public void closeChat() {
        for (ChatClient client : clients) {
            client.closeConn();
        }
        try {
            chatServer.close();
        } catch (IOException ignore) {
        }
    }
}
