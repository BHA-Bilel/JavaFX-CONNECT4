package shared;

public enum Game {
	COINCHE(4), DOMINOS(4), CONNECT4(2), XO(2), CHESS(2), CHECKERS(2);

	public int players;

	Game(int players) {
		this.players = players;
	}
}
