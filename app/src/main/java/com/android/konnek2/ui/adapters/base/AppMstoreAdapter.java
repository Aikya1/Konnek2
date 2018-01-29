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

public class AppMstoreAdapter extends BaseAdapter {


    private Context mContext;
    private final String[] mstoreName;
    //    private final int[] mstoreImage;
    private final TypedArray mstoreImage;

    public AppMstoreAdapter(Context c, String[] mstoreName, TypedArray mstoreImage) {
        mContext = c;
        this.mstoreName = mstoreName;
        this.mstoreImage = mstoreImage;
    }

    @Override
    public int getCount() {
        return mstoreName.length;
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
            grid = inflater.inflate(R.layout.item_mstore, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_msotretext);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_mstoreimage);
            textView.setText(mstoreName[position]);
            imageView.setImageResource(mstoreImage.getResourceId(position,0));
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
