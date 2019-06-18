package com.stark.smartwearableheadset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.StatsService;
import com.stark.smartwearableheadset.services.UserService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackHealth extends AppCompatActivity {
    private StatsService statsService;
    private TextView txt_bpm, txt_date, txt_time;
    SharedPreferences preferences;
    private boolean fetchingBPMLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_health);

        // init
        statsService = RetrofitClient.getClient().create(StatsService.class);
        preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        fetchingBPMLoop = true;
        txt_bpm = (TextView) findViewById(R.id.txt_bpm);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_time = (TextView) findViewById(R.id.txt_time);
        Button btn_return = (Button) findViewById(R.id.btn_return);

        // add listeners
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        // starts the thread that continuously fetch the latest bpm from the server
        startFetchingThread();
    }

    // exit the current activity and end the fetch loop
    private void exitActivity() {
        fetchingBPMLoop = false;
        finish();
    }

    // start the fetching loop for BPM
    private void startFetchingThread() {
        Thread fetchLatestBPMThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (fetchingBPMLoop) {
                    try {
                        Log.i("Test", "Fetching Latest BPM...");
                        fetchLatestBPM();
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }
        };
        fetchLatestBPMThread.start();
    }

    // implementation for getting the latest BPM
    private void fetchLatestBPM() {
        String username = getIntent().getStringExtra("blind_user_id");
        Call call = statsService.getLatestStats(username);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody;
                    if (response.isSuccessful()) {
                        responseBody = (ResponseBody) response.body();

                        // checking response to check if credentials were valid
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        txt_bpm.setText(jsonObject.getString("bpm"));
                        txt_date.setText(jsonObject.getString("date"));
                        txt_time.setText(jsonObject.getString("time"));
                        setBPMTextColor(jsonObject.getInt("bpm"));
                    } else {
//                        responseBody = response.errorBody();
                        Toast.makeText(TrackHealth.this, "An error occurred !", Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(TrackHealth.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(TrackHealth.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // change the bpm text color accordingly
    private void setBPMTextColor(int bpm) {
        int AVG_MAX_RATE = 100;
        int AVG_MIN_RATE = 60;

        if (bpm > AVG_MAX_RATE) {
            // bpm is too high
            txt_bpm.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else if (bpm < AVG_MIN_RATE) {
            // bpm is too low
            txt_bpm.setTextColor(ContextCompat.getColor(this, R.color.yellow));
        } else {
            // bpm is average
            txt_bpm.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
    }
}
