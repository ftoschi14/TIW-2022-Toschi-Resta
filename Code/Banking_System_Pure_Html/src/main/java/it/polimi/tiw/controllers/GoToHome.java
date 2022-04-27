package it.polimi.tiw.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.TemplateHandler;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/Home")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;
       
    
    public GoToHome() {
        super();
    }
    
    @Override
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    	engine = TemplateHandler.getHTMLTemplateEngine(getServletContext());
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
	 * Loads homepage data and redirects user to Home.html
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		// If no session is found or no User is found, then redirect to login page
		if(session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(Paths.pathToLogin);
		}
		
		User user = (User) session.getAttribute("user");
		List<BankAccount> accounts;
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		try {

			accounts = bankAccountDAO.findAllAccountsByUserID(user.getID());
		
		} catch(SQLException exc) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't find any bank account associated to user: " + user.getEmail());
		}
		
		if(accounts.isEmpty()) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No Bank accounts found");
		}
		
		//Redirect to page with Account list
		String path = Paths.pathToHomepage;
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("accounts", accounts);
		
		engine.process(path, context, response.getWriter());
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
