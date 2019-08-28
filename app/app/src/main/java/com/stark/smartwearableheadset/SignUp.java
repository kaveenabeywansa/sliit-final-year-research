package com.stark.smartwearableheadset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.stark.smartwearableheadset.adapters.AssociateListAdapter;
import com.stark.smartwearableheadset.adapters.BlindUserListAdapter;
import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.models.User;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
//    private ListView user_data;
//    private List<User> user_list;
    private UserService userService;
    private Button btn_signup;
//    private Button btn_search;
    private Button btn_nextpage;
    private Button btn_type_blind;
    private Button btn_type_associate;
//    private EditText txt_search_keywords;
    private EditText txt_fullName, txt_phoneNumb, txt_username, txt_password, txt_confirmpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // init
        userService = RetrofitClient.getClient().create(UserService.class);
//        user_data = (ListView) findViewById(R.id.associate_search_list);
        btn_signup = (Button) findViewById(R.id.btn_signup);
//        btn_search = (Button) findViewById(R.id.btn_search);
        btn_nextpage = (Button) findViewById(R.id.btn_nextpage);
        btn_type_blind = (Button) findViewById(R.id.btn_usertype_blind);
        btn_type_associate = (Button) findViewById(R.id.btn_usertype_associate);

        txt_fullName = (EditText) findViewById(R.id.txt_full_name);
        txt_phoneNumb = (EditText) findViewById(R.id.txt_phone);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirmpwd = (EditText) findViewById(R.id.txt_confirm_password);

//        txt_search_keywords = (EditText) findViewById(R.id.txt_search_associates);

//        user_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SignUp.this, "Hello ID:" + id + " position: " + position, Toast.LENGTH_SHORT).show();
//
//                // BlindUser blindUser = (BlindUser) parent.getItemAtPosition(position);
//                //
//                //                Intent intent = new Intent(BlindUserList.this, BlindUserDashboard.class);
//                //                intent.putExtra("blind_user_id", blindUser.getUsername());
//                //                startActivity(intent);
//            }
//        });
        btn_nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextPage();
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpKnownAssociate();
            }
        });

        btn_type_blind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTypeSelected("blind");
            }
        });

        btn_type_associate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTypeSelected("associate");
            }
        });

//        btn_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchAssociates();
//            }
//        });
    }

    private void userTypeSelected(String usertype) {
        if (usertype.equals("blind")) {
            // change button styles and show next button
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn_type_blind.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.darkblue));
                btn_type_blind.setTextColor(getResources().getColor(R.color.white));

                btn_type_associate.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                btn_type_associate.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                btn_type_blind.setBackgroundColor(getResources().getColor(R.color.darkblue));
                btn_type_blind.setTextColor(getResources().getColor(R.color.white));

                btn_type_associate.setBackgroundColor(getResources().getColor(R.color.white));
                btn_type_associate.setTextColor(getResources().getColor(R.color.darkblue));
            }

            btn_signup.setVisibility(View.GONE);
            btn_nextpage.setVisibility(View.VISIBLE);
        } else {
            // change button styles and show create account button
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn_type_associate.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.darkblue));
                btn_type_associate.setTextColor(getResources().getColor(R.color.white));

                btn_type_blind.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                btn_type_blind.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                btn_type_associate.setBackgroundColor(getResources().getColor(R.color.darkblue));
                btn_type_associate.setTextColor(getResources().getColor(R.color.white));

                btn_type_blind.setBackgroundColor(getResources().getColor(R.color.white));
                btn_type_blind.setTextColor(getResources().getColor(R.color.darkblue));
            }

            btn_nextpage.setVisibility(View.GONE);
            btn_signup.setVisibility(View.VISIBLE);
        }
    }

    private void SignUpKnownAssociate() {
        String fName = txt_fullName.getText().toString();
        String phone = txt_phoneNumb.getText().toString();
        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();
        String confirmPwd = txt_confirmpwd.getText().toString();

        if (fName.length()>0 && phone.length()>0 && username.length()>0 && password.length()>0) {
            if(!password.equals(confirmPwd)){
                Toast.makeText(SignUp.this, "Passwords do not match !", Toast.LENGTH_SHORT).show();
                return;
            }

            User associateUser = new User(fName,username,password,phone,"associate",null);
            Call call = userService.registerNewUser(associateUser);
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
                        Toast.makeText(SignUp.this, responseMessage, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                        Toast.makeText(SignUp.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.i("Error", t.getMessage());
                    Toast.makeText(SignUp.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SignUp.this, "Please fill in all fields !", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToNextPage() {
        String fName = txt_fullName.getText().toString();
        String phone = txt_phoneNumb.getText().toString();
        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();
        String confirmPwd = txt_confirmpwd.getText().toString();

        if (fName.length()>0 && phone.length()>0 && username.length()>0 && password.length()>0) {
            if (!password.equals(confirmPwd)) {
                Toast.makeText(SignUp.this, "Passwords do not match !", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), Signup2.class);
            intent.putExtra("fName", fName);
            intent.putExtra("phone", phone);
            intent.putExtra("username",username);
            intent.putExtra("password", password);
            startActivity(intent);
        }
    }

//    // search button clicked
//    private void searchAssociates() {
//        String keywords = txt_search_keywords.getText().toString();
//        if (keywords.length() == 0) {
//            loadAssociateList();
//        } else {
//            loadAssociateList(keywords);
//        }
//    }
//
//    // load all associates
//    private void loadAssociateList() {
//        Call call = userService.getAssociateSearchList();
//        processCallBackAndPopulate(call);
//    }
//
//    // load associates from keyword
//    private void loadAssociateList(String keywords) {
//        Call call = userService.getAssociateSearchList(keywords);
//        processCallBackAndPopulate(call);
//    }

//    // process and populate the associate list
//    private void processCallBackAndPopulate(Call call) {
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//                if (response.isSuccessful() && response.code() == 200) {
//                    user_list = (List<User>) response.body();
//                    // no data found. display no data image
////                    img_no_data.setVisibility(View.VISIBLE);
//                    if (user_list.size() < 1) {
//                        Toast.makeText(getApplicationContext(), "No Data Found !", Toast.LENGTH_SHORT).show();
//                        toggleNoDataText(true);
//                    } else {
//                        toggleNoDataText(false);
//                        user_data.setAdapter(new AssociateListAdapter(getApplicationContext(), user_list));
//                        Log.i("TestList", user_list.toString());
//                    }
//                } else {
//                    toggleNoDataText(true);
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                toggleNoDataText(true);
//                Toast.makeText(getApplicationContext(), "An internal error occurred !", Toast.LENGTH_LONG).show();
//                Log.e("Error", t.getMessage());
////                img_error.setVisibility(View.VISIBLE);
//            }
//        });
//    }

//    // toggle display of the no data error message text
//    private void toggleNoDataText(boolean status) {
//        if (status) {
//            findViewById(R.id.txt_no_data_error).setVisibility(View.VISIBLE);
//            user_data.setAdapter(null);
//            findViewById(R.id.associate_search_list).setVisibility(View.GONE);
//        } else {
//            findViewById(R.id.associate_search_list).setVisibility(View.VISIBLE);
//            findViewById(R.id.txt_no_data_error).setVisibility(View.GONE);
//        }
//    }
}
