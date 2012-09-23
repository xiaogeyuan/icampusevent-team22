package edu.usc.csci587.icampusevent.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import edu.usc.csci587.icampusevent.dbhandler.DatabaseHandler;

/**
 * Servlet implementation class HelloWorldServlet
 */
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	int length;
	String opt;
	boolean data;
	String getMark;
	String getCity;
	boolean mail;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloWorldServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Hello World");

		DatabaseHandler handler = new DatabaseHandler();
		// response.setContentType("application/string");
		// response.setContentType("text/x-json;charset=UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		String resultString = "";
		List<String> ret = handler.retrieveAllRecords();
		if (ret.size() == 0) {

		} else {
			Gson gson = new Gson();
			resultString = gson.toJson(ret);
		}

		System.out.println(resultString);
		out.write(resultString);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
