package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.TemplateHandler;

/**
 * Servlet implementation class Login. Handles login credentials checking and redirection
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;
       
    public Login() {
        super();
    }

    /**
     * Initializes Login Servlet, instantiating a SQL connection to the DB.
     */
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    	engine = TemplateHandler.getHTMLTemplateEngine(getServletContext());
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Obtain and sanitize user input
		String email;
		String password;
		
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		
		if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please type your username and password");
			return;
		}
		
		//Query db for credentials
		UserDAO dao = new UserDAO(connection);
		User user = null;
		
		try {
			user = dao.findUser(email, password);
		} catch (SQLException e) {
			// Redirect to error page -> Unable to check credentials
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error: Unable to check credentials");
		}
		
		//If user exists, add to session and redirect to homepage, otherwise redirect to login page (no user found)
		String path = "";
		
		if(user != null) {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid credentials");
		}

	}

}