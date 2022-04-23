package it.polimi.tiw.dao.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.dao.TransferDAO;

class TransferDAOTest {
	TransferDAO dao;
	Connection con;

	@BeforeEach
	void setUp() throws Exception {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank?serverTimezone=UTC",
				"tiw_admin", "tiw_admin");
		
		dao = new TransferDAO(con);
	}

	@AfterEach
	void tearDown() throws Exception {
		con.close();
	}

	@Test
	void testGetTransferByAccountID() {
		try {
			List<Transfer> transfers = dao.getTransferByAccountID(1);
			assertTrue(transfers.size() > 0);
			
			for(Transfer t : transfers) {
				System.out.println("Transfer: " + t.getID() + ", AMT: " + t.getAmount() + ", Reason: " + t.getReason()
				 + ", From: " + t.getSenderID() + ", To: " + t.getRecipientID());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testMakeTransfer() {
		fail("Not yet implemented");
	}

}
