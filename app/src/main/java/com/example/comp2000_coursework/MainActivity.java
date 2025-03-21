package com.example.comp2000_coursework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
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

import org.json.*;


public class MainActivity extends AppCompatActivity {


    // Public variables to keep app wide data
    boolean userVisible = false;
    RequestQueue queue;
    String url;
    String output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        queue = Volley.newRequestQueue(this);
        url = "http://web.socem.plymouth.ac.uk/COMP2000/api/Employees";
    }

    // Get request to fill view user
    public void get(){
        // Running in a new thread
        new Thread(new Runnable() {
            public void run() {
                // Requesting a Json array from the API
                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            // Method to handle the response
                            @Override
                            public void onResponse(JSONArray response) {
                                String ID = "";
                                String Surname = "";
                                String Forename = "";


                                // Formatting users to be displayed
                                LinearLayout scroll = (LinearLayout) findViewById(R.id.viewUsers);
                                for (int i = 0; i < response.length(); i++) {
                                    // Removing the extra characters
                                    try {
                                        String[] temp = response.get(i).toString().split(",");
                                        ID = temp[0].strip().replaceAll("[{\"}]", "");
                                        Surname = temp[1].strip().replaceAll("[{\"}]", "");
                                        Forename = temp[2].strip().replaceAll("[{\"}]", "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    // Creating a new textview and adding the user text to it
                                    TextView temp = new TextView(getBaseContext());
                                    try {
                                        temp.setText(Surname.substring(0, 1).toUpperCase() + Surname.substring(1).toLowerCase() + " " +
                                                Forename.substring(0, 1).toUpperCase() + Forename.substring(1).toLowerCase() + " " +
                                                ID.substring(0, 1).toUpperCase() + ID.substring(1).toLowerCase());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // UI Section
                                    // Adding the text view to the correct window in a UI safe method
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

    // HTTP post request
    public void post(String first, String last, String ID){
        // Running in a new thread
        new Thread(new Runnable() {
            public void run() {
                // Make new json object and put params in it
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("id", Integer.parseInt(ID));
                    jsonParams.put("forename", first);
                    jsonParams.put("surname", last);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Make the post request
                JsonObjectRequest request = new JsonObjectRequest(JsonRequest.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error.toString());
                            }
                        });

                queue.add(request);
            }
        }).start();
    }

    // HTTP put request
    public void put(String first, String last, String ID){
        // Running in a new thread
        new Thread(new Runnable() {
            public void run() {
                // Make new json object and put params in it
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("id", Integer.parseInt(ID));
                    jsonParams.put("forename", first);
                    jsonParams.put("surname", last);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Making the put request
                JsonObjectRequest request = new JsonObjectRequest(JsonRequest.Method.PUT, url+"/"+ID, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error.toString());
                            }
                        });

                queue.add(request);
            }
        }).start();
    }

    // HTTP Delete request
    public void delete(String ID){
        // Running in a new thread
        new Thread(new Runnable() {
            public void run() {
                // Making the delete request
                StringRequest request = new StringRequest(Request.Method.DELETE, url+"/"+ID, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                    }
                },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error.toString());
                            }
                        });

                queue.add(request);
            }
        }).start();
    }

    // Creating a notification
    public void createNotification(View view) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Admin App")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Holiday requested")
                .setContentText("An employee has requested some holiday")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    // Creating a notification channel for notifications
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Admin App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Admin App", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to get the parameters for a put request when the accept button is pressed
    public void editUser(View view){
        EditText fName = (EditText) findViewById(R.id.editUserName1);
        EditText sName = (EditText) findViewById(R.id.editUserName2);
        EditText ID = (EditText) findViewById(R.id.editUserID);

        put(fName.getText().toString(), sName.getText().toString(), ID.getText().toString());
        fName.getText().clear();
        sName.getText().clear();
        ID.getText().clear();
    }

    // Method to get the parameters for a delete request when the accept button is pressed
    public void deleteUser(View view){
        EditText ID = (EditText) findViewById(R.id.deleteUserID);

        delete(ID.getText().toString());
        ID.getText().clear();
    }

    // Method to get the parameters for a post request when the accept button is pressed
    public void addUser(View view){
        EditText fName = (EditText) findViewById(R.id.AddUserName1);
        EditText sName = (EditText) findViewById(R.id.AddUserName2);
        EditText ID = (EditText) findViewById(R.id.AddUserID);

        post(fName.getText().toString(), sName.getText().toString(), ID.getText().toString());
        fName.getText().clear();
        sName.getText().clear();
        ID.getText().clear();
    }

    // Method to clear the view users window when the button is pressed for a second time
    public void hideUsers(){
        LinearLayout scroll = (LinearLayout) findViewById(R.id.viewUsers);
        scroll.removeAllViews();
    }

    // Method to handle when the view users window should fill with users and when is should empty
    // as the same button is used for both
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