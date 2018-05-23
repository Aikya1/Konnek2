package com.aikya.konnek2.ui.activities.catchup;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.aikya.konnek2.call.core.utils.Utils;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.call.db.models.DialogOccupant;
import com.aikya.konnek2.call.db.models.Friend;
import com.aikya.konnek2.call.db.utils.DialogTransformUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.ui.activities.chats.PrivateDialogActivity;
import com.aikya.konnek2.ui.adapters.friends.FriendsAdapter;
import com.aikya.konnek2.utils.StringUtils;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.SystemPermissionHelper;
import com.aikya.konnek2.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.baoyz.actionsheet.ActionSheet;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.Performer;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.internal.Util;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CatchUpActivity extends BaseLoggableActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ActionSheet.ActionSheetListener, com.aikya.konnek2.utils.Locator.Listener {
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

    QBUser currentUser;
    UserCustomData currentUserCustomData, dbUserCustomData;
    double curUserLatitude, curUserLongitude, dbUserLatitude, dbUserLongitude;

    float radiusSetByUser = 0;


    @Bind(R.id.mile_seek)
    SeekBar mileSeekBar;
    @Bind(R.id.catch_miles)
    TextView milesValueTv;
    @Bind(R.id.shareLoc)
    Switch shareLocationSwitch;
    @Bind(R.id.catch_tv1)
    TextView milesTextView;
    @Bind(R.id.catchUpListView)
    RecyclerView userListView;

    Marker mapMarker;


    @Override
    protected int getContentResId() {
        return R.layout.activity_catchup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActionBarWithUpButton();
        initValues();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.catch_map);
        mapFragment.getMapAsync(this);
        shareLocationSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                ToastUtils.shortToast("refresh");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initValues() {

        mileSeekBar.setOnSeekBarChangeListener(this);
//        mileSeekBar.setProgress(100);
        mileSeekBar.setKeyProgressIncrement(1);
        systemPermissionHelper = new SystemPermissionHelper(CatchUpActivity.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        qbUserLists = new ArrayList<>();
        dataManager = DataManager.getInstance();
        qMUserList = new ArrayList<>();


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
                            double iMeter = 10 * 1609.34;

                            mMap = googleMap;
                            circleOptions = new CircleOptions()
                                    .center(currentLocation)
                                    .fillColor(0x550000FF)
                                    .radius(iMeter)
                                    .strokeWidth(0f);

                           /* milesTextView.setText(getString(R.string.milesTextView, String.valueOf(10.0)));
                            mileSeekBar.setProgress(10);*/


                            circle = mMap.addCircle(circleOptions);

                            float currentZoomLevel = getZoomLevel(circle);
                            float animateZomm = currentZoomLevel + 5;


                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("my loc"));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                                    location.getLongitude()), animateZomm));
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = AppSession.getSession().getUser();
        currentUserCustomData = Utils.customDataToObject(currentUser.getCustomData());
        if (currentUserCustomData.getLatitude() != 0 && currentUserCustomData.getLongitude() != 0) {
            curUserLatitude = currentUserCustomData.getLatitude();
            curUserLongitude = currentUserCustomData.getLongitude();
        }

        if (!checkLocationPermission()) {
            ToastUtils.shortToast("please grant location permission to continue using Catch-Up");
        } else {
            getRegisteredUsersFromQBAddressBook();
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
        double prog = (double) progress / 10;

        if (circle != null) {
            circle.setRadius(prog * 1609.34);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

            float currentZoomLevel = getZoomLevel(circle);
            float animateZomm = currentZoomLevel;


            mMap.addMarker(new MarkerOptions().position(userLatLng).title("my loc"));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), animateZomm));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel), 2000, null);

            radiusSetByUser = (float) progress / 10;
            milesValueTv.setText(getString(R.string.milesTextView, String.valueOf(radiusSetByUser)));


            updateContactsList(qbUserLists);
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("Share for 1 hour", "Share until end of the day", "Share indefinitely")
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();
        }
    }


    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        Log.d(TAG, "ACtionSHeet canceled");
        shareLocationSwitch.setChecked(false);


    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        Log.d(TAG, "ActionSheet = " + actionSheet.toString());
        Log.d(TAG, "ACtionSHeet On Other Button clicked" + index);
    }

    @Override
    public void onLocationFound(Location location) {

        ToastUtils.shortToast("Location found == " + location.getLongitude() + " " + location.getLatitude());

    }

    @Override
    public void onLocationNotFound() {
        ToastUtils.shortToast("Location not found");
    }

    private void getRegisteredUsersFromQBAddressBook() {
        showProgress();
        String UDID = "";
        boolean isCompact = false;
        Performer<ArrayList<QBUser>> performer = QBUsers.getRegisteredUsersFromAddressBook(UDID, isCompact);
        Observable<ArrayList<QBUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<QBUser>>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error == " + e.getMessage());
                        hideProgress();
                    }

                    @Override
                    public void onNext(ArrayList<QBUser> qbUsers) {
                        hideProgress();
                        if (qbUsers != null && !qbUsers.isEmpty()) {
                            if (qbUsers.contains(currentUser)) {
                                qbUsers.remove(currentUser);
                            }
                            updateContactsList(qbUsers);
                        }
                    }
                });


        friendsAdapter = new FriendsAdapter(this, qMUserList, false);
        friendsAdapter.setFriendListHelper(friendListHelper);
        userListView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration divider = new DividerItemDecoration(userListView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_horizontal));
        userListView.addItemDecoration(divider);

        userListView.setAdapter(friendsAdapter);

        initCustomListeners();
    }

    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {
            @Override
            public void onItemClicked(View view, QMUser user, int position) {
                super.onItemClicked(view, user, position);
                selectedUser = user;
                insertUserAsFriendToDb(selectedUser);
                try {
                    checkForOpenChat(user);
                } catch (QBResponseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateContactsList(List<QBUser> usersList) {
        this.qbUserLists = usersList;
        Log.d(TAG, "UserList Size = " + usersList.size());
        for (int i = 0; i < usersList.size(); i++) {
            placeUserMarkerOnMap(usersList.get(i));
        }
        friendsAdapter.setList(qMUserList);
    }

    /*Method that will get the users from the Database.
     * 2) get the latitude and longitude from the CustomData,
     * 3) find the distance between two co-ordinates and place
     * the marker on the google map.*/
    private void placeUserMarkerOnMap(QBUser qbUser) {
        dbUserCustomData = Utils.customDataToObject(qbUser.getCustomData());
        if (dbUserCustomData.getIsLocationToShare() && (dbUserCustomData.getLongitude() != 0 &&
                dbUserCustomData.getLatitude() != 0)) {

            dbUserLatitude = dbUserCustomData.getLatitude();
            dbUserLongitude = dbUserCustomData.getLongitude();
            double distanceInKm = CalculationByDistance(new LatLng(curUserLatitude, curUserLongitude),
                    new LatLng(dbUserLatitude, dbUserLongitude));
            Log.d(TAG, "Distance == " + distanceInKm);
            /*method that will check if the user is in the radius or not*/
            String val = milesValueTv.getText().toString();
            if (val.contains("miles")) {
                String arr[] = val.split("\\s+");
                double val3 = milesToKm(Double.valueOf(arr[0]));

                int result = Double.compare(distanceInKm, val3);

                if (result > 0) {
                    //distanceInKm is greater than val3
                    Log.d(TAG, "User not present in radius");
                    mapMarker.remove();

                } else if (result < 0) {
                    //distanceInKm is lesser than val3
                    //user is in the radius that is specified.
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(dbUserLatitude, dbUserLongitude));
                    options.title("someTitle");
                    options.snippet("someDesc");

                    mapMarker = mMap.addMarker(options);

                    qMUserList.add(QMUser.convert(qbUser));
                } else {
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(dbUserLatitude, dbUserLongitude));
                    options.title("someTitle");
                    options.snippet("someDesc");

                    mapMarker = mMap.addMarker(options);

                    qMUserList.add(QMUser.convert(qbUser));

                }
            }
        }
    }


    private static double milesToKm(double distanceInKm) {
        return distanceInKm * 1.60934;
    }

    private void insertUserAsFriendToDb(QMUser selectedUser) {
        Friend friend = new Friend();
        friend.setUser(selectedUser);
        dataManager.getFriendDataManager().createOrUpdate(friend, true);
    }

    private void checkForOpenChat(QMUser user) throws QBResponseException {

        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            startPrivateChat(chatDialog);
        } else {
            if (checkNetworkAvailableWithError()) {
//                showProgress();
                QBCreatePrivateChatCommand.start(this, user);
            }


          /*  mHandler.post(new Runnable() {
                @Override
                public void run() {
                    QBChatDialog privateDialog = null;
                    try {
                        privateDialog = chatHelper.createPrivateDialogIfNotExist(user.getId());
                    } catch (QBResponseException e) {
                        e.printStackTrace();
                    }
                    startPrivateChat(privateDialog);
                }
            });*/


        }
    }

    private void startPrivateChat(QBChatDialog dialog) {
        PrivateDialogActivity.start(this, selectedUser, dialog);
        this.finish();
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
