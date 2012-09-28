/**
 * 
 */
package edu.usc.csci587.icampusevent.dbhandler;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.OracleTypes;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import com.google.gson.Gson;
import edu.usc.csci587.icampusevent.helpers.Helper;
import edu.usc.csci587.icampusevent.objects.Category;
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
	 * Creates a new connection instance
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

	/****************************************************************************************************************/

	/**
	 * Closes the open connection
	 */
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

	/****************************************************************************************************************/

	/**
	 * Returns if there is a connection
	 * 
	 * @return true if we are connected to DB, otherwise false
	 */
	public boolean isConnected() {
		return this.connection == null;
	}

	/****************************************************************************************************************/
	/****************************************************************************************************************/

	/**
	 * Does a range query on events
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the events
	 * @param shape
	 *            the spatial object to relate event with. Can be point,
	 *            rectangle area or trajectory
	 * @param p_DISTANCE
	 *            the distance takes effect when shape is point or trajectory.
	 *            Is like a radius that covers an extended area.
	 * @param p_SHAPE_TYPE
	 *            defines the shape type we pass in shape, i.e. point, rectangle
	 *            or trajectory
	 * 
	 * @return a JSON object containing the events results
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String get_search_events_by_area_query(String p_USER_ID, JGeometry shape, double p_DISTANCE, String p_SHAPE_TYPE) throws SQLException {

		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_SEARCH_EVENTS_BY_AREA(?, ?, ?, ?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		STRUCT p_SHAPE = JGeometry.store(shape, connection);
		proc.setObject("p_SHAPE", p_SHAPE);
		proc.setDouble("p_DISTANCE", p_DISTANCE);
		proc.setString("p_SHAPE_TYPE", p_SHAPE_TYPE);

		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);

		proc.execute();

		ResultSet rs = (ResultSet) proc.getObject("o_CURSOR");

		List<Event> EventsList = Helper.getEventsFromResultSet(rs);

		proc.close();

		if (EventsList.size() == 0) {
			resp = new Response("Warning", "No results found. Try another area");
		} else {
			resp = new Response("Success", EventsList);
		}

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * Does a nearest neighbor query on events
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the events
	 * @param p_LIMIT
	 *            the maximum number of nearest events to return
	 * 
	 * @return a JSON object containing the events results
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String get_search_events_nearby_query(String p_USER_ID, int p_LIMIT) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_SEARCH_EVENTS_NEARBY(?, ?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		proc.setInt("p_LIMIT", p_LIMIT);

		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);

		proc.execute();

		ResultSet rs = (ResultSet) proc.getObject("o_CURSOR");

		List<Event> EventsList = Helper.getEventsFromResultSet(rs);

		proc.close();

		if (EventsList.size() == 0) {
			resp = new Response("Warning", "No results found. Try another area");
		} else {
			resp = new Response("Success", EventsList);
		}

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * Does a filter query on events
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the events
	 * @param p_KEYWORDS
	 *            the keywords
	 * @param p_FILTERS
	 *            other filters defined by user
	 * @return a JSON object containing the events results
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String get_search_events_by_filter_query(String p_USER_ID, String p_KEYWORDS, String p_FILTERS) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_SEARCH_EVENTS_BY_FILTER(?, ?, ?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		proc.setString("p_KEYWORDS", p_KEYWORDS);
		proc.setString("p_FILTERS", p_FILTERS);

		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);

		proc.execute();

		ResultSet rs = (ResultSet) proc.getObject("o_CURSOR");

		List<Event> EventsList = Helper.getEventsFromResultSet(rs);

		proc.close();

		if (EventsList.size() == 0) {
			resp = new Response("Warning", "No results found. Try another area");
		} else {
			resp = new Response("Success", EventsList);
		}

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * Queries and returns all joined events by this user
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the events
	 * @return a JSON object containing the joined events results
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String get_joined_events_query(String p_USER_ID) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_GET_USER_JOINED_EVENTS(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);

		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);

		proc.execute();

		ResultSet rs = (ResultSet) proc.getObject("o_CURSOR");

		List<Event> EventsList = Helper.getEventsFromResultSet(rs);

		proc.close();

		if (EventsList.size() == 0) {
			resp = new Response("Warning", "No results found. You have not joined an event so far.");
		} else {
			resp = new Response("Success", EventsList);
		}

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * Queries and returns all checked in events by this user
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the events
	 * @return a JSON object containing the checked in events results
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String get_checked_in_events_query(String p_USER_ID) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_GET_USER_CHECKED_IN_EVENTS(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);

		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);

		proc.execute();

		ResultSet rs = (ResultSet) proc.getObject("o_CURSOR");

		List<Event> EventsList = Helper.getEventsFromResultSet(rs);

		proc.close();

		if (EventsList.size() == 0) {
			resp = new Response("Warning", "No results found. You have not checked in to an event so far.");
		} else {
			resp = new Response("Success", EventsList);
		}

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * Queries and returns all categories
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the events
	 * @return a JSON object containing the categories' results
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String get_categories_query(String p_USER_ID) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_GET_CATEGORIES(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);

		proc.registerOutParameter("o_CURSOR", OracleTypes.CURSOR);

		proc.execute();

		ResultSet rs = (ResultSet) proc.getObject("o_CURSOR");

		List<Category> CategoriesList = Helper.getCategoriesFromResultSet(rs);

		proc.close();

		if (CategoriesList.size() == 0) {
			resp = new Response("Warning", "No results found.");
		} else {
			resp = new Response("Success", CategoriesList);
		}

		return new Gson().toJson(resp);
	}


	/****************************************************************************************************************/
	/****************************************************************************************************************/

	/**
	 * Updates the location of the user
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the update
	 * @param p_LOCATION
	 *            the new user location
	 * @return a JSON object containing the update result
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String do_location_update_query(String p_USER_ID, JGeometry point) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_UPDATE_USER_LOCATION(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		STRUCT p_LOCATION = JGeometry.store(point, connection);
		proc.setObject("p_LOCATION", p_LOCATION);

		proc.executeUpdate();

		proc.close();

		resp = new Response("Success", "Location updated");

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * User joins event
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the update
	 * @param p_EVENT_ID
	 *            the event id to join
	 * @return a JSON object containing the update result
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String do_join_event_query(String p_USER_ID, long p_EVENT_ID) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_USER_JOINS_EVENT(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		proc.setLong("p_EVENT_ID", p_EVENT_ID);

		proc.executeUpdate();

		proc.close();

		resp = new Response("Success", "You have joined the event");

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * User unjoins event
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the update
	 * @param p_EVENT_ID
	 *            the event id to unjoin
	 * @return a JSON object containing the update result
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String do_unjoin_event_query(String p_USER_ID, long p_EVENT_ID) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_USER_UNJOINS_EVENT(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		proc.setLong("p_EVENT_ID", p_EVENT_ID);

		proc.executeUpdate();

		proc.close();

		resp = new Response("Success", "You have unjoined the event");

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/

	/**
	 * User checks in event
	 * 
	 * @param p_USER_ID
	 *            the user id who requested the update
	 * @param p_EVENT_ID
	 *            the event id to check in
	 * @return a JSON object containing the update result
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public String do_checkin_event_query(String p_USER_ID, long p_EVENT_ID) throws SQLException {
		Response resp = null;

		if (this.connection == null) {
			resp = new Response("Error", "Unable to reach server. Try again later.");
			return new Gson().toJson(resp);
		}

		CallableStatement proc = connection.prepareCall("{ call SP_USER_CHECKIN_EVENT(?, ?) }");
		proc.setString("p_USER_ID", p_USER_ID);
		proc.setLong("p_EVENT_ID", p_EVENT_ID);

		proc.executeUpdate();

		proc.close();

		resp = new Response("Success", "You have checked in the event");

		return new Gson().toJson(resp);
	}

	/****************************************************************************************************************/
	/****************************************************************************************************************/

	public boolean test(JGeometry shape) {
		if (this.connection == null) {
			return false;
		}
		String sqlStmt = "INSERT INTO TEST VALUES (?)";
		try {
			PreparedStatement pstmt = this.connection.prepareStatement(sqlStmt);
			STRUCT p_SHAPE = JGeometry.store(shape, connection);
			pstmt.setObject(1, p_SHAPE);

			pstmt.execute();
			pstmt.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}

	}
	/****************************************************************************************************************/

}
