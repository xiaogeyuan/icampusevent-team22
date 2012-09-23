package edu.usc.csci587.icampusevent.servlets;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import oracle.spatial.geometry.JGeometry;

import org.json.JSONException;
import org.json.JSONObject;

import edu.usc.csci587.icampusevent.dbhandler.DatabaseHandler;

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
				break;
			case "search_events_by_filter":
				break;
			default:
				return null;
			}

		} catch (JSONException e) {
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String get_search_events_by_area_query(JSONObject jObj) throws JSONException, SQLException {
		String p_USER_ID = jObj.getString("uid");
		JSONObject parametersObj = new JSONObject(jObj.getString("par"));
		DatabaseHandler handler = new DatabaseHandler();
		String returnString = null;
		switch (parametersObj.getString("shape_type")) {
		case "circle":
			float p_DISTANCE = (float)parametersObj.getDouble("distance");

			Double lat = parametersObj.getDouble("lat");
			Double lon = parametersObj.getDouble("lon");
			JGeometry p_SHAPE = new JGeometry(lon, lat, 8307);

			returnString = handler.get_search_events_by_area_query(p_USER_ID, p_SHAPE, p_DISTANCE, "circle");

			break;
		case "rectangle":
			Double minLat = parametersObj.getDouble("minLat");
			Double minLon = parametersObj.getDouble("minLon");
			Double maxLat = parametersObj.getDouble("maxLat");
			Double maxLon = parametersObj.getDouble("maxLon");
			break;
		case "trajectory":
			String p_DISTANCE1 = parametersObj.getString("distance");
			break;
		}
		handler.closeConnection();
		return returnString;
	}
}
