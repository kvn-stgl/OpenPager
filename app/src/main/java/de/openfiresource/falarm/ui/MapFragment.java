package de.openfiresource.falarm.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private static final float startZoomLevel = 15;

    private double mLat;
    private double mLng;

    public MapFragment() {

    }

    public static SupportMapFragment newInstance(double lat, double lng) {
        GoogleMapOptions options = new GoogleMapOptions();
        options.compassEnabled(true);
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);

        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("MapOptions", options);
        mapFragment.setArguments(bundle);
        mapFragment.getMapAsync(mapFragment);
        mapFragment.mLat = lat;
        mapFragment.mLng = lng;
        return mapFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        mMap.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setAllGesturesEnabled(true);

        LatLng place = new LatLng(mLat, mLng);
        mMap.addMarker(new MarkerOptions().position(place).title("Operation place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, startZoomLevel));
    }
}
