package edu.usc.csci587.icampusevent.servlets;

import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import oracle.spatial.geometry.JGeometry;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import edu.usc.csci587.icampusevent.dbhandler.DatabaseHandler;
import edu.usc.csci587.icampusevent.helpers.Helper;
import edu.usc.csci587.icampusevent.helpers.QueryServlet;
import edu.usc.csci587.icampusevent.objects.Response;

/**
 * Servlet implementation class SearchEventsServlet
 */
@WebServlet("/SearchEvents")
public class SearchEventsServlet extends QueryServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Gets the requests and executes the query.
	 * 
	 * @param request
	 *            the client's request
	 * @return a JSON response with status line and data/message
	 * */
	@Override
	protected String executeQuery(HttpServletRequest request) {
		JSONObject jObj = null;
		try {
			// Get the request parameter and create a new JSON object containing
			// all data in the request
			jObj = new JSONObject(request.getParameter("request"));

			// Read request type from JSON. Must be one of the following:
			// 1) search_events_by_area
			// 2) search_events_nearby
			// 3) search_events_by_filter
			String req_type = jObj.getString("req_type");
			if (req_type.compareToIgnoreCase("search_events_by_area") == 0)
				return get_search_events_by_area_query(jObj);
			else if (req_type.compareToIgnoreCase("search_events_nearby") == 0)
				return get_search_events_nearby(jObj);
			else if (req_type.compareToIgnoreCase("search_events_by_filter") == 0)
				return get_search_events_by_filter(jObj);
			else if (req_type.compareToIgnoreCase("joined_events") == 0)
				return get_joined_events(jObj);
			else if (req_type.compareToIgnoreCase("checked_in_events") == 0)
				return get_checked_in_events(jObj);
			else if (req_type.compareToIgnoreCase("categories") == 0)
				return get_categories(jObj);
			else
				return new Gson().toJson(new Response("Error", "Unrecognized search parameter type."));

		} catch (JSONException e) {
			return new Gson().toJson(new Response("Error", "Unable to parse request parameters."));
		} catch (SQLException e) {
			return new Gson().toJson(new Response("Error", Helper.getError(e)));
		} catch (NullPointerException e) {
			return new Gson().toJson(new Response("Error", "Some parameters are missing."));
		}
	}

	/****************************************************************************************************************/

	/**
	 * Gets the requests and executes the spatial queries (by area).
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String get_search_events_by_area_query(JSONObject jObj) throws JSONException, SQLException {

		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		// Create JSON object with all query parameters
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// The spatial query object
		JGeometry p_SHAPE = null;

		// The shape type. Can be 'circle', 'rectangle' or 'trajectory'
		String p_SHAPE_TYPE = null;

		// The distance from a point or trajectory
		double p_DISTANCE = 0.0;

		String shape_type = parametersObj.getString("shape_type");

		// Set all appropriate arguments to pass to the DB stored procedures
		// based on each shape type
		if (shape_type.compareToIgnoreCase("circle") == 0) {
			p_DISTANCE = parametersObj.getDouble("distance");

			Double lat = parametersObj.getDouble("lat");
			Double lon = parametersObj.getDouble("lon");
			p_SHAPE = new JGeometry(lon, lat, 8307);
			p_SHAPE_TYPE = "circle";
		} else if (shape_type.compareToIgnoreCase("rectangle") == 0) {
			Double minLat = parametersObj.getDouble("minLat");
			Double minLon = parametersObj.getDouble("minLon");
			Double maxLat = parametersObj.getDouble("maxLat");
			Double maxLon = parametersObj.getDouble("maxLon");

			p_SHAPE = new JGeometry(minLon, minLat, maxLon, maxLat, 8307);
			p_SHAPE_TYPE = "rectangle";
		} else if (shape_type.compareToIgnoreCase("trajectory") == 0) {
			p_DISTANCE = parametersObj.getDouble("distance");

			String trajectory = parametersObj.getString("trajectory").replace(" ", "");

			String[] trajectoryArray = trajectory.split(",");
			if (trajectoryArray.length % 2 == 1)
				throw new JSONException("A value is missing");
			double[] trajectoryArrayDouble = new double[trajectoryArray.length];
			for (int i = 0; i < trajectoryArray.length; ++i) {
				trajectoryArrayDouble[i] = Double.parseDouble(trajectoryArray[i]);
			}

			p_SHAPE = new JGeometry(2002, 8307, new int[] { 1, 2, 1 }, trajectoryArrayDouble);
			p_SHAPE_TYPE = "trajectory";
		}

		// Query the DB
		returnString = handler.get_search_events_by_area_query(p_USER_ID, p_SHAPE, p_DISTANCE, p_SHAPE_TYPE);
		handler.closeConnection();
		return returnString;
	}

	/****************************************************************************************************************/

	/**
	 * Gets the requests and executes the spatial queries (nearby).
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String get_search_events_nearby(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		// Create JSON object with all query parameters
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// This value is the maximum number of rows returned
		int p_LIMIT = parametersObj.getInt("limit");

		// Query the DB
		returnString = handler.get_search_events_nearby_query(p_USER_ID, p_LIMIT);
		handler.closeConnection();
		return returnString;
	}

	/****************************************************************************************************************/

	/**
	 * Gets the requests and executes queries (by filtering).
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String get_search_events_by_filter(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		// Create JSON object with all query parameters
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// Specifies the keywords
		String p_KEYWORDS = parametersObj.getString("keywords");

		// Specifies other filters
		String p_FILTERS = parametersObj.getString("filters");

		// Query the DB
		returnString = handler.get_search_events_by_filter_query(p_USER_ID, p_KEYWORDS, p_FILTERS);
		handler.closeConnection();
		return returnString;
	}

	/****************************************************************************************************************/

	/**
	 * Gets the events that the user has joined so far
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String get_joined_events(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// Query the DB
		returnString = handler.get_joined_events_query(p_USER_ID);
		handler.closeConnection();
		return returnString;
	}

	/****************************************************************************************************************/

	/**
	 * Gets the events that the user has checked in so far
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String get_checked_in_events(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// Query the DB
		returnString = handler.get_checked_in_events_query(p_USER_ID);
		handler.closeConnection();
		return returnString;
	}

	/****************************************************************************************************************/

	/**
	 * Gets all the event's categories
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String get_categories(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// Query the DB
		returnString = handler.get_categories_query(p_USER_ID);
		handler.closeConnection();
		return returnString;
	}

}
