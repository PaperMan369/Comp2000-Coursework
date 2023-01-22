package com.example.comp2000_coursework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.*;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RequestQueue queue;
    String url;
    String output;
    Gson gson;

    // Public variables to keep app wide data
    public boolean userVisible = false;


    public MainActivity(){
         gson = new Gson();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
        url = "http://web.socem.plymouth.ac.uk/COMP2000/api/Employees";
    }

    public void get(){
        new Thread(new Runnable() {
            public void run() {
                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                String ID = "";
                                String Surname = "";
                                String Forename = "";


                                LinearLayout scroll = (LinearLayout) findViewById(R.id.viewUsers);
                                for (int i = 0; i < response.length(); i++) {
                                    // Formatting user to be displayed
                                    try {
                                        String[] temp = response.get(i).toString().split(",");
                                        ID = temp[0].strip().replaceAll("[{\"}]", "");
                                        Surname = temp[1].strip().replaceAll("[{\"}]", "");
                                        Forename = temp[2].strip().replaceAll("[{\"}]", "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    // Displaying user
                                    TextView temp = new TextView(getBaseContext());
                                    try {
                                        temp.setText(Surname.substring(0, 1).toUpperCase() + Surname.substring(1).toLowerCase() + " " +
                                                Forename.substring(0, 1).toUpperCase() + Forename.substring(1).toLowerCase() + " " +
                                                ID.substring(0, 1).toUpperCase() + ID.substring(1).toLowerCase());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // UI Section
                                    scroll.post(new Runnable() {
                                        public void run() {
                                            scroll.addView(temp);
                                        }
                                    });
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error.toString());
                            }
                        });
                queue.add(jsonObjectRequest);
            }
        }).start();
    }

    public void post(String first, String last, String ID){
        // Make new json object and put params in it
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", Integer.parseInt(ID));
            jsonParams.put("forename", first);
            jsonParams.put("surname", last);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonParams);


        // Building a request
        JsonObjectRequest request = new JsonObjectRequest(JsonRequest.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                });

            /*

              For the sake of the example I've called newRequestQueue(getApplicationContext()) here
              but the recommended way is to create a singleton that will handle this.

              Read more at : https://developer.android.com/training/volley/requestqueue

              Category -> Use a singleton pattern

            */
        Volley.newRequestQueue(getApplicationContext()).
                add(request);

    }



    public void hideUsers(){
        LinearLayout scroll = (LinearLayout) findViewById(R.id.viewUsers);
        scroll.removeAllViews();
    }

    public void addUser(View view){
        EditText fName = (EditText) findViewById(R.id.AddUserName1);
        EditText sName = (EditText) findViewById(R.id.AddUserName2);
        EditText ID = (EditText) findViewById(R.id.AddUserID);

        post(fName.getText().toString(), sName.getText().toString(), ID.getText().toString());
    }

    public void showUsers(View view) {
        if (userVisible){
            userVisible = false;
            this.hideUsers();

        }
        else if (!userVisible){
            userVisible = true;
            this.get();

        }
    }
}