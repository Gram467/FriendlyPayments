package sithrak.fps;

import android.content.Intent;
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
    }

    public void MakeNewPurchase(View view) {
        Intent Intent = new Intent(MainActivity.this, NewPurchase.class);
        startActivity(Intent);
    }

    public void GetHistory(View view){
//        Toast.makeText(this, "Not implemented yet, grid view of created purchases, click on square to open",
//                Toast.LENGTH_LONG).show();

        Intent Intent = new Intent(MainActivity.this, History.class);
        startActivity(Intent);

    }

    public void Contacts(View view){
        Toast.makeText(this, "Not implemented yet, list view selection of contacts, possibly acquiring information from Contacts app.",
                Toast.LENGTH_LONG).show();
    }



}
