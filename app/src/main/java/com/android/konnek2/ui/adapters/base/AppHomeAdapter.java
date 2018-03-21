package com.android.konnek2.ui.adapters.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.konnek2.R;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.AppPreference;


/**
 * Created by Lenovo on 15-10-2017.
 */

public class AppHomeAdapter extends BaseAdapter {


    private Context mContext;
    private final String[] title;
    private final String[] subtitle;
    private final TypedArray Imageid;
    LinearLayout HomeGrid;

    public AppHomeAdapter(Context c, String[] title, String[] subtitle, TypedArray Imageid) {
        mContext = c;
        this.Imageid = Imageid;
        this.title = title;
        this.subtitle = subtitle;
    }


    @Override
    public int getCount() {
        return title.length;
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
            grid = inflater.inflate(R.layout.item_home_grid, null);
            TextView textView = grid.findViewById(R.id.grid_text);
            TextView textView2 = grid.findViewById(R.id.grid_text2);
            ImageView imageView = grid.findViewById(R.id.grid_image);
            LinearLayout grid_home = grid.findViewById(R.id.grid_home);

            textView.setText(title[position]);
            if (position == 2 || position == 6) {
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(subtitle[2]);

            }
            if (position == 6) {

                textView2.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(subtitle[6]);
            }


            imageView.setImageResource(Imageid.getResourceId(position, 0));
        } else {
            grid = convertView;
        }
        return grid;
    }
}
