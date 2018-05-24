package sithrak.fps;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PurchaseAdapter extends BaseAdapter{
    private final Context mContext;
    private final PurchSupport[] purchases;
//    private static final String desc = "";

    public PurchaseAdapter(Context context, PurchSupport[] purchase){
        this.mContext = context;
        this.purchases = purchase;
    }

    @Override
    public int getCount() { return purchases.length; }

    @Override
    public long getItemId(int pos) { return 0; }

    @Override
    public Object getItem(int pos) { return null; }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        PurchSupport purchase = purchases[pos];

        if( convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_purchase, null);

            final TextView name = convertView.findViewById(R.id.textview_purchase_desc);
            final ImageView img = convertView.findViewById(R.id.imageview_cover_art);
            final TextView purchnum = convertView.findViewById(R.id.PNr);

            final ViewHolder ViewHolder = new ViewHolder(name, purchnum);
            convertView.setTag(ViewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();

        DBHandler db = new DBHandler(this.mContext, null, null, 1);

        // Description of purchase
        db.getPurchDesc(pos);
        viewHolder.nameTextView.setText(DBHandler.PurchDesc);

        // Number of purchase
        viewHolder.numberTextView.setText(String.valueOf(pos + 1));   // Position in grid seems better than PurchaseID, to avoid missing numbers
        db.close();

        return convertView;
    }

    private class ViewHolder {
        private final TextView nameTextView;
        private final TextView numberTextView;

        public ViewHolder(TextView nameTextView, TextView numberTextView){
            this.nameTextView = nameTextView;
            this.numberTextView = numberTextView;
        }
    }

}
