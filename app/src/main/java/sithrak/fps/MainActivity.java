package sithrak.fps;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Friendly Payments");

        DBHandler db = new DBHandler(this, null, null, 1);
        SQLiteDatabase dbz = db.getWritableDatabase();
        DBHandler.Tables(dbz);
        dbz.close();
        db.close();
    }

    public void MakeNewPurchase(View view) {
        Intent npurchase = new Intent(MainActivity.this, NewPurchase.class);
        startActivity(npurchase);
    }

    public void GetHistory(View view){
        Intent history = new Intent(MainActivity.this, History.class);
        startActivity(history);

    }

    public void Contacts(View view){
        Intent contacts = new Intent(this, ContactList.class);
        startActivity(contacts);
//        Toast.makeText(this, "Not implemented yet, list view selection of contacts from an adapter.", Toast.LENGTH_LONG).show();
    }

    public void Summary(View view){
        Toast.makeText(this, "Not implemented yet, check documentation.",
                Toast.LENGTH_LONG).show();
    }
}
