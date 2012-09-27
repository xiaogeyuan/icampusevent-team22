package edu.usc.csci587.icampusevent.helpers;

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
		String startTime = (args[0] != null) ? args[0] : MIN_START_TIME;
		String endTime = (args[1] != null) ? args[1] : MAX_END_TIME;
		String temporalComponent = " R." + temporalCol + " < to_timestamp('" + endTime + "', 'YYYY-MM-DD HH24:MI:SS') AND R." + temporalCol + " > to_timestamp('" + startTime + "', 'YYYY-MM-DD HH24:MI:SS') ";
		return temporalComponent;
	}
	
}
