package it.polimi.tiw.dao;

import it.polimi.tiw.beans.BankAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BankAccountDAO {
	private Connection connection;
	
	public BankAccountDAO(Connection connection) {
		this.connection = connection;
	}
	
	public BankAccount findAccountByID(int ID) throws SQLException {
		BankAccount account = null;
		
		String query = "SELECT userid, name, balance FROM bank_account WHERE ID = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ID);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				account = new BankAccount();
				account.setID(ID);
				account.setUserID(result.getInt("userid"));
				account.setName(result.getString("name"));
				account.setBalance(result.getBigDecimal("balance"));
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
	
	public List<BankAccount> findAllAccountsByUserID (int userID) throws SQLException{
		List<BankAccount> accounts = new ArrayList<>();
		BankAccount account = null;
		
		String query = "SELECT id, name, balance FROM bank_account WHERE userID = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, userID);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				account = new BankAccount();
				account.setID(result.getInt("id"));
				account.setUserID(userID);
				account.setName(result.getString("name"));
				account.setBalance(result.getBigDecimal("balance"));
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
	
	public int createAccount(int userid, String name, BigDecimal balance) throws SQLException {
		String query = "INSERT INTO bank_account(userID, name, balance) VALUES(?,?,?)";
		
		int result = 0;
		PreparedStatement preparedStatement = null;
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, userid);
			preparedStatement.setString(2, name);
			preparedStatement.setBigDecimal(3, balance);
			
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
