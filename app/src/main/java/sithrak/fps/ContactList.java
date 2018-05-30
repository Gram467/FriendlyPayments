package sithrak.fps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ContactList extends AppCompatActivity {
    public static String[] contactsArray;
    public static int contactID = 0;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        Setup();
    }

    @Override
    public void onResume() {
        super.onResume();
        Setup();
    }

    public void Setup() {
        DBHandler db = new DBHandler(this, null, null, 1);

        db.loadAllContacts(); // To get number of contacts
        db.loadContactInfo(); // get contact values

        ArrayAdapter contacts = new ArrayAdapter<String>(this, R.layout.contact_bar, contactsArray);
        listView = findViewById(R.id.list_contacts);

        listView.setAdapter(contacts);
        db.close();

        UseAdapters();
    }

    public void UseAdapters() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent Intent = new Intent(ContactList.this, AddContact.class);
                Intent.putExtra("caller", "CList_Edit");
                startActivity(Intent);

                contactID = DBHandler.CustID[position + 1];
            }
        });
    }

    public void AddContact (View view) {
        Intent Intent = new Intent(ContactList.this, AddContact.class);
        startActivity(Intent);
    }

    public void EditUser (View view) {
        Intent Intent = new Intent(ContactList.this, AddContact.class);
        Intent.putExtra("caller", "CList_EditUser");
        startActivity(Intent);
    }


}