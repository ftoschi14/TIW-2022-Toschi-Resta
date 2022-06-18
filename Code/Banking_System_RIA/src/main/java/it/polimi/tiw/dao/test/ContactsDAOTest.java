package it.polimi.tiw.dao.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.tiw.beans.Contacts;
import it.polimi.tiw.dao.ContactsDAO;
import it.polimi.tiw.utils.Serializer;

class ContactsDAOTest {
	ContactsDAO dao;
	Connection con;

	@BeforeEach
	void setUp() throws Exception {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_ria?serverTimezone=UTC",
				"tiw_admin", "tiw_admin");
		
		dao = new ContactsDAO(con);
	}

	@AfterEach
	void tearDown() throws Exception {
		con.close();
	}

	@Test
	void testFetch() {
		try {
			Contacts cts = dao.getContactsByUserID(1);
			System.out.println(Serializer.serialize(cts).toString());
			
			System.out.println("Owner: " + cts.getOwnerID());
			cts.getContacts().keySet().stream().forEach((entry) -> {
				System.out.println("AccountOwner: " + entry);
				cts.getContacts().get(entry).stream().forEach((i) -> {
					System.out.println(" - Account ID: " + i);
				});
			});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
