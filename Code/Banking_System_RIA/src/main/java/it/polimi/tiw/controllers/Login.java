package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.JsonObject;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Serializer;

/**
 * Servlet implementation class Login. Handles login credentials checking and redirection
 */
@WebServlet("/Login")
@MultipartConfig
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public Login() {
        super();
    }

    /**
     * Initializes Login Servlet, instantiating a SQL connection to the DB.
     */
    @Override
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    }


	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Obtain and sanitize user input
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));

		if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please type your username and password");
			System.out.println(request);
			return;
		}

		//Query db for credentials
		UserDAO dao = new UserDAO(connection);
		User user = null;

		try {
			user = dao.findUser(email, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Database connection error: Unable to check credentials");
			return;
		}

		if(user != null) {
			request.getSession().setAttribute("user", user);

			JsonObject userObj = new JsonObject();
			userObj = Serializer.serialize(user);
			userObj.remove("email");
			String responseUser = userObj.toString();

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(responseUser);
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Invalid credentials");
		}

	}
}
