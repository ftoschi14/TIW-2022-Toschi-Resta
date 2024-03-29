package it.polimi.tiw.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import it.polimi.tiw.beans.Transfer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class TransferDAO {
private Connection connection;

	public TransferDAO(Connection connection) {
		this.connection = connection;
	}

	public Transfer getLastTransferByUserID(int userID) throws SQLException{
		Transfer transfer = null;
		String query = "SELECT senderID, recipientID, timestamp, reason, amount FROM transfer WHERE senderID IN (SELECT id FROM bank_account WHERE userID = ?) ORDER BY timestamp DESC LIMIT 1";
		ResultSet result = null;

		try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			//Preparing the statement
			preparedStatement.setInt(1, userID);

			//Executing the query
			result = preparedStatement.executeQuery();
			if(result.isBeforeFirst()){
				// Only 1 match expected
				result.next();
				transfer = new Transfer();
				transfer.setSenderID(result.getInt("senderID"));
				transfer.setRecipientID(result.getInt("recipientID"));
				transfer.setTimestamp(result.getTimestamp("timestamp"));
				transfer.setReason(result.getString("reason"));
				transfer.setAmount(result.getBigDecimal("amount"));
			}

			preparedStatement.close();

			if(result != null){
				result.close();
			}
		}
		return transfer;
	}

	public List<Transfer> getTransferByAccountID (int accountID)  throws SQLException{
		List<Transfer> transfers = new ArrayList<>();
		Transfer transfer = null;

		String query = "SELECT senderID, recipientID, timestamp, reason, amount FROM transfer WHERE senderID = ? or recipientID = ? ORDER BY timestamp DESC";

		ResultSet result = null;


		try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			//Preparing the statement
			preparedStatement.setInt(1, accountID);
			preparedStatement.setInt(2, accountID);

			//Executing the query
			result = preparedStatement.executeQuery();

			while(result.next()) {
				transfer = new Transfer();
				transfer.setSenderID(result.getInt("senderID"));
				transfer.setRecipientID(result.getInt("recipientID"));
				transfer.setTimestamp(result.getTimestamp("timestamp"));
				transfer.setReason(result.getString("reason"));
				transfer.setAmount(result.getBigDecimal("amount"));
				transfers.add(transfer);
			}

			//close the result set
			if(result != null) {
				result.close();
			}

			//close the prepared statement
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
		return transfers;
	}

	public void makeTransfer(BigDecimal amount, String reason, int senderid, int recipentid) throws SQLException{
		String queryInsert = "INSERT INTO transfer(amount,reason,senderID,recipientID) VALUES (?,?,?,?)";
		String queryUpdateRecipient = "UPDATE bank_account SET balance = balance + ? WHERE id = ?" ;
		String queryUpdateSender = "UPDATE bank_account SET balance = balance - ? WHERE id = ?" ;
		PreparedStatement preparedStatementInsert = null;
		PreparedStatement preparedStatementUpdateRecipient = null;
		PreparedStatement preparedStatementUpdateSender = null;

		try {
			//Disabling autocommit for atomicity
			connection.setAutoCommit(false);

			//Preparing the statement for insertion of entry in the Transfers
			preparedStatementInsert = connection.prepareStatement(queryInsert);
			preparedStatementInsert.setBigDecimal(1, amount);
			preparedStatementInsert.setString(2, reason);
			preparedStatementInsert.setInt(3, senderid);
			preparedStatementInsert.setInt(4, recipentid);


			//Executing update
			preparedStatementInsert.executeUpdate();
			connection.commit();

			//close the prepared statement
			if(preparedStatementInsert != null) {
				preparedStatementInsert.close();
			}

			//Preparing the statement for updating of recipient account
			preparedStatementUpdateRecipient = connection.prepareStatement(queryUpdateRecipient);
			preparedStatementUpdateRecipient.setBigDecimal(1,amount);
			preparedStatementUpdateRecipient.setInt(2, recipentid);

			//Executing update
			preparedStatementUpdateRecipient.executeUpdate();
			connection.commit();

			//close the prepared statement
			if(preparedStatementUpdateRecipient != null) {
				preparedStatementUpdateRecipient.close();
			}

			//Preparing the statement for updating of sender account
			preparedStatementUpdateSender = connection.prepareStatement(queryUpdateSender);
			preparedStatementUpdateSender.setBigDecimal(1,amount);
			preparedStatementUpdateSender.setInt(2, senderid);

			//Executing update
			preparedStatementUpdateSender.executeUpdate();
			connection.commit();

			//close the prepared statement
			if(preparedStatementUpdateSender != null) {
				preparedStatementUpdateSender.close();
			}
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException(e.getMessage(), e.getSQLState(), e.getErrorCode(), e.getCause());
		} finally {
			//Re-Enabling autocommit
			connection.setAutoCommit(true);
		}

	}
}
