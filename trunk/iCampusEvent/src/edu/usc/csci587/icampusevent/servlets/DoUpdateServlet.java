package edu.usc.csci587.icampusevent.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DoUpdateServlet
 */
@WebServlet("/DoUpdateServlet")
public class DoUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
			StringBuffer sb = new StringBuffer();
			sb.append("Hello there!");
			out.println(sb);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
			StringBuffer sb = new StringBuffer();
			sb.append("Hello there! And you posted to me: ");
			String line = null;
			BufferedReader rd = request.getReader();
			while ((line = rd.readLine()) != null)
				sb.append(line);
			
			out.println(sb);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
