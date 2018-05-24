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
//        textView_contacts = findViewById(R.id.text_grid_contacts);

        getDetails();
        ShowItemz();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
        ShowItemz();
    }

    public void getDetails() {
        DBHandler db = new DBHandler(this, null, null, 1);

        String desctext = db.getDesc(History.purchase_identification_number);
        Desc.setText(desctext);

        db.close();
    }

    // to call the function via button from xml
    public void ShowItems(View view) {
        ShowItemz();
    }

    public void ShowItemz() {
        GridView ItemGrid = findViewById(R.id.purchase_items);
        DBHandler db = new DBHandler(this, null, null, 1);

        db.loadItemsForPurchase(History.purchase_identification_number);
        AITP();
        db.makeItems();

        final ItemAdapter adapter = new ItemAdapter(this, DBHandler.items);
        ItemGrid.setAdapter(adapter);

        db.close();
    }

    // Add Items To ItemAdapter
    public void AITP() {
        NewPurchase.Items = DBHandler.pid;
        ItemAdapter.correctitem = new int[1024];

        for (int i = 0; i < Items.length; i++) {
            if (Items[i] != 0){
                ItemAdapter.correctitem[i] = Items[i];
            } else if (Items[0] == 0){
                hide();
            }
        }
    }

    public void Check(View view) {
        PreV();
    }

    public void PreV() {
        Intent Intent = new Intent(this, Preview.class);
        Intent.putExtra("pid", String.valueOf(History.purchase_identification_number));
        startActivity(Intent);
    }

    public void DeleteMe(View view) {
        DBHandler db = new DBHandler(this, null, null, 1);

        db.DeleteP(History.purchase_identification_number);
        db.close();

        Toast.makeText(this,"Purchase deleted.",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void hide() {
        textView_items.setVisibility(View.INVISIBLE);

//        LinearLayout sup = findViewById(R.id.linlayout);
//        ViewGroup.LayoutParams check = sup.getLayoutParams();
//        check.height = (80 * 6);

//        textView_items.setHeight();
//        textView_contacts.setVisibility(View.INVISIBLE); //disabling contact views for now
    }
}


/*
* if no contacts ->
*
*
* */