package com.stark.smartwearableheadset.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {
    private UserService userService;
    Button btn_return, btn_proceed;
    EditText txtFullName, txtPhone;
    String loggedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // init
        userService = RetrofitClient.getClient().create(UserService.class);
        SharedPreferences preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        loggedUsername = preferences.getString("username", "");

        btn_return = (Button) findViewById(R.id.btn_return);
        btn_proceed = (Button) findViewById(R.id.btn_save);
        txtFullName = (EditText) findViewById(R.id.txt_full_name);
        txtPhone = (EditText) findViewById(R.id.txt_phone);

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPrevious();
            }
        });
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        loadDetails();
    }

    // load current data
    private void loadDetails() {
        Call call = userService.getUserData(loggedUsername);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody;
                    if (response.isSuccessful()) {
                        responseBody = (ResponseBody) response.body();
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        txtFullName.setText(jsonObject.getString("name"));
                        txtPhone.setText(jsonObject.getString("phone"));
                    } else {
//                        responseBody = response.errorBody();
                        Toast.makeText(EditProfile.this, "An error occurred !", Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(EditProfile.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(EditProfile.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // return to the previous activity
    private void goToPrevious() {
        finish();
    }

    // save details
    private void saveChanges() {
        String fullname = txtFullName.getText().toString();
        String phone = txtPhone.getText().toString();

        // validation
        if (!(fullname.length() > 0 && phone.length() > 0)) {
            Toast.makeText(EditProfile.this, "Fill in the fields to continue !", Toast.LENGTH_SHORT).show();
            return;
        }

        BlindUser user = new BlindUser();
        user.setName(fullname);
        user.setPhone(phone);

        Call call = userService.editUserProfile(loggedUsername, user);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody;
                    if (response.isSuccessful()) {
                        responseBody = (ResponseBody) response.body();
                    } else {
                        responseBody = (ResponseBody) response.errorBody();
                    }

                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String responseMessage = jsonObject.getString("message");
                    // display the server's response
                    Toast.makeText(EditProfile.this, responseMessage, Toast.LENGTH_SHORT).show();
                    loadDetails();
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(EditProfile.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(EditProfile.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
