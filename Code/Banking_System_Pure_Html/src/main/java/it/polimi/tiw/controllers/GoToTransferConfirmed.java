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
import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.dao.TransferDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Paths;
import it.polimi.tiw.utils.EngineHandler;

import org.thymeleaf.context.WebContext;


@WebServlet("/GoToTransferConfirmed")
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
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		TransferDAO transferDAO = new TransferDAO(connection);
		BankAccount sender;
		BankAccount recipient;
		Transfer transfer;
		BigDecimal amount;
		String reason;

		// Fetch last transfer by the sender and display it
		try{
			transfer = transferDAO.getLastTransferByUserID(user.getID());
			if(transfer == null){
				errorRedirect(request, response, "Unable to get the transfer from the server");
				return;
			}

			sender = bankAccountDAO.findAccountByID(transfer.getSenderID());
			if(sender == null){
				errorRedirect(request, response,"Unable to get the details about the sender account from the server");
				return;
			}

			recipient = bankAccountDAO.findAccountByID(transfer.getRecipientID());
			if(recipient == null){
				errorRedirect(request, response,"Unable to get the details about the recipient account from the server");
				return;
			}

			amount = transfer.getAmount();
			if(amount == null){
				errorRedirect(request, response,"Unable to get the details of the transfer from the server");
				return;
			}

			reason = transfer.getReason();
			if(reason == null){
				errorRedirect(request, response,"Unable to get the details of the transfer from the server");
				return;
			}
		}catch(SQLException e){
			errorRedirect(request, response,"Database error, unable to get data");
			e.printStackTrace();
			return;
		}
		//Unescape strings
		transfer.setReason(StringEscapeUtils.unescapeJava(transfer.getReason()));
		sender.setName(StringEscapeUtils.unescapeJava(sender.getName()));
		recipient.setName(StringEscapeUtils.unescapeJava(recipient.getName()));

		//redirect to the page with the account details
		String path = Paths.pathToTransferConfirmedPage;
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("sender", sender);
		context.setVariable("recipient", recipient);
		context.setVariable("oldBalanceSenderAccount", sender.getBalance().add(amount));
		context.setVariable("oldBalanceRecipientAccount", recipient.getBalance().subtract(amount));
		context.setVariable("reason", reason);
		context.setVariable("amount", amount);
		//context.setVariable("recipientAccountUser", recipient.getUserID());
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

	private void errorRedirect(HttpServletRequest req, HttpServletResponse res, String error) throws IOException {
		ServletContext servletContext = getServletContext();
		req.setAttribute("backPath", Paths.pathToGoToHomeServlet);
		req.setAttribute("error", error);

		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());

		engine.process(Paths.pathToErrorPage, context, res.getWriter());
	}
}
