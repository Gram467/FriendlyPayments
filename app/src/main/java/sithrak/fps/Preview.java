package sithrak.fps;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.BlockingDeque;

import static sithrak.fps.ItemAdapter.correctitem;
import static sithrak.fps.NewPurchase.Items;
import static sithrak.fps.DBHandler.pid;
import static sithrak.fps.History.purchase_identification_number;

public class Preview extends AppCompatActivity {

    String idea = "", msgToCopy = "", desc = "";
    TextView Desc, Msg;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_view);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Purchase #" + ( History.purchase_identification_number ));

        Msg = findViewById(R.id.Message);
        Desc = findViewById(R.id.description);

        Message();
        Msg.setText(idea);
        Desc.setText(desc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Message();
        Msg.setText(idea);
    }

    public void Message() {

        DBHandler db = new DBHandler(this, null, null, 1);

        if (getIntent().getStringExtra("pid") != null) {
            desc = db.getDesc(Integer.valueOf(getIntent().getStringExtra("pid")));
        } else {
            desc = db.getDesc(History.purchase_identification_number);
        }
        float total_sum = 0;
        idea = "Items : " + System.getProperty("line.separator");
        String fixedFloat = "";

        DecimalFormat dec = new DecimalFormat("#.##");

        db.loadItemsForPurchase(purchase_identification_number);
        EditPurchase.AITA(); // sets which items are going to be used in the adapter

        for (int i = 0; i < correctitem.length; i++) {
            if (correctitem[i] != 0) {
                fixedFloat = ItemAdapter.smallCheck(String.valueOf(db.loadItemPrice(correctitem[i])));
                total_sum += (db.loadItemQuantity(correctitem[i]) * db.loadItemPrice(correctitem[i]));
                total_sum = Float.valueOf(dec.format(total_sum));
                idea += db.loadItemQuantity(correctitem[i]) + "x "
                        + db.loadItemName(correctitem[i]) + " ( "
                        + fixedFloat + ", "
                        + db.loadItemCat(correctitem[i]) + " )"
                        + System.getProperty("line.separator");
            }
        }
        idea += "Total Sum of Event = " + ItemAdapter.smallCheck(String.valueOf(total_sum));
        db.close();
    }


    public void Copee(View view) {
        Copies();
    }

    public void Copies() {

        DBHandler db = new DBHandler(this, null, null, 1);
        msgToCopy = db.getDesc(History.purchase_identification_number) + idea;
        db.close();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", msgToCopy);
        clipboard.setPrimaryClip(clip);
    }

    public void Ex(View view) {
        verifyStoragePermissions(this);
        Export();
    }

    public void Export(){
        DBHandler db = new DBHandler(this, null, null, 1);

        String Columns = "\"Item\", \"Price\", \"Quantity\", \"Category\", \"Description\"";
        String Data = Columns + System.getProperty("line.separator");
        String fixedFloat = "";
        for (int i = 0; i < correctitem.length; i++) {
            if (correctitem[i] != 0) {
                fixedFloat = ItemAdapter.smallCheck(String.valueOf(db.loadItemPrice(correctitem[i])));
                Data += "\"" + db.loadItemName(correctitem[i])
                        + "\", \"" + fixedFloat
                        + "\", \"" + db.loadItemQuantity(correctitem[i])
                        + "\", \"" + db.loadItemCat(correctitem[i])
                        + "\", \""// + db.getDesc(correctitem[i])
                        + System.getProperty("line.separator");
            }
        }


        String X = Data + "\"\",\"\",\"\",\"\",\"" + db.getDesc(purchase_identification_number) + "\"";
        File file = null;

        File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/FP_Exports");

        if (!dir.exists()) {
            dir.mkdir();
        }

        String ExportName = "Purchase_" + purchase_identification_number + ".csv";

        file = new File(dir, ExportName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            }
            FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } try {
            out.write(X.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri u1  =   null;
        u1  =   Uri.fromFile(file);

        Sharing(u1);
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


    public void EditPress(View view) {
        Intent Intent = new Intent(this, EditPurchase.class);
        startActivity(Intent);
    }

    public void Sharing(Uri u1) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
        sendIntent.setType("text/html");
        startActivity(sendIntent);
    }

    public void Send (View view) {
        Copies();
        SharingText(msgToCopy);
    }

    public void SharingText(String text) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_SUBJECT, text);
        send.setType("text/html");
        startActivity(send);
    }

}
