package com.aikya.konnek2.ui.activities.catchup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.aikya.konnek2.R;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.call.CallActivity;
import com.aikya.konnek2.ui.activities.chats.BaseDialogActivity;
import com.aikya.konnek2.utils.StringUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.SystemPermissionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.aikya.konnek2.utils.helpers.SystemPermissionHelper.LOCATION_PERMISSION_REQUEST_CODE;


public class CatchUpActivity extends BaseLoggableActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private static final String TAG = CatchUpActivity.class.getSimpleName();
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SystemPermissionHelper systemPermissionHelper;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    FusedLocationProviderClient mFusedLocationProviderClient;
    boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;

    @Override
    protected int getContentResId() {
        return R.layout.activity_catchup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.catch_map);
        mapFragment.getMapAsync(this);
    }

    private void initValues() {
        systemPermissionHelper = new SystemPermissionHelper(CatchUpActivity.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (checkLocationPermission()) {

            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(sydney).title("my loc"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
                        }
                    });
        }
    }


    private boolean checkLocationPermission() {
        if (systemPermissionHelper.isAllLocationPermissionGranted()) {
            mLocationPermissionGranted = true;
            return true;
        } else {
            systemPermissionHelper.requestAllPermissionForLocation();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case SystemPermissionHelper.PERMISSIONS_FOR_CALL_REQUEST: {
                if (grantResults.length > 0) {
                    if (!systemPermissionHelper.isAllLocationPermissionGranted()) {
                        showToastDeniedPermissions(permissions, grantResults);
                    } else {
                        mLocationPermissionGranted = true;
                    }
                }
            }
        }
    }

    private void showToastDeniedPermissions(String[] permissions, int[] grantResults) {
        ArrayList<String> deniedPermissions = systemPermissionHelper
                .collectDeniedPermissionsFomResult(permissions, grantResults);

        ToastUtils.longToast(
                getString(com.aikya.konnek2.R.string.denied_permission_message, StringUtils.createCompositeString(deniedPermissions)));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
