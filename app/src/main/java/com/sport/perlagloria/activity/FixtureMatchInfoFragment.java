package com.sport.perlagloria.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sport.perlagloria.R;
import com.sport.perlagloria.model.Division;
import com.sport.perlagloria.model.FixtureDate;
import com.sport.perlagloria.model.FixtureMatchInfo;
import com.sport.perlagloria.model.Tactic;
import com.sport.perlagloria.model.Team;
import com.sport.perlagloria.util.AppController;
import com.sport.perlagloria.util.SharedPreferenceKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FixtureMatchInfoFragment extends Fragment {
    private static final String LOADING_FIXTURE_MATCH_TAG = "fixture_match_loading";
    private int teamId;

    private ImageView team1LogoImgView;
    private ImageView team2LogoImgView;
    private TextView team1NameTV;
    private TextView team2NameTV;
    private TextView dateOfMatchTV;
    private TextView timeOfMatchTV;
    private TextView fieldNumber;
    private TextView homeGoalsTV;
    private TextView awayGoalsTV;

    private ProgressDialog progressDialog;

    private FixtureMatchInfo fixtureMatchInfo;

    public FixtureMatchInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixture_match_info, container, false);

        team1LogoImgView = (ImageView) rootView.findViewById(R.id.team1LogoImgView);
        team2LogoImgView = (ImageView) rootView.findViewById(R.id.team2LogoImgView);
        team1NameTV = (TextView) rootView.findViewById(R.id.team1NameTV);
        team2NameTV = (TextView) rootView.findViewById(R.id.team2NameTV);
        dateOfMatchTV = (TextView) rootView.findViewById(R.id.dateOfMatchTV);
        timeOfMatchTV = (TextView) rootView.findViewById(R.id.timeOfMatchTV);
        fieldNumber = (TextView) rootView.findViewById(R.id.fieldNumber);
        homeGoalsTV = (TextView) rootView.findViewById(R.id.homeGoalsTV);
        awayGoalsTV = (TextView) rootView.findViewById(R.id.awayGoalsTV);

        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        loadFixtureMatchInfo();

        return rootView;
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

    private void loadFixtureMatchInfo() {
        String loadFixtureMatchInfoUrl = getString(R.string.server_host) + "/fixturematch/getnextfixturematch?teamId=" + teamId;
        showPDialog(getString(R.string.loading_data_progress_dialog));

        JsonObjectRequest fixtureMatchInfoJsonRequest = new JsonObjectRequest(loadFixtureMatchInfoUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(LOADING_FIXTURE_MATCH_TAG, response.toString());
                        hidePDialog();

                        if (!parseFixtudeMatchInfoJson(response)) { //case of response parse error
                            showErrorAlertDialog();
                        } else {
                            try {
                                setData();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_FIXTURE_MATCH_TAG, "Error: " + error.getMessage());
                        hidePDialog();

                        showErrorAlertDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(fixtureMatchInfoJsonRequest, LOADING_FIXTURE_MATCH_TAG); // Adding request to request queue
    }

    private boolean parseFixtudeMatchInfoJson(JSONObject response) {
        try {

            FixtureDate fixtureDate;
            Division fixtureDateDivision;
            Team homeTeam;
            Tactic homeTeamTactic;
            Team awayTeam;
            Tactic awayTeamTactic;

            JSONObject fixtureDateObj = response.getJSONObject("fixtureDate");
            JSONObject fixtureDateDivisionObj = fixtureDateObj.getJSONObject("division");
            fixtureDateDivision = new Division(fixtureDateDivisionObj.getInt("id"),
                    fixtureDateDivisionObj.getString("name"),
                    fixtureDateDivisionObj.getString("createdDate"),
                    fixtureDateDivisionObj.getBoolean("isActive"),
                    false);

            fixtureDate = new FixtureDate(fixtureDateObj.getInt("id"),
                    fixtureDateDivision,
                    fixtureDateObj.getString("date"),
                    fixtureDateObj.getString("dateNumber"));


            JSONObject homeTeamObj = response.getJSONObject("homeTeam");
            JSONObject homeTeamTacticObj = homeTeamObj.getJSONObject("tactic");
            homeTeamTactic = new Tactic(homeTeamTacticObj.getInt("id"),
                    homeTeamTacticObj.getString("code"),
                    homeTeamTacticObj.getString("description"));

            homeTeam = new Team(homeTeamObj.getInt("id"),
                    homeTeamObj.getString("name"),
                    homeTeamObj.getString("createdDate"),
                    homeTeamObj.getBoolean("isActive"),
                    homeTeamObj.getInt("position"),
                    homeTeamObj.getInt("points"),
                    homeTeamObj.getInt("gamesPlayed"),
                    homeTeamObj.getInt("wins"),
                    homeTeamObj.getInt("ties"),
                    homeTeamObj.getInt("losses"),
                    homeTeamObj.getInt("goalsFor"),
                    homeTeamObj.getInt("goalsAgainst"),
                    homeTeamObj.getInt("goalDifference"),
                    homeTeamObj.getInt("avgGoalForPerMatch"),
                    homeTeamObj.getInt("avgGoalAgainstPerMatch"),
                    homeTeamTactic,
                    false);

            JSONObject awayTeamObj = response.getJSONObject("awayTeam");
            JSONObject awayTeamTacticObj = awayTeamObj.getJSONObject("tactic");
            awayTeamTactic = new Tactic(awayTeamTacticObj.getInt("id"),
                    awayTeamTacticObj.getString("code"),
                    awayTeamTacticObj.getString("description"));

            awayTeam = new Team(awayTeamObj.getInt("id"),
                    awayTeamObj.getString("name"),
                    awayTeamObj.getString("createdDate"),
                    awayTeamObj.getBoolean("isActive"),
                    awayTeamObj.getInt("position"),
                    awayTeamObj.getInt("points"),
                    awayTeamObj.getInt("gamesPlayed"),
                    awayTeamObj.getInt("wins"),
                    awayTeamObj.getInt("ties"),
                    awayTeamObj.getInt("losses"),
                    awayTeamObj.getInt("goalsFor"),
                    awayTeamObj.getInt("goalsAgainst"),
                    awayTeamObj.getInt("goalDifference"),
                    awayTeamObj.getInt("avgGoalForPerMatch"),
                    awayTeamObj.getInt("avgGoalAgainstPerMatch"),
                    awayTeamTactic,
                    false);

            String homeGoals = (response.getString("homeGoals").equals("null")) ? "-" : response.getString("homeGoals");
            String awayGoals = (response.getString("awayGoals").equals("null")) ? "-" : response.getString("awayGoals");

            fixtureMatchInfo = new FixtureMatchInfo(response.getInt("id"),
                    fixtureDate,
                    homeTeam,
                    awayTeam,
                    response.getString("lastUpdateDate"),
                    response.getString("fieldNumber"),
                    response.getString("hour"),
                    homeGoals,
                    awayGoals);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void setData() throws ParseException {
        team1NameTV.setText(fixtureMatchInfo.getHomeTeam().getName());
        team2NameTV.setText(fixtureMatchInfo.getAwayTeam().getName());

        Locale spanish = new Locale("es", "ES");
        Configuration c = new Configuration(getResources().getConfiguration());
        c.locale = spanish;

        String strDate = fixtureMatchInfo.getFixtureDate().getDate().substring(0, fixtureMatchInfo.getFixtureDate().getDate().indexOf("T"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
        Date date = sdf.parse(strDate);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().locale);
        String outputDate = sdf2.format(date);

        dateOfMatchTV.setText(outputDate);
        timeOfMatchTV.setText(fixtureMatchInfo.getHour().substring(0, fixtureMatchInfo.getHour().lastIndexOf(":")));
        fieldNumber.setText(fixtureMatchInfo.getFieldNumber());
        homeGoalsTV.setText(String.valueOf(fixtureMatchInfo.getHomeGoals()));
        awayGoalsTV.setText(String.valueOf(fixtureMatchInfo.getAwayGoals()));
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


}
