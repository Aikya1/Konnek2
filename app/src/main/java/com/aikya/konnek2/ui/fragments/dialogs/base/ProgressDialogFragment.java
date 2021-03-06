package com.aikya.konnek2.ui.fragments.dialogs.base;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import com.aikya.konnek2.R;


public class ProgressDialogFragment extends DialogFragment {

    private static final String TAG = ProgressDialogFragment.class.getSimpleName();
    private static final String ARG_MESSAGE_ID = "message_id";

    ProgressDialog dialog;

    public static void show(FragmentManager fm) {

        if (fm.findFragmentByTag(TAG) == null) {
            fm.beginTransaction().add(newInstance(), TAG).commitAllowingStateLoss();
        }
    }

    public static void hide(FragmentManager fm) {
        DialogFragment fragment = (DialogFragment) fm.findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.dismissAllowingStateLoss();
        }
    }

    public static ProgressDialogFragment newInstance() {

        return newInstance(R.string.dlg_wait_please);
    }

    public static ProgressDialogFragment newInstance(int messageId)
    {

        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, messageId);

        ProgressDialogFragment dialog = new ProgressDialogFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog= new ProgressDialog(getActivity());
        dialog.setMessage(getString(getArguments().getInt(ARG_MESSAGE_ID)));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Disable the back button
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        };
        dialog.setOnKeyListener(keyListener);

        return dialog;
    }


   /*@Override
    public void onResume() {
        super.onResume();

        if (dialog.isShowing() && dialog != null) {
            dialog.dismiss();

        }
    }*/
}