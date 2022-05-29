package it.polimi.tiw.dao.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.dao.BankAccountDAO;

class BankAccountDAOTest {
	BankAccountDAO dao;
	Connection con;

	@BeforeEach
	void setUp() throws Exception {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank?serverTimezone=UTC",
				"tiw_admin", "tiw_admin");
		
		dao = new BankAccountDAO(con);
	}

	@AfterEach
	void tearDown() throws Exception {
		con.close();
	}

	@Test
	void testFindAccountByID() {
		try {
			BankAccount acc = dao.findAccountByID(1);
			System.out.println("Account: " + acc.getName() + ", " + acc.getID() + ", of User: " + acc.getUserID() + ", with balance: " + acc.getBalance());
			assertNotNull(acc);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testFindAllAccountsByUserID() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateAccount() {
		fail("Not yet implemented");
	}

}
