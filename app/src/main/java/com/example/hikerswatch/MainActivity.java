package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    Location addressLocation;
    TextView addTextView;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                startListening();
            }
        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);

            Location last_known_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(last_known_location!=null){
                updateLocationInfo(last_known_location);
            }


        }
    }

    public void updateLocationInfo(Location location){
        TextView latTextView = findViewById(R.id.latTextView);
        TextView longTextView = findViewById(R.id.longTextView);
        TextView accTextView = findViewById(R.id.accTextView);
        TextView altTextView = findViewById(R.id.altTextView);


        latTextView.setText("Latitude:" + location.getLatitude());
        longTextView.setText("Longitude:" + location.getLongitude());
        accTextView.setText("Accuracy:" + location.getAccuracy());
        altTextView.setText("Altitude:" + location.getAltitude());
        addressLocation=location;
        AddressFind addressFind = new AddressFind();
        addressFind.execute("blank");




    }

    private class AddressFind extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                System.out.println("list address start");
                List<Address>  addressList = geocoder.getFromLocation(addressLocation.getLatitude(), addressLocation.getLongitude(),1);
                System.out.println("list address end");
                String address = "could not find address";
                if(addressList.size()>0 && addressList!=null){
                    address= "Address: \n";
                    if (addressList.get(0).getSubThoroughfare()!=null){
                        address+=addressList.get(0).getSubThoroughfare()+"\n";

                    }
                    if (addressList.get(0).getThoroughfare()!=null){
                        address+=addressList.get(0).getThoroughfare()+"\n";
                    }


                    if (addressList.get(0).getLocality()!=null){
                        address+=addressList.get(0).getLocality()+"\n";
                    }
                    if (addressList.get(0).getPostalCode()!=null){
                        address+=addressList.get(0).getPostalCode()+"\n";
                    }
                    if (addressList.get(0).getCountryName()!=null){
                        address+=addressList.get(0).getCountryName()+"\n";
                    }

                }
                addTextView.setText(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTextView = findViewById(R.id.addTextView);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
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
        };


            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);

                Location last_known_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(last_known_location!=null){
                    System.out.println(last_known_location);
                    updateLocationInfo(last_known_location);
                }
            }

    }
}
