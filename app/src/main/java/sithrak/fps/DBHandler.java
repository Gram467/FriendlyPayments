package sithrak.fps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;

/**
 * Created by Sithrak on 18.04.2018..
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database column names
    private static final int DB_VER = 1;
    private static final String DB_NAME = "Purchases.db";
    public static final String PURC_TABLE = "Purchases";
    public static final String COL_ID = "PurchaseID";
    public static final String COL_DESC = "Description";

    public static final String COL_CONTID = "ContactID";
    public static final String CON_TABLE = "Contacts";
    public static final String CON_FNAME = "Name";
    public static final String CON_LNAME = "Surname";
    public static final String CON_NUM = "Number";
    public static final String CON_EMAIL = "Email"; // Not thinking of implementing, yet
    public static final String CON_BNAME = "BankName"; //For User Details
    public static final String CON_BNUM = "BankAccountNumber";

    public static final String COL_ITEMID = "ItemID";
    public static final String ITEM_TABLE = "Items";
    public static final String ITEM_NAME = "Name";
    public static final String ITEM_PRICE = "Price";
    public static final String ITEM_COUNT = "Quantity";
    public static final String ITEM_PIC = "PictureID";

    public static final String H_TABLE = "HelperTable";

    // a workaround to create frames for History
    public static int unknown;
//    public static Integer pnum;
    public static PurchSupport[] purchases = new PurchSupport[unknown];
    public static ItemSupport[] items = new ItemSupport[unknown];
    public static String PurchDesc;
    public static int pid[] = new int[1024];

    // a one of a kind description to make sure that the correct purchase has been selected encoded in base64
    public static String impossible = "V2h5IHdvdWxkIGFueW9uZSBldmVyIG1ha2UgYSBkZXNjcmlwdGlvbiBzdWNoIGFzIHRoaXMhPw=="; // Why would anyone ever make a description such as this!?

    //initialize DB
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VER);
    }

    //Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CON_TABLE);
        String CREATE_USER_TABLE = "CREATE TABLE " + CON_TABLE + "(" +
                COL_CONTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CON_FNAME + " TEXT, " +
                CON_LNAME + " TEXT, " +
                CON_NUM + " INTEGER, " +
                CON_EMAIL + " TEXT, " +
                CON_BNAME + " TEXT, " +
                CON_BNUM + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);


        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        String CREATE_I_TABLE = "CREATE TABLE " + ITEM_TABLE + "(" +
                COL_ITEMID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_PRICE + " FLOAT, " +
                ITEM_COUNT + " INTEGER, " +
                ITEM_PIC + " TEXT" + ")";
        db.execSQL(CREATE_I_TABLE);


        db.execSQL("DROP TABLE IF EXISTS " + PURC_TABLE);
        String CREATE_MAIN_TABLE = "CREATE TABLE " + PURC_TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESC + " TEXT " +  ")";
        db.execSQL(CREATE_MAIN_TABLE);


        db.execSQL("DROP TABLE IF EXISTS " + H_TABLE);
        String CREATE_H_TABLE = "CREATE TABLE " + H_TABLE + "(" +
                COL_ITEMID + " INTEGER, " +
                COL_ID + " INTEGER, " +
//                COL_CONTID + " INTEGER, " +
                "PRIMARY KEY( " + COL_ITEMID + ", " /*+ COL_CONTID + ", " */ + COL_ID + ")" + ")";
        db.execSQL(CREATE_H_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }




    public void loadItemsForPurchase(int PiD) {

        String PItems = "Select * From " + H_TABLE + " WHERE " + COL_ID + " = '" + PiD + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(PItems, null);

        pid = new int[1024];
        unknown = 0; // Needs to be cleared
        int pnum = 0;

        while (cursor.moveToNext()) {
            pid[pnum] = cursor.getInt(0);
            unknown++;
            pnum++;
        }

        cursor.close();
        db.close();
    }

    public String loadItemName(int itemid) {

        String ItemIDs = "Select * FROM " + ITEM_TABLE + " WHERE " + COL_ITEMID + " = '" + itemid + "'";
        String name = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ItemIDs, null);

        while (cursor.moveToNext()) {
            name = cursor.getString(1);
        }

        cursor.close();
        db.close();

        return name;
    }

    public float loadItemPrice(int itemid) {

        String ItemIDs = "Select * FROM " + ITEM_TABLE + " WHERE " + COL_ITEMID + " = '" + itemid + "'";
        float price = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ItemIDs, null);

        while (cursor.moveToNext()) {
            price = cursor.getFloat(2);
        }

        cursor.close();
        db.close();

        return price;
    }

    public int loadItemQuantity(int itemid) {

        String ItemIDs = "Select * FROM " + ITEM_TABLE + " WHERE " + COL_ITEMID + " = '" + itemid + "'";
        int quantity = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ItemIDs, null);

        while (cursor.moveToNext()) {
            quantity = cursor.getInt(3);
        }

        cursor.close();
        db.close();

        return quantity;
    }

    public String loadItemCat(int itemid) {

        String ItemIDs = "Select * FROM " + ITEM_TABLE + " WHERE " + COL_ITEMID + " = '" + itemid + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ItemIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(4);
        }

        cursor.close();
        db.close();

        return val;
    }


    // Adds itemID and ContactID for current purchase (CRUD - Create [HelperTable])
    public void addToHelper(HelperTable help) {

        ContentValues values = new ContentValues();
            values.put(COL_ID, help.getPurchaseID());
            values.put(COL_ITEMID, help.getItemID());
//        values.put(COL_CONTID, help.getContactID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(H_TABLE, null, values);

        db.close();
    }

    // Adds item details to item table (CRUD - Create [ItemsTable])
    public void addItem(ItemSupport item) {

        ContentValues values = new ContentValues();
            values.put(ITEM_NAME, item.getIName());
            values.put(ITEM_PRICE, item.getPrice());
            values.put(ITEM_COUNT, item.getQuantity());
            values.put(ITEM_PIC, item.getCategory());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(ITEM_TABLE, null, values);

        db.close();
    }

    // Adds contact details to contact table (CRUD - Create [ContactsTable])
    public void addContact(ContactSupport contact) {

        ContentValues values = new ContentValues();
            values.put(CON_FNAME, contact.getCName());
            values.put(CON_LNAME, contact.getCSurname());
            values.put(CON_NUM, contact.getNumber());
            values.put(CON_EMAIL, contact.getEmail());
            values.put(CON_BNAME, contact.getBName());
            values.put(CON_BNUM, contact.getBNum());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(CON_TABLE, null, values);

        db.close();
    }

    // Creates the purchase at the launch of the view so that there's a PurchaseID to which to assign the rest of the objects
    public void addPurchase(PurchSupport cart) {

        NewPurchase.Items = new int[1024];
        NewPurchase.aNumberForUpdate = 1;
        AddItems.PurchaseItemPosition = 0;

        ContentValues values = new ContentValues();
            values.put(COL_DESC, impossible);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(PURC_TABLE, null, values);

        db.close();
    }

    // gets amount of purchases created for frame count in History
    public void getHistory() {

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + PURC_TABLE;
        int pnum = 0;
        unknown = 0; // Has to be cleared, otherwise there's empty frames every time the History view gets loaded again

        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()){
            pid[pnum] = cursor.getInt(0);
            unknown++;
            pnum++;
        }
    }

    //gets specific purchase
    public String getDesc(int num) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * FROM " + PURC_TABLE + " WHERE " + COL_ID + " = '" + num + "'";
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            PurchDesc = cursor.getString(1);
        }
        return PurchDesc;
    }


    // getting description for each frame in History
    public String getPurchDesc(int num) {

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * FROM " + PURC_TABLE + " WHERE " + COL_ID + " = '" + pid[num] + "'";
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()){
            PurchDesc = cursor.getString(1);
        }
        return PurchDesc;
    }

    // creates a new frame for each purchase in History
    public PurchSupport[] makePurchases() {

        purchases = new PurchSupport[unknown];
        for (int i = 0; i < unknown; i++){
            purchases[i] = new PurchSupport();
            purchases[i].getDescription();
        }
        return purchases;
    }

    public ItemSupport[] makeItems() {

        items = new ItemSupport[unknown];
        for (int i = 0; i < unknown; i++) {
            items[i] = new ItemSupport();
        }
        return items;
    }

    public void updatePurchase (int ID, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
//        value.put(COL_ID, ID);
        value.put(COL_DESC, desc);
        db.update(PURC_TABLE, value, COL_ID + " = " + ID, null);

        db.close();
    }

    public void updateItem (int ID, String name, float price, int quantity, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
            val.put(ITEM_NAME, name);
            val.put(ITEM_PRICE, price);
            val.put(ITEM_COUNT, quantity);
            val.put(ITEM_PIC, category);

        db.update(ITEM_TABLE, val, COL_ITEMID + " = " + ID, null);
        db.close();
    }

    public void DeleteP(int PiD) {

        // get items of current purchase
        loadItemsForPurchase(PiD);
        deletePurchase(PiD);

        deletePurchaseFromHelper(PiD);

        for (int i = 0; i < pid.length; i++) {
            if (pid[i] != 0) {
                deleteItem(pid[i]);
            }
        }

        // Same for contacts
        // delteContacts(PiD);
    }

    public void deletePurchaseFromHelper(int ID) {
        String Query = "Select * From " + H_TABLE + " WHERE " + COL_ID + " = '" + String.valueOf(ID) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);

        HelperTable helper = new HelperTable();
        while (cursor.moveToNext()) {
            helper.setPurchaseID(cursor.getInt(0));
            helper.setItemID(cursor.getInt(1));
            db.delete(H_TABLE, COL_ID + " = " + ID, null);
        }

        cursor.close();
        db.close();
    }

    public void deleteItemFromHelper(int PiD, int IiD) {
        String Query = "Select * From " + H_TABLE + " WHERE " + COL_ID + " = '" + String.valueOf(PiD) + "' AND " + COL_ITEMID + " = '" + String.valueOf(IiD) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);

        HelperTable helper = new HelperTable();
        while (cursor.moveToNext()) {
            helper.setPurchaseID(cursor.getInt(0));
            helper.setItemID(cursor.getInt(1));
            db.delete(H_TABLE, COL_ITEMID + " = " + IiD, null);
        }

        cursor.close();
        db.close();
    }

    public void deleteItem(int ID) {
        String Query = "Select * From " + ITEM_TABLE + " WHERE " + COL_ITEMID + " = '" + String.valueOf(ID) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);

        ItemSupport item = new ItemSupport();
        while (cursor.moveToNext()) {
            item.setItemID(cursor.getInt(0));
            db.delete(ITEM_TABLE, COL_ITEMID + " = " + ID, null);
        }

        cursor.close();
        db.close();
    }

    public void deletePurchase(int ID) {
        String query = "SELECT * FROM " + PURC_TABLE + " WHERE " + COL_ID + " = '" + String.valueOf(ID) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        PurchSupport purchase = new PurchSupport();
        while (cursor.moveToNext()) {
            purchase.setPurchaseID(cursor.getInt(0)); // why.. no idea, but as long as it works...
            db.delete(PURC_TABLE, COL_ID + " = " + ID, null);
        }

        cursor.close();
        db.close();
    }

}