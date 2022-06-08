package it.polimi.tiw.utils.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mysql.cj.result.SqlTimestampValueFactory;

import it.polimi.tiw.beans.BankAccount;
import it.polimi.tiw.beans.Transfer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.utils.Serializer;

class SerializerTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSerializeSingle() {
		User u = new User();
		u.setEmail("bbbabababba@gmail.com");
		u.setID(7);
		u.setName("Sara");
		u.setSurname("Resta");
		String userString = Serializer.serialize(u).toString();
		System.out.println(userString);
	}
	
	@Test
	void testSerializeList() {
		BankAccount acc1 = new BankAccount();
		acc1.setBalance(new BigDecimal(12389.99));
		acc1.setID(9);
		acc1.setName("Trullo");
		acc1.setUserID(1);

		BankAccount acc2 = new BankAccount();
		acc2.setBalance(new BigDecimal(750000));
		acc2.setID(14);
		acc2.setName("Fede");
		acc2.setUserID(3);
		
		List<BankAccount> accs = new ArrayList<BankAccount>();
		accs.add(acc1);
		accs.add(acc2);
		
		String accountsString = Serializer.serializeAll(accs, "accounts").toString();
		System.out.println(accountsString);
	}
	
	@Test
	void testSerializeTransfer() {
		Transfer t1 = new Transfer();
		t1.setAmount(new BigDecimal(7500));
		t1.setID(33);
		t1.setReason("testttest");
		t1.setRecipientID(15);
		t1.setSenderID(2);
		t1.setTimestamp(new Timestamp(0));
		System.out.println(Serializer.serialize(t1).toString());
	}

}
