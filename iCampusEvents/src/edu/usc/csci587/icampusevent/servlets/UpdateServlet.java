package edu.usc.csci587.icampusevent.servlets;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import oracle.spatial.geometry.JGeometry;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import edu.usc.csci587.icampusevent.dbhandler.DatabaseHandler;
import edu.usc.csci587.icampusevent.helpers.Helper;
import edu.usc.csci587.icampusevent.helpers.PostServlet;
import edu.usc.csci587.icampusevent.objects.Response;

/**
 * Servlet implementation class DoUpdateServlet
 */
@WebServlet("/Update")
public class UpdateServlet extends PostServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateServlet() {
		super();
	}

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
			// 1) location_update
			// 2) event_update
			String req_type = jObj.getString("req_type");
			if (req_type.compareToIgnoreCase("location_update") == 0)
				return do_location_update(jObj);
			else if (req_type.compareToIgnoreCase("event_update") == 0)
				return do_event_update(jObj);
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
	 * Updates the location of the user.
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String do_location_update(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		// Create JSON object with all query parameters
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// Get the new coordinates
		double lat = parametersObj.getDouble("lat");
		double lon = parametersObj.getDouble("lon");

		if (!Helper.validCoordinates(lat, lon))
			return new Gson().toJson(new Response("Error", "Invalid Latitude or Longitude values."));
		
		JGeometry p_LOCATION = new JGeometry(lon, lat, 8307);

		// Query the DB
		returnString = handler.do_location_update_query(p_USER_ID, p_LOCATION);

		handler.closeConnection();

		return returnString;
	}

	/****************************************************************************************************************/

	/**
	 * Update user for an event. The user can join, unjoin or check in the
	 * event. In order to check in to an event the user is physically to the
	 * location of the event and already joined it.
	 * 
	 * @param jObj
	 *            the JSON object containing all request parameters
	 * @return a JSON response with status line and data/message
	 * @throws JSONException
	 *             when parameters are not set properly in jObj
	 * @throws SQLException
	 *             when DB connection throws exception
	 * */
	private String do_event_update(JSONObject jObj) throws JSONException, SQLException {
		// Get the user id who sent the request
		String p_USER_ID = jObj.getString("uid");

		// Create JSON object with all query parameters
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();

		// The JSON result to return
		String returnString = null;

		// Get the new coordinates
		long p_EVENT_ID = parametersObj.getLong("eid");

		// Get the type of event update
		String type = parametersObj.getString("type");

		// Read the event update type from JSON. Must be one of the following:
		// 1) join
		// 2) unjoin
		// 3) checkin
		if (type.compareToIgnoreCase("join") == 0)
			returnString = handler.do_join_event_query(p_USER_ID, p_EVENT_ID);
		else if (type.compareToIgnoreCase("unjoin") == 0)
			returnString = handler.do_unjoin_event_query(p_USER_ID, p_EVENT_ID);
		else if (type.compareToIgnoreCase("checkin") == 0)
			returnString = handler.do_checkin_event_query(p_USER_ID, p_EVENT_ID);

		handler.closeConnection();

		return returnString;
	}

}
