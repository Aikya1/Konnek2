package com.aikya.konnek2.ui.adapters.call;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.List;

/**
 * Created by Lenovo on 21-11-2017.
 */

public class IncomingCallImageAdapter extends BaseAdapter {


    private LayoutInflater inflater = null;
    private List<QMUser> qmUserList;

    public IncomingCallImageAdapter(Context context,List<QMUser> qmUserList) {

        this.qmUserList=qmUserList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return qmUserList.size();
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
         Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.item_incomingcall_users, null);
        holder.txtOpponentName = rowView.findViewById(R.id.txt_users_name);
        holder.image_incoming_call = rowView.findViewById(R.id.image_incoming_call);
        if(qmUserList!=null && !qmUserList.isEmpty())
        {
            holder.txtOpponentName.setText(qmUserList.get(position).getFullName());
            displayAvatarImage(qmUserList.get(position).getAvatar(), holder.image_incoming_call);
        }

        return rowView;
    }

    protected void displayAvatarImage(String uri, ImageView imageView) {
        if (ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        }
        else {
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    public class Holder {

        TextView txtOpponentName;
        RoundedImageView image_incoming_call;
    }
}
