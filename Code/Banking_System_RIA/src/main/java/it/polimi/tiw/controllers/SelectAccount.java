package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.TransferDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Serializer;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class SelectAccount
 */
@WebServlet("/SelectAccount")
public class SelectAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public SelectAccount() {
        super();
    }

    /**
     * Overriding init method to use thymeleaf
     */
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}

		//if the account exists for the user obtain the details
		try {
			bankAccount = bankAccountDAO.findAccountByID(bankAccountID);
			if (bankAccount == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("This bank account doesn't exist");
				return;
			}
			else if (bankAccount.getUserID() != user.getID()){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("This bank account is not yours");
				return;
			}

		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to fetch information about this Bank account, please try again");
			return;
		}

		//get the transfers for the selected account
		try {
			transfers = transferDAO.getTransferByAccountID(bankAccountID);
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to fetch transfers for this Bank account, please try again");
			return;
		}
		//Unescape strings
		bankAccount.setName(StringEscapeUtils.unescapeJava(bankAccount.getName()));
		
		for(Transfer transfer : transfers) {
			transfer.setReason(StringEscapeUtils.unescapeJava(transfer.getReason()));
		}

		//Preparing the response
		JsonObject accountObject = Serializer.serializeAll(transfers,"transfers");
		accountObject.add("account", Serializer.serialize(bankAccount));
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(accountObject.toString());
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
