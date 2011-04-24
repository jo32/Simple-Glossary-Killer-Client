package us.bandj.SimpleGlossaryKiller.Controller;

public class WordView {

	private String word;
	private int familiarity;

	public WordView() {
		super();
	}

	public WordView(String word, int familiarity) {
		this.word = word;
		this.familiarity = familiarity;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getFamiliarity() {
		return familiarity;
	}

	public void setFamiliarity(int familiarity) {
		this.familiarity = familiarity;
	}

}
