package edu.usc.csci587.icampusevent.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class QueryServlet
 */
public abstract class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String MIN_START_TIME = "1900-01-01 00:00:00";
	public static String MAX_END_TIME = "3000-01-01 00:00:00";

	protected String tableName;
	protected String spacialCol;
	protected String temporalCol;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QueryServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");

		String query = executeQuery(request);

		System.out.println(query);
		out.write(query);
		
	}

	protected abstract String executeQuery(HttpServletRequest req);
	
	protected static String GetTemporalComponent(String temporalCol, String[] args){
		String startTime = (args[0] != null) ? args[0] : "1900-01-01 00:00:00";
		String endTime = (args[1] != null) ? args[1] : "3000-01-01 00:00:00";
		String temporalComponent = " R." + temporalCol + " < to_timestamp('" + endTime + "', 'YYYY-MM-DD HH24:MI:SS') AND R." + temporalCol + " > to_timestamp('" + startTime + "', 'YYYY-MM-DD HH24:MI:SS') ";
		return temporalComponent;
	}
	
	/*protected static String GetSpacialComponent(String spacialCol, String[] args){
		String spacialComponent = new String();
		switch (args[0]){
		case "line"://args[1] = trajectory, args[2] = radius
			spacialComponent = " SDO_WITHIN_DISTANCE(R." + spacialCol+ ", SDO_GEOMETRY(2002, 8307, NULL, SDO_ELEM_INFO_ARRAY(1, 2, 1), SDO_ORDINATE_ARRAY(" + args[1] + ")), 'DISTANCE=" + args[2] + " UNIT MILE') = 'TRUE' ";
			break;
		case "range"://args[1] = minLon, args[2] = minLat, args[3] = maxLon, args[4] = maxLat
			spacialComponent = " SDO_RELATE(R." + spacialCol+ ", SDO_GEOMETRY(3003, 8307, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 3), SDO_ORDINATE_ARRAY(" + args[1] + "," + args[2] + ", -100, " + args[3] + "," + args[4] + ", 100)), 'mask=inside queryType=WINDOW') = 'TRUE' "; 
			break;
		case "point"://args[1] = pointLon, args[2] = pointLat, args[3] = radius
			spacialComponent = " SDO_WITHIN_DISTANCE(R." + spacialCol+ ", SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(" + args[1] + ", " + args[2] + ", null), null, null), 'DISTANCE=" + args[3] + " UNIT=MILE') = 'TRUE' ";
			break;
		}
		return spacialComponent;
	}*/
}
