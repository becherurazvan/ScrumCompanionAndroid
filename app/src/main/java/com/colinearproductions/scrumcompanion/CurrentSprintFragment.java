package com.colinearproductions.scrumcompanion;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import RecyclerViewHolders.CurrentSprintFragmentPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentSprintFragment extends Fragment implements ViewPager.OnPageChangeListener {
    CurrentSprintFragmentPagerAdapter currentSprintFragmentPagerAdapter;

    Fragment currentFragmentPage;
    public CurrentSprintFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_sprint, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.current_scrum_view_pager);
        currentSprintFragmentPagerAdapter = new CurrentSprintFragmentPagerAdapter((getChildFragmentManager()));

        viewPager.setAdapter(currentSprintFragmentPagerAdapter);

        viewPager.addOnPageChangeListener(this);

        currentFragmentPage = currentSprintFragmentPagerAdapter.getFragment(0);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.current_scrum_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    public void dataSetChanged(){
        currentSprintFragmentPagerAdapter.updateAll();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentFragmentPage = currentSprintFragmentPagerAdapter.getFragment(position);
        currentSprintFragmentPagerAdapter.getFragment(position).onResume();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onResumeCurrentPage(){

        if(currentFragmentPage == null){
            currentFragmentPage = currentSprintFragmentPagerAdapter.getFragment(0);
        }
            currentFragmentPage.onResume();
    }
}
