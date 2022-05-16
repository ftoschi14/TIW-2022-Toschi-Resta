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

	@Override
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){

			}
		}
	}

	/**
	 * Makes the transfer
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		BankAccount senderAccount = null;
		BankAccount recipientAccount = null;

		//get, sanitize and checks params
		Integer recipientID = null;
		try {
			recipientID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("recipientID")));
		}catch(NumberFormatException | NullPointerException e) {
			errorRedirect(request, response, "Incorrect recipient id");
			return;
		}

		Integer recipientUserID = null;
		try {
			recipientUserID = Integer.parseInt(request.getParameter("recipientUserID"));
		}catch(NumberFormatException | NullPointerException e) {
			errorRedirect(request, response, "Incorrect recipient user id");
			return;
		}

		Integer senderID = null;
		try {
			senderID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("senderID")));
		}catch(NumberFormatException | NullPointerException e) {
			errorRedirect(request, response, "Invalid sender id");
			return;
		}

		BigDecimal amount = null;
		String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));
		if(amountString == null) {
			errorRedirect(request, response, "Please specify a valid amount");
			return;
		}
		else {
			amount = new BigDecimal(amountString.replace(",","."));
		}

		String reason = StringEscapeUtils.escapeJava(request.getParameter("reason"));
		if(reason == null || reason.isEmpty()) {
			errorRedirect(request, response, "Please specify a valid reason");
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
				request.setAttribute("failReason", "The recipient account doesn't exist");
				request.setAttribute("senderid", senderAccount.getID());
				path = Paths.pathToTransferFailedPage;
				
			}
			else if(recipientAccount.getUserID() != recipientUserID) {
				//TO-DO REDIRECT TransactionFailed
				request.setAttribute("failReason", "The user selected is not the owner of the recipient account selected");
				request.setAttribute("senderid", senderAccount.getID());
				path = Paths.pathToTransferFailedPage;

			}
			else if(recipientAccount.getID() == senderAccount.getID()) {
				//TO-DO REDIRECT TransactionFailed
				request.setAttribute("failReason", "sender account ID = recipient account ID");
				request.setAttribute("senderid", senderAccount.getID());
				path = Paths.pathToTransferFailedPage;

			}
			else if(amount.compareTo(senderAccount.getBalance()) == 1) {
				//TO-DO REDIRECT TransactionFailed
				request.setAttribute("failReason", "Your account can't afford this transfer");
				request.setAttribute("senderid", senderAccount.getID());
				path =  Paths.pathToTransferFailedPage;

			}
			else {
				TransferDAO transferDAO = new TransferDAO(connection);
				transferDAO.makeTransfer(amount, reason, senderID, recipientID);
				//TO-DO REDIRECT TransactionConfirmed
				request.setAttribute("sender", senderAccount);
				request.setAttribute("recipient", recipientAccount);
				request.setAttribute("amount", amount);
				request.setAttribute("reason", reason);
				request.setAttribute("newBalanceSenderAccount", senderAccount.getBalance().subtract(amount));
				request.setAttribute("newBalanceRecipientAccount", recipientAccount.getBalance().add(amount));

				path = Paths.pathToTransferConfirmedPage;

			}
			forwardToTransferDetails(request, response, path);

		}catch (SQLException e) {
			errorRedirect(request, response, "Unable to submit this transfer, please try again");
			e.printStackTrace();
			return;
		}
	}

	private void forwardToTransferDetails(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, context, response.getWriter());
	}

	private void errorRedirect(HttpServletRequest req, HttpServletResponse res, String error) throws IOException {
		ServletContext servletContext = getServletContext();
		int senderID = Integer.parseInt(StringEscapeUtils.escapeJava(req.getParameter("senderID")));
		req.setAttribute("bankAccountID", senderID);
		req.setAttribute("backPath", Paths.pathToSelectAccountServlet);
		req.setAttribute("error", error);

		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());

		engine.process(Paths.pathToErrorPage, context, res.getWriter());
	}

}
