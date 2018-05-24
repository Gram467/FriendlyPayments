package sithrak.fps;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by Sithrak on 30.04.2018..
 */

public class History extends AppCompatActivity{
    GridView gridView;
    public static int purchase_identification_number = 0;
    ActionBar aBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.history);

        aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" History [" + DBHandler.purchases.length + "]");

        gridView = findViewById(R.id.gridview);

        Setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aBar.setTitle(" History [" + DBHandler.purchases.length + "]");
        Setup();
    }

    public void Setup () {

        DBHandler db = new DBHandler(this, null, null, 1);

        db.getHistory();
        db.makePurchases();

        final PurchaseAdapter adapter = new PurchaseAdapter(this, DBHandler.purchases);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PurchSupport purchase = DBHandler.purchases[position];

                Intent Intent = new Intent(History.this, Purchase.class);
                startActivity(Intent);

                purchase_identification_number = DBHandler.pid[position];
            }
        });

        db.close();
    }
}


