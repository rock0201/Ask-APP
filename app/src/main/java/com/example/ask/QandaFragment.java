package com.example.ask;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QandaFragment extends Fragment {

    private TabLayout myTabLayout;
    private ViewPager myViewPager;
    private List<String> myTitle;
    private List<Fragment>myFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qanda, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initview();
    }

    private void initview() {
        myFragment = new ArrayList<>();
        myTitle = new ArrayList<>();
        myFragment.add(new UserQuestionsFragment());
        myTitle.add("Questions");
        myFragment.add(new UserAnswersFragment());
        myTitle.add("Answers");

        //设置page数量
        myViewPager.setOffscreenPageLimit(myFragment.size());
        myViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return myFragment.get(position);
            }

            @Override
            public int getCount() {
                return myFragment.size();
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return myTitle.get(position);
            }
        });
        myTabLayout.setupWithViewPager(myViewPager);
    }

    private void init() {
        myTabLayout = requireActivity().findViewById(R.id.myTab);
        myViewPager = requireActivity().findViewById(R.id.myViewPager);
    }
}
