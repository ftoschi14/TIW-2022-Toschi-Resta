package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User findUser(String email, String password) throws SQLException {
		User user = null;
		
		String query = "SELECT id, name, surname FROM user WHERE username = ? AND password = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		//Preparing the statement
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			if(!result.isBeforeFirst()) { //ResultSet contains no rows
				return null;
			} else {
				//Credentials can only match 1 user. 
				result.next();
				user = new User();
				user.setEmail(email);
				user.setID(result.getInt("id"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
			}
				
		} catch (SQLException e) {
			//Will be catched at controller level
			throw new SQLException(e);
		} finally {
			//Close ResultSet
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				//Will be catched at controller level
				throw new SQLException(e1);
			}
			//Close PreparedStatement
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				//Will be catched at controller level
				throw new SQLException(e2);
			}
		}
		return user;
	}
	
	public User findUserByID(int ID) throws SQLException {
		User user = null;
		
		String query = "SELECT username, name, surname FROM user WHERE id = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		//Preparing the statement
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ID);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			if(!result.isBeforeFirst()) {
				return null;
			} else {
				// Only 1 match expected
				result.next();
				user = new User();
				user.setID(ID);
				user.setEmail(result.getString("username"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
			}
			
		} catch (SQLException e) {
			//Will be catched at controller level
			throw new SQLException(e);
		} finally {
			//Close ResultSet
			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e1) {
				//Will be catched at controller level
				throw new SQLException(e1);
			}
			//Close PreparedStatement
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e2) {
				//Will be catched at controller level
				throw new SQLException(e2);
			}
		}
		return user;
	}
	
	public boolean isEmailTaken(String email) throws SQLException {
		boolean taken = false;
		String query = "SELECT id FROM user WHERE username = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		//Preparing the statement
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			if(result.isBeforeFirst()) {
				// Matched some other user
				taken = true;
			}
			
		} catch (SQLException e) {
			//Will be catched at controller level
			throw new SQLException(e);
		} finally {
			//Close ResultSet
			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e1) {
				//Will be catched at controller level
				throw new SQLException(e1);
			}
			//Close PreparedStatement
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e2) {
				//Will be catched at controller level
				throw new SQLException(e2);
			}
		}
		return taken;
	}
	
	public int registerUser(String email, String password, String name, String surname) throws SQLException {
		String query = "INSERT INTO user(username, password, name, surname) VALUES (?,?,?,?)";
		
		int result = 0;
		PreparedStatement preparedStatement = null;
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, name);
			preparedStatement.setString(4, surname);
			
			//Executing update
			result = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			
			//Close PreparedStatement
			try {
				if(preparedStatement != null) {
					preparedStatement.close();
				}
			}catch (SQLException e1) {
				throw new SQLException(e1);
			}
		}
		return result;
	}
}
