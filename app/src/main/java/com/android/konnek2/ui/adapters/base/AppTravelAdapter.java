package com.android.konnek2.ui.adapters.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.konnek2.R;


/**
 * Created by Lenovo on 15-10-2017.
 */

public class AppTravelAdapter extends BaseAdapter {


    private Context mContext;
    private final String[] travelName;
    private final TypedArray travelImage;

    public AppTravelAdapter(Context c, String[] travelName, TypedArray travelImage) {
        mContext = c;
        this.travelName = travelName;
        this.travelImage = travelImage;
    }


    @Override
    public int getCount() {
        return travelName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.item_travel, null);
            TextView textView = grid.findViewById(R.id.grid_trveltext);
            ImageView imageView = grid.findViewById(R.id.grid_trvelimage);
            textView.setText(travelName[position]);
            imageView.setImageResource(travelImage.getResourceId(position, 0));
        } else {
            grid = convertView;
        }


        return grid;
    }
}
