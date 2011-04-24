package us.bandj.SimpleGlossaryKiller.Controller;

public class UserView {
	
	private String username;
	private String password;
	private int wordsPerday;

	public UserView() {
		super();
	}

	public UserView(String usr, String pwd, int wpd) {
		this.username = usr;
		this.password = pwd;
		this.wordsPerday = wpd;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getWordsPerday() {
		return wordsPerday;
	}

	public void setWordsPerday(int wordsPerday) {
		this.wordsPerday = wordsPerday;
	}

}
