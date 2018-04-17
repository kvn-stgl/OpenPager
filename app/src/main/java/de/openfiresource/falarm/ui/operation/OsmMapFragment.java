package de.openfiresource.falarm.ui.operation;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Pair;
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

import javax.inject.Inject;

import de.openfiresource.falarm.BuildConfig;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.dagger.Injectable;

public class OsmMapFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    MapView mapView;

    private static final double startZoomLevel = 17.0;

    public OsmMapFragment() {
        // Required empty public constructor
    }

    public static OsmMapFragment newInstance() {
        OsmMapFragment mapFragment = new OsmMapFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osm, container, false);
        OperationViewModel viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(OperationViewModel.class);

        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMaxZoomLevel(17.0);

        // create OpenFireMap tile provider
        MapTileProviderBasic ofmTileProvider = new MapTileProviderBasic(requireContext());
        ITileSource ofmTileSource = new XYTileSource("OpenFireMap", 10, 17, 256, ".png",
                new String[]{"http://openfiremap.org/hytiles/"});
        ofmTileProvider.setTileSource(ofmTileSource);

        TilesOverlay ofmTilesOverlay = new TilesOverlay(ofmTileProvider, requireContext());
        ofmTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
        mapView.getOverlays().add(ofmTilesOverlay);

        viewModel.getOperation().observe(this, operationMessage -> {
            if (operationMessage == null) {
                return;
            }

            Pair<Double, Double> latlng = operationMessage.getLatLngPair();
            if (latlng == null) {
                return;
            }

            // alarm marker
            GeoPoint startPoint = new GeoPoint(latlng.first, latlng.second);
            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(startPoint);
            startMarker.setTitle(getString(R.string.operation_base));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(startMarker);

            IMapController mapController = mapView.getController();
            mapController.setZoom(startZoomLevel);
            mapController.setCenter(startPoint);
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
}
