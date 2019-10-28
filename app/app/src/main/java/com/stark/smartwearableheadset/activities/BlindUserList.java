package com.stark.smartwearableheadset.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.adapters.BlindUserListAdapter;
import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlindUserList extends AppCompatActivity {
    private ListView user_data;
    private List<BlindUser> user_list;
    private UserService userService;
    private ImageView img_no_data;
    private ImageView img_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_user_list);

        // init
        userService = RetrofitClient.getClient().create(UserService.class);
        img_no_data = (ImageView) findViewById(R.id.img_no_data);
        img_error = (ImageView) findViewById(R.id.img_error);

        user_data = (ListView) findViewById(R.id.blind_list);

        user_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlindUser blindUser = (BlindUser) parent.getItemAtPosition(position);

                Intent intent = new Intent(BlindUserList.this, BlindUserDashboard.class);
                intent.putExtra("blind_user_id", blindUser.getUsername());
                startActivity(intent);
            }
        });

        loadBlindUsers();
    }

    private void loadBlindUsers() {
        SharedPreferences preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String loggedUsername = preferences.getString("username", "");

        Call call = userService.getBlindUserListForAssociate(loggedUsername); // create service to get users for given associate
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                // hide error and no data images
                img_no_data.setVisibility(View.GONE);
                img_error.setVisibility(View.GONE);

                user_list = (List<BlindUser>) response.body();
                user_data.setAdapter(new BlindUserListAdapter(getApplicationContext(), user_list));

                // no data found. display no data image
                if (user_list.size() < 1) {
                    Toast.makeText(getApplicationContext(), "No Data Found !", Toast.LENGTH_SHORT).show();
                    img_no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An internal error occurred !", Toast.LENGTH_LONG).show();
                Log.e("Error", t.getMessage());
                img_error.setVisibility(View.VISIBLE);
            }
        });
    }
}
