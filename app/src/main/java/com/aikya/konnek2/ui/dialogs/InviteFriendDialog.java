package com.aikya.konnek2.ui.dialogs;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.aikya.konnek2.R;

public class InviteFriendDialog extends DialogFragment {

    public InviteFriendDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.invite_friend_dialog, new LinearLayout(getActivity()), false);

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);
        return builder;
    }
}
