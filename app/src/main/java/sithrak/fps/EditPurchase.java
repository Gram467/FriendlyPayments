package sithrak.fps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import static sithrak.fps.History.purchase_identification_number;
import static sithrak.fps.NewPurchase.Contacts;
import static sithrak.fps.NewPurchase.Items;
import static sithrak.fps.NewPurchase.PurchaseID;

public class EditPurchase extends AppCompatActivity{

    GridView ItemGrid, ContactGrid;
    EditText Desc;
    public static int item_identification_number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_purch);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Edit Purchase #" + ( purchase_identification_number ));

        GetStuff(purchase_identification_number);
        UseAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GetStuff(purchase_identification_number);
        UseAdapter();
    }

    public void GetStuff(int id) {
        ItemGrid = findViewById(R.id.grid_items);
        ContactGrid = findViewById(R.id.grid_contacts);
        DBHandler db = new DBHandler(this, null, null, 1);

        db.loadItemsForPurchase(purchase_identification_number);
        AITA();
        db.makeItems();
        final ItemAdapter i_adapter = new ItemAdapter(this, DBHandler.items);
        ItemGrid.setAdapter(i_adapter);

        db.loadContactsForPurchase(purchase_identification_number);
        ACTA();
        db.makeContacts();
        final ContactAdapter c_adapter = new ContactAdapter(this, DBHandler.contacts);
        ContactGrid.setAdapter(c_adapter);

        Desc = findViewById(R.id.description);
        Desc.setText(db.getDesc(id));

        db.close();

    }

    //Add Items To Adapter
    public static void AITA() {
        NewPurchase.Items = DBHandler.pid;
        ItemAdapter.correctitem = new int[1024];

        for (int i = 0; i < Items.length; i++) {
            if (Items[i] != 0){
                ItemAdapter.correctitem[i] = Items[i];
            } else if (Items[0] == 0){

            }
        }
    }

    public void ACTA() {
        NewPurchase.Contacts = DBHandler.CustID;
        ContactAdapter.rightContact = new int[1024];

        for (int i = 0; i < Contacts.length; i++) {
            if (Contacts[i] != 0) {
                ContactAdapter.rightContact[i] = Contacts[i];
            } else if (Contacts[0] == 0) {
//                hide(textView_contacts);
            }
        }
    }

    public void UseAdapter() {
        ItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemSupport item = DBHandler.items[position];

                Intent Intent = new Intent(EditPurchase.this, EditItem.class);
                startActivity(Intent);

                item_identification_number = DBHandler.pid[position];
            }
        });
    }

    public void AddItem(View view) {
        Intent NewItem = new Intent(this, AddItems.class);
        NewItem.putExtra("caller", "EditPurchase");
        startActivity(NewItem);
    }

    public void AddContact(View view) {
        Intent NewContact = new Intent(this, AddContact.class);
        NewContact.putExtra("caller", "EditPurchase");
        startActivity(NewContact);
    }

    public void UpdatePurchase(View view) {
        DBHandler db = new DBHandler(this, null, null, 1);
        String desc = Desc.getText().toString();

        db.updatePurchase(purchase_identification_number, desc);

        db.close();
        finish();
    }

    public void DeletePurchase(View view) {
        DBHandler db = new DBHandler(this, null, null, 1);

        db.DeleteP(History.purchase_identification_number);
        db.close();

        Toast.makeText(this, "Purchase deleted", Toast.LENGTH_SHORT).show();

        Intent BackToHistory = new Intent(this, History.class);
        BackToHistory.putExtra("del", "Edit_Purchase_Delete");
        startActivity(BackToHistory);

//        finish();
    }
}
