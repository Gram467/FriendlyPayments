package sithrak.fps;

public class HelperTable {

    //fields
    private int PurchaseID;
    private int ItemID;
    private int ContactID;

    // constructor
    public HelperTable(){}
    public HelperTable(int id, int itemID, int contID){
        this.PurchaseID = id;
        this.ItemID = itemID;
        this.ContactID = contID;
    }

    // properties
    public void setItemID(int id) { this.ItemID = id; }
    public int getItemID() { return this.ItemID; }

    public void setContactID(int cid) { this.ContactID = cid; }
    public int getContactID() { return this.ContactID; }

    public void setPurchaseID(int pid) { this.PurchaseID = pid; }
    public int getPurchaseID() { return this.PurchaseID; }
}
