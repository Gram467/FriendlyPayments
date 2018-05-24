package sithrak.fps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;

import static sithrak.fps.EditPurchase.item_identification_number;
import static sithrak.fps.History.purchase_identification_number;

public class EditItem extends AppCompatActivity{

    String[] items = new String[]{"Food", "Vegetables", "Fruits", "Meat & Fish", "Snacks", "Beverages", "Alcohol", "Clothing", "Toys", "Electronics", "Miscellaneous"};
    EditText name, price, quantity;
    Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_item);

            name = findViewById(R.id.name);
            price = findViewById(R.id.price);
            quantity = findViewById(R.id.quantity);
            category = findViewById(R.id.spinner_cat);

        // fill dropdown with category choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        category.setAdapter(adapter);

        Load(item_identification_number);
    }

    public void Load(int id) {

        DBHandler db = new DBHandler(this, null, null, 1);

        name.setText(db.loadItemName(id));
        price.setText(String.valueOf(ItemAdapter.smallCheck(String.valueOf(db.loadItemPrice(id)))));
        quantity.setText(String.valueOf(db.loadItemQuantity(id)));
        category.getSelectedItem();

        db.close();
    }

    public void Update(View view) {
        UpdItem(purchase_identification_number, item_identification_number);
        Toast.makeText(this, "Item succesfully updated.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void UpdItem(int PiD, int IiD) {

        DBHandler db = new DBHandler(this, null, null, 1);
        SQLiteDatabase dbz = db.getWritableDatabase();

        String Query = "Select * From " + DBHandler.H_TABLE + " WHERE " + DBHandler.COL_ID + " = '" + PiD + "' AND " + DBHandler.COL_ITEMID + " = '" + IiD + "'";
        Cursor cursor = dbz.rawQuery(Query, null);

        DecimalFormat dec = new DecimalFormat("#.##");

        while (cursor.moveToNext()) {}

        String item = name.getText().toString();
        float val = Float.parseFloat(price.getText().toString());
            float value = Float.valueOf(dec.format(val));
        int count = Integer.parseInt(quantity.getText().toString());
        String type = String.valueOf(category.getSelectedItem());

        db.updateItem(IiD, item, value, count, type);

        dbz.close();
        db.close();
    }

    public void DelItem(View view) {
        DBHandler db = new DBHandler(this, null, null, 1);

        db.deleteItemFromHelper(purchase_identification_number,item_identification_number);
        db.deleteItem(item_identification_number);

        db.close();
        Toast.makeText(this, "Item deleted.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
