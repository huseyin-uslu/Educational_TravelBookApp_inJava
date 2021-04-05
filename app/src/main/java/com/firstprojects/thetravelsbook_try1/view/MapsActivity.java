package com.firstprojects.thetravelsbook_try1.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.firstprojects.thetravelsbook_try1.R;
import com.firstprojects.thetravelsbook_try1.models.AddressRegister;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    Boolean trackBoolean;
    Intent receive;
    SQLiteDatabase sqLiteDatabase;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = this.getSharedPreferences("com.firstprojects.thetravelsbook_try1.view",MODE_PRIVATE);
        trackBoolean = sharedPreferences.getBoolean("trackBoolean",false);
        sqLiteDatabase = this.openOrCreateDatabase("Address",MODE_PRIVATE,null);







    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(MapsActivity.this);

        receive = getIntent();

        String whichPage = receive.getStringExtra("whichPage");
        if (whichPage.matches("new")) {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    sharedPreferences = MapsActivity.this.getSharedPreferences("com.firstprojects.thetravelsbook_try1.view", MODE_PRIVATE);
                    trackBoolean = sharedPreferences.getBoolean("trackBoolean", false);

                    if (!trackBoolean) {
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
                        sharedPreferences.edit().putBoolean("trackBoolean", true).apply();
                    }
                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 16));
                }
            }

        } else {
            mMap.clear();
            AddressRegister addressRegister = (AddressRegister) receive.getSerializableExtra("addressSerial");
            Double latitude = addressRegister.getLatitude();
            Double longitude = addressRegister.getLongitude();
            LatLng latLng = new LatLng(latitude,longitude);

            String PlaceName = addressRegister.getPlaceName();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            mMap.addMarker(new MarkerOptions().title(PlaceName).position(latLng));


        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0 && ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            receive = getIntent();

            String whichPage = receive.getStringExtra("whichPage");
            if (whichPage.matches("new")) {
                locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 16));

                } else {
                    mMap.clear();
                    int idName = receive.getIntExtra("id", 1);
                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM address WHERE id = ?", new String[]{String.valueOf(idName)});
                    int nameIX = cursor.getColumnIndex("name");
                    int latitudeIX = cursor.getColumnIndex("latitude");
                    int longitudeIX = cursor.getColumnIndex("longitude");
                    while (cursor.moveToNext()) {
                        double lat = cursor.getDouble(latitudeIX);
                        double lng = cursor.getDouble(longitudeIX);
                        String name = cursor.getString(nameIX);
                        LatLng latLng = new LatLng(lat, lng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                    }
                    cursor.close();
                }
            }}}


    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent = new Intent(MapsActivity.this, RegisterScreen.class);
        Intent receive = getIntent();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String addressText = " ";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addressList.size() > 0 && addressList.get(0) != null && addressList != null) {
            if(addressList.get(0).getThoroughfare().length() > 0 ) {
                addressText += addressList.get(0).getThoroughfare();
            }
            if(addressList.get(0).getSubThoroughfare().length() > 0) {
            addressText += addressList.get(0).getSubThoroughfare();
            }
            }else {
                addressText = "The Address Unknown";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(addressText));
        Double lat = latLng.latitude;
        Double lng = latLng.longitude;

        AddressRegister addressRegister = new AddressRegister(lat,lng,addressText);

        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setCancelable(false);
        alert.setTitle("Are you sure?");
        alert.setMessage(addressText + " " + " ' is saved?");
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS address(id INTEGER PRIMARY KEY , name VANCHAR, latitude REAL, longitude REAL)");
                    String StringOfValues = "INSERT INTO address(name,latitude,longitude) VALUES(?,?,?)";
                    SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(StringOfValues);
                    sqLiteStatement.bindString(1,addressRegister.getPlaceName());
                    sqLiteStatement.bindDouble(2,addressRegister.getLatitude());
                    sqLiteStatement.bindDouble(3,addressRegister.getLongitude());
                    sqLiteStatement.execute();

                    Toast.makeText(MapsActivity.this, "SAVING IS SUCCESFUL", Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "isn't Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();



    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        Intent intent = new Intent(MapsActivity.this,MainMenu.class);
        startActivity(intent);
    }
}


