package it.polimi.tiw.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transfer {
	private int ID, senderID, recipientID;
	private BigDecimal amount;
	private Timestamp timestamp;
	private String reason;
	
	/*
	 * Getters
	 */
	
	public int getID() {
		return ID;
	}
	
	public int getSenderID() {
		return senderID;
	}
	
	public int getRecipientID() {
		return recipientID;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public String getReason() {
		return reason;
	}
	
	/*
	 * Setters
	 */
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}
	
	public void setRecipientID(int recipientID) {
		this.recipientID = recipientID;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}

}
