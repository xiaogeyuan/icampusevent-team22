/**
 * 
 */
package edu.usc.csci587.icampusevent.dbhandler;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.OracleTypes;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import com.google.gson.Gson;
import edu.usc.csci587.icampusevent.objects.Event;
import edu.usc.csci587.icampusevent.objects.Response;

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

	public String get_search_events_by_area_query(String p_USER_ID, JGeometry shape, float p_DISTANCE, String p_SHAPE_TYPE) throws SQLException {

		Response resp = null;
		
		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_SEARCH_EVENTS_BY_AREA(?, ?, ?, ?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		STRUCT p_SHAPE = JGeometry.store(shape, connection);
		proc.setObject("p_SHAPE", p_SHAPE);
		proc.setFloat("p_DISTANCE", p_DISTANCE);
		proc.setString("p_SHAPE_TYPE", p_SHAPE_TYPE);
		
		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);
		
		proc.execute();
		
		ResultSet rs = (ResultSet)proc.getObject("o_CURSOR");

		List<Event> EventsList = new ArrayList<Event>();

		while (rs != null && rs.next()) {
			
			String CATEGORY_NAME = rs.getString("CATEGORY_NAME");
			String CATEGORY_DESCRIPTION = rs.getString("CATEGORY_DESCRIPTION");

			long EVENT_ID = rs.getLong("EVENT_ID");
			String EVENT_NAME = rs.getString("EVENT_NAME");
			Timestamp START_DATE = rs.getTimestamp("START_DATE");
			Timestamp END_DATE = rs.getTimestamp("END_DATE");
			String EVENT_DESCRIPTION = rs.getString("EVENT_DESCRIPTION");
			String IMAGE_URL = rs.getString("IMAGE_URL");
			String LINK = rs.getString("LINK");
			
			STRUCT st = (oracle.sql.STRUCT) rs.getObject("LOCATION");
			JGeometry LOCATION_POINT = JGeometry.load(st);
			
			double[] LOCATION=LOCATION_POINT.getPoint();
			
			Event e = new Event(CATEGORY_NAME, CATEGORY_DESCRIPTION, EVENT_ID, EVENT_NAME, START_DATE, END_DATE, EVENT_DESCRIPTION, IMAGE_URL, LINK,
					LOCATION);
			EventsList.add(e);
		}

		proc.close();

		if (EventsList.size() == 0) {
			resp = new Response("Warning", "No results found. Try another area");
		} else {
			resp = new Response("Success", EventsList);
		}
		
		return new Gson().toJson(resp);
	}
}
