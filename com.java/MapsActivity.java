package com.syrinxsoft.riocarioca;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener
{
    //To implement My Position
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation; //In use
    private Marker mCurrLocationMarker; //In use
    private LocationRequest mLocationRequest;

    //Get DB in service and get back here
    private SQLiteDatabase[] fromService;
    private Cursor[] cursorFomService;
    private String sqlFromService;

    private int returnLength;

    //Define how many places will be placed
    private Marker[] markers;

    //Variables to storage DB data in memory
    private LatLng[] latLngList;
    private String[] lattest;
    private String[] lngtest;
    private String[] nametest;
    private String[] addresstest;
    private double latt[];
    private double lngg[];
    private TextView[] nameTv;
    private TextView[] addressTv;
    private TextView[] numberTv;
    private TextView[] availableTv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Create a back button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Receiving length from Service
        ReceivingLength();

        //Useful In Future...
        /*if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {}
            else if (extras.getBoolean("Notify")) { Notify(); }
        }*/
    }

    //Make this class to close by click on back button
    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
    @Override
    public void onBackPressed() { super.onBackPressed(); finish(); }

    //Map loaded completely
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //Finish to load places!!!
        LoadGoogleMapsPlaces();

        //WORKS!!! To use after...
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList[i], 7));
        //LatLng sydney = new LatLng(-22.904641, -43.288346);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Test Marker")
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.position)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //WORKS!!!

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            // Generate window
            @Override
            public View getInfoWindow(Marker marker)
            {
                View window = null;
                try
                {
                    window = getLayoutInflater().inflate(R.layout.info_window, null);

                    for (int i = 0; i < returnLength; i++)
                    {
                        CreatePlacesDB(i);
                        ComparePlacesDB(i);

                        if (marker.equals(markers[i]))
                        {
                            nameTv[i] = window.findViewById(R.id.txtname);
                            nameTv[i].setText(cursorFomService[i].getString
                                    (cursorFomService[i].getColumnIndex("name")));

                            addressTv[i] = window.findViewById(R.id.txtaddress);
                            addressTv[i].setText(cursorFomService[i].getString
                                    (cursorFomService[i].getColumnIndex("address")));

                            numberTv[i] = window.findViewById(R.id.txtnumber);
                            numberTv[i].setText(cursorFomService[i].getString
                                    (cursorFomService[i].getColumnIndex("number")));

                            availableTv[i] = window.findViewById(R.id.txtavailable);
                            availableTv[i].setText("Cervejas: "+cursorFomService[i].getString
                                    (cursorFomService[i].getColumnIndex("available")));
                        }
                    }
                }
                catch (Exception ev) { System.out.print(ev.getMessage()); }

                return window;
            }
            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) { return null; }
        });

        //Trigger for when marker were clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            public boolean onMarkerClick(Marker marker)
            {
            if (!marker.isInfoWindowShown())
                marker.showInfoWindow();

            //This code block make marker to be positioning below center
            double center = mMap.getCameraPosition().target.latitude;
            double southMap = mMap.getProjection().getVisibleRegion().latLngBounds.southwest.latitude;
            double diff = (center - southMap);
            double newLat = marker.getPosition().latitude + diff / 1.5;

            CameraUpdate centerCam = CameraUpdateFactory.newLatLng
                    (new LatLng(newLat, marker.getPosition().longitude));

            mMap.animateCamera(centerCam, 500, null);

            return true;
            }
        });

        //Detect if was clicked in some balloon
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
              @Override
              public void onInfoWindowClick(Marker marker)
              {
          for (int i = 0; i < returnLength; i++)
          {
              CreatePlacesDB(i);
              ComparePlacesDB(i);

              if (marker.equals(markers[i]))
              {
                  final String number = cursorFomService[i].getString
                          (cursorFomService[i].getColumnIndex("number"));

                  AlertDialog.Builder builder = new AlertDialog.Builder
                          (MapsActivity.this, R.style.MyAlertDialogStyle);
                  builder.setIcon(R.drawable.common_full_open_on_phone);
                  builder.setTitle(cursorFomService[i].getString(cursorFomService[i].getColumnIndex("name")));
                  builder.setMessage("Ligar para "+number+"?");
                  builder.setPositiveButton("Ligar", new DialogInterface.OnClickListener()
                  {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i)
                      {
                      Uri uri = Uri.parse("tel:"+number);
                      Intent intent = new Intent(Intent.ACTION_CALL, uri);

                      if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                              Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){}
                      else
                      {
                          startActivity(intent);
                      }
                      }
                  });
                  builder.setNegativeButton("Cancelar", null);
                  builder.setCancelable(false);
                  builder.show();
              }
          }
              }
        });

        //After user click in Notification this Class will be called and inform a new place
        Bundle extras = getIntent().getExtras();
        if(extras == null) {}
        else if (extras.getBoolean("Notify")) { Notify(); }
    }

    //Show a window with a new place
    protected void Notify()
    {
        new AlertDialog.Builder(this).setTitle("Novo ponto de venda!")
        .setMessage(nametest[nametest.length - 1] + "\n\n" + addresstest[addresstest.length - 1])
        .setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        })
        .setIcon(R.drawable.iconin)
        .setCancelable(false)
        .show();
    }

    //Receive length from service
    private boolean ReceivingLength()
    {
        SQLiteDatabase receiveLength = openOrCreateDatabase("c_db", MODE_PRIVATE, null);
        receiveLength.execSQL("CREATE TABLE IF NOT EXISTS c_table (compare);");

        Cursor cursorLength = receiveLength.query
            ("c_table", new String[]{"compare"},
            null, null, null, null, null);

        if (cursorLength.getCount() != 0)
        {
            cursorLength.moveToLast();

            String returnValue = cursorLength.getString(cursorLength.getColumnIndex("compare"));
            returnLength = Integer.parseInt(returnValue);

            fromService = new SQLiteDatabase[returnLength];
            cursorFomService = new Cursor[returnLength];

            markers = new Marker[returnLength];
            latLngList = new LatLng[returnLength];

            lattest = new String[returnLength];
            lngtest = new String[returnLength];
            latt = new double[returnLength];
            lngg = new double[returnLength];
            nameTv = new TextView[returnLength];
            addressTv = new TextView[returnLength];
            numberTv = new TextView[returnLength];
            availableTv = new TextView[returnLength];
            nametest = new String[returnLength];
            addresstest = new String[returnLength];

            return true;
        }
        else { return false; }
    }

    //Load places from Service
    private void CreatePlacesDB(int index)
    {
        for (int i = index; i < returnLength; i++)
        {
            fromService[i] = openOrCreateDatabase("places" + i, MODE_PRIVATE, null);
            sqlFromService = "CREATE TABLE IF NOT EXISTS p (lat, lng, name, address, number, available);";
            fromService[i].execSQL(sqlFromService);
        }
    }

    //Open cursor to send to GoogleMapsPlaces
    private boolean ComparePlacesDB(int index)
    {
        for (int i = index; i < returnLength; i++)
        {
            cursorFomService[i] = fromService[i].query("p",
                new String[]{"lat, lng, name, address, number, available"},
                    null, null, null, null, null);

            if (cursorFomService[i].getCount() != 0)
            {
                cursorFomService[i].moveToLast();
                return true;
            }
            else
                return false;
        }
        return false;
    }

    //Load all places saved in DB
    private void LoadGoogleMapsPlaces()
    {
        for (int i = 0; i < returnLength; i++)
        {
            CreatePlacesDB(i);
            ComparePlacesDB(i);

            lattest[i] = cursorFomService[i].getString(cursorFomService[i].getColumnIndex("lat"));
            lngtest[i] = cursorFomService[i].getString(cursorFomService[i].getColumnIndex("lng"));
            nametest[i] = cursorFomService[i].getString(cursorFomService[i].getColumnIndex("name"));
            addresstest[i] = cursorFomService[i].getString(cursorFomService[i].getColumnIndex("address"));
            latt[i] = Double.parseDouble(lattest[i]);
            lngg[i] = Double.parseDouble(lngtest[i]);
            latLngList[i] = new LatLng(latt[i], lngg[i]);

            markers[i] = mMap.addMarker(new MarkerOptions().position(latLngList[i])//.title("Cervejaria Joäo")
                //.snippet("Cerveja Artesanal Tipo " + i)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.position)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList[i], 7));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngList[i]));
        }
    }

    //LOGIC FOR SHOW CURRENT POSITION================================================
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle)
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

        if (mCurrLocationMarker != null)
            mCurrLocationMarker.remove();

        //Place current location marker KEEP!!!
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions(); KEEP!!!
        //markerOptions.position(latLng); KEEP!!!
        //markerOptions.title("Current Position"); KEEP!!!
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)); KEEP!!!
        //mCurrLocationMarker = mMap.addMarker(markerOptions); KEEP!!!

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        else
            return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                    {
                        if (mGoogleApiClient == null)
                            buildGoogleApiClient();

                        mMap.setMyLocationEnabled(true);
                    }
                }

                return;
            }
        }
    }
    //LOGIC FOR SHOW CURRENT POSITION================================================
}