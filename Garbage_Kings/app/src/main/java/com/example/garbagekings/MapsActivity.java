package com.example.garbagekings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garbagekings.Modules.PlacesAutoCompleteAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.example.garbagekings.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MapsActivity extends Fragment implements OnMapReadyCallback, PlacesAutoCompleteAdapter.ClickListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private MapView mapView;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition cameraPosition;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private EditText locationSearch;
    private ImageButton searchButton;
    private ImageButton orderButton;
    private Marker currentMarker;
    private View mapElement;
    Boolean markerClick = false;




    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_maps, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Places.initialize(getActivity(), getResources().getString(R.string.google_maps_key));
        recyclerView = (RecyclerView) v.findViewById(R.id.places_recycler_view);
        ((EditText) v.findViewById(R.id.editText)).addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationSearch = (EditText) view.findViewById(R.id.editText);
        mAutoCompleteAdapter.setEditText(locationSearch);
        searchButton = (ImageButton) view.findViewById(R.id.search_button);
        orderButton= (ImageButton) view.findViewById(R.id.orderButton);
        orderButton.setVisibility(View.GONE);
        mapElement = view.findViewById(R.id.mapElement);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    String thoroughfare = null;
                    String subThoroughfare = null;
                    if (address.getThoroughfare() == null)
                    {
                        thoroughfare = "";
                    }
                    else
                    {
                        thoroughfare = address.getThoroughfare();
                    }
                    if (address.getSubThoroughfare() == null)
                    {
                        subThoroughfare = "";
                    }
                    else
                    {
                        subThoroughfare = ", " + address.getSubThoroughfare();
                    }
                    if (thoroughfare.equals("") && subThoroughfare.equals(""))
                    {
                        markerOptions.title(address.getLocality());
                    }
                    else {
                        markerOptions.title(thoroughfare + subThoroughfare);
                    }
                }

                // Clears the previously touched position
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                markerOptions.draggable(true);
                currentMarker = googleMap.addMarker(markerOptions);
                orderButton.setVisibility(View.VISIBLE);
                markerClick = false;
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
                }
                catch (NullPointerException ignored)
                {
                    imm.hideSoftInputFromWindow(locationSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                locationSearch.clearFocus();
                recyclerView.setVisibility(View.GONE);
            }


        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addressList = geocoder.getFromLocation(currentMarker.getPosition().latitude,
                            currentMarker.getPosition().longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    String postalCode = (address.getPostalCode() != null)? address.getPostalCode() : "";
                    String countryName = (address.getCountryName() != null)? ", " + address.getCountryName(): "";
                    String locality = (address.getLocality() != null)? ", " + address.getLocality(): "";
                    String thoroughfare = (address.getThoroughfare() != null)? ", " + address.getThoroughfare(): "";
                    String subThoroughfare = (address.getSubThoroughfare() != null)? ", " + address.getSubThoroughfare(): "";
                    String stringAddress = postalCode + countryName + locality + thoroughfare + subThoroughfare;
                    Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
                    intent.putExtra("address", stringAddress);
                    startActivity(intent);
                }
            }
        });

        locationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                locationSearch.clearFocus();
                recyclerView.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
                }
                catch (NullPointerException e)
                {
                    imm.hideSoftInputFromWindow(locationSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }

                String location = locationSearch.getText().toString();
                List<Address> addressList = null;

                if (location != null && !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        String thoroughfare = null;
                        String subThoroughfare = null;
                        if (address.getThoroughfare() == null)
                        {
                            thoroughfare = "";
                        }
                        else {
                            thoroughfare = address.getThoroughfare();
                        }
                        if (address.getSubThoroughfare() == null)
                        {
                            subThoroughfare = "";
                        }
                        else {
                            subThoroughfare = ", " + address.getSubThoroughfare();
                        }
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        if (thoroughfare.equals("") && subThoroughfare.equals(""))
                        {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(address.getLocality()));
                            markerClick = false;
                            orderButton.setVisibility(View.VISIBLE);
                        }
                        else {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(thoroughfare + subThoroughfare));
                            markerClick = false;
                            orderButton.setVisibility(View.VISIBLE);
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    else
                    {
                        Snackbar mSnackbar = Snackbar.make(mapElement, "Не удалось найти адрес", Snackbar.LENGTH_SHORT);
                        View mView = mSnackbar.getView();
                        Snackbar.SnackbarLayout lp = (Snackbar.SnackbarLayout) mView;
                        lp.setForegroundGravity(Gravity.CENTER);
                        mView.setBackgroundColor(Color.WHITE);
                        TextView mTextView = (TextView) mView.findViewById(R.id.snackbar_text);
                        mTextView.setTextColor(Color.BLACK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        else
                            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                        mSnackbar.show();
                    }
                }
                else
                {
                    Snackbar mSnackbar = Snackbar.make(mapElement, "Не удалось найти адрес", Snackbar.LENGTH_SHORT);
                    View mView = mSnackbar.getView();
                    Snackbar.SnackbarLayout lp = (Snackbar.SnackbarLayout) mView;
                    lp.setForegroundGravity(Gravity.CENTER);
                    mView.setBackgroundColor(Color.WHITE);
                    TextView mTextView = (TextView) mView.findViewById(R.id.snackbar_text);
                    mTextView.setTextColor(Color.BLACK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                        mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    else
                        mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    mSnackbar.show();
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSearch.clearFocus();
                recyclerView.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
                }
                catch (NullPointerException e)
                {
                    imm.hideSoftInputFromWindow(locationSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }

                String location = locationSearch.getText().toString();
                List<Address> addressList = null;

                if (location != null && !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        String thoroughfare = null;
                        String subThoroughfare = null;
                        if (address.getThoroughfare() == null)
                        {
                            thoroughfare = "";
                        }
                        else {
                            thoroughfare = address.getThoroughfare();
                        }
                        if (address.getSubThoroughfare() == null)
                        {
                            subThoroughfare = "";
                        }
                        else {
                            subThoroughfare = ", " + address.getSubThoroughfare();
                        }
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        if (thoroughfare.equals("") && subThoroughfare.equals(""))
                        {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(address.getLocality()));
                            markerClick = false;
                            orderButton.setVisibility(View.VISIBLE);
                        }
                        else {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(thoroughfare + subThoroughfare));
                            markerClick = false;
                            orderButton.setVisibility(View.VISIBLE);
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    else
                    {
                        Snackbar mSnackbar = Snackbar.make(mapElement, "Не удалось найти адрес", Snackbar.LENGTH_SHORT);
                        View mView = mSnackbar.getView();
                        Snackbar.SnackbarLayout lp = (Snackbar.SnackbarLayout) mView;
                        lp.setForegroundGravity(Gravity.CENTER);
                        mView.setBackgroundColor(Color.WHITE);
                        TextView mTextView = (TextView) mView.findViewById(R.id.snackbar_text);
                        mTextView.setTextColor(Color.BLACK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        else
                            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                        mSnackbar.show();
                    }
                }
                else
                {
                    Snackbar mSnackbar = Snackbar.make(mapElement, "Не удалось найти адрес", Snackbar.LENGTH_SHORT);
                    View mView = mSnackbar.getView();
                    Snackbar.SnackbarLayout lp = (Snackbar.SnackbarLayout) mView;
                    lp.setForegroundGravity(Gravity.CENTER);
                    mView.setBackgroundColor(Color.WHITE);
                    TextView mTextView = (TextView) mView.findViewById(R.id.snackbar_text);
                    mTextView.setTextColor(Color.BLACK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                        mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    else
                        mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    mSnackbar.show();
                }
            }
        });

        locationSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (markerClick)
                {
                    marker.hideInfoWindow();
                    markerClick = false;
                }
                else
                {
                    marker.showInfoWindow();
                    markerClick = true;
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                return true;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude,  1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    String thoroughfare = null;
                    String subThoroughfare = null;
                    if (address.getThoroughfare() == null)
                    {
                        thoroughfare = "";
                    }
                    else
                    {
                        thoroughfare = address.getThoroughfare();
                    }
                    if (address.getSubThoroughfare() == null)
                    {
                        subThoroughfare = "";
                    }
                    else
                    {
                        subThoroughfare = ", " + address.getSubThoroughfare();
                    }
                    if (thoroughfare.equals("") && subThoroughfare.equals(""))
                    {
                        marker.setTitle(address.getLocality());
                    }
                    else {
                        marker.setTitle(thoroughfare + subThoroughfare);
                    }
                }

            }
        });
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void click(Place place) {
        Toast.makeText(getActivity(), place.getAddress()+", "+place.getLatLng().latitude+place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (recyclerView.getVisibility() == View.GONE) {recyclerView.setVisibility(View.VISIBLE);}
            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {recyclerView.setVisibility(View.GONE);}
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    };

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void onMapSearch(View view) {
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

}