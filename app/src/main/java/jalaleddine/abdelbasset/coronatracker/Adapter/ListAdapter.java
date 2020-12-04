package jalaleddine.abdelbasset.coronatracker.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import jalaleddine.abdelbasset.coronatracker.CustomObjects.ContactInformation;
import jalaleddine.abdelbasset.coronatracker.R;

public class ListAdapter extends ArrayAdapter<ContactInformation> {

    private int resourceLayout;
    private Context mContext;

    public ListAdapter(Context context, int resource, List<ContactInformation> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        ContactInformation p = getItem(position);

       if (p != null) {
           LinearLayout background = v.findViewById(R.id.background);
            ImageView IV = (ImageView) v.findViewById(R.id.flag);
            TextView tt2 = (TextView) v.findViewById(R.id.txt);
            TextView tt3 = (TextView) v.findViewById(R.id.cur);

            if (IV != null && p.getGender() != null) {
                if(p.getGender().equals("Male")){
                  IV.setImageResource(R.mipmap.male_avatar);
                }
                else if(p.getGender().equals("Female")) {
                    IV.setImageResource(R.mipmap.female_avatar);
                }
                //Glide.with(mContext).load("http:" + p.getName()).into(IV);
            }

            if (tt2 != null) {
                tt2.setText(p.getName());
            }

            if (tt3 != null) {
                tt3.setText(p.getLastSeen());
            }
            if(p.isCorona()){
                background.setBackgroundColor(Color.RED);
                IV.setBackgroundColor(Color.RED);
                //background.setBackgroundColor(16396860);
            }
            if(!p.isCorona()){

            }

           // Log.d("ListAdapter " ,"tt2 " + tt2.getText().toString() + " tt3" + tt3.getText().toString());
        }

        return v;
    }}

