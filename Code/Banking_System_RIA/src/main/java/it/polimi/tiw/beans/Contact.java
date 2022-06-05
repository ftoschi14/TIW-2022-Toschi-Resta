package it.polimi.tiw.beans;

public class Contact {

  private int ownerID;
  private int contactID;

  /**
  *Getters
  **/

  public int getOwnerID(){
    return ownerID;
  }

  public int getContactID(){
    return contactID;
  }

  /**
  *Setters
  **/

  public void setOwnerID(int ownerID){
    this.ownerID = ownerID;
  }

  public void setContactID(int contactID){
    this.contactID = contactID;
  }
}
