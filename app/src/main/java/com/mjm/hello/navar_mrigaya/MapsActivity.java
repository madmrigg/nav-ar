package com.mjm.hello.navar_mrigaya;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.makeText;

//import com.google.android.libraries.places.compat.Place;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, TaskLoadedCallback {

    public GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public MarkerOptions source;
    public MarkerOptions dest;
    private static final int requestUserLocationCode = 99;
    private Polyline polyline;
    private static final String TAG = "MapsActivity";
    public String transPref="driving";
    public int flagCheck=0;
    public int dynaRoute=0;
    public Button centreB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        overridePendingTransition(R.anim.fadeinfast, R.anim.fadeinfast);

        centreB=findViewById(R.id.zonein);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initAutoComplete();


    }


    public void checkDrive(View view){

        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        //mCheckBox.setChecked(true); //to check
        mCheckBox.setChecked(false); //to uncheck
        dynaRoute=0;

        if(flagCheck==1){
            mMap.clear();
            //mMap.addMarker(source);
            mMap.addMarker(dest);
            transPref="driving";
        Toast toast = makeText(this, "Path preference set to DRIVING", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
            callUrl();
        }
        else {
            mMap.clear();
            //mMap.addMarker(source);
            //mMap.addMarker(dest);
            transPref="driving";
            Toast toast = makeText(this, "Path preference set to DRIVING", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    public void checkWalk(View view){

        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        //mCheckBox.setChecked(true); //to check
        mCheckBox.setChecked(false); //to uncheck
        dynaRoute=0;

        if(flagCheck==1){
            mMap.clear();
            //mMap.addMarker(source);
            mMap.addMarker(dest);
            transPref="walking";
            Toast toast = makeText(this, "Path preference set to WALKING", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            callUrl();
        }
        else {
            mMap.clear();
            //mMap.addMarker(source);
            //mMap.addMarker(dest);
            transPref="walking";
            Toast toast = makeText(this, "Path preference set to WALKING", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    public void checkCycle(View view) {

        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        //mCheckBox.setChecked(true); //to check
        mCheckBox.setChecked(false); //to uncheck
        dynaRoute=0;

        if (flagCheck == 1) {
            mMap.clear();
            //mMap.addMarker(source);
            mMap.addMarker(dest);
            transPref="cycling";
            Toast toast = makeText(this, "Path preference set to CYCLE", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            callUrl();
        } else {
            mMap.clear();
            //mMap.addMarker(source);
            //mMap.addMarker(dest);
            transPref="cycling";
            Toast toast = makeText(this, "Path preference set to CYCLE", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    public void checkBike(View view) {

        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        //mCheckBox.setChecked(true); //to check
        mCheckBox.setChecked(false); //to uncheck
        dynaRoute=0;

        if (flagCheck == 1) {
            mMap.clear();
            //mMap.addMarker(source);
            mMap.addMarker(dest);
            transPref="motorcycle";
            Toast toast = makeText(this, "Path preference set to BIKE", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            callUrl();
        } else {
            mMap.clear();
            //mMap.addMarker(source);
            //mMap.addMarker(dest);
            transPref="motorcycle";
            Toast toast = makeText(this, "Path preference set to BIKE", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    public void dynaRoute(View v){
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            dynaRoute=1;
        }
        else{
            dynaRoute=0;
        }
    }

    public void NavAR(View view) {
        Toast toast = makeText(this, "Going AR...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        startActivity(new Intent(MapsActivity.this, NavAR.class));
    }

    public void goBack(View view) {
        flagCheck=0;
        overridePendingTransition(R.anim.fadeinfast, R.anim.fadeinfast);
        finish();
    }


    public void centre(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(14)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        // "marker" refers to the Marker object you wish to center on
        //LatLng latLng = source.getPosition(); // returns LatLng object
        //mMap.setCenter(latLng); // setCenter takes a LatLng object
        //mMap.panTo(source.getPosition());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

    }



    public void clearMap(View view){
        flagCheck=0;
        mMap.clear();
       // mMap.addMarker(source);
        //mMap.setMyLocation(true);
        // onLocationChanged(Location);
    }

    public void initAutoComplete(){

        String apiKey = "AIzaSyDngIsShLq4XcQtZpKw1Vz7AYenfYyEIl4";
        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        //autocompleteFragment.setTypeFilter(TypeFilter.REGIONS);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {

                makeText(MapsActivity.this, ""+ place.getName(), Toast.LENGTH_LONG).show();;

                String searchLocation = place.getName();
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                if(!searchLocation.equals("")) {

                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {

                        addressList = geocoder.getFromLocationName(searchLocation, 1);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    for (int i = 0; i < addressList.size(); i++) {

                        Address myAddress = addressList.get(i);
                        LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                        dest = new MarkerOptions().position(latLng);
                        markerOptions.position(latLng);
                        markerOptions.title("Result");
                        //mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                        Toast.makeText(MapsActivity.this, "" + latLng, Toast.LENGTH_LONG).show();
                    }
                    mMap.clear();
                    //mMap.addMarker(source);
                    mMap.addMarker(dest);
                    flagCheck=1;
                    makeText(MapsActivity.this, "" + "Destination:" + dest.getPosition(), Toast.LENGTH_SHORT).show();
                    //centreB.performClick();
                }

                callUrl();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /** Add a marker in current location and move the camera*/
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            //mMap.resetMinMaxZoomPreference();
        }*/

        //if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        //} else {
        //    buildGoogleApiClient();
        //    mMap.setMyLocationEnabled(true);
        }
   // }

    public boolean checkUserLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestUserLocationCode);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestUserLocationCode);
            }

            return false;
        } else {

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case requestUserLocationCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {

                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient(){

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    //public Location location;
    //marks the current location
    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if(currentLocationMarker != null){

            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        source = new MarkerOptions().position(latLng);
       // mMap.addMarker(source);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //currentLocationMarker = mMap.addMarker(markerOptions);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        //updateCameraBearing(location.getBearing());
        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        centreB.performClick();
        makeText(this, "Current: "+ source.getPosition(), Toast.LENGTH_LONG).show();
        //callUrl();
    }

    private void updateCameraBearing(float bearing) {
        if ( mMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        mMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    public void onClick(View v){

        EditText TFLocation = findViewById(R.id.TFLocation);
        String searchLocation = TFLocation.getText().toString();

        if(searchLocation.equals("")){
            mMap.clear();
            flagCheck=0;
            //mMap.addMarker(source);
            makeText(getApplicationContext(), "NullPointerException Encountered!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mMap.clear();
        flagCheck=1;
       // mMap.addMarker(source);

        if(v.getId() == R.id.B_search){

            //EditText TFLocation = findViewById(R.id.TFLocation);
            //String searchLocation = TFLocation.getText().toString();
            List<Address> addressList = null;
            MarkerOptions markerOptions = new MarkerOptions();
            if(!searchLocation.equals("")) {

                Geocoder geocoder = new Geocoder(this);
                try {

                    addressList = geocoder.getFromLocationName(searchLocation, 1);
                } catch (IOException e) {

                    e.printStackTrace();

                }

                for (int i = 0; i < addressList.size(); i++) {

                    Address myAddress = addressList.get(i);
                    LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                    dest = new MarkerOptions().position(latLng);
                    markerOptions.position(latLng);
                    markerOptions.title("Result");
                    //mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    makeText(this, "" + latLng, Toast.LENGTH_LONG).show();
                }
                mMap.addMarker(dest);
                makeText(this, "" + "Destination:" + dest.getPosition(), Toast.LENGTH_SHORT).show();
            }
            flagCheck=1;
            callUrl();
        }

        //Direct PolyLine from Source to Destination when drawing via FIND ROUTE
        /*Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(source.getPosition(), dest.getPosition())
                .width(2)
                .color(Color.BLUE));*/
    }


    public void callUrl() {
        String url = getUrl(source.getPosition(), dest.getPosition(),transPref); //"driving");
        new FetchURL(MapsActivity.this).execute(url,transPref);//"driving");
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {

        Toast.makeText(this, "Inside get URL", Toast.LENGTH_SHORT).show();
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key_string);
    }
// "&avoid=highways" + //before keys parameter in url
//String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&avoid=highways" + "&key=" + getString(R.string.google_maps_key);
        //return url; //directionMode


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(250);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onTaskDone(Object... values) {

        if(polyline != null)
            polyline.remove();
        polyline = mMap.addPolyline((PolylineOptions)values[0]);

        //if(dynaRoute==1) {
        //    updatePolyLine();
        //}

    }


    public void updatePolyLine(Object... values){
        //onLocationChanged(Location get);
        callUrl();
        if(polyline != null)
            polyline.remove();
        polyline = mMap.addPolyline((PolylineOptions)values[0]);

    }



    @Override
    public void onConnectionSuspended(int i) {
        finish();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        finish();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press 'BACK' again to exit MAPS...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
