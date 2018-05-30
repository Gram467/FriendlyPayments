package sithrak.fps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static sithrak.fps.ContactList.contactID;
import static sithrak.fps.History.purchase_identification_number;
import static sithrak.fps.NewPurchase.Contacts;

public class AddContact extends AppCompatActivity{
    ActionBar aBar;
    EditText InputName, InputSurname, InputNumber, InputEmail, Inputbn, Inputban, InputPassword;
    TextView DesBN, DesBAN, DesPass;
    Button btnEdit, btnDelete, btnAdd;
    public static int PurchaseContactPosition = 0;
    String caller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        aBar = getSupportActionBar();
        aBar.setDisplayShowHomeEnabled(true);
        aBar.setIcon(R.drawable.ic_noun_1352054_cc);
        aBar.setTitle(" Add Contact");

        // Input fields
        InputName = findViewById(R.id.input_name);
        InputSurname = findViewById(R.id.input_surname);
        InputNumber = findViewById(R.id.input_num);
        InputEmail = findViewById(R.id.input_email);
        Inputbn = findViewById(R.id.input_bn);
        Inputban = findViewById(R.id.input_ban);
        InputPassword = findViewById(R.id.input_passw);

        // Buttons
        btnAdd = findViewById(R.id.addCont);
        btnEdit = findViewById(R.id.edit_Contact);
        btnDelete = findViewById(R.id.delete_Contact);

        // A few TextFields that aren't necessary for created contacts, but are for the user
        DesPass = findViewById(R.id.password);
        DesBAN = findViewById(R.id.ban);
        DesBN = findViewById(R.id.bankname);

        // checking where the user came from
        if (getIntent().getStringExtra("caller") == null) {
            caller = "";
            NotUser();
            Add();
        } else {
            caller = getIntent().getStringExtra("caller");

            if (caller.equals("EditPurchase")) {
            NotUser(); //caller hasn't been initiated, user is adding a new contact
            Add();
            } else if (caller.equals("CList_EditUser")) {
                aBar.setTitle(" Edit User Data");
                Inputbn.setVisibility(View.VISIBLE);
                Inputban.setVisibility(View.VISIBLE);
                InputPassword.setVisibility(View.VISIBLE);
                DesBN.setVisibility(View.VISIBLE);
                DesBAN.setVisibility(View.VISIBLE);
                DesPass.setVisibility(View.VISIBLE);

                btnAdd.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);

                Editing();
            } else if (caller.equals("CList_Edit")) {
                NotUser();
                Editing();
            }
        }
    }

    // hide unnecessary fields
    public void NotUser () {
        Inputbn.setVisibility(View.INVISIBLE);
        Inputban.setVisibility(View.INVISIBLE);
        InputPassword.setVisibility(View.INVISIBLE);
        DesBN.setVisibility(View.INVISIBLE);
        DesBAN.setVisibility(View.INVISIBLE);
        DesPass.setVisibility(View.INVISIBLE);

        btnAdd.setVisibility(View.INVISIBLE);
        btnEdit.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
    }

    public void Add() {
        btnAdd.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);
    }

    public void Editing () {
        DBHandler db = new DBHandler(this, null, null, 1);
        // fill editable values
        if (caller.equals("CList_EditUser")) {
            contactID = 1;
        }
        InputName.setText(db.loadContactName(contactID));
        InputSurname.setText(db.loadContactSurname(contactID));
        InputNumber.setText(db.loadContactNumber(contactID));
        InputEmail.setText(db.loadContactEmail(contactID));
        Inputbn.setText(db.loadUserBN(contactID));
        Inputban.setText(db.loadUserBAN(contactID));
        InputPassword.setText(db.loadUserPass(contactID));

        db.close();
    }

    public void AddContact (View view) {
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        String name, surname, email;
        int number;

        // set values if fields are empty
        name = InputName.getText().toString(); if (InputName.getText().toString().equals("")) { name = "something"; }
        surname = InputSurname.getText().toString(); if (InputSurname.getText().toString().equals("")) { surname = ""; }
        if (InputNumber.getText().toString().equals("")) { number = 0; }
            else { number = Integer.parseInt(InputNumber.getText().toString()); }
        email = InputEmail.getText().toString(); if (InputEmail.getText().toString().equals("")) { email = ""; }

        ContactSupport contact = new ContactSupport(name, surname, number, email, "", "", "");
        dbHandler.addContact(contact);

        // find latest contact & add it to purchase
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String res = "";
        int id, num;
        String query = "SELECT * FROM " + DBHandler.CON_TABLE + " WHERE " + DBHandler.CON_FNAME + " = '" + contact.getCName() + "' AND " + DBHandler.CON_LNAME + " = '" + contact.getCSurname() + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
            res = String.valueOf(id);
        }
        num = Integer.parseInt(res.replaceAll("\\D", ""));
        cursor.close();
        db.close();
        dbHandler.close();

        Contacts[PurchaseContactPosition] = num;
        PurchaseContactPosition++;

        if (caller.equals("EditPurchase")) {
            ACTH();
        }

        Toast.makeText(this, "Contact has been added.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Add Contact to Helper table
    private void ACTH() {
        DBHandler dbHelp = new DBHandler(this, null, null, 1);
        ContactAdapter.rightContact = new int[1024];

        for (int i = 0; i < Contacts.length; i++) {
            if (Contacts[i] != 0){
                HelperTable help = new HelperTable(purchase_identification_number, 0, Contacts[i]);
                dbHelp.addToHelper(help);
                ContactAdapter.rightContact[i] = Contacts[i];
            }
        }
        dbHelp.close();
    }

    public void Update_Con (View view) {
        UpdContact(contactID);
        Toast.makeText(this, "Contact #" + contactID + " updated.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // updating contact info
    public void UpdContact(int CiD) {
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        String Name = "", Surname = "", Email = "", BankName = "", BankAccountNumber = "", Password = "";
        int Number = 0;
        String Query = "Select * From " + DBHandler.CON_TABLE + " WHERE " /*+ DBHandler.COL_ID + " = '" + PiD + "' AND " */+ DBHandler.COL_CONTID + " = '" + CiD + "'";
        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            Name = InputName.getText().toString(); if (InputName.getText().toString().equals("")) { Name = "something"; }
            Surname = InputSurname.getText().toString(); if (InputSurname.getText().toString().equals("")) { Surname = ""; }
            if (InputNumber.getText().toString().equals("")) { Number = 0; }
                else { Number = Integer.parseInt(InputNumber.getText().toString()); }
            Email = InputEmail.getText().toString(); if (InputEmail.getText().toString().equals("")) { Email = ""; }

            if (caller.equals("CList_EditUser")) {
                BankName = Inputbn.getText().toString();
                BankAccountNumber = Inputban.getText().toString();
                Password = InputPassword.getText().toString();
            }
        }

        dbHandler.updateContact(CiD, Name, Surname, Number, Email, BankName, BankAccountNumber, Password);

        cursor.close();
        db.close();
        dbHandler.close();
    }

    // deletes contact
    public void DeleteContact (View view) {
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        if (caller.equals("CList_Edit")) {
            dbHandler.removeContact(purchase_identification_number, contactID);
        } else {
            dbHandler.deleteContact(contactID);
            dbHandler.removeContact(purchase_identification_number, contactID);
        }
        dbHandler.close();
        Toast.makeText(this, "Deleted a contact", Toast.LENGTH_SHORT).show();
        finish();
    }
}


