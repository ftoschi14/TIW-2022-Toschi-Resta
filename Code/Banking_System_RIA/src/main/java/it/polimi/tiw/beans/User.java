package it.polimi.tiw.beans;

public class User {
	private int ID;
	private String email, name, surname;

	/*
	 * Getters
	 */

	public int getID() {
		return ID;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	/*
	 * Setters
	 */

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}
