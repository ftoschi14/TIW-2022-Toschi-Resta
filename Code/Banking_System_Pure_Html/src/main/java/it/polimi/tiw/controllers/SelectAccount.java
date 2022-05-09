package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.TransferDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.EngineHandler;
import it.polimi.tiw.utils.Paths;

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
		engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
	}

	/**
	 * Select an account to see details, redirects to the view component AccountDetails
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		BankAccount bankAccount = null;
		TransferDAO transferDAO = new TransferDAO(connection);
		List<Transfer> transfers = new ArrayList<>();
		
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
		
		//get the transfers for the selected account
		try {
			transfers = transferDAO.getTransferByAccountID(bankAccountID);
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get the transfers of the selected bank account");
			return;
		}
		
		//redirect to the page with the account details
		String path = "/WEB-INF/AccountDetails.html";
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("account", bankAccount);
		context.setVariable("transfers", transfers);
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
