package com.thursday.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

public class PlayList extends Fragment {
private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
ViewGroup root;
MainActivity.PlaylistViewPagerAdapter adapter;
Button button;
    public PlayList() {
    }
public static PlayList newInstance(String param1, String param2) {
        PlayList fragment = new PlayList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                root=( ViewGroup)inflater.inflate(R.layout.fragment_play_list,null);
        ViewPager viewPager2;
        viewPager2 = root.findViewById(R.id.viewpager);
        TabLayout tablayout=root.findViewById(R.id.Tableyout);
        tablayout.setupWithViewPager(viewPager2);
        adapter= new MainActivity.PlaylistViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager2.setAdapter(adapter);
        getActivity().setTitle("PlayList");
        return root;
    }
}