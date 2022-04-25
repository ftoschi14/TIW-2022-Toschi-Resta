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

/**
 * Servlet implementation class MakeTransfer
 */
@WebServlet("/MakeTransfer")
public class MakeTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
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
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
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
		String loginpath = getServletContext().getContextPath() + "/Login.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
	
		User user = (User) session.getAttribute("user");
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		BankAccount senderAccount = null;
		BankAccount recipientAccount = null;
		User recipientUser = null;
		
		//get and checks params
		Integer recipientID = null;
		try {
			recipientID = Integer.parseInt(request.getParameter("recipientID"));
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
			senderID = Integer.parseInt(request.getParameter("senderID"));
		}catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect sender id");
			return;
		}
		
		BigDecimal amount = null;
		String amountString = request.getParameter("amount");
		if(amountString == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect amount");
			return;
		}
		else {
			amount = new BigDecimal(amountString.replace(",","."));
		}
		
		String reason = request.getParameter("reason");
		if(reason == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect reason");
			return;
		}
		
		//If the recipient account exists for the recipient user selected
		//and amount <= sender account balance make the transfer
		try {
			recipientAccount = bankAccountDAO.findAccountByID(recipientID);
			senderAccount = bankAccountDAO.findAccountByID(senderID);
			if(recipientAccount == null) {
				//TO-DO REDIRECT TransactionFailed
			}
			else if(recipientAccount.getUserID() != recipientUserID) {
				//TO-DO REDIRECT TransactionFailed
			}
			else if(amount.compareTo(senderAccount.getBalance()) == 1) {
				//TO-DO REDIRECT TransactionFailed
			
			}
			
			TransferDAO transferDAO = new TransferDAO(connection);
			transferDAO.makeTransfer(amount, reason, senderID, recipientID);
			//TO-DO REDIRECT TransactionConfirmed
			String path = "/WEB-INF/TransactionConfirmed.html";
			ServletContext servletContext = getServletContext();
			final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
			context.setVariable("sender", senderAccount);
			context.setVariable("recipient", recipientAccount);
			context.setVariable("recipientuser", recipientUserID);
			context.setVariable("amount", amount);
			templateEngine.process(path, context, response.getWriter());
			
		}catch (SQLException e) {
			e.printStackTrace();
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover mission");
			return;
		}
	}

}
