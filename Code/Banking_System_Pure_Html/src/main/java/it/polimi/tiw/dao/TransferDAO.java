package it.polimi.tiw.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import it.polimi.tiw.beans.Transfer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.Timestamp;


public class TransferDAO {
private Connection connection;
	
	public TransferDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Transfer> getTransferByAccountID (int accountID)  throws SQLException{
		List<Transfer> transfers = new ArrayList<>();
		Transfer transfer = null;
		
		String query = "SELECT id, amount, timestamp, reason, senderID, recipientID FROM transfer WHERE senderID = ? or recipientID = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, accountID);
			preparedStatement.setInt(2, accountID);
			
			//Executing the query
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				transfer = new Transfer();
				transfer.setID(result.getInt("id"));
				transfer.setAmount(result.getDouble("amount"));
				transfer.setReason(result.getString("reason"));
				transfer.setSenderID(result.getInt("senderID"));
				transfer.setRecipientID(result.getInt("recipientID"));
				transfers.add(transfer);
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
		return transfers;
	}
	
	public void makeTransfer(BigDecimal amount, Timestamp timestamp, String reason, int senderid, int recipentid) throws SQLException{
		String queryInsert = "INSERT INTO transfer(amount,timestamp,reason,senderID,recipientID) VALUES (?,?,?,?,?)";
		String queryUpdateRecipient = "UPDATE bank.bank_account SET balance = balance + ? WHERE id = ?" ;
		String queryUpdateSender = "UPDATE bank.bank_account SET balance = balance - ? WHERE id = ?" ;
		PreparedStatement preparedStatementInsert = null;
		PreparedStatement preparedStatementUpdateRecipient = null;
		PreparedStatement preparedStatementUpdateSender = null;
		
		try {
			//Disabling autocommit for atomicity
			connection.setAutoCommit(false);
			
			//Preparing the statement for inserting the entry
			preparedStatementInsert = connection.prepareStatement(queryInsert);
			preparedStatementInsert.setBigDecimal(1, amount);
			preparedStatementInsert.setTimestamp(2, timestamp);
			preparedStatementInsert.setString(3, reason);
			preparedStatementInsert.setInt(4, senderid);
			preparedStatementInsert.setInt(5, recipentid);
			
			
			//Executing update
			preparedStatementInsert.executeUpdate();
			connection.commit();
			
			//Preparing the statement for updating recipient account
			preparedStatementUpdateRecipient = connection.prepareStatement(queryUpdateRecipient);
			preparedStatementUpdateRecipient.setBigDecimal(1,amount);
			preparedStatementUpdateRecipient.setInt(2, recipentid);
			
			//Executing update
			preparedStatementUpdateRecipient.executeUpdate();
			connection.commit();
			
			//Preparing the statement for updating recipient account
			preparedStatementUpdateSender = connection.prepareStatement(queryUpdateSender);
			preparedStatementUpdateSender.setBigDecimal(1,amount);
			preparedStatementUpdateSender.setInt(2, senderid);
			
			//Executing update
			preparedStatementUpdateSender.executeUpdate();
			connection.commit();
			
			
			
		} catch (SQLException e) {
			connection.rollback();
			//TO-DO: GESTIONE MIGLIORE DELLE ECCEZIONI
			throw new SQLException(e);
		} finally {
			//Enabling autocommit
			connection.setAutoCommit(true);
			
			//Close PreparedStatement
			try {
				if(preparedStatementInsert != null) {
					preparedStatementInsert.close();
				}
				if(preparedStatementUpdateRecipient != null) {
					preparedStatementUpdateRecipient.close();
				}
				if(preparedStatementUpdateSender != null) {
					preparedStatementUpdateSender.close();
				}
			}catch (SQLException e1) {
				throw new SQLException(e1);
			}
		}
		
	}
}
