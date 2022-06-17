package it.polimi.tiw.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Contacts {

  private int ownerID;
  private Map<Integer, Set<Integer>> contacts = new HashMap<>();

  /**
  *Getters
  **/

  public int getOwnerID(){
    return ownerID;
  }

  public Map<Integer, Set<Integer>> getContacts() {
	  return contacts;
  }

  /**
  *Setters
  **/

  public void setOwnerID(int ownerID){
    this.ownerID = ownerID;
  }

  public void addContact(int accountOwnerID, int accountID) {
	  if(contacts.containsKey(accountOwnerID)) {
		  contacts.get(accountOwnerID).add(accountID);
	  } else {
		  Set<Integer> set = new HashSet<>();
		  set.add(accountID);
		  contacts.put(accountOwnerID, set);
	  }
  }
}
