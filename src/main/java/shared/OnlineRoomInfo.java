package shared;

import java.io.Serializable;

public class OnlineRoomInfo implements Serializable {

	public final int room_id;
	public int room_players;
	public String host_name;

	public OnlineRoomInfo(int room_id, String host_name, int room_players) {
		this.room_id = room_id;
		this.host_name = host_name;
		this.room_players = room_players;
	}

}
