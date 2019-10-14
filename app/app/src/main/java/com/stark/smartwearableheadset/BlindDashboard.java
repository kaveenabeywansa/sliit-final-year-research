package com.stark.smartwearableheadset;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.smartwearableheadset.services.BackgroundService;

public class BlindDashboard extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private Button btn_edit_profile, btn_change_pwd, btn_signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_dashboard);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

//        // background service // move this to dashboard later
        BackgroundService backgroundService = new BackgroundService();
        Intent intent = new Intent(getApplicationContext(), backgroundService.getClass());
        startService(intent);

        btn_edit_profile = (Button) findViewById(R.id.btn_edit_profile);
        btn_change_pwd = (Button) findViewById(R.id.btn_change_pwd);
        btn_signout = (Button) findViewById(R.id.btn_sign_out);

        // add listeners
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileClicked();
            }
        });
        btn_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwdClicked();
            }
        });
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUserOut();
            }
        });

        // set user name text view
        SharedPreferences preferences;
        preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String Name = preferences.getString("user_name", "");
        TextView dspName = (TextView) findViewById(R.id.user_fullname);
        dspName.setText(Name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(BlindDashboard.this, BackgroundService.class);
        stopService(intent);
    }

    // edit profile
    private void editProfileClicked() {
        Intent intent = new Intent( BlindDashboard.this, EditProfile.class);
        startActivity(intent);
    }

    // chaange user password
    private void changePwdClicked() {
        Intent intent = new Intent( BlindDashboard.this, ChangePassword.class);
        startActivity(intent);
    }

    // sign user out
    private void signUserOut() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
