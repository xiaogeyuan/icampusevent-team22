/**
 * 
 */
package edu.usc.csci587.icampusevent.dbhandler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import edu.usc.csci587.icampusevent.objects.Event;

import oracle.jdbc.OracleConnection;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

/**
 * @author Giorgos & Xiaoge
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
		String url = URL + HOST + ":" + PORT + ":" + DBNAME;

		try {
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			connection = (OracleConnection) DriverManager.getConnection(url, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
				this.connection = null;
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
			pstmt.setLong(3, pubdate);
			pstmt.execute();
			pstmt.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public List<String> retrieveAllRecords() {
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
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// /*
	// * this is to test whether the java program can write data to database
	// */
	// public static void main(String[] args) {
	// DatabaseHandler handler = new DatabaseHandler();
	// try {
	// //insert an record to the database table
	// // handler.insertRecordToDB("Ling Hu", "Helloworld!!",
	// System.currentTimeMillis());
	// //retrieve all records from the database table
	// List<UserMessage> results = handler.retrieveAllRecords();
	// for (UserMessage ret : results) {
	// System.out.println(ret.getUsername()+"\t"+ ret.getMessage()+"\t"
	// +Constants.getTimeAsString(ret.getPubdate()));
	// }
	// handler.closeConnection();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public String get_search_events_by_area_query(String p_USER_ID, JGeometry p_SHAPE, double p_DISTANCE, String shape_type) throws SQLException {

		if (this.connection == null) {
			return null;
		}

		CallableStatement proc = connection.prepareCall("{ call SP_SEARCH_EVENTS_BY_AREA(?, ?, ?, ?) }");
		proc.setString(1, p_USER_ID);
		STRUCT shape = JGeometry.store(p_SHAPE, connection);
		proc.setObject(2, shape);
		proc.setDouble(3, p_DISTANCE);
		proc.setString(4, shape_type);
		ResultSet rs = proc.executeQuery();

		String ret = "";

		List<Event> EventsList = new ArrayList<Event>();

		while (rs != null && rs.next()) {
			
			String CATEGORY_NAME = rs.getString("o_CATEGORY_NAME");
			String CATEGORY_DESCRIPTION = rs.getString("o_CATEGORY_DESCRIPTION");

			long EVENT_ID = rs.getLong("o_EVENT_ID");
			String EVENT_NAME = rs.getString("o_EVENT_NAME");
			Date START_DATE = rs.getDate("o_START_DATE");
			Date END_DATE = rs.getDate("o_END_DATE");
			String EVENT_DESCRIPTION = rs.getString("o_EVENT_DESCRIPTION");
			String IMAGE_URL = rs.getString("o_IMAGE_URL");
			String LINK = rs.getString("o_LINK");
			
			STRUCT st = (oracle.sql.STRUCT) rs.getObject("o_LOCATION");
			JGeometry LOCATION = JGeometry.load(st);
			
			Event e = new Event(CATEGORY_NAME, CATEGORY_DESCRIPTION, EVENT_ID, EVENT_NAME, START_DATE, END_DATE, EVENT_DESCRIPTION, IMAGE_URL, LINK,
					LOCATION);
			EventsList.add(e);
		}

		proc.close();

		if (EventsList.size() == 0) {

		} else {
			Gson gson = new Gson();
			ret = gson.toJson(EventsList);
		}

		System.out.println(ret);
		
		return ret;
	}
}
