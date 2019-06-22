package com.stark.smartwearableheadset;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.StatsService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackLocation extends FragmentActivity implements OnMapReadyCallback {
    private StatsService statsService;
    private boolean fetchingLocLoop;
    private GoogleMap googleMap;
    private Marker map_marker;
    LatLng markerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);

        // init
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerPosition = new LatLng(-1, -1);
        statsService = RetrofitClient.getClient().create(StatsService.class);
        fetchingLocLoop = true;

        // start the thread to fetch real-time location
        startFetchingThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fetchingLocLoop = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetchingLocLoop = false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        map_marker = map.addMarker(new MarkerOptions().position(markerPosition).title("Fetching location..."));
        fetchLatestLocation();
    }

    // start the fetching loop for BPM
    private void startFetchingThread() {
        Thread fetchLatestLocationThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (fetchingLocLoop) {
                    try {
                        Log.i("Test", "Fetching Latest Location...");
                        fetchLatestLocation();
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }
        };
        fetchLatestLocationThread.start();
    }

    // get the most recent update on location
    private void fetchLatestLocation() {
        String username = getIntent().getStringExtra("blind_user_id");
        Call call = statsService.getLatestStats(username);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody;
                    if (response.isSuccessful()) {
                        responseBody = (ResponseBody) response.body();

                        boolean zoomToPointer = true;

                        // checking response to check if credentials were valid
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONObject locationObj = (jsonObject.getJSONObject("location"));
                        double lati = locationObj.getDouble("latitude");
                        double longi = locationObj.getDouble("longitude");
                        if (markerPosition.latitude == lati && markerPosition.longitude == longi) {
                            zoomToPointer = false;
                        }
                        markerPosition = new LatLng(lati, longi);
                        map_marker.setPosition(markerPosition);
                        map_marker.setTitle("Location @ " + jsonObject.getString("date") + "-" + jsonObject.getString("time"));

                        if (zoomToPointer) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15));
                        }
                    } else {
//                        responseBody = response.errorBody();
                        Toast.makeText(TrackLocation.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(TrackLocation.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(TrackLocation.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
