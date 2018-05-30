package sithrak.fps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.DecimalFormat;

import static sithrak.fps.ContactList.contactsArray;

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
    public static final String CON_PASS = "Password";

    public static final String COL_ITEMID = "ItemID";
    public static final String ITEM_TABLE = "Items";
    public static final String ITEM_NAME = "Name";
    public static final String ITEM_PRICE = "Price";
    public static final String ITEM_COUNT = "Quantity";
    public static final String ITEM_PIC = "PictureID";

    public static final String H_TABLE = "HelperTable";

    // a workaround to create frames for History
    private static int unknown; // used for calculating amount of purchases and items
    private static int contactsInPurchase;
    public static PurchSupport[] purchases = new PurchSupport[unknown];
    public static ItemSupport[] items = new ItemSupport[unknown];
    public static ContactSupport[] contacts = new ContactSupport[contactsInPurchase];
    public static String PurchDesc;
    public static int pid[] = new int[1024]; // product(item) id
    public static int CustID[] = new int[1024]; // customer id

    // a one of a kind description to make sure that the correct purchase has been selected encoded in base64
    public static String impossible = "V2h5IHdvdWxkIGFueW9uZSBldmVyIG1ha2UgYSBkZXNjcmlwdGlvbiBzdWNoIGFzIHRoaXMhPw=="; // Why would anyone ever make a description such as this!?

    //initialize DB
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VER);
    }

    //Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Tables(db);
        String sql = "INSERT OR REPLACE INTO " + CON_TABLE + " (" + COL_CONTID + ", " + CON_FNAME + ", " + CON_LNAME + ", " + CON_NUM + ", " + CON_EMAIL + ", " + CON_BNAME + ", " + CON_BNUM + ", " + CON_PASS + ") VALUES " +
                "('1','', '', '', '', '', '', '')";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public static void Tables(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE " + CON_TABLE);            //for table updating purposes
//        db.execSQL("DROP TABLE " + ITEM_TABLE);
//        db.execSQL("DROP TABLE " + PURC_TABLE);
//        db.execSQL("DROP TABLE " + H_TABLE);

        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + CON_TABLE + "(" +
                COL_CONTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CON_FNAME + " TEXT, " +
                CON_LNAME + " TEXT, " +
                CON_NUM + " INTEGER, " +
                CON_EMAIL + " TEXT, " +
                CON_BNAME + " TEXT, " +
                CON_BNUM + " TEXT, " +
                CON_PASS + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);


        String CREATE_I_TABLE = "CREATE TABLE IF NOT EXISTS " + ITEM_TABLE + "(" +
                COL_ITEMID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_PRICE + " FLOAT, " +
                ITEM_COUNT + " INTEGER, " +
                ITEM_PIC + " TEXT" + ")";
        db.execSQL(CREATE_I_TABLE);


        String CREATE_MAIN_TABLE = "CREATE TABLE IF NOT EXISTS " + PURC_TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESC + " TEXT " +  ")";
        db.execSQL(CREATE_MAIN_TABLE);


        String CREATE_H_TABLE = "CREATE TABLE IF NOT EXISTS " + H_TABLE + "(" +
                COL_ITEMID + " INTEGER, " +
                COL_ID + " INTEGER, " +
                COL_CONTID + " INTEGER, " +
                "PRIMARY KEY( " + COL_ITEMID + ", " + COL_CONTID + ", "  + COL_ID + ")" + ")";
        db.execSQL(CREATE_H_TABLE);


    }

    public void loadItemsForPurchase(int PiD) {
        String PItems = "Select * From " + H_TABLE + " WHERE " + COL_ID + " = '" + PiD + "' AND " + COL_ITEMID + " != 0";

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

    public void loadAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * From " + CON_TABLE;
        int num = 0;
        contactsInPurchase = 0; // re-used for Adapter purposes
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            CustID[num] = cursor.getInt(0);
            contactsInPurchase++;
            num++;
        }
        cursor.close();
        db.close();
    }

    public void loadContactsForPurchase(int PiD) {
        String Contacts = "Select * From " + H_TABLE + " WHERE " + COL_ID + " = '" + PiD + "' AND " + COL_CONTID + " != 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Contacts, null);
        contactsInPurchase = 0;
        CustID = new int[1024];
        int num = 0;

        while (cursor.moveToNext()) {
            CustID[num] = cursor.getInt(2);
            contactsInPurchase++;
            num++;
        }
        cursor.close();
        db.close();
    }

    public String loadContactName(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(1);
        }

        cursor.close();
        db.close();
        return val;
    }

    public String loadContactSurname(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(2);
        }

        cursor.close();
        db.close();
        return val;
    }

    public String loadContactNumber(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(3);
        }

        cursor.close();
        db.close();
        return val;
    }

    public String loadContactEmail(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(4);
        }

        cursor.close();
        db.close();
        return val;
    }

    public String loadUserBN(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(5);
        }

        cursor.close();
        db.close();
        return val;
    }

    public String loadUserBAN(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(6);
        }

        cursor.close();
        db.close();
        return val;
    }

    public String loadUserPass(int contactID) {
        String ContactIDs = "Select * From " + CON_TABLE + " WHERE " + COL_CONTID + " = '" + contactID + "'";
        String val = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactIDs, null);

        while (cursor.moveToNext()) {
            val = cursor.getString(7);
        }

        cursor.close();
        db.close();
        return val;
    }


    //could be made with mixing other functions
    public void loadContactInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * From " + CON_TABLE;
        contactsArray = new String[contactsInPurchase - 1];
        int num = 0;
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            if (num == 0) { // skips first contact (the user)
            num++;}
            else {
                contactsArray[num - 1] = cursor.getString(1) + ", " + cursor.getString(2);
                num++;
            }
        }

        cursor.close();
        db.close();
    }



    // Adds itemID and ContactID for current purchase (CRUD - Create [HelperTable])
    public void addToHelper(HelperTable help) {
        ContentValues values = new ContentValues();
            values.put(COL_ID, help.getPurchaseID());
            values.put(COL_ITEMID, help.getItemID());
            values.put(COL_CONTID, help.getContactID());

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

    // add contact to table
    public void addContact(ContactSupport contact) {
        ContentValues val = new ContentValues();
            val.put(CON_FNAME, contact.getCName());
            val.put(CON_LNAME, contact.getCSurname());
            val.put(CON_NUM, contact.getNumber());
            val.put(CON_EMAIL, contact.getEmail());
            val.put(CON_BNAME, contact.getBName());
            val.put(CON_BNUM, contact.getBNum());
            val.put(CON_PASS, contact.getPassword());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(CON_TABLE, null, val);

        db.close();
    }

    // Creates the purchase at the launch of the view so that there's a PurchaseID to which to assign the rest of the objects
    public void addPurchase(PurchSupport cart) {
        NewPurchase.Items = new int[1024];
        NewPurchase.Contacts = new int[1024];
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
        cursor.close();
        db.close();
    }

    // loads description of a specific purchase
    public String getDesc(int num) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * FROM " + PURC_TABLE + " WHERE " + COL_ID + " = '" + num + "'";
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            PurchDesc = cursor.getString(1);
        }
        cursor.close();
        db.close();
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
        cursor.close();
        db.close();
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

    // creates a frame for each item
    public ItemSupport[] makeItems() {
        items = new ItemSupport[unknown];
        for (int i = 0; i < unknown; i++) {
            items[i] = new ItemSupport();
        }
        return items;
    }

    // creates a frame for each contact
    public ContactSupport[] makeContacts() {
        contacts = new ContactSupport[contactsInPurchase];
        for (int i = 0; i < contactsInPurchase; i++) {
            contacts[i] = new ContactSupport();
        }
        return contacts;
    }

    // updates purchase information
    public void updatePurchase (int ID, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(COL_DESC, desc);
        db.update(PURC_TABLE, value, COL_ID + " = " + ID, null);

        db.close();
    }

    // updates item information
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

    // updates contact information
    public void updateContact (int ID, String name, String surname, int num, String email, String bn, String ban, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
            val.put(CON_FNAME, name);
            val.put(CON_LNAME, surname);
            val.put(CON_NUM, num);
            val.put(CON_EMAIL, email);
            val.put(CON_BNAME, bn);
            val.put(CON_BNUM, ban);
            val.put(CON_PASS, pass);

        db.update(CON_TABLE, val, COL_CONTID + " = " + ID, null);
        db.close();
    }

    // completely removes purchase
    public void DeleteP(int PiD) {
        loadItemsForPurchase(PiD); // get items of current purchase
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

    // removes purchase from helper
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

    // removes items in purchase from helper
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

    // deletes contact from purchase (helper table)
    public void removeContact(int PiD, int CiD) {
        String query = "Select * from " + H_TABLE + " where " + COL_ID + " = '" + String.valueOf(PiD) + "' AND " + COL_CONTID + " = '" + CiD + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ContactSupport contact = new ContactSupport();
        while (cursor.moveToNext()) {
            contact.setContactID(cursor.getInt(0));
            db.delete(H_TABLE, COL_CONTID + " = " + CiD, null);
        }
        cursor.close();
        db.close();
    }

    // deletes item from item table
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

    // deletes purchase from purchases table
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

    // deletes contact
    public void deleteContact(int ID) {
        String query = "Select * from " + CON_TABLE + " where " + COL_CONTID + " = '" + String.valueOf(ID) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ContactSupport contact = new ContactSupport();
        while (cursor.moveToNext()) {
            contact.setContactID(cursor.getInt(0));
            db.delete(CON_TABLE, COL_CONTID + " = " + ID, null);
        }
        cursor.close();
        db.close();
    }
}