package com.aikya.konnek2.ui.fragments.mediapicker;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.util.Log;


import com.aikya.konnek2.R;
import com.aikya.konnek2.tasks.GetFilepathFromUriTask;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.utils.listeners.OnContactPickedListener;
import com.aikya.konnek2.utils.listeners.OnMediaPickedListener;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.call.db.models.Attachment;
import com.aikya.konnek2.utils.MediaUtils;
import com.quickblox.ui.kit.chatmessage.adapter.utils.LocationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MediaPickHelperFragment extends Fragment {

    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String ARG_PARENT_FRAGMENT = "parentFragment";

    private static final String TAG = MediaPickHelperFragment.class.getSimpleName();

    private OnMediaPickedListener listener;
    private OnContactPickedListener contactPickedListener;

    public MediaPickHelperFragment() {
    }

    public static MediaPickHelperFragment start(Fragment fragment, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putString(ARG_PARENT_FRAGMENT, fragment.getClass().getSimpleName());

        return start(fragment.getActivity().getSupportFragmentManager(), args);
    }

    public static MediaPickHelperFragment start(FragmentActivity activity, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);

        return start(activity.getSupportFragmentManager(), args);
    }

    private static MediaPickHelperFragment start(FragmentManager fm, Bundle args) {
        MediaPickHelperFragment fragment = (MediaPickHelperFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new MediaPickHelperFragment();
            fm.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
            fragment.setArguments(args);
        }
        return fragment;
    }

    public static void stop(FragmentManager fm) {
        Fragment fragment = fm.findFragmentByTag(TAG);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    public Intent showPickImageChooserIntent(Context context) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        File photoFile = MediaUtils.getTemporaryCameraFilePhoto();
        if (photoFile != null) {
            Uri photoURI = MediaUtils.getValidUri(photoFile, context);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.d("Media", "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isResultFromMediaPick(requestCode, resultCode, data)) {
            if (requestCode == MediaUtils.IMAGE_VIDEO_LOCATION_REQUEST_CODE) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    double latitude = bundle.getDouble(ConstsCore.EXTRA_LOCATION_LATITUDE);
                    double longitude = bundle.getDouble(ConstsCore.EXTRA_LOCATION_LONGITUDE);
                    String location = LocationUtils.generateLocationJson(new Pair<>(ConstsCore.LATITUDE_PARAM, latitude),
                            new Pair<>(ConstsCore.LONGITUDE_PARAM, longitude));
                    listener.onMediaPicked(requestCode, Attachment.Type.LOCATION, location);
                }
            } else {
                if ((requestCode == MediaUtils.CAMERA_PHOTO_REQUEST_CODE || requestCode == MediaUtils.CAMERA_VIDEO_REQUEST_CODE) && (data == null || data.getData() == null)) {
                    // Hacky way to get EXTRA_OUTPUT param to work.
                    // When setting EXTRA_OUTPUT param in the camera intent there is a chance that data will return as null
                    // So we just pass temporary camera file as a data, because RESULT_OK means that photo was written in the file.
                    data = new Intent();
                    data.setData(MediaUtils.getValidUri(MediaUtils.getLastUsedCameraFile(), this.getContext()));
                }

                new GetFilepathFromUriTask(getChildFragmentManager(), listener,
                        getArguments().getInt(ARG_REQUEST_CODE)).execute(data);
            }
            if (requestCode == MediaUtils.CONTACT_REQUEST_CODE) {
                if (contactPickedListener != null) {
                    contactPickedListener.onContactSelected(data);
                }
            }
        } else {
            stop(getChildFragmentManager());
            if (listener != null) {
                listener.onMediaPickClosed(getArguments().getInt(ARG_REQUEST_CODE));
            }
        }

        //remove fragments from stack added before getting media from device
        getFragmentManager().popBackStack();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment fragment = ((BaseActivity) activity).getSupportFragmentManager()
                .findFragmentByTag(getArguments().getString(ARG_PARENT_FRAGMENT));
        if (fragment != null) {
            if (fragment instanceof OnMediaPickedListener) {
                listener = (OnMediaPickedListener) fragment;
            } else if (fragment instanceof OnContactPickedListener) {
                contactPickedListener = (OnContactPickedListener) fragment;
            }
        } else {
            if (activity instanceof OnMediaPickedListener) {
                listener = (OnMediaPickedListener) activity;
            } else if (activity instanceof OnContactPickedListener) {
                contactPickedListener = (OnContactPickedListener) activity;
            }
        }

        if (listener == null /*|| contactPickedListener == null*/) {
            throw new IllegalStateException(
                    "Either activity or fragment should implement OnMediaPickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        contactPickedListener = null;
    }

    public void setListener(OnMediaPickedListener listener) {
        this.listener = listener;
    }

    public void setContactPickedListener(OnContactPickedListener contactPickedListener) {
        this.contactPickedListener = contactPickedListener;
    }

    private boolean isResultFromMediaPick(int requestCode, int resultCode, Intent data) {
        return resultCode == Activity.RESULT_OK &&
                ((requestCode == MediaUtils.CAMERA_PHOTO_REQUEST_CODE
                        || requestCode == MediaUtils.CAMERA_VIDEO_REQUEST_CODE)
                        || (requestCode == MediaUtils.GALLERY_REQUEST_CODE && data != null)
                        || (requestCode == MediaUtils.IMAGE_VIDEO_LOCATION_REQUEST_CODE && data != null)
                        || (requestCode == MediaUtils.CONTACT_REQUEST_CODE && data != null)
                        || (requestCode == MediaUtils.DOC_REQUEST_CODE && data != null));
    }
}