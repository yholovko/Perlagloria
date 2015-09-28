package com.sport.perlagloria.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sport.perlagloria.R;
import com.sport.perlagloria.model.Customer;
import com.sport.perlagloria.util.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOADING_CUSTOMERS_LIST_TAG = "group_list_loading";
    private ArrayList<Customer> customerArrayList;
    private RelativeLayout splashScreenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        splashScreenLayout = (RelativeLayout) findViewById(R.id.splashScreenLayout);
        customerArrayList = new ArrayList<>();

        if (isNetworkConnected()) {
            loadCustomersInfo();
        } else {
            showNoConnectionSB();
        }
    }

    private void loadCustomersInfo() {
        String loadCustomersUrl = getString(R.string.server_host) + "/customer/getcustomers";

        JsonArrayRequest customersJsonRequest = new JsonArrayRequest(loadCustomersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, response.toString());
                        parseCustomersJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, "Error: " + error.getMessage());
                        showServerErrorSB();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(customersJsonRequest, LOADING_CUSTOMERS_LIST_TAG); // Adding request to request queue
    }

    private void parseCustomersJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Customer customer = new Customer(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"));

                customerArrayList.add(customer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks whether mobile is connected to internet and returns true if connected
     *
     * @return {@code true} if the network is connected, {@code false} otherwise
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

//    /**
//     * Checks if device is connected to internet (There is a possibility it's connected to
//     * a network but not to internet).
//     *
//     * @return {@code true} if the internet is available, {@code false} otherwise
//     */
//    public boolean isInternetAvailable() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//
//            if (ipAddr.equals("")) {
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//    }

    private void showNoConnectionSB() {
        Snackbar
                .make(splashScreenLayout, R.string.no_connection_snackbar, Snackbar.LENGTH_LONG)
                .setAction(R.string.no_connection_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(0);
                    }
                })
                .show();
    }

    private void showServerErrorSB() {
        Snackbar
                .make(splashScreenLayout, R.string.server_error_snackbar, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.server_error_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(0);
                    }
                })
                .show();
    }
}
