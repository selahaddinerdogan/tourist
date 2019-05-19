package com.example.cansu.touristguidde;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<String> cityList = null;
    private ArrayList<String> arraylist;

    public ListViewAdapter(Context context, List<String> cityNamesList) {
        mContext = context;
        this.cityList = cityNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(cityNamesList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public String getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
//            view.bringToFront();
            view = inflater.inflate(R.layout.listview_items, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intocan = new Intent(mContext, select.class);
                    intocan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intocan.putExtra("city",cityList.get(position));
                    mContext.startActivity(intocan);
                }
            });
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(cityList.get(position));
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        cityList.clear();
        if (charText.length() == 0) {
            cityList.addAll(arraylist);
        } else {
            for (String city : arraylist) {
                if (city.toLowerCase(Locale.getDefault()).contains(charText)) {
                    cityList.add(city);
                }
            }
        }
        notifyDataSetChanged();
    }

}