package edu.usc.csci587.icampusevent.servlets;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import oracle.spatial.geometry.JGeometry;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import edu.usc.csci587.icampusevent.dbhandler.DatabaseHandler;
import edu.usc.csci587.icampusevent.objects.Response;

/**
 * Servlet implementation class SearchEventsServlet
 */
@WebServlet("/SearchEvents")
public class SearchEventsServlet extends QueryServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected String executeQuery(HttpServletRequest request) {
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(request.getParameter("request"));

			switch (jObj.getString("req_type")) {
			case "search_events_by_area":
				return get_search_events_by_area_query(jObj);
			case "search_events_nearby":
				return get_search_events_nearby(jObj);
			case "search_events_by_filter":
				return get_search_events_by_filter(jObj);
			default:
				return null;
			}

		} catch (JSONException e) {
			return new Gson().toJson(new Response("Error", "Unable to parse request parameters."));
		} catch (SQLException e) {
			return new Gson().toJson(new Response("Error", "Unable to reach server. Try again later."));
		}
	}




	private String get_search_events_by_area_query(JSONObject jObj) throws JSONException, SQLException {
		String p_USER_ID = jObj.getString("uid");
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();
		String returnString = null;
		JGeometry p_SHAPE = null;
		String p_SHAPE_TYPE = null;
		double p_DISTANCE = 0.0;

		switch (parametersObj.getString("shape_type")) {
		case "circle":
			p_DISTANCE = parametersObj.getDouble("distance");

			Double lat = parametersObj.getDouble("lat");
			Double lon = parametersObj.getDouble("lon");

			p_SHAPE = new JGeometry(lon, lat, 8307);
			p_SHAPE_TYPE = "circle";

			break;
		case "rectangle":

			Double minLat = parametersObj.getDouble("minLat");
			Double minLon = parametersObj.getDouble("minLon");
			Double maxLat = parametersObj.getDouble("maxLat");
			Double maxLon = parametersObj.getDouble("maxLon");

			p_SHAPE = new JGeometry(minLon, minLat, maxLon, maxLat, 8307);
			p_SHAPE_TYPE = "rectangle";

			break;
		case "trajectory":
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
			
			break;
		default:
			break;
		}
		returnString = handler.get_search_events_by_area_query(p_USER_ID, p_SHAPE, p_DISTANCE, p_SHAPE_TYPE);
		handler.closeConnection();
		return returnString;
	}
	

	private String get_search_events_nearby(JSONObject jObj) throws JSONException, SQLException {
		String p_USER_ID = jObj.getString("uid");
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();
		String returnString = null;
		int p_LIMIT = parametersObj.getInt("limit");
		
		returnString = handler.get_search_events_nearby_query(p_USER_ID, p_LIMIT);
		handler.closeConnection();
		return returnString;
	}
	

	private String get_search_events_by_filter(JSONObject jObj) throws JSONException, SQLException {
		String p_USER_ID = jObj.getString("uid");
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();
		String returnString = null;
		String p_KEYWORDS = parametersObj.getString("keywords");
		String p_FILTERS = parametersObj.getString("filters");
		
		returnString = handler.get_search_events_by_filter_query(p_USER_ID, p_KEYWORDS,p_FILTERS);
		handler.closeConnection();
		return returnString;
	}
}
