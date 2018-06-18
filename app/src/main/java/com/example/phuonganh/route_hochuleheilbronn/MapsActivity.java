package com.example.phuonganh.route_hochuleheilbronn;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, AdapterView.OnItemClickListener, SpinnerAdapter{

    private GoogleMap mMap;
    android.support.v7.widget.Toolbar toolbar;
    private Button route;
    // Key for Google maps Direction
    private String serverKey = "AIzaSyCnqL9swKWwtnkV38LHyvDY0pzxraSMZK0";
    LocationManager locationManager;
    String provider;
    LatLng endZiel;
    LatLng startZiel;
    // Select LatLng of Hochschule Heilbronn
    LatLng sonntheim = new LatLng(49.12259030846666, 9.210834503173828);
    LatLng campus = new LatLng(49.14840130906733,9.216477870941162) ;
    LatLng gebaudey = new LatLng(49.1226428, 9.206116500000007);
    LatLng gebaudex = new LatLng(49.122640492777336,9.206093533585317);
    // Sale the Info of Adress
    List<Address> standInfo;
    List<Address> sonntheimInfo;
    List<Address> campusInfo ;
    List<Address> gebaudeyInfo;
    List<Address> gebaudexInfo;

    // Create a String for Spinner

    String arr[]={
            "Hochschule Heilbronn",
            "Hochschule Heilbronn Campus am Europaplatz",
            "Hochschule Heilbronn Gebäude Y",
            "Hochschule Heilbronn Gebäude X"
            };
    TextView selection;

    // Function for button
    public void routeSonntheim (View view){
        route = findViewById(R.id.route_hs_sonntheim);
        route.setOnClickListener(this);
    }
    public void routeEuropaplatz (View view) {
        route = findViewById(R.id.route_hs_campus);
        route. setOnClickListener(this);
    }
    public void routeGebaudeX (View view) {
        route = findViewById(R.id.route_hs_gebaede_x);
        route. setOnClickListener(this);
    }
    public void routeGebaudeY (View view) {
        route = findViewById(R.id.route_hs_gebaede_y);
        route. setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.mytoolbar);
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/







        // Selection Spinner
        selection =(TextView) findViewById(R.id.selection);

        //Spinner element
        Spinner spinner =(Spinner) findViewById(R.id.route_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (
                        this,
                        android.R.layout.simple_spinner_item,
                        arr
                );
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        //Spinner click listener
         spinner.setAdapter(adapter);

         // Select Choice
        spinner.setOnItemSelectedListener(new MyProcessEvent());


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);

    }
   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    //Google Direction ApI
    /// //https://maps.googleapis.com/maps/api/directions/json?origin=49.13549166666667,9.193523333333333&destination=49.1301644,9.1987978&key=AIzaSyCnqL9swKWwtnkV38LHyvDY0pzxraSMZK0
    @Override
    public void onMapReady(final GoogleMap googleMap ) {
        mMap = googleMap;
        // Change map auf verschiedene MapType
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
        mMap.setMyLocationEnabled(true);


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
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);

        }


        // Getting URl to the Google Dicrections ApI
        onLocationChanged(location);
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        final LatLng startziel = new LatLng(lat, lng);
        startZiel = startziel;
    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        // Select Info of Adresse from Latlng

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            standInfo = geocoder.getFromLocation(lat, lng, 1);
            sonntheimInfo = geocoder.getFromLocationName(arr[0], 1);
            campusInfo = geocoder.getFromLocationName(arr[1],1);
            gebaudeyInfo = geocoder.getFromLocationName(arr[2],1);
            gebaudexInfo = geocoder.getFromLocationName(arr[3],1);
            Log.i("sonntheimInfo", sonntheimInfo.get(0).getAddressLine(0).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("Latitude", lat.toString());
        Log.i("Longtitude", lng.toString());

        if (mMap != null) {
            //mMap.clear(); // diese  Funtion sorgt dafür, dass alle Marker und alle Inhalte von Map gelöscht werden
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("My Standort").snippet(standInfo.get(0).getAddressLine(0).toString()).draggable(true).flat(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13));
            mMap.addMarker(new MarkerOptions().position(sonntheim).title(arr[0]).snippet(sonntheimInfo.get(0).getAddressLine(0).toString()));
            mMap.addMarker(new MarkerOptions().position(campus).title(arr[1]).snippet(campusInfo.get(0).getAddressLine(0).toString()));
            mMap.addMarker(new MarkerOptions().position(gebaudey).title(arr[2]).snippet(gebaudeyInfo.get(0).getAddressLine(0).toString()));
            mMap.addMarker(new MarkerOptions().position(gebaudex).title(arr[3]).snippet(gebaudexInfo.get(0).getAddressLine(0).toString()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("info", "keine setMyLocation");
                return;
            }




        }




    }

    @Override
    protected void onResume() {
        super.onResume();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("error", " permission was not granted");
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.route_hs_sonntheim) {
            endZiel = sonntheim;
            requesttDirection(endZiel);
        }
        if (id == R.id.route_hs_campus){
            endZiel = campus;
            requesttDirection(endZiel);
        }
        if (id == R.id.route_hs_gebaede_x){
            endZiel = gebaudex;
            requesttDirection(endZiel);
        }
        if (id == R.id.route_hs_gebaede_y){
            endZiel = gebaudey;
            requesttDirection(endZiel);
        }


    }

    private void requesttDirection(final LatLng endZiel) {
        GoogleDirection.withServerKey(serverKey)
                .from(startZiel)
                .to(endZiel)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                             @Override
                             public void onDirectionSuccess(Direction direction, String rawBody) {
                                 if (direction.isOK()) {
                                     //Do Something
                                     Route route = direction.getRouteList().get(0);
                                     mMap.addMarker(new MarkerOptions().position(startZiel));
                                     mMap.addMarker(new MarkerOptions().position(endZiel));
                                     ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                     Polyline polyline = mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED));
                                     //setCameraWithCoordinationBounds(route);
                                     mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startZiel, 11));
                                     Log.i("route", directionPositionList.toString());
                                 } else {
                                     Log.i("error", "ist not erfolgreich");
                                 }
                             }




                    @Override
                    public void onDirectionFailure(Throwable t) {
                        //Do Something
                    }
                });

    }



    /*private void setCameraWithCoordinationBounds(Route route) {

    }*/

    private class MyProcessEvent implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("textview", "es ist erfolg");
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selection.setText(arr[i]);
            if (i ==1){
                endZiel = sonntheim;
                requesttDirection(endZiel);
            }
            if (i ==2){
                endZiel = campus;
                requesttDirection(endZiel);
            }
            if (i ==3){
                endZiel = gebaudey;
                requesttDirection(endZiel);
            }
            if (i ==3){
                endZiel = gebaudex;
                requesttDirection(endZiel);
            }









            Log.i("textview", arr[i].toString());


        }


        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            selection.setText("");

        }
    }
}
