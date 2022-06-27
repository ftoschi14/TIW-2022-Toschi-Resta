package it.polimi.tiw.utils;

import javax.servlet.ServletContext;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class EngineHandler {
	
	/**
	 * Returns an HTML <code>TemplateEngine</code> for the specified <code>ServletContext</code> 
	 * 
	 * @param context the <code>ServletContext</code> for this template engine.
	 * @return a <code>TemplateEngine</code>
	 */
	public static TemplateEngine getHTMLTemplateEngine(ServletContext context) {
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setSuffix(".html");
		
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		
		
		return templateEngine;
	}

}
