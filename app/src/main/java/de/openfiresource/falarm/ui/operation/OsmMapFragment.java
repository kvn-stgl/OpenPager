package de.openfiresource.falarm.ui.operation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.openfiresource.falarm.BuildConfig;
import de.openfiresource.falarm.R;

public class OsmMapFragment extends Fragment {

    private static final int startZoomLevel = 17;

    private Unbinder mUnbinder;
    private double mLat;
    private double mLng;

    @BindView(R.id.map)
    MapView mapView;

    public OsmMapFragment() {
        // Required empty public constructor
    }

    public static OsmMapFragment newInstance(double lat, double lng) {
        OsmMapFragment mapFragment = new OsmMapFragment();
        mapFragment.mLat = lat;
        mapFragment.mLng = lng;
        mapFragment.setRetainInstance(true);
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_osm, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        GeoPoint startPoint = new GeoPoint(mLat, mLng);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMaxZoomLevel(17.0);

        //create the second one
        final MapTileProviderBasic ofmTileProvider = new MapTileProviderBasic(requireContext());
        final ITileSource ofmTileSource = new XYTileSource("OpenFireMap", 10, 17, 256, ".png",
                new String[]{"http://openfiremap.org/hytiles/"});
        ofmTileProvider.setTileSource(ofmTileSource);
        final TilesOverlay ofmTilesOverlay = new TilesOverlay(ofmTileProvider, requireContext());
        ofmTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
        mapView.getOverlays().add(ofmTilesOverlay);

        //Alarm Marker
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setTitle(getString(R.string.operation_base));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);

        IMapController mapController = mapView.getController();
        mapController.setZoom(startZoomLevel);
        mapController.setCenter(startPoint);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null)
            mUnbinder.unbind();
    }

}
