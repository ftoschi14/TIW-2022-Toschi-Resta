package it.polimi.tiw.dao;

import java.math.BigDecimal;
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
		String userQuery = "INSERT INTO user(username, password, name, surname) VALUES (?,?,?,?)";
		String bankAccountQuery = "INSERT INTO bank_account(userID, name, balance) VALUES(?,?,?)";
		
		int result = 0;
		PreparedStatement userPreparedStatement = null;
		PreparedStatement bankAccPreparedStatement = null;
		
		try {
			connection.setAutoCommit(false);
			
			//Preparing the statement
			userPreparedStatement = connection.prepareStatement(userQuery);
			
			userPreparedStatement.setString(1, email);
			userPreparedStatement.setString(2, password);
			userPreparedStatement.setString(3, name);
			userPreparedStatement.setString(4, surname);
			
			//Executing update
			result = userPreparedStatement.executeUpdate();
			
			//Commit first part
			connection.commit();
			
			User user = findUser(email, password);
			
			//Preparing bank account creation statement
			bankAccPreparedStatement = connection.prepareStatement(bankAccountQuery);
			
			bankAccPreparedStatement.setInt(1, user.getID());
			bankAccPreparedStatement.setString(2, "Default account");
			bankAccPreparedStatement.setBigDecimal(3, new BigDecimal(0));
			
			result = bankAccPreparedStatement.executeUpdate();
			
			connection.commit();
			
		} catch (SQLException e) {
			
			connection.rollback();
			throw new SQLException(e);
			
		} finally {
			connection.setAutoCommit(true);
			//Close PreparedStatement
			try {
				
				if(userPreparedStatement != null) {
					userPreparedStatement.close();
				}
				
				if(bankAccPreparedStatement != null) {
					bankAccPreparedStatement.close();
				}
				
			}catch (SQLException e1) {
				throw new SQLException(e1);
			}
		}
		return result;
	}
}
