package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import it.polimi.tiw.beans.Contact;

public class ContactDAO{
  private Connection connection;

  public ContactDAO(Connection connection){
    this.connection = connection;
  }

  public List<Contact> getContactsByUserID(int userID) throws SQLException{
    List<Contact> contacts = new ArrayList<>();
    Contact contact = null;
    String query = "SELECT ownerID, contactID FROM contacts WHERE ownerID = ?";

    ResultSet result = null;

    try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			//Preparing the statement
			preparedStatement.setInt(1, userID);
      //Executing the query
			result = preparedStatement.executeQuery();

      while(result.next()) {
				contact = new Contact();
				contact.setOwnerID(result.getInt("ownerID"));
        contact.setContactID(result.getInt("contactID"));
        contacts.add(contact);
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

  public int insertContact(int ownerID, int contactID) throws SQLException{
    String query = "INSERT INTO Contacts(ownerID,contactID) VALUES(?,?)";
    int result = 0;

		try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			//Preparing the statement

			preparedStatement.setInt(1, ownerID);
			preparedStatement.setInt(2, contactID);

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
