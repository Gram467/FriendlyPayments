package sithrak.fps;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sithrak on 22.01.2018..
 */

public class NewPurchase extends AppCompatActivity{

    GridView ItemGrid;
    TextView list;
    EditText Description;
    public static int[] Items;// = new int[1024];
    public static int[] Contacts = new int[1024];
    public static int PurchaseID;
    public static int aNumberForUpdate;

    int purchase = 0;
    String description = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.npurchase);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" New Purchase");

        ItemGrid = findViewById(R.id.grid_items);
        Description = findViewById(R.id.description);

        CreatePurchase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowItemz();
    }

    // to call the function via button from xml
    public void ShowItems(View view) {
        ShowItemz();
    }

    public void ShowItemz() {

        AITH();     //all good in here
        DBHandler db = new DBHandler(this, null, null, 1);

        db.loadItemsForPurchase(PurchaseID);
        db.makeItems();

        final ItemAdapter adapter = new ItemAdapter(this, DBHandler.items);
        ItemGrid.setAdapter(adapter);

        db.close();
    }

    // Add Item To Helper (Table)
    public void AITH() {

        DBHandler dbHelp = new DBHandler(this, null, null, 1);
        ItemAdapter.correctitem = new int[1024];

        for (int i = 0; i < Items.length; i++) {
            if (Items[i] != 0){
                HelperTable help = new HelperTable(PurchaseID, Items[i]);
                dbHelp.addToHelper(help);
                ItemAdapter.correctitem[i] = Items[i];
            }
        }

        dbHelp.close();
    }

    // Creates Purchase so that other objects might be assigned to its ID
    public void CreatePurchase() {
        DBHandler dbPurch = new DBHandler(this, null, null, 1);

        String desc = Description.getText().toString();

        PurchSupport cart = new PurchSupport(desc);
        dbPurch.addPurchase(cart);

        Description.setText("");

        //Acquires the ID of the latest Purchase
        SQLiteDatabase dbz = dbPurch.getReadableDatabase();
        String query = "SELECT " + DBHandler.COL_ID + " FROM " + DBHandler.PURC_TABLE +
                " WHERE " + DBHandler.COL_DESC + " = '" + DBHandler.impossible + "'";

        Cursor cursor = dbz.rawQuery(query, null);
            while (cursor.moveToNext()){
                PurchaseID = cursor.getInt(0);
            }

        dbPurch.close();
        cursor.close();
        dbz.close();
    }

    // Moves to AddItems view
    public void AddItemz(View view) {
        Intent Intent = new Intent(NewPurchase.this, AddItems.class);
        startActivity(Intent);
    }

    //
    public void AddContactToHelper() {
        DBHandler dbHelp = new DBHandler(this, null, null, 1);
        for (int i = 0; i < Contacts.length; i++) {
            if (Contacts[i] != 0) {
                HelperTable help = new HelperTable(PurchaseID, Contacts[i]);
                dbHelp.addToHelper(help);
            }
        }
    }

    public void UpdatePurchase(View view) {

        aNumberForUpdate++;
        PurchCheck();
        Toast.makeText(this,"The Purchase has been saved.",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void PurchCheck() {

        DBHandler db = new DBHandler(this, null, null, 1);

        if (aNumberForUpdate > 1) {
            description = DBHandler.impossible;
            UpdPurch();
        } else {
            // delete purchase
            db.DeleteP(PurchaseID);
            Toast.makeText(this,"Creation of the purchase has been cancelled.",Toast.LENGTH_LONG).show();
            finish(); // returns to previous view
        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            PurchCheck();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    public void UpdPurch() {

        DBHandler db = new DBHandler(this, null, null, 1);
        SQLiteDatabase dbz = db.getWritableDatabase();

        String query = "Select * FROM " + DBHandler.PURC_TABLE + " WHERE " + DBHandler.COL_DESC + " = '" + description + "'";
        Cursor cursor = dbz.rawQuery(query, null);

        while (cursor.moveToNext()) {
            purchase = cursor.getInt(0);
            description = cursor.getString(1);
        }

        description = Description.getText().toString();

        db.updatePurchase(purchase, description);

        dbz.close();
        db.close();
    }

}
