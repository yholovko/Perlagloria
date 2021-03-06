package com.sport.perlagloria.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.sport.perlagloria.activity.ChooseTeamActivity;
import com.sport.perlagloria.adapter.TeamListAdapter;
import com.sport.perlagloria.model.Tactic;
import com.sport.perlagloria.model.Team;
import com.sport.perlagloria.util.AppController;
import com.sport.perlagloria.util.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activities that contain this fragment must implement the
 * {@link SelectTeamFragment.OnTeamPassListener} interface
 * to handle interaction events.
 * Use the {@link SelectTeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectTeamFragment extends Fragment implements TeamListAdapter.OnCheckboxCheckedListener {
    private static final String CUSTOMER_NAME = "customerName";
    private static final String TOURNAMENT_NAME = "tournamentName";
    private static final String DIVISION_NAME = "divisionName";
    private static final String DIVISION_ID = "divisionId";
    private static final String LOADING_TEAMS_LIST_TAG = "teams_list_loading";
    private String tournamentName;
    private String customerName;
    private String divisionName;
    private int divisionId;

    private LinearLayoutManager mLayoutManager;
    private RecyclerView teamListRecView;
    private TeamListAdapter teamListAdapter;
    private ArrayList<Team> teamArrayList;

    private TextView champValueTextView;
    private TextView tournValueTextView;
    private TextView divisValueTextView;

    private OnTeamPassListener teamPassListener;  //pass selected team back to the activity

    public SelectTeamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param customerName   selected customer name.
     * @param tournamentName selected tournament name.
     * @param divisionName   selected division name.
     * @param divisionId     selected division id.
     * @return A new instance of fragment SelectTeamFragment.
     */
    public static SelectTeamFragment newInstance(String customerName, String tournamentName, String divisionName, int divisionId) {
        SelectTeamFragment fragment = new SelectTeamFragment();
        Bundle args = new Bundle();
        args.putString(CUSTOMER_NAME, customerName);
        args.putString(TOURNAMENT_NAME, tournamentName);
        args.putString(DIVISION_NAME, divisionName);
        args.putInt(DIVISION_ID, divisionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerName = getArguments().getString(CUSTOMER_NAME);
            tournamentName = getArguments().getString(TOURNAMENT_NAME);
            divisionName = getArguments().getString(DIVISION_NAME);
            divisionId = getArguments().getInt(DIVISION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_team, container, false);
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);
        champValueTextView.setText(customerName);
        tournValueTextView = (TextView) rootView.findViewById(R.id.tournValueTextView);
        tournValueTextView.setText(tournamentName);
        divisValueTextView = (TextView) rootView.findViewById(R.id.divisValueTextView);
        divisValueTextView.setText(divisionName);

        ((ChooseTeamActivity) getActivity()).setToolbarTitle(getString(R.string.toolbar_choose_team_title));

        teamArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());

        teamListRecView = (RecyclerView) rootView.findViewById(R.id.container_teams);
        teamListAdapter = new TeamListAdapter(teamArrayList, this);
        teamListRecView.setAdapter(teamListAdapter);
        teamListRecView.setItemAnimator(null);
        teamListRecView.setLayoutManager(mLayoutManager);

        loadTeamInfo();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            teamPassListener = (OnTeamPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTeamPassListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        teamPassListener = null;
    }

    /**
     * Is being executed after any checkbox was checked in recycleview
     */
    @Override
    public void onCheckboxChecked(Team team) {
        if (teamPassListener != null) {
            teamPassListener.onTeamPass(team);
        }
    }

    private void loadTeamInfo() {
        String loadTeamUrl = ServerApi.loadTeamUrl + divisionId;
        ((ChooseTeamActivity) getActivity()).showPDialog(getString(R.string.loading_data_progress_dialog));

        JsonArrayRequest teamsJsonRequest = new JsonArrayRequest(loadTeamUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_TEAMS_LIST_TAG, response.toString());
                        ((ChooseTeamActivity) getActivity()).hidePDialog();

                        if (!parseTeamsJson(response)) {                                            //case of response parse error
                            Toast.makeText(getActivity(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();
                        } else {
                            teamListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TEAMS_LIST_TAG, "Error: " + error.getMessage());
                        ((ChooseTeamActivity) getActivity()).hidePDialog();

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            ((ChooseTeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null) { //com.android.volley.NoConnectionError
                            ((ChooseTeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getActivity(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(teamsJsonRequest, LOADING_TEAMS_LIST_TAG); // Adding request to request queue
    }

    private boolean parseTeamsJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                JSONObject tacticObj = obj.getJSONObject("tactic");
                Tactic tactic = new Tactic(tacticObj.getInt("id"),
                        tacticObj.getString("code"),
                        tacticObj.getString("description"));


                Team team = new Team(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        obj.getInt("position"),
                        obj.getInt("points"),
                        obj.getInt("gamesPlayed"),
                        obj.getInt("wins"),
                        obj.getInt("ties"),
                        obj.getInt("losses"),
                        obj.getInt("goalsFor"),
                        obj.getInt("goalsAgainst"),
                        obj.getInt("goalDifference"),
                        obj.getInt("avgGoalForPerMatch"),
                        obj.getInt("avgGoalAgainstPerMatch"),
                        tactic,
                        false);

                teamArrayList.add(team);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Pass data back to the activity (selected team)
     */
    public interface OnTeamPassListener {
        void onTeamPass(Team team);
    }
}
