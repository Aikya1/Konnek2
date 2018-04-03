package com.aikya.konnek2.base.db;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.utils.ValidationUtils;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.services.QMUserCacheImpl;
import com.aikya.konnek2.ui.views.roundedimageview.RoundedImageView;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;




import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 26-10-2017.
 */

public class AppCallHistoryAdapter extends BaseAdapter {


    private static final String TAG = AppCallHistoryAdapter.class.getSimpleName();
    private ArrayList<AppCallLogModel> appcallLogModelArrayList;
    private Context context;
    private static LayoutInflater inflater = null;
    private QMUserCacheImpl qmUserCache;
    private List<QMUser> qmUsers;
    private ArrayList<Integer> opponentList = null;

    public AppCallHistoryAdapter(Context context, ArrayList<AppCallLogModel> appcallLogModelArrayList) {
        this.context = context;
        this.appcallLogModelArrayList = appcallLogModelArrayList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        qmUserCache = new QMUserCacheImpl(context);

    }


    @Override
    public int getCount() {
        return appcallLogModelArrayList.size();
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
        rowView = inflater.inflate(R.layout.item_call_history_new, null);
        try {
            qmUsers = new ArrayList<>();

            qmUsers = (getOpponents(Integer.parseInt(appcallLogModelArrayList.get(position).getCallOpponentId())));
            holder.call_UserImage = rowView.findViewById(R.id.image_user);
            holder.call_Priority = rowView.findViewById(R.id.call_priority);
            holder.call_status = rowView.findViewById(R.id.call_status);
            holder.callType = rowView.findViewById(R.id.Image_call_type);
            holder.call_userName = rowView.findViewById(R.id.call_userName);
            holder.call_LogDate = rowView.findViewById(R.id.call_LogDate);
            holder.call_LogTime = rowView.findViewById(R.id.call_LogTime);
            holder.call_Timer = rowView.findViewById(R.id.call_timer);

            holder.call_userName.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.call_LogDate.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.call_LogTime.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.call_Timer.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.call_userName.setTextSize(16);
            holder.call_userName.setTypeface(null, Typeface.BOLD);


            if (!appcallLogModelArrayList.isEmpty() && appcallLogModelArrayList != null) {

                if (!(appcallLogModelArrayList.get(position).getUserId() == AppConstant.GROUP_CALL)) {
//                    holder.call_UserImage.setImageResource(R.drawable.ic_chat_face1);
                    displayAvatarImage(qmUsers.get(0).getAvatar(), holder.call_UserImage);

                } else {
                    //holder.call_UserImage.setImageResource(R.drawable.ic_chat_face1);
                    displayAvatarImage(qmUsers.get(0).getAvatar(), holder.call_UserImage);
                }
                holder.call_userName.setText(appcallLogModelArrayList.get(position).getCallOpponentName());
                holder.call_LogDate.setText(appcallLogModelArrayList.get(position).getCallDate());
                holder.call_LogTime.setText(appcallLogModelArrayList.get(position).getCallTime());
                holder.call_Timer.setText(appcallLogModelArrayList.get(position).getCallDuration());

                if (appcallLogModelArrayList.get(position).getCallType().equalsIgnoreCase(AppConstant.CALL_AUDIO)) {
                    holder.callType.setImageResource(R.drawable.ic_call_audiocall);
                } else {
                    holder.callType.setImageResource(R.drawable.ic_call_videocall);
                }

                if (appcallLogModelArrayList.get(position).getCallPriority().equalsIgnoreCase(AppConstant.CALL_PRIORITY_HIGH)) {
//                    holder.call_Priority.setImageResource(R.drawable.ic_call_high_priority);
                } else {
                    if (appcallLogModelArrayList.get(position).getCallPriority().equalsIgnoreCase(AppConstant.CALL_PRIORITY_MEDIUM)) {
//                        holder.call_Priority.setImageResource(R.drawable.ic_call_medium_priority);
                    } else {
//                        holder.call_Priority.setImageResource(R.drawable.ic_call_low_priority);
                    }
                }


                if (appcallLogModelArrayList.get(position).getCallStatus().equalsIgnoreCase(AppConstant.CALL_STATUS_DIALED)) {
                    holder.call_status.setImageResource(R.drawable.ic_call_uparrow);
                } else {
                    if (appcallLogModelArrayList.get(position).getCallStatus().equalsIgnoreCase(AppConstant.CALL_STATUS_RECEIVED)) {
                        holder.call_status.setImageResource(R.drawable.ic_call_downarrow);

                    } else {
                        holder.call_status.setImageResource(R.drawable.ic_call_missedcall);
                    }

                }

            }

        } catch (Exception e) {
            e.getMessage();
        }

        return rowView;
    }

    protected void displayAvatarImage(String uri, ImageView imageView) {

        if (ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    public List<QMUser> getOpponents(int opponentId) {
        if (opponentList == null)
            opponentList = new ArrayList<>();
        else
            opponentList.clear();
        opponentList.add(opponentId);
        return qmUserCache.getUsersByIDs(opponentList);
    }


    public class Holder {

        TextView call_userName;
        TextView call_LogDate;
        TextView call_LogTime;
        TextView call_Timer;
        RoundedImageView call_UserImage;
        ImageView call_Priority;
        ImageView callType;
        ImageView call_status;


    }
}
