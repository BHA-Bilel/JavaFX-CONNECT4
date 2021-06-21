package bg.connectFour.game;

public class Combo {
	private final Disc[] Discs;

	public Combo(Disc[] Discs) {
		this.Discs = Discs;
	}

	public boolean isNotEmpty() {
		return !Discs[0].getValue().equals("") && !Discs[1].getValue().equals("") && !Discs[2].getValue().equals("")
				&& !Discs[3].getValue().equals("");
	}

	public boolean isComplete() {
		return isNotEmpty() && Discs[0].getValue().equals(Discs[1].getValue()) && Discs[1].getValue().equals(Discs[2].getValue())
				&& Discs[2].getValue().equals(Discs[3].getValue());
	}

	public Disc[] getDiscs() {
		return Discs;
	}
}
