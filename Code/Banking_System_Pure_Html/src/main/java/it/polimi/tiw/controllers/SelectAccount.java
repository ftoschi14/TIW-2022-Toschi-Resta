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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.TemplateHandler;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Servlet implementation class SelectAccount
 */
@WebServlet("/SelectAccount")
public class SelectAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine engine;
    
    public SelectAccount() {
        super();
    }
    
    /**
     * Overriding init method to use thymeleaf
     */
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		engine = TemplateHandler.getHTMLTemplateEngine(getServletContext());
	}

	/**
	 * Select an account to see details, redirects to the view component AccountDetails
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		// TO-DO: FILTER TO CHECK IF THE USER IS LOGGED
		String loginpath = getServletContext().getContextPath() + "/Login.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		BankAccount bankAccount = null;
		
		// gets and checks params
		Integer bankAccountID;
		try {
			bankAccountID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("bankAccountID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		//if the account exists for the user obtain the details
		try {
			bankAccount = bankAccountDAO.findAccountByID(bankAccountID);
			if (bankAccount == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "This bank account doesn't exist");
				return;
			}
			else if (bankAccount.getUserID() != user.getID()){
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "This bank account is not yours");
				return;
			}
			
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get this bank account");
			return;
		}
		
		//redirect to the page with the account details
		String path = "/WEB-INF/AccountDetails.html";
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("account", bankAccount);
		engine.process(path, context, response.getWriter());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
