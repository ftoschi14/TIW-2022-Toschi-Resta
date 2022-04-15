package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccountDAO {
	private Connection connection;
	
	public AccountDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Account findAccountByID(int ID) throws SQLException {
		Account account = null;
		
		String query = "SELECT name, balance FROM bankaccount WHERE ID = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(0, ID);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				account = new Account();
				account.setID(ID);
				account.setUserID(result.getInt("userid"));
				account.setName(result.getString("name"));
				account.setBalance(result.getDouble("balance"));
			}
			
		}catch(SQLException e) {
			//will be catched at controller level
			throw new SQLException(e);
		}finally {
			//close the resultSet
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
		return account;
		
	}
	
	public List<Account> findAllAccountsByUserID (int userID) throws SQLException{
		List<Account> accounts = new ArrayList<>();
		Account account = null;
		
		String query = "SELECT name, balance FROM bankaccount WHERE userID = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(0, userID);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				account = new Account();
				account.setID(result.getInt("id"));
				account.setUserID(userID);
				account.setName(result.getString("name"));
				account.setBalance(result.getDouble("balance"));
				accounts.add(account);
			}
			
		}catch(SQLException e) {
			//will be catched at controller level
			throw new SQLException(e);
		}finally {
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
		return accounts;
	}
	
	public int createAccount(int userid, String name, double balance) throws SQLException {
		String query = "INSERT INTO bankaccount(userID, name, balance) VALUES(?,?,?)";
		
		int result = 0;
		PreparedStatement preparedStatement = null;
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(0, userid);
			preparedStatement.setString(1, name);
			preparedStatement.setDouble(2, balance);
			
			//Executing update
			result = preparedStatement.executeUpdate();
			
		}catch(SQLException e) {
			//will be catched at controller level
			throw new SQLException(e);
		}finally {
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
		return result;
	}
	
}
