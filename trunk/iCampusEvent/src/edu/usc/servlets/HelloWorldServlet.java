package edu.usc.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class HelloWorldServlet
 */
@WebServlet("/HelloWorldServlet")
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setContentType("application/json");

		PrintWriter out = response.getWriter();
		JSONObject object = new JSONObject();

		try {
			object.put("name", "Deepa");
			object.put("Reg No", new Integer(12345));
			object.put("Mark", new Double(99));
			object.put("mail", "deepa@ebullitent.com");
			object.put("City", "Chennai");
			length = object.length();
			opt = object.optString("name");
			data = object.isNull("name");
			getMark = object.getString("Mark");
			getCity = (String) object.get("City");
			mail = object.has("mail");
			// object.append("mark1","98");
		} catch (JSONException e) {

			e.printStackTrace();
		}
		out.println("<html>");
		out.println(object);
		out.println("<head></head>");
		out.println("<body bgcolor='pink'>");
		out.println("<br/>");
		out.println("Name: " + opt);
		out.println("<br/>");
		out.println("Is Null: " + data);
		out.println("<br/>");
		out.println("city: " + getCity);
		out.println("<br/>");
		out.println("has mail: " + mail);
		out.println("<br/>");
		out.println("Mark:" + getMark);
		out.println("<br/>");
		out.println("List of keys:");
		out.println("<br/>");
		out.println("--------------");
		out.println("<br/>");

		out.println("length: " + length + "\n");
		out.println("</body></html>");
		out.println();
		Iterator i = object.keys();
		while (i.hasNext()) {
			out.println(i.next());
		}
		object.toString();
	}

}
