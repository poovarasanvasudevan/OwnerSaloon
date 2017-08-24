package com.woolgather.app.main;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.rey.material.widget.ImageButton;
import com.woolgather.lib.kernel.Base;
import com.woolgather.lib.kernel.activity.BaseActivity;
import com.woolgather.lib.kernel.core.Prefs;
import com.woolgather.lib.kernel.widget.RippleIconButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private AppBarLayout         appbar;
    private SupportMapFragment   map;
    private Toolbar              toolbar;

    String displayName = "";
    String photoUrl    = "";
    String email       = "";


    GoogleMap                googleMap;
    FusedLocationProviderApi fusedLocationProviderApi;
    GoogleApiClient          googleApiClient;

    Boolean markerAdded = false;
    Marker myMarker;
    private ImageButton searchButton;
    private ImageButton myLocationButton;
    private EditText    searchBox;
    private Location currentLocation;

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    PermissionListener locationPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            LogUtils.d("I am No 3");
            fetchLocation();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            ToastUtils.showLong("Unable to get your Current Location due to Permission Denied.");
        }
    };

    public void fetchLocation() {
        LogUtils.d("I am No 4");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LogUtils.d("I am No 5");
            fusedLocationProviderApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        email = Prefs.with(this).read("email");
        photoUrl = Prefs.with(this).read("profilePic");
        displayName = Prefs.with(this).read("display_name");

        googleApiClient = Base.getGoogleApiClient(this, this);
        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_menu).color(Color.WHITE).actionBar());

        fusedLocationProviderApi = LocationServices.FusedLocationApi;

        map.getMapAsync(this);

        LogUtils.d("I am No 1");


        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                if(currentLocation !=null) {
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), googleMap.getCameraPosition().zoom));
                                }
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                            }
                        })
                        .setDeniedTitle("Permission denied")
                        .setGotoSettingButtonText("Go to Settings")
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .check();
            }
        });

    }

    private void initView() {
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        myLocationButton = (ImageButton) findViewById(R.id.myLocationButton);
        searchBox = (EditText) findViewById(R.id.searchBox);



        myLocationButton.setImageDrawable(
                new IconicsDrawable(this,CommunityMaterial.Icon.cmd_crosshairs_gps).sizeDp(18).color(Color.BLACK)
        );
    }

    public void location() {
        LogUtils.d("I am No 2");
        TedPermission.with(this)
                .setPermissionListener(locationPermissionListener)
                .setDeniedTitle("Permission denied")
                .setGotoSettingButtonText("Go to Settings")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = this.googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map));

            //boolean success = true;

            if (!success) {
                Log.e("TAG", "Style parsing failed.");

            }
            this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
            this.googleMap.getUiSettings().setZoomControlsEnabled(true);
            this.googleMap.getUiSettings().setMapToolbarEnabled(false);
            this.googleMap.setBuildingsEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.map_popup_self, null);

                    ((RippleIconButton)view.findViewById(R.id.direction)).setImageIcon(
                            CommunityMaterial.Icon.cmd_directions,24, ContextCompat.getColor(getApplicationContext(),R.color.material_indigo_500)
                    );

                    ((RippleIconButton)view.findViewById(R.id.information)).setImageIcon(
                            CommunityMaterial.Icon.cmd_information,24,ContextCompat.getColor(getApplicationContext(),R.color.material_green_500)
                    );

                    ((RippleIconButton)view.findViewById(R.id.places)).setImageIcon(
                            CommunityMaterial.Icon.cmd_store,24,ContextCompat.getColor(getApplicationContext(),R.color.material_red_500)
                    );

                    ((RippleIconButton)view.findViewById(R.id.help)).setImageIcon(
                            CommunityMaterial.Icon.cmd_help,24,ContextCompat.getColor(getApplicationContext(),R.color.material_orange_500)
                    );

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        Address address = geocoder
                                .getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).get(0);


                        String finalAddress = "";
                        if (address != null) {
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                finalAddress += address.getAddressLine(i) + "\n";
                            }
                        }
                        ((AppCompatTextView) view.findViewById(R.id.selfAddress)).setText(finalAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return view;
                }
            });

            location();
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_customer_menu, menu);

        menu.findItem(R.id.grid).setIcon(new IconicsDrawable(getApplicationContext(), CommunityMaterial.Icon.cmd_apps).sizeDp(18).color(Color.WHITE));
        menu.findItem(R.id.nearbysaloon).setIcon(new IconicsDrawable(getApplicationContext(), CommunityMaterial.Icon.cmd_store).sizeDp(18).color(Color.WHITE));
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ToastUtils.showLong(connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.d("I am No 6");
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        currentLocation = location;

        if (markerAdded == true) {
            myMarker.setPosition(latLng);
         //   googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.getCameraPosition().zoom));
        } else {
            Ion.with(getApplicationContext())
                    .load(photoUrl)
                    .withBitmap()
                    .resize(65, 65)
                    .centerCrop()
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            LogUtils.d("I am No7");
                            markerAdded = true;
                            myMarker = googleMap.addMarker(
                                    new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(Base.getCircleBitmap(result)))
                                            .title("Hi " + displayName + " \n You are Here"));

                        }
                    });
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
