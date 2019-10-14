package com.stark.smartwearableheadset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stark.smartwearableheadset.models.LoginCredentials;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {
    private UserService userService;
    Button btn_return, btn_proceed;
    EditText txt_pass1, txt_pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // init
        userService = RetrofitClient.getClient().create(UserService.class);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_proceed = (Button) findViewById(R.id.btn_save);
        txt_pass1 = (EditText) findViewById(R.id.pwd_new);
        txt_pass2 = (EditText) findViewById(R.id.pwd_confirm);

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPrevious();
            }
        });
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });
    }

    // return to the previous activity
    private void goToPrevious() {
        finish();
    }

    // changes the password
    private void changePass() {
        SharedPreferences preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String loggedUsername = preferences.getString("username", "");
        String password1 = txt_pass1.getText().toString();
        String password2 = txt_pass2.getText().toString();

        // validations
        if (!(password1.length() > 0 && password2.length() > 0)) {
            Toast.makeText(ChangePassword.this, "Fill in the fields to continue !", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password1.equals(password2)) {
            Toast.makeText(ChangePassword.this, "Passwords do not match !", Toast.LENGTH_SHORT).show();
            return;
        }

        Call call = userService.changePassword(loggedUsername, new LoginCredentials(loggedUsername, password1));
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
                    Toast.makeText(ChangePassword.this, responseMessage, Toast.LENGTH_SHORT).show();
                    txt_pass1.setText("");
                    txt_pass2.setText("");
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(ChangePassword.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(ChangePassword.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
