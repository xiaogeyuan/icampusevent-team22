/**
 * 
 */
package edu.usc.csci587.icampusevent.dbhandler;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleConnection;
import edu.usc.cs587.examples.Constants;
import edu.usc.csci587.icampusevent.objects.UserMessage;

/**
 * @author Ling
 *
 */
public class DatabaseHandler {
	private static final String HOST = "128.125.163.168";
	private static final String PORT = "1521";
	private static final String USERNAME = "team22";   
	private static final String PASSWORD = "geoxia";
	private static final String DBNAME = "csci585";
	private static final String URL = "jdbc:oracle:thin:@";

	protected OracleConnection connection;

	/**
	 * 
	 */
	public DatabaseHandler() {
		String url = URL + HOST + ":" + PORT + ":" + DBNAME;;
		try {
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			connection = (OracleConnection) DriverManager.getConnection(url,
					USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected() {
		return this.connection == null;
	}
	
	public boolean insertRecordToDB(String username, String randomstring, long pubdate) {
		if (this.connection == null) {
			return false;
		}
		String sqlStmt = "INSERT INTO EXAMPLE VALUES (?,?,?)";
		try {
			PreparedStatement pstmt = this.connection.prepareStatement(sqlStmt);
			
			pstmt.setString(1, username);
			pstmt.setString(2, randomstring);
			pstmt.setLong  (3, pubdate);
			pstmt.execute();
			pstmt.close();
			return true;
		}catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public List<String> retrieveAllRecords () {
		if (this.connection == null) {
			return null;
		}
		String sqlStmt = "SELECT * FROM USERS";
		
		try {
			PreparedStatement pstmt = this.connection.prepareStatement(sqlStmt);
			ResultSet rs = pstmt.executeQuery();
			List<String> ret = new ArrayList<String>();
			while (rs != null && rs.next()) {
				String userid = rs.getString("USER_ID");
				ret.add(userid);
			}
			pstmt.close();
			return ret;
		}catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
//	/*
//	 * this is to test whether the java program can write data to database
//	 */
//	public static void main(String[] args) {
//		DatabaseHandler handler = new DatabaseHandler();
//		try {
//			//insert an record to the database table 
////			handler.insertRecordToDB("Ling Hu", "Helloworld!!", System.currentTimeMillis());
//			//retrieve all records from the database table 
//			List<UserMessage> results = handler.retrieveAllRecords();
//			for (UserMessage ret : results) {
//				System.out.println(ret.getUsername()+"\t"+ ret.getMessage()+"\t"
//						+Constants.getTimeAsString(ret.getPubdate()));
//			}
//			handler.closeConnection();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
