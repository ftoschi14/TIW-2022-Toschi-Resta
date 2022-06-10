package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
@MultipartConfig
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private Pattern emailRegexPattern, nameRegexPattern;

	// Regex for email address validation
	private final String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	private final String nameRegex = "^([A-Za-z\\u00C0-\\u024F])+([A-Za-z\\u00C0-\\u024F]|\\s)*";

    public Register() {
        super();
    }

    @Override
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
		emailRegexPattern = Pattern.compile(emailRegex);
		nameRegexPattern = Pattern.compile(nameRegex);
    }

    @Override
    public void destroy() {
    	try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    /**
     * Redirects to Register page
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Handles User creation and redirection to Homepage
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));

		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		String passwordRep = StringEscapeUtils.escapeJava(request.getParameter("passwordRep"));

		String name = request.getParameter("name");
		String surname = request.getParameter("surname");

		// Basic param nullcheck
		if(email == null || password == null || passwordRep == null || name == null || surname == null
		|| email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please fill out all the required fields");
			return;
		}

		// Email validity check
		Matcher matcher = emailRegexPattern.matcher(email);
		if(!matcher.matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid email format");
			return;
		}

		// Passwords match check
		if(!password.equals(passwordRep)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Passwords do not match");
			return;
		}

		if(password.length() < 8) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please use a stronger password (at least 8 characters)");
			return;
		}

		Matcher nameMatcher = nameRegexPattern.matcher(name);
		Matcher surnameMatcher = nameRegexPattern.matcher(surname);
		if(!nameMatcher.matches() || !surnameMatcher.matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please do not use special characters in your name (only accented characters are allowed)");
			return;
		}

		UserDAO userDAO = new UserDAO(connection);
		// Check if email is already taken
		try {
			if(userDAO.isEmailTaken(email)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Email " + email + " is already taken");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error connecting to database");
			return;
		}

		// Proceed with user registration, and creation of default bank account
		User user = null;
		try {
			userDAO.registerUser(email, password, name, surname);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error during account creation: " + e.getSQLState() + "\n please try again");
			return;
		}

		//HttpSession session = request.getSession();
		//session.setAttribute("user", user);


		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println("Successfully registered with user " + email);
	}
}
