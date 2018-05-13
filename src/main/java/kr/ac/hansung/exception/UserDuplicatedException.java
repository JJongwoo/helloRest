package kr.ac.hansung.exception;

public class UserDuplicatedException extends RuntimeException {

	private static final long serialVersionUID = 2081249869346761091L;

	private String username;

	public String getUsername() {
		return username;
	}

	public UserDuplicatedException(String username) {
		this.username = username;
	}
	
	
}
