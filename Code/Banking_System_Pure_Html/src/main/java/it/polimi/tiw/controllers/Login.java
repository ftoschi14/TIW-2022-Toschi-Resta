package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Paths;
import it.polimi.tiw.utils.EngineHandler;

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
    @Override
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    	engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
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
			System.out.println("email: " + request.getParameter("email") + ", password: " + request.getParameter("password"));
			errorRedirect(request, response, "Please type your username and password");
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please type your username and password");
			return;
		}
		
		//Query db for credentials
		UserDAO dao = new UserDAO(connection);
		User user = null;
		
		try {
			user = dao.findUser(email, password);
		} catch (SQLException e) {
			// Redirect to error page -> Unable to check credentials
			showWarning(request, response, "Database connection error: Unable to check credentials");
			return;
		}
		
		//If user exists, add to session and redirect to homepage, otherwise redirect to login page (no user found)
		String path = "";
		
		if(user != null) {
			//Unescape strings
			user.setEmail(StringEscapeUtils.unescapeJava(user.getEmail()));
			user.setName(StringEscapeUtils.unescapeJava(user.getName()));
			user.setSurname(StringEscapeUtils.unescapeJava(user.getSurname()));
			
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + Paths.pathToGoToHomeServlet;
			response.sendRedirect(path);
		} else {
			showWarning(request, response, "Invalid credentials");
		}
		
	}
	
	private void errorRedirect(HttpServletRequest req, HttpServletResponse res, String error) throws IOException {
		ServletContext servletContext = getServletContext();
		req.setAttribute("backPath", Paths.pathToGoToLoginServlet);
		req.setAttribute("error", error);
		
		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());
				
		engine.process(Paths.pathToErrorPage, context, res.getWriter());
	}
	
	private void showWarning(HttpServletRequest req, HttpServletResponse res, String warning) throws IOException {
		ServletContext servletContext = getServletContext();
		req.setAttribute("warning", warning);
		
		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());
				
		engine.process(Paths.pathToLoginPage, context, res.getWriter());
	}

}
