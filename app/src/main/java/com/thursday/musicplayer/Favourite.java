package com.thursday.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.thursday.musicplayer.MainActivity.mInterstitialAd;
import static com.thursday.musicplayer.MainActivity.svc;

public class Favourite extends Fragment implements Filterable, SearchView.OnQueryTextListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<String> FavouriteElementsArrayList = new ArrayList<>();
    List<String> FavouriteElementsArrayListPosition = new ArrayList<>();
    ListView Favourited;
    ViewGroup rooted;
    public List<String> ListElementsArrayList2;
    public List<String> ListElementsArrayList3 = new ArrayList<>();
    public ArrayList<String> original = new ArrayList<>();
    public ArrayList<String> filteredlist = new ArrayList<>();
    myAdapter listAdapter;
    private filterhere filterh;
    public SearchView searchView3;

    public Favourite() {
    }

    public static Favourite newInstance(String param1, String param2) {
        Favourite fragment = new Favourite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        FavouriteElementsArrayListPosition.clear();
        FavouriteElementsArrayList.clear();
        SharedPreferences preferences = getActivity().getSharedPreferences("com.Thursday.FavouriteSharedPref", MODE_MULTI_PROCESS);
        Map<String, ?> allEntries = preferences.getAll();
        int i = 0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            FavouriteElementsArrayList.add(entry.getValue().toString());
            FavouriteElementsArrayListPosition.add(entry.getKey());
            i++;
        }
        getActivity().setTitle("Favourite");
        getFavouriteList();
        Random random = new Random();
        int t = random.nextInt(30);
        if (t == 19) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                    }
                });
            } else {
            }
        }
    }

    public void getFavouriteList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ListElementsArrayList2 = FavouriteElementsArrayList;
                ListElementsArrayList3.clear();
                ListElementsArrayList3.addAll(ListElementsArrayList2);
                original = (ArrayList<String>) ListElementsArrayList2;
                if (FavouriteElementsArrayList.size() == 0 || FavouriteElementsArrayList == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Favourite is Empty", Toast.LENGTH_LONG).show();
                            TextView textView = new TextView(getActivity());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            textView.setGravity(1);
                            textView.setTextSize(15);
                            textView.setId(0);
                            textView.setText("No Song Found");
                            rooted.addView(textView);
                        }
                    });
                } else {
                    listAdapter = new myAdapter(getActivity().getApplicationContext(), ListElementsArrayList3);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Favourited.setAdapter(listAdapter);
                        }
                    });
                    Favourited.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("final.temopray.forimages", MODE_MULTI_PROCESS);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putInt("fortheimages", ListElementsArrayList2.indexOf(Favourited.getItemAtPosition(position)));
                            edit.putInt("forthesize", ListElementsArrayList2.size());
                            for (int i = 0; i < ListElementsArrayList2.size(); i++) {
                                edit.putString("#1234@" + i, ListElementsArrayList2.get(i));
                            }
                            edit.apply();
                            MainActivity.bottomNavigationView.getMenu().getItem(1).setChecked(true);
                            getParentFragmentManager().beginTransaction().replace(R.id.fragment_Host, MainActivity.image, "IMAGE").commit();
                            svc = new Intent(getActivity(), playingbackgroundService.class);
                            svc.putExtra("positionideal", ListElementsArrayList2.indexOf(Favourited.getItemAtPosition(position)));
                            svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) ListElementsArrayList2);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getActivity().startForegroundService(svc);
                            }
                            Toast.makeText(getActivity().getApplicationContext(), parent.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setUpSearchView() {
        searchView3.setOnQueryTextListener(this);
        searchView3.setSubmitButtonEnabled(true);
        searchView3.setQueryHint("Search Here");
    }

    public void clicklitsen() {
        searchView3.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSearchView();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rooted = (ViewGroup) inflater.inflate(R.layout.fragment_favourite, null);
        Favourited = rooted.findViewById(R.id.Favouritelistview);
        searchView3 = rooted.findViewById(R.id.serachview2);
        return rooted;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.searchbarmenu, menu);
        MenuItem item = menu.findItem(R.id.serachview2);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView3 = (SearchView) item.getActionView();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serachview2: {
                clicklitsen();
                getFilter();
                searchView3.setIconified(false);
                break;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterh.filter(query);
        searchView3.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            filterh.filter(null);
        }

        filterh.filter(newText);
        return false;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filterh == null) {
            filterh = new filterhere();
        }
        return filterh;
    }

    private class filterhere extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults oReturn = new FilterResults();
            synchronized (oReturn) {
                if (constraint.toString() == null) {
                    filteredlist.clear();
                    filteredlist = original;
                } else {

                    ArrayList<String> results = new ArrayList<String>();
                    for (String g : original) {
                        if (g.toLowerCase().contains(constraint.toString())) {
                            results.add(g);
                        }
                    }
                    filteredlist = results;
                }
                oReturn.values = filteredlist;

                return oReturn;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                ListElementsArrayList3.clear();
                ListElementsArrayList3.addAll((Collection<? extends String>) results.values);
                listAdapter.notifyDataSetChanged();
            } else {
                ListElementsArrayList3.clear();
                ListElementsArrayList3.addAll(ListElementsArrayList2);
            }

        }
    }
}
