package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.JsonObject;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Serializer;

/**
 * Servlet implementation class GetAccounts
 */
@WebServlet("/GetAccounts")
public class GetAccounts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    
    public GetAccounts() {
        super();
    }
    
    @Override
    	public void init() throws ServletException {
    		connection = ConnectionHandler.getConnection(getServletContext());
    	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		List<BankAccount> accounts;
		try {
			accounts = bankAccountDAO.findAllAccountsByUserID(user.getID());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to fetch accounts for user " + user.getID() + ": " + e.getSQLState());
			return;
		}
		//Unescape strings
		accounts.stream().forEach((a) -> a.setName(StringEscapeUtils.unescapeJava(a.getName())));
		
		//Preparing the json object for the response
		JsonObject jsonAccounts = Serializer.serializeAll(accounts, "accounts");
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonAccounts.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
