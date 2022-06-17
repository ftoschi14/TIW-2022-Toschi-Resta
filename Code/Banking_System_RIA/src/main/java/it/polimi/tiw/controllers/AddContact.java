package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.ContactsDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class AddContact
 */
@WebServlet("/AddContact")
@MultipartConfig
public class AddContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
  private Connection connection = null;

    public AddContact() {
        super();
    }

    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		UserDAO userDAO = new UserDAO(connection);
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		ContactsDAO contactDAO = new ContactsDAO(connection);

		//get, sanitize and checks params
		Integer contactID = null;
		try {
			contactID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("contactID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid contact id");
			return;
		}
		
		// Check if the specified account ID exists
		BankAccount account = null;
		
		try {
			account = bankAccountDAO.findAccountByID(contactID);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to find the specified account ID, please try again.");
			return;
		}

		if(account.getUserID() == user.getID()){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("You can't add your account as a contact!");
			return;
		}
		
		try{
			contactDAO.insertContact(user.getID(), account.getID());
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to add the contact, please try again");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
