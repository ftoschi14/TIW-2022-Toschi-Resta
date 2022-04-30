package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

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
import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.TransferDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Paths;
import it.polimi.tiw.utils.EngineHandler;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Servlet implementation class MakeTransfer
 */
@WebServlet("/MakeTransfer")
public class MakeTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine engine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakeTransfer() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Overriding init method to use thymeleaf
     */
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Makes the transfer
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		// TO-DO: FILTER TO CHECK IF THE USER IS LOGGED
		String loginpath = getServletContext().getContextPath() + Paths.pathToGoToLoginServlet;
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		

		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		BankAccount senderAccount = null;
		BankAccount recipientAccount = null;
		
		//get, sanitize and checks params
		Integer recipientID = null;
		try {
			recipientID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("recipientID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect recipient id");
			return;
		}
		
		Integer recipientUserID = null;
		try {
			recipientUserID = Integer.parseInt(request.getParameter("recipientUserID"));
		}catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect recipient user id");
			return;
		}
		
		Integer senderID = null;
		try {
			senderID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("senderID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect sender id");
			return;
		}
		
		BigDecimal amount = null;
		String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));
		if(amountString == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect amount");
			return;
		}
		else {
			amount = new BigDecimal(amountString.replace(",","."));
		}
		
		String reason = StringEscapeUtils.escapeJava(request.getParameter("reason"));
		if(reason == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect reason");
			return;
		}
		
		//If the recipient account exists for the recipient user selected
		//and amount <= sender account balance make the transfer
		String path;
		try {
			recipientAccount = bankAccountDAO.findAccountByID(recipientID);
			senderAccount = bankAccountDAO.findAccountByID(senderID);
			if(recipientAccount == null) {
				//TO-DO REDIRECT TransactionFailed
				request.setAttribute("failReason", "No recipient account selected");
				request.setAttribute("senderid", senderAccount.getID());
				path = getServletContext().getContextPath() + Paths.pathToGoToTransferFailedServlet;
				response.sendRedirect(path);
			}
			else if(recipientAccount.getUserID() != recipientUserID) {
				//TO-DO REDIRECT TransactionFailed
				request.setAttribute("failReason", "The user selected is not the owner of the recipient account selected");
				request.setAttribute("senderid", senderAccount.getID());
				path = getServletContext().getContextPath() + Paths.pathToGoToTransferFailedServlet;
				response.sendRedirect(path);
			}
			else if(amount.compareTo(senderAccount.getBalance()) == 1) {
				//TO-DO REDIRECT TransactionFailed
				request.setAttribute("failReason", "Your account can't afford this transfer");
				request.setAttribute("senderid", senderAccount.getID());
				path = getServletContext().getContextPath() + Paths.pathToGoToTransferFailedServlet;
				response.sendRedirect(path);
			}
			else {
				TransferDAO transferDAO = new TransferDAO(connection);
				transferDAO.makeTransfer(amount, reason, senderID, recipientID);
				//TO-DO REDIRECT TransactionConfirmed
				request.setAttribute("sender", senderAccount);
				request.setAttribute("recipient", recipientAccount);
				request.setAttribute("amount", amount);
				request.setAttribute("reason", reason);
				
				path = getServletContext().getContextPath() + Paths.pathToGoToTransferConfirmedServlet;
				response.sendRedirect(path);
			}
			
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error: Unable to make the transfer");
			return;
		}
	}
	@Override
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				
			}
		}
	}
}
