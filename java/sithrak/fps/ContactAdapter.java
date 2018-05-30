package sithrak.fps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// fills frame for each contact in purchase
public class ContactAdapter extends BaseAdapter{
    private final Context mContext;
    private final ContactSupport[] contacts;
    public static int[] rightContact;// = new int[1024];

    public ContactAdapter(Context context, ContactSupport[] contact){
        this.mContext = context;
        this.contacts = contact;
    }

    @Override
    public int getCount() { return contacts.length; }

    @Override
    public long getItemId(int pos) { return 0; }

    @Override
    public Object getItem(int pos) { return null; }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        int Actual_ID = rightContact[pos];
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.contact_frame, null);

            final TextView name = convertView.findViewById(R.id.fname);
            final TextView surname = convertView.findViewById(R.id.lname);

            final ViewHolder ViewHolder = new ViewHolder(name, surname);
            convertView.setTag(ViewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        DBHandler db = new DBHandler(this.mContext, null, null, 1);

        viewHolder.nameTextView.setText(db.loadContactName(Actual_ID));
        viewHolder.surnameTextView.setText(db.loadContactSurname(Actual_ID));

        db.close();
        return convertView;
    }

    private class ViewHolder {
        private final TextView nameTextView;
        private final TextView surnameTextView;

        public ViewHolder(TextView name, TextView surname) {
            this.nameTextView = name;
            this.surnameTextView = surname;
        }
    }
}
