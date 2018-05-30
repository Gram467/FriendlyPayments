package sithrak.fps;

public class PurchSupport {
    //fields
    private int PurchaseID;
    private String Description;

    // constructor
    public PurchSupport(){}
    public PurchSupport(String desc){
        this.Description = desc;
    }

    // properties
    public void setPurchaseID(int pid) { this.PurchaseID = pid; }
    public int getPurchaseID() { return this.PurchaseID; }

    public void setDescription(String desc) { this.Description = desc; }
    public String getDescription() { return this.Description; }

}
