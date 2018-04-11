package com.aikya.konnek2.utils.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.aikya.konnek2.R;
import com.aikya.konnek2.ui.fragments.mediapicker.MediaPickHelperFragment;
import com.aikya.konnek2.ui.fragments.mediapicker.MediaSourcePickDialogFragment;
import com.aikya.konnek2.utils.MediaUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MediaPickHelper {

    public void pickAnMedia(Fragment fragment, int requestCode) {
        MediaPickHelperFragment mediaPickHelperFragment = MediaPickHelperFragment
                .start(fragment, requestCode);
        showMediaSourcePickerDialog(fragment.getChildFragmentManager(), mediaPickHelperFragment);
    }

    public void pickAnMedia(FragmentActivity activity, int requestCode) {
        MediaPickHelperFragment mediaPickHelperFragment = MediaPickHelperFragment
                .start(activity, requestCode);
        showMediaSourcePickerDialog(activity.getSupportFragmentManager(), mediaPickHelperFragment);
    }

    private void showMediaSourcePickerDialog(FragmentManager fm, MediaPickHelperFragment fragment) {
        MediaSourcePickDialogFragment.show(fm, fragment);
    }

    public void pickImageChooser(FragmentActivity activity, int requestCode) {
        MediaPickHelperFragment mediaPickHelperFragment = MediaPickHelperFragment
                .start(activity, requestCode);
    }

}