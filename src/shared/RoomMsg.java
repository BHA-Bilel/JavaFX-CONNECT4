package shared;

import java.io.Serializable;

public class RoomMsg implements Serializable {

	private static final long serialVersionUID = 1138364712431492550L;
	public int from;
	public int comm;
	public Object[] adt_data;

	/** For START_GAME, CLOSE_CONN */
	public RoomMsg(RoomComm comm) {
		this.comm = comm.ordinal();
	}

	/** For LEFT, KICKED, READY, NOT_READY */
	public RoomMsg(int from, RoomComm comm) {
		this.from = from;
		this.comm = comm.ordinal();
	}

	/** For JOINED */
	public RoomMsg(int from, RoomComm comm, Object[] adt_data) {
		this.from = from;
		this.comm = comm.ordinal();
		this.adt_data = adt_data;
	}

	/** For GAME_STARTED */
	public RoomMsg(RoomComm comm, Object[] adt_data) {
		this.comm = comm.ordinal();
		this.adt_data = adt_data;
	}

}
