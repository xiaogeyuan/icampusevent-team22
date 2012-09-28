package edu.usc.csci587.icampusevent.helpers;

import java.sql.SQLException;

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
			
			switch(e.getErrorCode()){
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

}
