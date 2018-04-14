package omy.boston.googlemapsone;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final float ZOOM_LEVEL = 9.2f;
    private static final String ADRESA = "Trg bana Josipa Jelačića, Zagreb";
    private static LatLng ZADANA_LOKACIJA= new LatLng(23.134001,-82.358719);

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        configureMap();
        addMarker(ZADANA_LOKACIJA, "Lokacija");
        setAdapter();
        animateCamera(ZADANA_LOKACIJA);
        //ZADANA_LOKACIJA = getLatLngFromAddress(ADRESA);
        setListner();
    }

    private void configureMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setTrafficEnabled(false);
    }

    private void addMarker(LatLng latlng, String title) {
        mMap.addMarker(new MarkerOptions()
                .position(latlng).title(title));
    }

    private void animateCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
    }

    private void setAdapter() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View oblacic = getLayoutInflater().inflate(R.layout.info_window, null);
                ImageView ikona = (ImageView) oblacic.findViewById(R.id.imageView);
                TextView tekst = (TextView) oblacic.findViewById(R.id.tekst);
                ikona.setImageResource(R.drawable.ic_palms24);
                tekst.setText(getTitle());
                return oblacic;
            }
        });
    }

    /**DOZNAVANJE KOORDINATA**/
    private LatLng getLatLngFromAddress(String address) {
        LatLng latLng = ZADANA_LOKACIJA;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                latLng = new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    /**DOZNAVANJE ADRESE**/
    private String getAddressFromLatlng(LatLng latLng) {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    address += addresses.get(0).getAddressLine(i) + (char) 13 + (char) 10;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void setListner(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String address = getAddressFromLatlng(latLng);
                addMarker(latLng, address);
                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
