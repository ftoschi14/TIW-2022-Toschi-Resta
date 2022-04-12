package it.polimi.tiw.beans;

public class Account {
	private int ID, userID;
	private String name;
	private double balance;
	
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
	
	public double getBalance() {
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
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
