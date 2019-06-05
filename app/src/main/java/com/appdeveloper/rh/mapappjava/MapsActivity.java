package com.appdeveloper.rh.mapappjava;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    float zoom = 15;
    Marker currentMarker;
    Polygon polygon;
    EditText locSearch;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locSearch = findViewById(R.id.editText);
        searchBtn = findViewById(R.id.button);

        locSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        // Add a marker in Jibestream and move the camera 43.6543582,-79.4262946
        LatLng latLng = new LatLng(43.6543582, -79.4262946);
        currentMarker = showMarker("Jibestream", latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void SearchMapBtn(View view) {

        String location = locSearch.getText().toString().trim();
        List<Address> addressList = null;

        InputMethodManager imm = (InputMethodManager) getSystemService(MapsActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locSearch.getWindowToken(), 0);

        try {
            if (!location.isEmpty()) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                if (currentMarker != null) {
                    currentMarker.remove();
                    polygon.remove();
                }
                currentMarker = showMarker(location, latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Search!", Toast.LENGTH_LONG).show();
        }
    }

    public Marker showMarker(String location, LatLng latLng) {
        polygon = showPolygon(latLng);
        return mMap.addMarker(new MarkerOptions().position(latLng).title(location)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
    }

    public Polygon showPolygon(LatLng latLng){
        // Instantiates a new Polygon object and adds points to define a star
        PolygonOptions starOptions = new PolygonOptions()
                .add(new LatLng(latLng.latitude + 0.001, latLng.longitude - 0.003),
                        new LatLng(latLng.latitude + 0.001, latLng.longitude + 0.003),
                        new LatLng(latLng.latitude - 0.002, latLng.longitude - 0.002),
                        new LatLng(latLng.latitude + 0.0025, latLng.longitude - 0.000),
                        new LatLng(latLng.latitude - 0.002, latLng.longitude + 0.002),
                        new LatLng(latLng.latitude + 0.001, latLng.longitude - 0.003))
                .strokeColor(Color.YELLOW);

        return mMap.addPolygon(starOptions);
    }
}
