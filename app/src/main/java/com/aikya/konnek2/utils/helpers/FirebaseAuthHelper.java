package com.aikya.konnek2.utils.helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Arrays;
import java.util.Collections;

public class FirebaseAuthHelper {

    private static final String TAG = FirebaseAuthHelper.class.getSimpleName();

    public static final int RC_SIGN_IN = 456;
    public static final String EXTRA_FIREBASE_ACCESS_TOKEN = "extra_firebase_access_token";


    public void loginByPhone(Activity activity, String phNo, String countryCode) {

        if (!TextUtils.isEmpty(phNo)) {
            activity.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().setDefaultNumber(countryCode + phNo)
                                                    .build()))
                            .build(),
                    RC_SIGN_IN);


        } else {
            activity.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                            .setTheme(com.aikya.konnek2.R.style.FirebaseStyle)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }

    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    public static void getIdTokenForCurrentUser(final RequestFirebaseIdTokenCallback callback) {

        if (getCurrentFirebaseUser() == null) {
            SharedHelper.getInstance().saveFirebaseToken(null);
            callback.onError(new NullPointerException("Current Firebse User is null"));
            return;
        }

        getCurrentFirebaseUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String accessToken = task.getResult().getToken();
                    SharedHelper.getInstance().saveFirebaseToken(accessToken);
                    callback.onSuccess(accessToken);
                } else {
                    callback.onError(task.getException());
                }
            }
        });
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedHelper.getInstance().saveFirebaseToken(null);
    }

    public interface RequestFirebaseIdTokenCallback {

        void onSuccess(String accessToken);

        void onError(Exception e);
    }
}