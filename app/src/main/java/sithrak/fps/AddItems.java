package sithrak.fps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;

import static sithrak.fps.History.purchase_identification_number;
import static sithrak.fps.NewPurchase.Items;

public class AddItems extends AppCompatActivity {

    private EditText namae, quantity, price;
    private Spinner dropdown;
    String[] items = new String[]{"Food", "Vegetables", "Fruits", "Meat & Fish", "Snacks", "Beverages", "Alcohol", "Clothing", "Toys", "Electronics", "Miscellaneous"};
    public static int PurchaseItemPosition = 0;
    String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additem);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Add Item");

        // get fields from XML file
        namae = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        dropdown = findViewById(R.id.spinner_cat);

        // needed for studying
        if (getIntent().getStringExtra("caller") == null) {
            caller = "";
        } else {
            caller = getIntent().getStringExtra("caller");
        }

        // fill dropdown with category choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    public void addItem(View view) {

        DBHandler dbItem = new DBHandler(this, null, null, 1);
        float val;
        int count;

        String name = namae.getText().toString();
        // get field values
        DecimalFormat dec = new DecimalFormat("#.##");
        if (price.getText().toString().equals("")) { val = 0; }
            else {
                val = Float.parseFloat((price.getText().toString())
                        .replaceAll(",", ".") // convert incoming comma's to dots for float type
                        .replaceAll("k", "000")); // turns each k to a thousand (000)
        }
        float value = Float.valueOf(dec.format(val)); // sets 2 numbers after the dot.. example - 123.97
        if (quantity.getText().toString().equals("")) { count = 0; }
            else {
                count = Integer.parseInt((quantity.getText().toString())
                        .replaceAll("k", "000") // turns each k to a thousand (000)
                        .replaceAll("[\\D]", "")); // removes every other symbol that is not 0..9
        }
        String type = String.valueOf(dropdown.getSelectedItem()); // switched to a dropdown, for easier control of categories

        // create table line for Item
        ItemSupport item = new ItemSupport(name, count, type, value);
        dbItem.addItem(item);

        // Selection of a specific item
        // Needed to assign several items to the same purchase id from one add item view
        // (not necessarily needed, but don't really have the leisure to fix it right now)
        SQLiteDatabase db = dbItem.getReadableDatabase();
        String result = "";
        int id = 0;
        String query = "SELECT " + DBHandler.COL_ITEMID + " from " + DBHandler.ITEM_TABLE +
                " WHERE " + DBHandler.ITEM_NAME + " = '" + item.getIName() + "' AND " +
                DBHandler.ITEM_PIC + " = '" + item.getCategory() + "'";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
                result = String.valueOf(id);
        }
            int itemnum = Integer.parseInt(result.replaceAll("[\\D]", ""));
        cursor.close();
        db.close();
        dbItem.close();

        Items[PurchaseItemPosition] = itemnum;
        PurchaseItemPosition++;

        if (caller.equals("EditPurchase")) {
            AITH();
        }

        Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void AITH() {

        DBHandler dbHelp = new DBHandler(this, null, null, 1);
        ItemAdapter.correctitem = new int[1024];

        for (int i = 0; i < Items.length; i++) {
            if (Items[i] != 0){
                HelperTable help = new HelperTable(purchase_identification_number, Items[i], 0);
                dbHelp.addToHelper(help);
                ItemAdapter.correctitem[i] = Items[i];
            }
        }

        dbHelp.close();
    }

}
