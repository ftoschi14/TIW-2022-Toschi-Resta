package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.utils.ConnectionHandler;


/**
 * Servlet implementation class CreateAccount
 */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private String regex = "^\\w+[\\w|\\s]*"; //Match first character as word, then allow whitespace
	private Pattern pattern;
       
    public CreateAccount() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
		pattern = Pattern.compile(regex);
    }
    
    @Override
    public void destroy() {
    	try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		String accountName = StringEscapeUtils.escapeJava(request.getParameter("accountName"));
		
		// Basic nullcheck
		if(accountName == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please type a name for the new account");
			return;
		}
		
		// Check for valid account name (at least one non-whitespace character)
		Matcher matcher = pattern.matcher(accountName);
		if(!matcher.matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad Account name: No special characters allowed");
			return;
		}
		
		// Proceed with BankAccount creation
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		
		try {
			bankAccountDAO.createAccount(user.getID(), accountName, new BigDecimal(0));
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unable to create Bank Account: " + e.getSQLState());
			return;
		}
		
		//String path = getServletContext().getContextPath() + Paths.pathToGoToHomeServlet;
		//response.sendRedirect(path);
	}

}
