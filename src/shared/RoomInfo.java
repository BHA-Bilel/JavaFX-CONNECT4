package shared;

import java.io.Serial;
import java.io.Serializable;

public class RoomInfo implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 544691827159949942L;
    public final int room_id;
    public final String host_name;
    public final int room_players;

    public RoomInfo(int room_id, String host_name, int room_players) {
        this.room_id = room_id;
        this.host_name = host_name;
        this.room_players = room_players;
    }

}
