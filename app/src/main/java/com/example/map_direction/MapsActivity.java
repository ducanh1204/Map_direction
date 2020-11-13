package com.example.map_direction;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.map_direction.Network.RetrofitService;
import com.example.map_direction.gson.Example;
import com.example.map_direction.morder.LocationInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private ArrayList<LocationInformation> waypointList;
    private ArrayList<LocationInformation> locationList;
    private List<LatLng> latLngs;
    private final String api_key = "AIzaSyCmxFS2arHibTbROQAfTkZAJRkEpz8LErU";
    private int position_location = 0;
    private List<Colors> colorsList = new ArrayList<>();
    private int colorIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addLocationHhint();
        addLocation();
        addColor();

        getRetrofit(position_location, position_location + 1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(21.070120, 105.786339);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        if (polylineOptions != null) {
            mMap.addPolyline(polylineOptions);
        }

        addMarkerAll();


    }

    private void getRetrofit(int location1, int location2) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        String waypoints = "";
        for (int i = 0; i < waypointList.get(position_location).getWaypoints().size(); i++) {
            waypoints += waypointList.get(position_location).getWaypoints().get(i) + "|";
        }
        retrofitService.getHttp(getLatLng(location1), getLatLng(location2), waypoints, api_key).enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                polylineOptions = new PolylineOptions();
                if (colorIndex < colorsList.size()) {
                    polylineOptions.color(colorsList.get(colorIndex).getColor());
                    colorIndex++;
                } else {
                    colorIndex = 0;
                    polylineOptions.color(colorsList.get(colorIndex).getColor());
                }
                polylineOptions.width(10);
                polylineOptions.addAll(decodePolyLine(response.body().getRoutes().get(0).getOverviewPolyline().getPoints()));
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync((OnMapReadyCallback) MapsActivity.this);

                if (position_location < locationList.size() - 2) {
                    position_location++;
                    getRetrofit(position_location, position_location + 1);
                } else if (position_location == locationList.size() - 2) {
                    position_location++;
                    getRetrofit(position_location, 0);

                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addColor() {
        colorsList.add(new Colors(Color.RED));
        colorsList.add(new Colors(Color.GREEN));
        colorsList.add(new Colors(Color.BLUE));
        colorsList.add(new Colors(Color.YELLOW));
        colorsList.add(new Colors(Color.CYAN));
        colorsList.add(new Colors(Color.MAGENTA));
    }

    // Thêm location gợi ý đường đi, nếu không cần thêm gợi ý phải add list trống giống cái add thứ 2
    private void addLocationHhint() {
        waypointList = new ArrayList<>();
        waypointList.add(new LocationInformation(Arrays.asList(new String[]{"21.062502,105.797996", "21.046352,105.805313"})));

        // địa điểm nào không cần thêm vị trí gợi ý phải add list trống giống cái dưới này
        waypointList.add(new LocationInformation(Arrays.asList(new String[]{})));
    }

    // Thêm location, có thể thêm nhiều địa điểm, đường đi tự động từ 1->2, 2->3...điểm cuối cùng đến điểm 0
    private void addLocation() {
        locationList = new ArrayList<>();
        locationList.add(new LocationInformation(21.070120, 105.786339, "Location 1"));
        locationList.add(new LocationInformation(21.046492, 105.785243, "Location 2"));
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    private String getLatLng(int i) {

        String latLng = locationList.get(i).getLatitude() + "," + locationList.get(i).getLongitude();

        return latLng;
    }

    private void addMarkerAll() {
        latLngs = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            final LatLng position = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
            MarkerOptions option = new MarkerOptions();
            option.position(position);
            option.title(locationList.get(i).getTitle());
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            final Marker maker = mMap.addMarker(option);
            maker.showInfoWindow();
            latLngs.add(position);
        }
    }
}