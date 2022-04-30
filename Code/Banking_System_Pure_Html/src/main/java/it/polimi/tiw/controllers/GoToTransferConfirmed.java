package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Paths;
import it.polimi.tiw.utils.EngineHandler;

import org.thymeleaf.context.WebContext;


@WebServlet("/GoToTransactionConfirmed")
public class GoToTransferConfirmed extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine engine;
    
    public GoToTransferConfirmed() {
        super();
    }
    
    /**
     * Overriding init method to use thymeleaf
     */
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		// TO-DO: FILTER TO CHECK IF THE USER IS LOGGED
		String loginpath = getServletContext().getContextPath() + Paths.pathToGoToLoginServlet;
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User recipientUser = null;
		BankAccount sender = null;
		BankAccount recipient = null;
		UserDAO userDAO = new UserDAO(connection);
		BigDecimal amount;
		String reason = (String)request.getAttribute("reason");
		
		sender = (BankAccount)request.getAttribute("sender");
		recipient = (BankAccount)request.getAttribute("recipient");
		amount = (BigDecimal)request.getAttribute("amount");
		
		try {
			recipientUser = userDAO.findUserByID(recipient.getUserID());
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error: Unable to find the user");
			return;
		}
		
		if(sender == null || recipient == null || recipientUser == null || amount == null || reason == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong parameters");
			return;
		}
		
		//redirect to the page with the account details
		String path = Paths.pathToTransferConfirmedPage;
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("senderAccount", sender);
		context.setVariable("recipientAccount", recipient);
		context.setVariable("oldBalanceSenderAccount", sender.getBalance().add(amount));
		context.setVariable("oldBalanceRecipientAccount", recipient.getBalance().subtract(amount));
		context.setVariable("reason", reason);
		context.setVariable("amount", amount);
		context.setVariable("recipientAccountUser", recipientUser);
		engine.process(path, context, response.getWriter());
		
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
