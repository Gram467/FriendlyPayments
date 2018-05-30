package sithrak.fps;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import static sithrak.fps.DBHandler.CustID;
import static sithrak.fps.ItemAdapter.correctitem;
import static sithrak.fps.NewPurchase.Contacts;
import static sithrak.fps.NewPurchase.Items;
import static sithrak.fps.History.purchase_identification_number;

public class Preview extends AppCompatActivity {

    String message = "", msgToCopy = "", desc = "";
    TextView Desc, Msg, textView_items, textView_contacts;
    int cnum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_view);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Purchase #" + (History.purchase_identification_number));

        Msg = findViewById(R.id.Message);
        Desc = findViewById(R.id.description);

        Message();
        Msg.setText(message);
        Desc.setText(desc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Message();
        Msg.setText(message);
        Desc.setText(desc);
    }

    // creates a message describing the purchase
    public void Message() {
        DBHandler db = new DBHandler(this, null, null, 1);

        if (getIntent().getStringExtra("pid") != null) {
            desc = db.getDesc(Integer.valueOf(getIntent().getStringExtra("pid")));
        } else {
            desc = db.getDesc(History.purchase_identification_number);
        }
        float total_sum = 0;
        message = System.getProperty("line.separator") + "Items : " + System.getProperty("line.separator");
        String fixedFloat;

        DecimalFormat dec = new DecimalFormat("#.##");

        db.loadItemsForPurchase(purchase_identification_number);
        AITA(); // sets which items are going to be used in the message
        for (int i = 0; i < correctitem.length; i++) {
            if (correctitem[i] != 0) {
                fixedFloat = ItemAdapter.smallCheck(String.valueOf(db.loadItemPrice(correctitem[i])));
                total_sum += (db.loadItemQuantity(correctitem[i]) * db.loadItemPrice(correctitem[i]));
                total_sum = Float.valueOf(dec.format(total_sum));
                message += db.loadItemQuantity(correctitem[i]) + "x "
                        + db.loadItemName(correctitem[i]) + " ( "
                        + fixedFloat + ", "
                        + db.loadItemCat(correctitem[i]) + " )"
                        + System.getProperty("line.separator");
            }
        }
        message += "Total Sum of Event = " + ItemAdapter.smallCheck(String.valueOf(total_sum)) + System.getProperty("line.separator");

        db.loadContactsForPurchase(purchase_identification_number);
        ACTA();
        if (CustID[0] != 0) {
            message += "Participants:" + System.getProperty("line.separator");
            for (int i = 0; i < CustID.length; i++) {
                if (CustID[i] != 0) {
                    message += db.loadContactName(CustID[i]) + " " + db.loadContactSurname(CustID[i]) + System.getProperty("line.separator");
                }
            }
        }
        if (CustID[0] != 0) {
            message += db.loadContactName(1) + " " + db.loadContactSurname(1) + System.getProperty("line.separator") // add user
                    + "Sum for each = " + Float.valueOf(dec.format((total_sum / (cnum + 1)))) + System.getProperty("line.separator")
                    + "Bank - " + db.loadUserBN(1) + System.getProperty("line.separator")
                    + "Bank Account Number - " + db.loadUserBAN(1);
        }
        db.close();
    }

    // allows the function to be called via press of a button
    public void Copee(View view) {
        Copies();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", msgToCopy);
        clipboard.setPrimaryClip(clip);
    }

    // Retrieves message text
    public void Copies() {
        DBHandler db = new DBHandler(this, null, null, 1);
        msgToCopy = db.getDesc(History.purchase_identification_number) + message;
        db.close();
    }

    // allows the function to be called via press of a button
    public void Ex(View view) {
        Export();
    }

    // Logic for CSV export
    public void Export() {
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
                        + "\", \"" // + db.getDesc(correctitem[i]) - don't need the description in every line
                        + System.getProperty("line.separator");
            }
        }

        String EndRes = Data + "\"\",\"\",\"\",\"\",\"" + db.getDesc(purchase_identification_number) + "\"";
        db.close();

        File file = null;
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FP_Exports");
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
        }
        try {
            out.write(EndRes.getBytes()); // writes the string into the .csv file
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri u1 = Uri.fromFile(file);
        Sharing(u1);
    }

    public void EditPress(View view) {
        Intent Intent = new Intent(this, EditPurchase.class);
        startActivity(Intent);
    }

    // Launches "Share"
    public void Sharing(Uri u1) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
        sendIntent.setType("text/html");
        startActivity(sendIntent);
    }

    public void Send(View view) {
        Copies();
        SharingText(msgToCopy);
    }

    // Launches "Share" but just for the msg text
    public void SharingText(String text) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_SUBJECT, text);
        send.setType("text/html");
        startActivity(send);
    }

    //Add Items To Array
    public void AITA() {
        NewPurchase.Items = DBHandler.pid;
        ItemAdapter.correctitem = new int[1024];

        for (int i = 0; i < Items.length; i++) {
            if (Items[i] != 0){
                ItemAdapter.correctitem[i] = Items[i];
            }
        }
    }

    // Add Contacts To Array
    public void ACTA() {
        NewPurchase.Contacts = DBHandler.CustID;
        ContactAdapter.rightContact = new int[1024];
        cnum = 0;

        for (int i = 0; i < Contacts.length; i++) {
            if (Contacts[i] != 0) {
                ContactAdapter.rightContact[i] = Contacts[i];
                cnum++;
            }
        }
    }
}
