package com.stark.smartwearableheadset;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AssociateDashboard extends AppCompatActivity {
    private Button btn_edit_profile, btn_change_pwd, btn_monitor_users, btn_sign_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_dashboard);

        // This page will list all blind people who are connected
        // When clicked on the corresponding, will direct to the specific options activity

        // init
        btn_edit_profile = (Button) findViewById(R.id.btn_edit_profile);
        btn_change_pwd = (Button) findViewById(R.id.btn_change_pwd);
        btn_monitor_users = (Button) findViewById(R.id.btn_monitor_users);
        btn_sign_out = (Button) findViewById(R.id.btn_sign_out);

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

        btn_monitor_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitorUsersClicked();
            }
        });

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutClicked();
            }
        });
    }

    // edit profile
    private void editProfileClicked() {
    }

    // chaange user password
    private void changePwdClicked() {
    }

    // go to user list page
    private void monitorUsersClicked() {
        Intent intent = new Intent(AssociateDashboard.this, BlindUserList.class);
        startActivity(intent);
    }

    // sign user out. remove sessions
    private void signoutClicked() {
    }
}
