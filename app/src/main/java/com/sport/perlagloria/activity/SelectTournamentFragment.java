package com.sport.perlagloria.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sport.perlagloria.R;
import com.sport.perlagloria.adapter.TournamentListAdapter;
import com.sport.perlagloria.model.Tournament;
import com.sport.perlagloria.util.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  Activities that contain this fragment must implement the
 * {@link SelectTournamentFragment.OnTournamentPassListener} interface
 * to handle interaction events.
 * Use the {@link SelectTournamentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectTournamentFragment extends Fragment implements TournamentListAdapter.OnCheckboxCheckedListener {
    private static final String CUSTOMER_ID = "customerId";
    private static final String CUSTOMER_NAME = "customerName";
    private static final String LOADING_TOURNAMENTS_LIST_TAG = "tournaments_list_loading";
    private int customerId;
    private String customerName;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView tournamentListRecView;
    private TournamentListAdapter tournamentListAdapter;
    private ArrayList<Tournament> tournamentArrayList;

    private TextView champValueTextView;
    private TextView tournValueTextView;

    private ProgressDialog progressDialog;

    private OnTournamentPassListener tournamentPassListener;  //pass selected tournament back to the activity

    public SelectTournamentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param customerId   selected customer id.
     * @param customerName selected customer name.
     * @return A new instance of fragment SelectTournamentFragment.
     */
    public static SelectTournamentFragment newInstance(int customerId, String customerName) {
        SelectTournamentFragment fragment = new SelectTournamentFragment();
        Bundle args = new Bundle();
        args.putInt(CUSTOMER_ID, customerId);
        args.putString(CUSTOMER_NAME, customerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getInt(CUSTOMER_ID);
            customerName = getArguments().getString(CUSTOMER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_tournament, container, false);
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);
        champValueTextView.setText(customerName);
        tournValueTextView = (TextView) rootView.findViewById(R.id.tournValueTextView);

        ((ChooseTeamActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_choose_team_title));

        tournamentArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());

        tournamentListRecView = (RecyclerView) rootView.findViewById(R.id.container_tournaments);
        tournamentListAdapter = new TournamentListAdapter(tournamentArrayList, this);
        tournamentListRecView.setAdapter(tournamentListAdapter);
        tournamentListRecView.setItemAnimator(null);
        tournamentListRecView.setLayoutManager(mLayoutManager);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        loadTournamentInfo();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            tournamentPassListener = (OnTournamentPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTournamentPassListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tournamentPassListener = null;
    }

    /**
     * Is being executed after any checkbox was checked in recycleview
     */
    @Override
    public void onCheckboxChecked(Tournament tournament) {
        tournValueTextView.setText(tournament.getName());

        if (tournamentPassListener != null) {
            tournamentPassListener.onTournamentPass(tournament);
        }
    }

    private void showPDialog(String message) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    private void hidePDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void loadTournamentInfo() {
        String loadTournamentUrl = getString(R.string.server_host) + "/tournament/gettournaments?customerId=" + customerId;
        showPDialog(getString(R.string.loading_data_progress_dialog));

        JsonArrayRequest tournamentsJsonRequest = new JsonArrayRequest(loadTournamentUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_TOURNAMENTS_LIST_TAG, response.toString());
                        hidePDialog();

                        if (!parseTournamentsJson(response)) {                                      //case of response parse error
                            Toast.makeText(getActivity(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();
                        } else {
                            tournamentListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TOURNAMENTS_LIST_TAG, "Error: " + error.getMessage());
                        hidePDialog();

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            showErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null) { //com.android.volley.NoConnectionError
                            showErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getActivity(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(tournamentsJsonRequest, LOADING_TOURNAMENTS_LIST_TAG); // Adding request to request queue
    }

    private boolean parseTournamentsJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Tournament tournament = new Tournament(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        false);

                tournamentArrayList.add(tournament);
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
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Pass data back to the activity (selected tournament)
     */
    public interface OnTournamentPassListener {
        void onTournamentPass(Tournament tournament);
    }
}
