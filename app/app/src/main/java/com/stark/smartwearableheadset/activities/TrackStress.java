package com.stark.smartwearableheadset.activities;

import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.StatsService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackStress extends AppCompatActivity {
    private StatsService statsService;
    private TextView txt_stress, txt_date, txt_time;
    SharedPreferences preferences;
    private boolean fetchingBPMLoop;
    private ImageView imgStress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_stress);

        // init
        statsService = RetrofitClient.getClient().create(StatsService.class);
        preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        fetchingBPMLoop = true;
        txt_stress = (TextView) findViewById(R.id.txt_stress);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_time = (TextView) findViewById(R.id.txt_time);
        Button btn_return = (Button) findViewById(R.id.btn_return);
        imgStress = (ImageView) findViewById(R.id.img_stress_guy);

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

    @Override
    protected void onStop() {
        super.onStop();
        fetchingBPMLoop = false;
    }

    // start the fetching loop for BPM
    private void startFetchingThread() {
        Thread fetchLatestStressThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (fetchingBPMLoop) {
                    try {
                        Log.i("Test", "Fetching Latest Stress Level...");
                        fetchLatestStress();
                        Thread.sleep(2500);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }
        };
        fetchLatestStressThread.start();
    }

    // implementation for getting the latest BPM
    private void fetchLatestStress() {
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
                        txt_stress.setText(jsonObject.getString("stress"));
                        txt_date.setText(jsonObject.getString("date"));
                        txt_time.setText(jsonObject.getString("time"));
                        setStressTextColor(jsonObject.getInt("stress"));
                    } else {
//                        responseBody = response.errorBody();
                        Toast.makeText(TrackStress.this, "An error occurred !", Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(TrackStress.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(TrackStress.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // change the bpm text color accordingly
    private void setStressTextColor(int stress) {
        int AVG_MAX_RATE = 50;

        if (stress > AVG_MAX_RATE) {
            // stress is high
            txt_stress.setTextColor(ContextCompat.getColor(this, R.color.red));
//            imgStress.setBackgroundResource(R.drawable.stress_yes);
            imgStress.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.stress_yes));
        } else {
            // stress is low
            txt_stress.setTextColor(ContextCompat.getColor(this, R.color.green));
//            imgStress.setBackgroundResource(R.drawable.stress_no);
            imgStress.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.stress_no));
        }
    }
}
