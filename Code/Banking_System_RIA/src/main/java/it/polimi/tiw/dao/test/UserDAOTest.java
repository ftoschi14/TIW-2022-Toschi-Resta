package it.polimi.tiw.dao.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class UserDAOTest {
	UserDAO dao;
	Connection con;

	@BeforeEach
	void setUp() throws Exception {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank?serverTimezone=UTC",
				"tiw_admin", "tiw_admin");
		
		dao = new UserDAO(con);
	}

	@AfterEach
	void tearDown() throws Exception {
		con.close();
	}

	@Test
	void testFindUser() {
		try {
			User user = dao.findUser("federico.toschi@mail.polimi.it", "pw002");
			System.out.println("Test: " + user.getEmail() + ", " + user.getID() + ", " + user.getName());
			assertNotNull(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testFindUserByID() {
		try {
			User user = dao.findUserByID(2);
			assertNotNull(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testRegisterUser_New() {
		String email = "Federico5@toschi.it"; //Change me
		String pw = "abcde";
		String name = "Federico";
		String surname = "Toschi";
		
		try {
			int res = dao.registerUser(email, pw, name, surname);
			assertEquals(1, res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	void testRegisterUser_AlreadyExisting() {
		String email = "Federico4@toschi.it";
		String pw = "abcde";
		String name = "Federico";
		String surname = "Toschi";
		
		try {
			int res = dao.registerUser(email, pw, name, surname);
			assertEquals(1, res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

