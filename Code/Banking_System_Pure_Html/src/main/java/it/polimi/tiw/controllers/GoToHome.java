package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.BankAccountDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.EngineHandler;
import it.polimi.tiw.utils.Paths;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;
       
    
    public GoToHome() {
        super();
    }
    
    @Override
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    	engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
    }
    
    @Override
    public void destroy() {
    	try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	/**
	 * Loads homepage data and redirects user to Home.html
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		List<BankAccount> accounts = new ArrayList<>();
		BankAccountDAO bankAccountDAO = new BankAccountDAO(connection);
		try {

			accounts = bankAccountDAO.findAllAccountsByUserID(user.getID());
		
		} catch(SQLException exc) {
			errorRedirect(request, response, "Couldn't fetch bank accounts associated to user: " + user.getEmail());
			return;
		}
		
		if(accounts.isEmpty()) {
			errorRedirect(request, response, "No Bank accounts found associated to user: " + user.getEmail());
			return;
		}
		
		//Unescape strings
		accounts.stream().forEach((a) -> a.setName(StringEscapeUtils.unescapeJava(a.getName())));
		
		//Redirect to page with Account list
		response.setCharacterEncoding("UTF-8"); //Ensure correct accented characters visualization
		String path = Paths.pathToHomePage;
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("accounts", accounts);
		context.setVariable("cashSum", accounts.stream().map( (a) -> a.getBalance()).reduce( (sum, bal) -> sum.add(bal) ).get());;
		
		engine.process(path, context, response.getWriter());
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void errorRedirect(HttpServletRequest req, HttpServletResponse res, String error) throws IOException {
		ServletContext servletContext = getServletContext();
		req.setAttribute("backPath", Paths.pathToGoToLoginServlet);
		req.setAttribute("error", error);
		
		HttpSession session = req.getSession(false);
		session.removeAttribute("user"); //Remove user from session so filters don't mess up redirection
		
		final WebContext context = new WebContext(req, res, servletContext, req.getLocale());
				
		engine.process(Paths.pathToErrorPage, context, res.getWriter());
	}

}
