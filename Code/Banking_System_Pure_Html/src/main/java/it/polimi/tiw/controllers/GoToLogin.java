package it.polimi.tiw.controllers;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.utils.EngineHandler;
import it.polimi.tiw.utils.Paths;

/**
 * Servlet implementation class GoToLogin
 */
@WebServlet("/GoToLogin")
public class GoToLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine engine;
       
    public GoToLogin() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	engine = EngineHandler.getHTMLTemplateEngine(getServletContext());
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		
		engine.process(Paths.pathToLoginPage, context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
