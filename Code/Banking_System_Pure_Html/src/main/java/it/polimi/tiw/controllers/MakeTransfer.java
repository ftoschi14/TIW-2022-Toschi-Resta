package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		BankAccount senderAccount = null;
		BankAccount recipientAccount = null;

		//get, sanitize and checks params
		Integer senderID = null;
		try {
			senderID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("senderID")));
		}catch(NumberFormatException | NullPointerException e) {
			errorRedirect(request, response, "Invalid sender id");
			return;
		}

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

		BigDecimal amount = null;
		String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));
		if(amountString == null) {
			errorRedirect(request, response, "Please specify a valid amount");
			return;
		}
		else {
			try{
				amount = new BigDecimal(amountString.replace(",","."));
				amount = amount.setScale(2,RoundingMode.HALF_EVEN);
			}catch(ArithmeticException | IllegalArgumentException e){
				errorRedirect(request, response, "Incorrect amount format");
				return;
			}

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
			if(senderAccount == null){
				request.setAttribute("failReason", "The sender account doesn't exist");
				path = Paths.pathToTransferFailedPage;
			}
			else if(senderAccount.getUserID() != user.getID()){
				request.setAttribute("failReason", "You can't make a transfer from this account because it's not yours");
				request.setAttribute("senderid", senderAccount.getID());
				path = Paths.pathToTransferFailedPage;
			}
			else if(recipientAccount == null) {
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
			else if(amount.compareTo(new BigDecimal(0)) == -1){
				request.setAttribute("failReason", "The amount must be positive");
				request.setAttribute("senderid", senderAccount.getID());
				path =  Paths.pathToTransferFailedPage;
			}
			else {
				TransferDAO transferDAO = new TransferDAO(connection);
				transferDAO.makeTransfer(amount, reason, senderID, recipientID);
				path = getServletContext().getContextPath() + Paths.pathToGoToTransferConfirmedServlet;
				response.sendRedirect(path);
				return;
			}
			forwardToTransferFailedDetails(request, response, path);

		}catch (SQLException e) {
			errorRedirect(request, response, "Unable to submit this transfer, please try again");
			//e.printStackTrace();
			return;
		}
	}

	private void forwardToTransferFailedDetails(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, context, response.getWriter());
	}

	private void errorRedirect(HttpServletRequest req, HttpServletResponse res, String error) throws IOException {
		ServletContext servletContext = getServletContext();
		Integer senderID = null;
		try{
			senderID = Integer.parseInt(StringEscapeUtils.escapeJava(req.getParameter("senderID")));
		}catch(NumberFormatException | NullPointerException e) {
			curruptedDataErrorRedirect(req,res,error);
			return;
		}
		req.setAttribute("bankAccountID", senderID);
		req.setAttribute("backPath", Paths.pathToSelectAccountServlet);
		req.setAttribute("error", error);

		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());

		engine.process(Paths.pathToErrorPage, context, res.getWriter());
	}

	private void curruptedDataErrorRedirect(HttpServletRequest req, HttpServletResponse res, String error) throws IOException {
		ServletContext servletContext = getServletContext();
		req.setAttribute("backPath", Paths.pathToGoToHomeServlet);
		req.setAttribute("error", error + "; corrupted data, click the yellow button to go back to your home");
		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());

		engine.process(Paths.pathToErrorPage, context, res.getWriter());
	}

}
