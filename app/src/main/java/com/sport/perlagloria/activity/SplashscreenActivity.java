package com.sport.perlagloria.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

public class SplashScreenActivity extends AppCompatActivity {
    private static final String LOADING_CUSTOMERS_LIST_TAG = "group_list_loading";
    private ArrayList<Customer> customerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        customerArrayList = new ArrayList<>();
        loadCustomersInfo();
    }

    private void loadCustomersInfo(){
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
                        //todo Show error msg
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, "Error: " + error.getMessage());
                    }
                }
        );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(customersJsonRequest, LOADING_CUSTOMERS_LIST_TAG);
    }

    private void parseCustomersJson(JSONArray response){
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Customer customer = new Customer();
                customer.setId(obj.getInt("id"))
                        .setName(obj.getString("name"))
                        .setCreatedDate(obj.getString("createdDate"))
                        .setIsActive(obj.getBoolean("isActive"));


                customerArrayList.add(customer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
