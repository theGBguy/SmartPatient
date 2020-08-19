package com.gbsoft.smartpillreminder.ui.nearbyplaces;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentNearbyBinding;
import com.gbsoft.smartpillreminder.ui.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class NearbyFragment extends Fragment implements OnMapReadyCallback,
        MapboxMap.OnMapClickListener, PermissionsListener, MapboxMap.OnMoveListener {
    private FragmentNearbyBinding binding;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private static final String HOSPITAL_INDICATOR = "HOSPITAL_INDICATOR";
    private static final String HOSPITALS_GEOJSON_SOURCE_ID = "HOSPITALS_GEOJSON_SOURCE_ID";
    private static final String HOSPITALS_LAYER_ID = "HOSPITALS_LAYER_ID";

    @SuppressWarnings("MissingPermission")
    private void displayDeviceLocation(@NonNull Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            LocationComponentOptions options = LocationComponentOptions.builder(requireContext())
                    .pulseEnabled(true)
                    .pulseColor(getResources().getColor(R.color.colorPrimary900))
                    .pulseAlpha(.4f)
                    .pulseInterpolator(new BounceInterpolator())
                    .build();

            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(requireContext(),
                    style).locationComponentOptions(options).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            //code to show current position and hospitals/pharmacies nearby
            Location lastKnownLocation = locationComponent.getLastKnownLocation();
            if (lastKnownLocation != null) {
                LatLng currentPoint = new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude());
                if (!displayNearestHospitalsAndPharmacies(currentPoint)) {
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(currentPoint.getLatitude(), currentPoint.getLongitude()))
                            .zoom(17).build()
                    ), 3500);
                }
            }

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(requireActivity());
        }
    }

    private boolean displayNearestHospitalsAndPharmacies(LatLng point) {
        AtomicBoolean isNearestShown = new AtomicBoolean(false);
        MapboxTilequery mapboxTilequery = MapboxTilequery.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .tilesetIds("mapbox.mapbox-streets-v8")
                .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                .radius(7000)
                .limit(50)
                .geometry("point")
                .dedupe(true)
                .layers("poi_label")
                .build();
        mapboxTilequery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(@NonNull Call<FeatureCollection> call, @NonNull Response<FeatureCollection> response) {
                if (response.body() != null) {
                    FeatureCollection featureCollection = response.body();
                    mapboxMap.getStyle(style -> {
                        GeoJsonSource resultSrc = style.getSourceAs(HOSPITALS_GEOJSON_SOURCE_ID);
                        if (resultSrc != null) {
                            List<Feature> featureList = featureCollection.features();
                            List<Feature> filteredFeatures = new ArrayList<>();
                            Feature nearest = null;
                            BigDecimal nearestDist = null, currentDist;
                            if (featureList != null && !featureList.isEmpty()) {
                                for (Feature feature : featureList) {
                                    if (feature.hasProperty("type")) {
                                        String type = feature.getStringProperty("type");
                                        if (type.equals("Hospital") || type.equals("Pharmacy")) {
                                            filteredFeatures.add(feature);
                                            if (nearest == null) {
                                                nearest = feature;
                                                nearestDist = nearest.getProperty("tilequery").getAsJsonObject()
                                                        .get("distance").getAsBigDecimal();
                                            } else {
                                                currentDist = feature.getProperty("tilequery").getAsJsonObject()
                                                        .get("distance").getAsBigDecimal();
                                                if (currentDist.compareTo(nearestDist) < 0) {
                                                    nearest = feature;
                                                    nearestDist = currentDist;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (filteredFeatures.isEmpty())
                                    Snackbar.make(requireView(), "No medical places nearby", Snackbar.LENGTH_LONG).show();
                                else {
                                    resultSrc.setGeoJson(FeatureCollection.fromFeatures(filteredFeatures));
                                    Timber.d("Successfully fetched the data");
                                    isNearestShown.set(true);
                                    if (nearest != null && nearest.geometry() != null) {
                                        if (nearest.geometry() instanceof Point) {
                                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                    .target(new LatLng(((Point) nearest.geometry()).latitude(),
                                                            ((Point) nearest.geometry()).longitude()))
                                                    .zoom(19)
                                                    .build()
                                            ), 3500);
                                        }
                                    }
                                }
                            } else
                                Snackbar.make(requireView(), "No medical places nearby", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeatureCollection> call, @NonNull Throwable t) {
                Timber.d("API failure");
                Snackbar.make(requireView(), "API Failure!", Snackbar.LENGTH_LONG).show();
            }
        });
        return isNearestShown.get();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNearbyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.nearbyToolbar);
        NavigationUI.setupWithNavController(binding.nearbyToolbar, Navigation.findNavController(requireView()),
                ((MainActivity) requireActivity()).getAppBarConfig());

        mapView = binding.map;
        mapView.onCreate(savedInstanceState);

        if (mapView != null) {
            mapView.getMapAsync(this);
        }

        binding.nearbyFab.setOnClickListener((v) -> {
            Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            if (lastKnownLocation != null) {
                LatLng currentPoint = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                displayNearestHospitalsAndPharmacies(currentPoint);
            }
        });
        // see if location is enabled in the user's settings or not
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!(LocationManagerCompat.isLocationEnabled(locationManager))) {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

            builder.setTitle("Location disabled!");
            builder.setMessage("Location needs to be enabled for this feature to work.\n" +
                    "Do you want to open Settings to enable the location access?");
            builder.setIcon(R.drawable.report_problem);
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
                Navigation.findNavController(requireView()).navigateUp();
            });
            builder.setPositiveButton("Okay", (dialogInterface, i) -> {
                Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(locationIntent);
            });
            builder.show();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        displayNearestHospitalsAndPharmacies(point);
        return true;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, (style) -> {
            addHospitalLayer(style);
            displayDeviceLocation(style);
            mapboxMap.addOnMapClickListener(this);
        });
        mapboxMap.addOnMoveListener(NearbyFragment.this);
    }

    private void addHospitalLayer(Style style) {
        style.addImage(HOSPITAL_INDICATOR, BitmapFactory.decodeResource(getResources(),
                R.drawable.mapbox_marker_icon_default));
        style.addSource(new GeoJsonSource(HOSPITALS_GEOJSON_SOURCE_ID));
        style.addLayer(new SymbolLayer(HOSPITALS_LAYER_ID, HOSPITALS_GEOJSON_SOURCE_ID).withProperties(
                iconImage(HOSPITAL_INDICATOR),
                iconOffset(new Float[]{0f, -12f}),
                iconIgnorePlacement(true),
                iconAllowOverlap(true)
        ));
    }

    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector detector) {
        binding.nearbyFab.shrink();
    }

    @Override
    public void onMove(@NonNull MoveGestureDetector detector) {

    }

    @Override
    public void onMoveEnd(@NonNull MoveGestureDetector detector) {
        binding.nearbyFab.extend();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        binding = null;
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Snackbar.make(requireView(), "Your permission to required to get your current location.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::displayDeviceLocation);
        } else {
            Snackbar.make(requireView(), "Location permission is not granted so we cannot show" +
                    "nearby health places now", Snackbar.LENGTH_LONG).show();
        }
    }
}