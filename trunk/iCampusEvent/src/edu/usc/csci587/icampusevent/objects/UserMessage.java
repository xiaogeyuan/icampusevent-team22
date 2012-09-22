/**
 * 
 */
package edu.usc.csci587.icampusevent.objects;

/**
 * @author Ling
 *
 */
public class UserMessage {
	private String username;
	private String message;
	private long pubdate;
	public String getUsername() {
		return username;
	}
	public String getMessage() {
		return message;
	}
	public long getPubdate() {
		return pubdate;
	}
	/**
	 * @param username
	 * @param message
	 * @param pubdate
	 */
	public UserMessage(String username, String message, long pubdate) {
		super();
		this.username = username;
		this.message = message;
		this.pubdate = pubdate;
	}
	
	
}
