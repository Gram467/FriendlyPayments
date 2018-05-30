package sithrak.fps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static sithrak.fps.NewPurchase.Contacts;
import static sithrak.fps.NewPurchase.Items;

public class Purchase extends AppCompatActivity {

    public TextView Desc, textView_items, textView_contacts;// = findViewById(R.id.PDesc);            Can't set them until we've acquired the layout that has their specific ids

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.show_purchase);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_noun_1352054_cc);
        actionBar.setTitle(" Purchase #" + ( History.purchase_identification_number ));

        Desc = findViewById(R.id.PDesc);
        textView_items = findViewById(R.id.text_grid_items);
        textView_contacts = findViewById(R.id.text_grid_contacts);

        getDetails();
        ShowItemz();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
        ShowItemz();
    }

    // loads purchase description
    public void getDetails() {
        DBHandler db = new DBHandler(this, null, null, 1);

        String desctext = db.getDesc(History.purchase_identification_number);
        Desc.setText(desctext);

        db.close();
    }

    // loads items and contacts that are added to the purchase
    public void ShowItemz() {
        GridView ItemGrid = findViewById(R.id.purchase_items);
        GridView ContactGrid = findViewById(R.id.grid_contacts);
        DBHandler db = new DBHandler(this, null, null, 1);

        db.loadItemsForPurchase(History.purchase_identification_number);
        AITA();
        db.makeItems();

        final ItemAdapter i_adapter = new ItemAdapter(this, DBHandler.items);
        ItemGrid.setAdapter(i_adapter);

        db.loadContactsForPurchase(History.purchase_identification_number);
        ACTA();
        db.makeContacts();

        final ContactAdapter c_adapter = new ContactAdapter(this, DBHandler.contacts);
        ContactGrid.setAdapter(c_adapter);

        db.close();
    }

    // Add Items To ItemAdapter
    public void AITA() {
        Items = DBHandler.pid;
        ItemAdapter.correctitem = new int[1024];

        for (int i = 0; i < Items.length; i++) {
            if (Items[i] != 0){
                ItemAdapter.correctitem[i] = Items[i];
            } else if (Items[0] == 0){
                hide(textView_items);
            }
        }
    }

    // Add Contacts To ContactAdapter
    public void ACTA() {
        Contacts = DBHandler.CustID;
        ContactAdapter.rightContact = new int[1024];

        for (int i = 0; i < Contacts.length; i++) {
            if (Contacts[i] != 0) {
                ContactAdapter.rightContact[i] = Contacts[i];
            } else if (Contacts[0] == 0) {
                hide(textView_contacts);
            }
        }
    }

    public void Check(View view) { PreV(); }

    public void PreV() {
        Intent Intent = new Intent(this, Preview.class);
        Intent.putExtra("pid", String.valueOf(History.purchase_identification_number));
        startActivity(Intent);
    }

    public void EditMe(View view) {
        Intent goToEdit = new Intent (this, EditPurchase.class);
        startActivity(goToEdit);
        finish();
    }

    // if there are no frames, hides the view
    public static void hide(TextView empty) {
        empty.setVisibility(View.INVISIBLE);
    }
}