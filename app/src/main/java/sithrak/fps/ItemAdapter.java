package sithrak.fps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ItemAdapter extends BaseAdapter {

    private final Context mContext;
    private final ItemSupport[] items;
    public static int[] correctitem;// = new int[1024];

    public  ItemAdapter(Context context, ItemSupport[] item){
        this.mContext = context;
        this.items = item;
    }

    @Override
    public int getCount() { return items.length; }

    @Override
    public long getItemId(int pos) { return 0; }

    @Override
    public Object getItem(int pos) { return null; }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        int actual_ID = correctitem[pos];

        // Define fields of the current grid frame
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_frame, null);

            final TextView name = convertView.findViewById(R.id.Item_name);
            final TextView totalsum = convertView.findViewById(R.id.Item_sum);
            final ImageView cat = convertView.findViewById(R.id.cat_image);

            final ViewHolder ViewHolder = new ViewHolder(name, totalsum, cat);
            convertView.setTag(ViewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        DBHandler db = new DBHandler(this.mContext, null, null, 1);

        viewHolder.nameTextView.setText(db.loadItemName(actual_ID));

        DecimalFormat dec = new DecimalFormat("#.##");
        int quantity = db.loadItemQuantity(actual_ID);

        // Calculating the total sum of the items
        float value = (Float.valueOf(dec.format(db.loadItemPrice(actual_ID)))) * quantity;
        String total_item_sum = String.valueOf(value);

        // Makes sure that there are 2 numbers after the decimal point for the output
        String fixed_sum = smallCheck(total_item_sum);
        viewHolder.sumTextView.setText(fixed_sum);

        // Selects an SVG image for the grid frame
        String Category = db.loadItemCat(actual_ID);
        SelectCat(viewHolder, Category);

        db.close();

        return convertView;
    }

    private class ViewHolder {

        private final TextView nameTextView;
        private final ImageView imageViewCover;
        private final TextView sumTextView;

        public ViewHolder(TextView nameTextView, TextView sumTextView, ImageView imageViewCover){
            this.nameTextView = nameTextView;
            this.imageViewCover = imageViewCover;
            this.sumTextView = sumTextView;
        }
    }

    // Checks the return value (what the user sees, so that there isn't just one number after the dot)
    public static String smallCheck(String sum) {
        String substring = sum.substring(Math.max(sum.length() - 2, 0));

        if (substring.equals(".0") || substring.equals(".1") || substring.equals(".2")
                || substring.equals(".3") || substring.equals(".4") || substring.equals(".5")
                || substring.equals(".6") || substring.equals(".7") || substring.equals(".8")
                || substring.equals(".9")) {
            sum = sum.replace(substring,substring + "0"); // Replace requires that the new value is given to another string variable
        }

        return sum;
    }

    // Selection of SVG image
    // Thanks to the creators of the SVG's from TheNounProject.com
    private void SelectCat(ViewHolder viewHolder, String Categ){
        switch (Categ) {
            case "Food": {
                viewHolder.imageViewCover.setImageResource(R.drawable.food); // Created by Royyan Wijaya
                break;
            }
            case "Vegetables": {
                viewHolder.imageViewCover.setImageResource(R.drawable.veggies); // Created by Clockwise
                break;
            }
            case "Fruits": {
                viewHolder.imageViewCover.setImageResource(R.drawable.fruitz); // Created by icon 54
                break;
            }
            case "Meat & Fish": {
                viewHolder.imageViewCover.setImageResource(R.drawable.f_steak); //Created by Paul Smile
                break;
            }
            case "Snacks": {
                viewHolder.imageViewCover.setImageResource(R.drawable.snacks); // Created by bmijnlieff
                break;
            }
            case "Beverages": {
                viewHolder.imageViewCover.setImageResource(R.drawable.beverage); // Created by Adnen Kadri
                break;
            }
            case "Alcohol": {
                viewHolder.imageViewCover.setImageResource(R.drawable.alcohol); // Created by Aleksandr Vector
                break;
            }
            case "Clothing": {
                viewHolder.imageViewCover.setImageResource(R.drawable.clothes); // Created by Husein Aziz
                break;
            }
            case "Toys": {
                viewHolder.imageViewCover.setImageResource(R.drawable.toys); // Created by Gregor Cresnar
                break;
            }
            case "Electronics": {
                viewHolder.imageViewCover.setImageResource(R.drawable.appliance); // Created by Yazmin Alanis
                break;
            }
            case "Miscellaneous": {
                viewHolder.imageViewCover.setImageResource(R.drawable.misc); // Created by heri sugianto
                break;
            }
        }
    }
}
