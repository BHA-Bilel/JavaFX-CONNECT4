package shared;

import java.io.Serializable;
import java.util.List;

public class LocalRoomInfo implements Serializable {

    public final int room_id;
    public int room_players;
    public String host_name;
    public List<String> ip;

    public LocalRoomInfo(int room_id, String host_name, int room_players) {
        this.room_id = room_id;
        this.host_name = host_name;
        this.room_players = room_players;
    }

    public LocalRoomInfo(List<String> ip, int room_id) {
        this.ip = ip;
        this.room_id = room_id;
    }
}
