package game;

public class Combo {
	private Disc[] Discs;

	public Combo(Disc[] Discs) {
		this.Discs = Discs;
	}

	public boolean isNotEmpty() {
		return Discs[0].getValue() != "" && Discs[1].getValue() != "" && Discs[2].getValue() != ""
				&& Discs[3].getValue() != "";
	}

	public boolean isComplete() {
		return isNotEmpty() && Discs[0].getValue() == Discs[1].getValue() && Discs[1].getValue() == Discs[2].getValue()
				&& Discs[2].getValue() == Discs[3].getValue();
	}

	public Disc[] getDiscs() {
		return Discs;
	}
}
