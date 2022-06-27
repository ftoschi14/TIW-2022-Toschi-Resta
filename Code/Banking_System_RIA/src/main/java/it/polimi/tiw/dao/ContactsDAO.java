package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import it.polimi.tiw.beans.Contacts;

public class ContactsDAO{
  private Connection connection;

  public ContactsDAO(Connection connection){
    this.connection = connection;
  }

  public Contacts getContactsByUserID(int userID) throws SQLException{
    Contacts contacts = new Contacts();
    contacts.setOwnerID(userID);

    String query = "SELECT b.userID, c.accountID FROM contacts AS c JOIN bank_account AS b ON c.accountID = b.ID WHERE c.ownerID = ?";

    ResultSet result = null;

    try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			//Preparing the statement
			preparedStatement.setInt(1, userID);
		    //Executing the query
			result = preparedStatement.executeQuery();

			while(result.next()) {
				contacts.addContact(result.getInt("userID"), result.getInt("accountID"));
			}

			//close the statement
			if(result != null) {
				result.close();
			}

			//close the result set
			if(preparedStatement != null) {
				preparedStatement.close();
			}
	  }
		return contacts;
  }
  
  public boolean isContact(int ownerID, int accountID) throws SQLException {
	  String query = "SELECT * FROM contacts WHERE ownerID = ? AND accountID = ?";
	  ResultSet result = null;
	  boolean isContact = false;
	  
	  try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
		preparedStatement.setInt(1, ownerID);
		preparedStatement.setInt(2, accountID);
		
		result = preparedStatement.executeQuery();
		
		if(result.isBeforeFirst()) 
			isContact = true;
		
		if(result != null) {
			result.close();
		}
		
		if(preparedStatement != null) {
			preparedStatement.close();
		}
		
	  }
	  
	  return isContact;
  }

  public int insertContact(int ownerID, int accountID) throws SQLException{
    String query = "INSERT INTO Contacts(ownerID,accountID) VALUES(?,?)";
    int result = 0;

		try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			//Preparing the statement

			preparedStatement.setInt(1, ownerID);
			preparedStatement.setInt(2, accountID);

			//Executing update
			result = preparedStatement.executeUpdate();

			//Close the prepared statement
			if(preparedStatement != null) {
				preparedStatement.close();
			}


		}
		return result;
  }
}
