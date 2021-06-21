package bg.connectFour.server.local;

import bg.connectFour.MainApp;
import bg.connectFour.server.room.RoomServer;
import shared.LocalRoomInfo;

import java.io.*;
import java.net.*;
import java.util.*;

public class LocalClient {

    private static final int bufferSize = 1024;
    private static int joiners_port, hosters_port;
    private static InetAddress local_group;
    private static MulticastSocket joiners_socket, hosters_socket;
    private static List<InetAddress> local_addr;
    private static volatile boolean hosting = false, in_local_mode = false;

    public static boolean init_local(String multicast_ip, int joiners_port, int hosters_port) {
        in_local_mode = true;
        LocalClient.joiners_port = joiners_port;
        LocalClient.hosters_port = hosters_port;
        try {
            local_group = InetAddress.getByName(multicast_ip);
            joiners_socket = new MulticastSocket(joiners_port);
            hosters_socket = new MulticastSocket(hosters_port);
            hosters_socket.setSoTimeout(MainApp.LOCAL_TIMEOUT);
            setup_lan_addr();
            boolean success = receive_join_req();
            if (!success) return false;
            success = setup_hosters();
            if (!success) return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static void setup_lan_addr() throws SocketException {
        local_addr = new ArrayList<>();
        for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
             interfaces.hasMoreElements(); ) {
            final NetworkInterface cur = interfaces.nextElement();
            for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                final InetAddress inet_addr = addr.getAddress();
                if (!(inet_addr instanceof Inet4Address)) continue;
                String ip = inet_addr.getHostAddress();
                if (ip.startsWith("10.") || ip.startsWith("172.") || ip.startsWith("192.168"))
                    local_addr.add(inet_addr);
            }
        }
    }

    public static List<String> get_lan_ip() {
        List<String> temp = new ArrayList<>();
        for (InetAddress addr : local_addr) {
            temp.add(addr.getHostAddress());
        }
        return temp;
    }

    public static void stop_local() {
        in_local_mode = false;
        local_group = null;
        if (joiners_socket != null) {
            joiners_socket.close();
            hosters_socket.close();
        }
        joiners_socket = null;
        hosters_socket = null;
    }

    private static void empty_input_stream(MulticastSocket socket, int port) {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, local_group, port);
        int so_timeout = 100;
        try {
            so_timeout = socket.getSoTimeout();
            socket.setSoTimeout(1);
            while (true) socket.receive(packet);
        } catch (IOException ignore) {
        } finally {
            try {
                socket.setSoTimeout(so_timeout);
            } catch (IOException ignore) {
            }
        }
    }

    private static boolean receive_join_req() {
        Sender sender = null;
        for (InetAddress addr : local_addr) {
            try {
                joiners_socket.setInterface(addr);
                joiners_socket.joinGroup(local_group);
                sender = new Sender();
            } catch (IOException e) {
                return false;
            }
        }
        Sender finalSender = sender;
        Thread receive = new Thread(() -> {
            byte[] buffer = new byte[0];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, local_group, joiners_port);
            while (in_local_mode) {
                try {
                    joiners_socket.receive(packet);
                    if (hosting) {
                        LocalRoomInfo local_room = RoomServer.getJoinRoomInfo();
                        Thread send = new Thread(() -> finalSender.send(local_room));
                        send.start();
                    }
                } catch (IOException ignore) {
                }
            }
        });
        receive.start();
        return true;
    }

    private static boolean setup_hosters() {
        for (InetAddress addr : local_addr) {
            try {
                hosters_socket.setInterface(addr);
                hosters_socket.joinGroup(local_group);
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static void hosting(boolean hosting) {
        LocalClient.hosting = hosting;
    }

    public static Map<String, LocalRoomInfo> send_join_req() {
        empty_input_stream(hosters_socket, hosters_port);
        byte[] data = new byte[0];
        Map<String, LocalRoomInfo> local_rooms = new HashMap<>();
        try {
            joiners_socket.send(new DatagramPacket(data, data.length, local_group, joiners_port));
        } catch (IOException ignore) {
        }
        boolean available = true;
        int i = 0;
        try {
            List<String> temp = new ArrayList<>();
            while (available) {
                byte[] buffer = new byte[bufferSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, local_group, hosters_port);
                hosters_socket.receive(packet);
                String src_addr = packet.getAddress().getHostAddress();
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);

                available = bais.available() > 0;
                LocalRoomInfo info = (LocalRoomInfo) ois.readObject();
                if (!temp.contains(info.host_name + info.room_id)) {
                    temp.add(info.host_name + info.room_id);
                    local_rooms.put(i + src_addr, info);
                    i++;
                }
            }
        } catch (IOException | ClassNotFoundException ignore) {
        }
        return local_rooms;
    }

    static class Sender {
        static ByteArrayOutputStream baos;
        static ObjectOutputStream oos;

        public Sender() throws IOException {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
        }

        public void send(LocalRoomInfo room_info) {
            try {
                if (room_info == null) return;
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(room_info);
                byte[] data = baos.toByteArray();
                hosters_socket.send(new DatagramPacket(data, data.length, local_group, hosters_port));
            } catch (IOException ignore) {
            }
        }
    }
}
