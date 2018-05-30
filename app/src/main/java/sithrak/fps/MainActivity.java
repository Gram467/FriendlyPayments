package sithrak.fps;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Friendly Payments");
        verifyStoragePermissions(this);

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


    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
