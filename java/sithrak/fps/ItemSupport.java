package sithrak.fps;

public class ItemSupport {
    // fields
    private int ItemID;
    private String IName;
    private float Price;
    private int Quantity;
    private String Category;

    // constructorz
    public ItemSupport(){}
    public ItemSupport(String name, int quantity, String cat, float price){
        this.IName = name;
        this.Price = price;
        this.Quantity = quantity;
        this.Category = cat;
    }

    // properties
    public void setItemID(int id) { this.ItemID = id; }
    public int getItemID() { return this.ItemID; }

    public void setIName(String name) { this.IName = name; }
    public String getIName() { return this.IName; }

    public void setPrice(float price) { this.Price = price; }
    public float getPrice() {return this.Price; }

    public void setQuantity(int quantity) { this.Quantity = quantity; }
    public int getQuantity() { return this.Quantity; }

    public void setCategory(String cat) { this.Category = cat; }
    public String getCategory() { return this.Category; }

}
