package com.aikya.konnek2.ui.activities.catchup;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aikya.konnek2.R;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.utils.StringUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.SystemPermissionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.Bind;


public class CatchUpActivity extends BaseLoggableActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = CatchUpActivity.class.getSimpleName();
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SystemPermissionHelper systemPermissionHelper;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    FusedLocationProviderClient mFusedLocationProviderClient;
    boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    CircleOptions circleOptions;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    private Circle circle;
    LatLng userLatLng;
    Location userLocation;


    @Bind(R.id.mile_seek)
    SeekBar mileSeekBar;
    @Bind(R.id.catch_miles)
    TextView milesValueTv;


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

        mileSeekBar.setOnSeekBarChangeListener(this);
        mileSeekBar.setKeyProgressIncrement(10);
        systemPermissionHelper = new SystemPermissionHelper(CatchUpActivity.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (checkLocationPermission()) {

            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            userLatLng = currentLocation;
                            userLocation = location;
                            double iMeter = 5 * 1609.34;

                            mMap = googleMap;
                            circleOptions = new CircleOptions()
                                    .center(currentLocation)
                                    .fillColor(0x550000FF)
                                    .radius(iMeter)
                                    .strokeWidth(0f);

                            circle = mMap.addCircle(circleOptions);

                            float currentZoomLevel = getZoomLevel(circle);
                            float animateZomm = currentZoomLevel + 5;


                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("my loc"));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), animateZomm));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel), 2000, null);

                        }
                    });
        }
    }

    public float getZoomLevel(Circle circle) {
        float zoomLevel = 0;
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel + .5f;
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = 1 * progress;
        circle.setRadius(progress * 1609.34);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        milesValueTv.setText(String.valueOf(progress));
        float currentZoomLevel = getZoomLevel(circle);
        float animateZomm = currentZoomLevel ;


        mMap.addMarker(new MarkerOptions().position(userLatLng).title("my loc"));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), animateZomm));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel), 2000, null);

        Log.d(TAG, "Progress = " + progress);
    }




    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
