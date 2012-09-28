package edu.usc.csci587.icampusevent.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import edu.usc.csci587.icampusevent.objects.Category;
import edu.usc.csci587.icampusevent.objects.Event;

public class Helper {

	/**
	 * Gets the string from the DB application exception
	 * 
	 * @param e
	 *            the SQL exception occured
	 * @return the 20005 code exception string
	 * */
	public static String getError(SQLException e) {
		String ret_err_str = "Unable to reach server. Try again later.";

		if (e != null && e.getMessage() != null) {
			String exception_str = e.getMessage();

			switch (e.getErrorCode()) {
			case 20005:
				String[] err_str_array = exception_str.split("[:\n]");
				if (err_str_array.length > 1)
					ret_err_str = err_str_array[1] + ".";
				break;
			case 20001:
				ret_err_str = "Unable to fulfill your request. Try again later.";
				break;
			}
		}

		return ret_err_str.trim();

	}

	/****************************************************************************************************************/

	/**
	 * Validates the coordinates
	 * 
	 * @param lat
	 *            the latitude of the location
	 * @param lon
	 *            the longitude of the location
	 * @return true if both lat and lon are in the range, otherwise false
	 * */
	public static boolean validCoordinates(double lat, double lon) {
		return (lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180);
	}

	/****************************************************************************************************************/

	/**
	 * Creates a new List of Events and adds all relevant event details in each
	 * event object.
	 * 
	 * @param rs
	 *            the result set returned by a search query
	 * @return a List of Events
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public static List<Event> getEventsFromResultSet(ResultSet rs) throws SQLException {
		List<Event> EventsList = new ArrayList<Event>();

		while (rs != null && rs.next()) {

			String CATEGORY_NAME = rs.getString("CATEGORY_NAME");
			String CATEGORY_DESCRIPTION = rs.getString("CATEGORY_DESCRIPTION");

			String EVENT_ID = rs.getString("EVENT_ID");
			String EVENT_NAME = rs.getString("EVENT_NAME");
			Timestamp START_DATE = rs.getTimestamp("START_DATE");
			Timestamp END_DATE = rs.getTimestamp("END_DATE");
			String EVENT_DESCRIPTION = rs.getString("EVENT_DESCRIPTION");
			String IMAGE_URL = rs.getString("IMAGE_URL");
			String LINK = rs.getString("LINK");
			Double DISTANCE_IN_MILES = rs.getDouble("DISTANCE_IN_MILES");
			Integer PARTICIPANTS = rs.getInt("PARTICIPANTS");

			STRUCT st = (oracle.sql.STRUCT) rs.getObject("LOCATION");
			JGeometry LOCATION_POINT = JGeometry.load(st);

			double[] LOCATION = LOCATION_POINT.getPoint();

			Event e = new Event(CATEGORY_NAME, CATEGORY_DESCRIPTION, EVENT_ID, EVENT_NAME, START_DATE, END_DATE, EVENT_DESCRIPTION, IMAGE_URL, LINK,
					LOCATION, DISTANCE_IN_MILES, PARTICIPANTS);
			EventsList.add(e);
		}
		return EventsList;
	}

	/****************************************************************************************************************/

	/**
	 * Creates a new List of Categories and returns it
	 * 
	 * @param rs
	 *            the result set returned for categories
	 * @return a List of Categories
	 * @throws SQLException
	 *             when DB connection throws exception
	 */
	public static List<Category> getCategoriesFromResultSet(ResultSet rs) throws SQLException {
		List<Category> CategoriesList = new ArrayList<Category>();

		while (rs != null && rs.next()) {

			String CATEGORY_ID = rs.getString("CATEGORY_ID");
			String CATEGORY_NAME = rs.getString("CATEGORY_NAME");
			String CATEGORY_DESCRIPTION = rs.getString("CATEGORY_DESCRIPTION");

			Category c = new Category(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
			CategoriesList.add(c);
		}
		return CategoriesList;
	}
	/****************************************************************************************************************/

}
