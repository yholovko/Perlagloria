package com.sport.perlagloria.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sport.perlagloria.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeamTacticMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamTacticMapFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public TeamTacticMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TeamTacticMapFragment.
     */
    public static TeamTacticMapFragment newInstance(String param1) {
        TeamTacticMapFragment fragment = new TeamTacticMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_tactic_map, container, false);
    }
}
