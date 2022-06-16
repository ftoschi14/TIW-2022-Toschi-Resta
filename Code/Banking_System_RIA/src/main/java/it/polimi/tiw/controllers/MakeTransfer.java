package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
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
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.TransferDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Serializer;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Servlet implementation class MakeTransfer
 */
@WebServlet("/MakeTransfer")
@MultipartConfig
public class MakeTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private Pattern reasonPattern;
	private final String reasonRegex = "^([A-Za-z\\u00C0-\\u024F])+([A-Za-z\\u00C0-\\u024F]|\\s)*";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakeTransfer() {
        super();
        // TODO Auto-generated constructor stub
    }


    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		reasonPattern = Pattern.compile(reasonRegex);
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
		TransferDAO transferDAO = new TransferDAO(connection);
		BankAccount senderAccount = null;
		BankAccount recipientAccount = null;

		//get, sanitize and checks params
		Integer senderID = null;
		try {
			senderID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("senderID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid sender id");
			return;
		}

		Integer recipientID = null;
		try {
			recipientID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("recipientID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid recipient id");
			return;
		}

		Integer recipientUserID = null;
		try {
			recipientUserID = Integer.parseInt(request.getParameter("recipientUserID"));
		}catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid recipient user id");
			return;
		}

		BigDecimal amount = null;
		String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));
		if(amountString == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please specify a valid amount");
			return;
		}
		else {
			try{
				amount = new BigDecimal(amountString.replace(",","."));
			}catch(NumberFormatException e){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect amount format");
				return;
			}

		}

		String reason = request.getParameter("reason");
		Matcher reasonMatcher = reasonPattern.matcher(reason);
		if(reason == null || reason.isEmpty() || !reasonMatcher.matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please specify a valid reason (No special characters allowed)");
			return;
		}

		//If the recipient account exists for the recipient user selected
		//and amount <= sender account balance make the transfer
		try {
			recipientAccount = bankAccountDAO.findAccountByID(recipientID);
			senderAccount = bankAccountDAO.findAccountByID(senderID);
			if(senderAccount == null){
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("The sender account doesn't exist");
				return;
			}
			else if(senderAccount.getUserID() != user.getID()){
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("You can't make a transfer from this account because it's not yours");
				return;
			}
			else if(recipientAccount == null) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("The recipient account doesn't exist");
				return;
			}
			else if(recipientAccount.getUserID() != recipientUserID) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("The user selected is not the owner of the recipient account selected");
				return;
			}
			else if(recipientAccount.getID() == senderAccount.getID()) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("Sender account ID must be different from recipient account ID");
				return;
			}
			else if(amount.compareTo(senderAccount.getBalance()) == 1) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("Your account can't afford this transfer");
				return;
			}
			else if(amount.compareTo(new BigDecimal(0)) >= 0){
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("The amount must be positive");
				return;
			}
			else {
				//makes the transfer
				transferDAO.makeTransfer(amount, reason, senderID, recipientID);
			}

		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to submit this transfer, please try again");
			return;
		}

		try{
			//extracts the transfer from the database
			Transfer transfer = transferDAO.getLastTransferByUserID(user.getID());

			//prepares the response
			JsonObject respObj = new JsonObject();
			JsonObject transferObj = new JsonObject();
			transferObj.add("transfer",Serializer.serialize(transfer));
			transferObj.remove("senderID");
			transferObj.remove("recipientID");
			transferObj.remove("timestamp");
			respObj.add("transfer",transferObj);

			BigDecimal senderNewBal, recipientNewBal;
			senderNewBal = senderAccount.getBalance().subtract(amount);
			JsonObject senderObj = Serializer.serialize(senderAccount);
			senderObj.add("newBal", new JsonPrimitive(senderNewBal));
			respObj.add("sender", senderObj);

			recipientNewBal = recipientAccount.getBalance().add(amount);
			JsonObject recipientObj = Serializer.serialize(recipientAccount);
			recipientObj.add("newBal", new JsonPrimitive(recipientNewBal));
			respObj.add("recipient", recipientObj);

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(respObj.toString());
			return;
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to fetch details about your last transfer");
			return;
		}
	}
}
