package com.sport.perlagloria.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sport.perlagloria.R;
import com.sport.perlagloria.adapter.ChampionshipListAdapter;
import com.sport.perlagloria.model.Customer;
import com.sport.perlagloria.util.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectChampionshipFragment extends Fragment implements ChampionshipListAdapter.OnCheckboxCheckedListener {

    private static final String LOADING_CUSTOMERS_LIST_TAG = "group_list_loading";

    private LinearLayoutManager mLayoutManager;
    private RecyclerView championshipListRecView;
    private ChampionshipListAdapter championshipListAdapter;
    private ArrayList<Customer> championshipArrayList;

    private TextView champValueTextView;

    public SelectChampionshipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_championship, container, false);
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);

        ((ChooseTeamActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_choose_team_title));

        championshipArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());

        championshipListRecView = (RecyclerView) rootView.findViewById(R.id.container_championships);
        championshipListAdapter = new ChampionshipListAdapter(championshipArrayList, this);
        championshipListRecView.setAdapter(championshipListAdapter);
        championshipListRecView.setItemAnimator(new DefaultItemAnimator());
        championshipListRecView.setLayoutManager(mLayoutManager);

        loadChampionshipInfo();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Is being executed after any checkbox was checked in recycleview
     */
    @Override
    public void onCheckboxChecked() {
        //Log.d("TAG", "checked :)");
        champValueTextView.setText(championshipListAdapter.getSelectedItem().getName());
    }

    private void loadChampionshipInfo() {
        String loadCustomersUrl = getString(R.string.server_host) + "/customer/getcustomers";

        JsonArrayRequest customersJsonRequest = new JsonArrayRequest(loadCustomersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, response.toString());

                        if (!parseCustomersJson(response)) { //case of response parse error
                            showErrorAlertDialog();
                        } else {
                            championshipListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, "Error: " + error.getMessage());
                        showErrorAlertDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(customersJsonRequest, LOADING_CUSTOMERS_LIST_TAG); // Adding request to request queue
    }

    private boolean parseCustomersJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Customer customer = new Customer(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        false);

                championshipArrayList.add(customer);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void showErrorAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.check_connection_dialog));
        builder.setNegativeButton(getString(R.string.check_connection_dialog_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                System.exit(0);
            }
        });
        builder.show();
    }

}