package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.EngineHandler;
import it.polimi.tiw.utils.Paths;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;
	private Pattern pattern;
	
	// Regex for email address validation
	private final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
       
    public Register() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    	engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
		pattern = Pattern.compile(regex);
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
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		
		engine.process(Paths.pathToRegistrationPage, context, response.getWriter());
	}

	/**
	 * Handles User creation and redirection to Homepage
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		String passwordRep = StringEscapeUtils.escapeJava(request.getParameter("passwordRep"));
		
		String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		
		// Basic param nullcheck
		if(email == null || password == null || passwordRep == null || name == null || surname == null
		|| email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please fill out all the required fields");
		}
		
		// Email validity check
		Matcher matcher = pattern.matcher(email);
		if(!matcher.matches()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email format");
		}
		
		// Passwords match check
		if(!password.equals(passwordRep)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Passwords do not match!");
		}
		
		if(password.length() < 8) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please use a stronger password (at least 8 characters)");
		}
		
		UserDAO userDAO = new UserDAO(connection);
		// Check if email is already taken
		try {
			if(userDAO.isEmailTaken(email)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email " + email + " is already taken");
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error connecting to database");
		}
		
		// Proceed with user registration, and creation of default bank account
		User user = null;
		try {
			userDAO.registerUser(email, password, name, surname);	
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during account creation");
		}
		
		//HttpSession session = request.getSession();
		//session.setAttribute("user", user);
		response.sendRedirect(Paths.pathToLoginPage);
	}

}
