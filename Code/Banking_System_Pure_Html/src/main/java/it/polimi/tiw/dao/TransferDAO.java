package it.polimi.tiw.dao;

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
		
		String query = "SELECT amount, timestamp, reason, senderID, recipientID FROM transfer WHERE idoriginaccount = ? or iddestinationaccount = ?";
		
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(0, accountID);
			preparedStatement.setInt(1, accountID);
			
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
	
	public int makeTransfer(double amount, Timestamp timestamp, String reason, int senderid, int recipentid) throws SQLException{
		String query = "INSERT INTO transfer(amount,timestamp,reason,senderID,recipientID) VALUES (?,?,?,?,?)";
		int result = 0;
		PreparedStatement preparedStatement = null;
		
		try {
			//Preparing the statement
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setDouble(0, amount);
			preparedStatement.setTimestamp(1, timestamp);
			preparedStatement.setString(2, reason);
			preparedStatement.setInt(3, senderid);
			preparedStatement.setInt(4, recipentid);
			
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
