package com.stark.smartwearableheadset.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.models.LoginCredentials;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private UserService userService;
    private EditText txt_username, txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserPermissions();

        // init
        userService = RetrofitClient.getClient().create(UserService.class);
        Button btn_login = (Button) findViewById(R.id.btn_login),
                btn_signup = (Button) findViewById(R.id.btn_signup);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);

        // listeners
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    // user permissions
    private void getUserPermissions() {
        //        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE}, 1);
        String[] androidPermissionsSet = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
        };
        ActivityCompat.requestPermissions(this, androidPermissionsSet, 1);
    }

    // validate user and make a login request
    private void loginUser() {
        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();

        if (validateCredentials(username, password)) {
            loginRequest(username, password);
        } else {
            Toast.makeText(this, "Please enter username and password !", Toast.LENGTH_SHORT).show();
        }
    }

    // redirect to sign up activity
    private void goToSignUp() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    // validate if credentials are entered
    private boolean validateCredentials(String username, String password) {
        return (username.length() > 0 && password.length() > 0);
    }

    // make a login request
    private void loginRequest(final String username, String password) {
        // credential object
        LoginCredentials loginCredentials = new LoginCredentials(username, password);

        // create and send login request
        Call call = userService.loginUser(loginCredentials);
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

                    // checking response to check if credentials were valid
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String responseMessage = jsonObject.getString("message");
                    // display the server's response
                    Toast.makeText(MainActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                    // get the logged json object
                    JSONObject loggedJsonObj = (jsonObject.getJSONObject("logged"));
                    boolean userLogged = loggedJsonObj.getBoolean("status");
                    Log.i("Test", loggedJsonObj.toString());

                    // if user credentials are valid
                    if (userLogged) {
                        String UserType = loggedJsonObj.getString("userType");
                        String UserName = loggedJsonObj.getString("userName");
                        Log.i("Test", UserType);
                        Log.i("Test", UserName);

                        // store user details in sessions
                        SharedPreferences preferences;
                        preferences = getSharedPreferences("user_details", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user_name", UserName);
                        editor.putString("user_type", UserType);
                        editor.putString("username", username);
//                        editor.commit();
                        editor.apply();

                        // check user type and redirect accordingly
                        if (UserType.equals("blind")) {
                            Intent intent = new Intent(MainActivity.this, BlindDashboard.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, AssociateDashboard.class);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(MainActivity.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
