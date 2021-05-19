package shared;

public enum RoomComm {

	// sent by client for server
	/** player(host) is requesting to kick another player with id in adt_data */
	REQUEST_KICK,
	/** player with from id is requesting to change his name in adt_data */
	REQUEST_CHANGE_NAME,
	/** player with from id is requesting to take the empty RoomPosition in adt_data */
	TAKE_EMPTY_PLACE,
	/** player with from id is requesting to team up with another player with id in adt_data */
	REQUEST_TEAM_UP,
	/** player(host) is starting the game */
	START_GAME,
	/** a player is ending the game */
	END_GAME,
	/** player(host) wants to make his room public */
	GO_PUBLIC,
	/** player(host) wants to make his room private */
	GO_PRIVATE,
	/** player with from id has accepted to team up with player with id in adt_data */
	ACCEPT_TEAM_UP,
	/** player with from id has denied to team up with player with id in adt_data */
	DENY_TEAM_UP,

	// sent by client for clients
	/** player with from id is ready */
	READY,
	/** player with from id is not ready */
	NOT_READY,

	// sent by server
	/** player with from id joined the room */
	JOINED,
	/** player with from id left the room */
	LEFT,
	/** player with from id is kicked */
	KICKED,
	/** player with from id changed his name */
	CHANGED_NAME,
	/** player with from id has taken empty RoomPosition in adt_data */
	TOOK_EMPTY_PLACE,
	/** player with ids in adt_data has teamed up */
	TEAMED_UP,
	/** server has successfully made the room public */
	GONE_PUBLIC,
	/** server has successfully made the room private */
	GONE_PRIVATE,
	/** server is starting the game */
	GAME_STARTING,
	/** server started the game */
	GAME_STARTED,
	/** server ended the game */
	GAME_ENDED;
}