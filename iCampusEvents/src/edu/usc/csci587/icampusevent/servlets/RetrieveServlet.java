package edu.usc.csci587.icampusevent.servlets;

import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import edu.usc.csci587.icampusevent.dbhandler.DatabaseHandler;
import edu.usc.csci587.icampusevent.helpers.Helper;
import edu.usc.csci587.icampusevent.helpers.QueryServlet;
import edu.usc.csci587.icampusevent.objects.Response;

/**
 * Servlet implementation class RetrieveServlet
 */
@WebServlet("/Retrieve")
public class RetrieveServlet extends QueryServlet {
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
			// 1) joined_events
			// 2) checked_in_events
			// 3) categories
			String req_type = jObj.getString("req_type");
			if (req_type.compareToIgnoreCase("joined_events") == 0)
				return get_joined_events(jObj);
			else if (req_type.compareToIgnoreCase("checked_in_events") == 0)
				return get_checked_in_events(jObj);
			else if (req_type.compareToIgnoreCase("categories") == 0)
				return get_categories(jObj);
			else
				return new Gson().toJson(new Response("Error", "Unrecognized parameter type."));

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
