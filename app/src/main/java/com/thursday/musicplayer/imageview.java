package com.thursday.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;

public class imageview extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    ViewGroup rooting;
 MainActivity.ViewPagerAdapter adapter;

    public imageview() {
        // Required empty public constructor
    }

    public static imageview newInstance(String param1, String param2) {
        imageview fragment = new imageview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setTitle("Now Playing");
    setHasOptionsMenu(false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rooting=(ViewGroup) inflater.inflate(R.layout.fragment_imageview, null);
        ViewPager viewPager2;
        viewPager2 = rooting.findViewById(R.id.viewPager);
        TabLayout tablayout=rooting.findViewById(R.id.Tablyout);
        tablayout.setupWithViewPager(viewPager2);
        adapter=new MainActivity.ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager2.setAdapter(adapter);
        return rooting;
    }
}