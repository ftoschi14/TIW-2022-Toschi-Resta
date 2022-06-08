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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Contact;
import it.polimi.tiw.dao.ContactDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Serializer;

/**
 * Servlet implementation class GetContacts
 */
@WebServlet("/GetContacts")
public class GetContacts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetContacts() {
        super();
    }
    
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		ContactDAO contactDAO = new ContactDAO(connection);
		List<Contact> contacts = new ArrayList<>();
		try{
			contacts = contactDAO.getContactsByUserID(user.getID());
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to fetch contacts for this Bank account, please try again");
			return;
		}

		//Preparing the response
		String contactsResponse = Serializer.serializeAll(contacts,"contacts").toString();
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(contactsResponse);
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
