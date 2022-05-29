package it.polimi.tiw.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
/**
 * Handles the database connection 
 */
public class ConnectionHandler {
	
	/**
	 * Establishes a connection with the database
	 * 
	 * @param context the servlet context
	 * @return the connection established with the database
	 * @throws UnavailableException 
	 */
	public static Connection getConnection(ServletContext context) throws UnavailableException{
		Connection connection = null;
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		return connection;
	}
	
	/**
	 * Closes the connection with the database
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
