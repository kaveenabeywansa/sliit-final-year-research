package com.stark.smartwearableheadset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlindUserDashboard extends AppCompatActivity {
    private UserService userService;
    SharedPreferences preferences;
    private TextView txt_user_name, txt_user_phone;
    private Button btn_track_location, btn_track_health, btn_location_history, btn_health_history, btn_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_user_dashboard);

        // init
        userService = RetrofitClient.getClient().create(UserService.class);
        preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        txt_user_name = (TextView) findViewById(R.id.user_name);
        txt_user_phone = (TextView) findViewById(R.id.user_phone);
        btn_track_location = (Button) findViewById(R.id.btn_track_location);
        btn_track_health = (Button) findViewById(R.id.btn_track_health);
        btn_location_history = (Button) findViewById(R.id.btn_location_history);
        btn_health_history = (Button) findViewById(R.id.btn_health_history);
        btn_return = (Button) findViewById(R.id.btn_return);

        // add listeners
        btn_track_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackLocation();
            }
        });
        btn_track_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackHealth();
            }
        });
        btn_location_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHistory();
            }
        });
        btn_health_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                healthHistory();
            }
        });
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPrevious();
            }
        });

        loadProfile();
    }

    // open selected user's real-time location
    private void trackLocation() {
    }

    // open selected user's real-time health
    private void trackHealth() {
    }

    // open selected user's location history
    private void locationHistory() {
    }

    // open selected user's health history
    private void healthHistory() {
    }

    // return to the previous activity
    private void goToPrevious() {
        finish();
    }

    // load the selected profile using the username passed
    private void loadProfile() {
        String loggedUsername = getIntent().getStringExtra("blind_user_id");
        Call call = userService.getBlindUserDetails(loggedUsername);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                BlindUser blindUser = (BlindUser) response.body();
                txt_user_name.setText(blindUser.getName());
                txt_user_phone.setText(blindUser.getPhone());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BlindUserDashboard.this, "An error occurred !", Toast.LENGTH_LONG).show();
                Log.e("Error", t.getMessage());
                finish();
            }
        });
    }
}
