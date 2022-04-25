package it.polimi.tiw.beans;

import java.math.BigDecimal;

public class BankAccount {
	private int ID, userID;
	private String name;
	private BigDecimal balance;
	
	/*
	 * Getters
	 */
	
	public int getID() {
		return ID;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public String getName() {
		return name;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	/*
	 * Setters
	 */
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
