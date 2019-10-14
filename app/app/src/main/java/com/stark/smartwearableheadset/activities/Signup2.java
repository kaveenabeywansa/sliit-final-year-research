package com.stark.smartwearableheadset.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.stark.smartwearableheadset.R;
import com.stark.smartwearableheadset.adapters.AssociateListAdapter;
import com.stark.smartwearableheadset.models.User;
import com.stark.smartwearableheadset.services.RetrofitClient;
import com.stark.smartwearableheadset.services.UserService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup2 extends AppCompatActivity {
    private ListView user_data;
    private List<User> user_list;

    private ListView selected_users;
    private List<User> selected_list;

    private UserService userService;
    private Button btn_search;
    private EditText txt_search_keywords;

    private User blindUser;
    private Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        userService = RetrofitClient.getClient().create(UserService.class);
        user_data = (ListView) findViewById(R.id.associate_search_list);
        selected_users = (ListView) findViewById(R.id.selected_associate_list);
        selected_list = new ArrayList<>();
        selected_users.setAdapter(new AssociateListAdapter(getApplicationContext(), selected_list));
        btn_search = (Button) findViewById(R.id.btn_search);
        txt_search_keywords = (EditText) findViewById(R.id.txt_search_associates);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        user_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addToSelectedItems(parent, view, position, id);
            }
        });

        selected_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeFromSelectedList(adapterView, view, i, l);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAssociates();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerBlindUser();
            }
        });
    }

    // add the selected list item to the selected associate list
    private void addToSelectedItems(AdapterView<?> parent, View view, int position, long id) {
        // get the object of the selected user
        User blindUser = (User) parent.getItemAtPosition(position);
        selected_list.add(blindUser); // add the item to the selected list
        user_list.remove(position); // remove the item from the list

        // notify adapters
        ((BaseAdapter) user_data.getAdapter()).notifyDataSetChanged();
        ((BaseAdapter) selected_users.getAdapter()).notifyDataSetChanged();
    }

    // remove the selected list item from the selected list
    private void removeFromSelectedList(AdapterView<?> parent, View view, int position, long id) {
        // get the object of the selected user
        selected_list.remove(position); // remove the item from the list

        // notify adapters
        ((BaseAdapter) selected_users.getAdapter()).notifyDataSetChanged();
    }

    // search button clicked
    private void searchAssociates() {
        String keywords = txt_search_keywords.getText().toString();
        if (keywords.length() == 0) {
            loadAssociateList();
        } else {
            loadAssociateList(keywords);
        }
    }

    // load all associates
    private void loadAssociateList() {
        Call call = userService.getAssociateSearchList();
        processCallBackAndPopulate(call);
    }

    // load associates from keyword
    private void loadAssociateList(String keywords) {
        Call call = userService.getAssociateSearchList(keywords);
        processCallBackAndPopulate(call);
    }

    // process and populate the associate list
    private void processCallBackAndPopulate(Call call) {
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.code() == 200) {
                    user_list = (List<User>) response.body();
                    // no data found. display no data image
//                    img_no_data.setVisibility(View.VISIBLE);
                    if (user_list.size() < 1) {
                        Toast.makeText(getApplicationContext(), "No Data Found !", Toast.LENGTH_SHORT).show();
                        toggleNoDataText(true);
                    } else {
                        toggleNoDataText(false);
                        user_data.setAdapter(new AssociateListAdapter(getApplicationContext(), user_list));
                        Log.i("TestList", user_list.toString());
                    }
                } else {
                    toggleNoDataText(true);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                toggleNoDataText(true);
                Toast.makeText(getApplicationContext(), "An internal error occurred !", Toast.LENGTH_LONG).show();
                Log.e("Error", t.getMessage());
//                img_error.setVisibility(View.VISIBLE);
            }
        });
    }

    // toggle display of the no data error message text
    private void toggleNoDataText(boolean status) {
        if (status) {
            findViewById(R.id.txt_no_data_error).setVisibility(View.VISIBLE);
            user_data.setAdapter(null);
            findViewById(R.id.associate_search_list).setVisibility(View.GONE);
        } else {
            findViewById(R.id.associate_search_list).setVisibility(View.VISIBLE);
            findViewById(R.id.txt_no_data_error).setVisibility(View.GONE);
        }
    }

    // creates a new blind user account
    private void registerBlindUser() {
        if (!(selected_list.size() > 0)) {
            Toast.makeText(this, "Please select atleast one known associate !", Toast.LENGTH_SHORT).show();
            return;
        }

        String fName = getIntent().getStringExtra("fName");
        String phone = getIntent().getStringExtra("phone");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String associateList[] = new String[selected_list.size()];
        for (int i = 0; i < selected_list.size(); i++) {
            associateList[i] = selected_list.get(i).getUsername();
        }

        User associateUser = new User(fName, username, password, phone, "blind", associateList);
        Call call = userService.registerNewUser(associateUser);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody;
                    if (response.isSuccessful()) {
                        responseBody = (ResponseBody) response.body();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        responseBody = (ResponseBody) response.errorBody();
                    }

                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String responseMessage = jsonObject.getString("message");
                    // display the server's response
                    Toast.makeText(Signup2.this, responseMessage, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    Toast.makeText(Signup2.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(Signup2.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
