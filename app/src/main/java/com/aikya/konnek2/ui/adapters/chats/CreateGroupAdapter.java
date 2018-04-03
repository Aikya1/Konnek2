package com.aikya.konnek2.ui.adapters.chats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.R;
import com.aikya.konnek2.utils.listeners.Chats.SelectedUserListListener;

import java.util.List;

/**
 * Created by rajeev on 1/3/18.
 */

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.MyView> {

    private List<QMUser> list;
    private SelectedUserListListener selectUsersListener;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView closeBtnIv, userProfileIv;


        public MyView(View view) {
            super(view);
            textView = view.findViewById(R.id.members_edittext);
            closeBtnIv = view.findViewById(R.id.closeBtnIv);
            userProfileIv = view.findViewById(R.id.userProfileIv);


        }
    }


    public CreateGroupAdapter(List<QMUser> selectedList, SelectedUserListListener listener) {
        this.list = selectedList;
        selectUsersListener = listener;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_group_top_user_layout, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        holder.textView.setText(list.get(position).getFullName());
        holder.closeBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUsersListener.removeSelectedUser(position, list.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void removeItem(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

}
