package com.sport.perlagloria.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sport.perlagloria.R;


public class MyTeamFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentAdapter myFragmentAdapter;

    public MyTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_team, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        myFragmentAdapter = new MyFragmentAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(myFragmentAdapter);
        tabLayout.setTabsFromPagerAdapter(myFragmentAdapter);
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabNormal), getResources().getColor(R.color.tabSelected));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tabSelected));
        tabLayout.setSelectedTabIndicatorHeight(1);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return rootView;
    }

    class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FixtureMatchInfoFragment();
                case 1:
                    return new TeamTacticMapFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.next_game);
                case 1:
                    return getString(R.string.tactic_map);
                default:
                    return null;
            }
        }
    }


}
