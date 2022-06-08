package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.dao.ContactDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class AddContact
 */
@WebServlet("/AddContact")
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
		ContactDAO contactDAO = new ContactDAO(connection);

		//get, sanitize and checks params
		Integer contactID = null;
		try {
			contactID = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("contactID")));
		}catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid contact id");
			return;
		}

		//Creates a new user with the contactID given to see if the user exists in the data base
		User contact = null;
		try{
			contact = userDAO.findUserByID(contactID);
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to fetch details about your last transfer");
			return;
		}

		//Checks if the userID exists in the data base
		if(contact == null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The user doesn't exist");
			return;
		}
		else if(contact.getID() == user.getID()){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("You can't add your userID in your contact");
			return;
		}
		else{
			try{
				contactDAO.insertContact(user.getID(),contact.getID());
			}catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Unable to add the contact, please try again");
				return;
			}
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
