package com.thursday.musicplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.thursday.musicplayer.MainActivity.ImageSongPathList;
import static com.thursday.musicplayer.MainActivity.ListElementsArrayList;
import static com.thursday.musicplayer.MainActivity.mInterstitialAd;
import static com.thursday.musicplayer.MainActivity.playlist;
import static com.thursday.musicplayer.MainActivity.svc;

public class Allplaylist_list extends Fragment implements SearchView.OnQueryTextListener, Filterable {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public List<String> ListElementsArrayList_songs = new ArrayList<>();
    private String mParam1;
    private String mParam2;
    public ArrayList<String> original = new ArrayList<>();
    public ArrayList<String> filteredlist = new ArrayList<>();
    filterhere filter;
    public SearchView Searchview55;
    public ViewGroup leaves;
    myAdapter theAdapter;
    ListView listView55;
    TextView textView;
    myAdapter tada;
    ListView listView98;
    ListView listView96;
    DBhandler dBhandler;
    public Button removebutton;
    public List<String> ListElementsArrayList_searching_2 = new ArrayList<>();
    public List<String> Sonselectorlist = new ArrayList<>();
    public List<String> Sonremoverlist = new ArrayList<>();
    String moriarty;

    public Allplaylist_list() {
    }

    public static Allplaylist_list newInstance(String param1, String param2) {
        Allplaylist_list fragment = new Allplaylist_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dBhandler = new DBhandler(getActivity().getApplicationContext());
        Bundle bandle = getArguments();
        moriarty = bandle.getString("recycler", null);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListElementsArrayList_songs.clear();
        ListElementsArrayList_songs = (dBhandler.getSongsName(moriarty));
        getActivity().setTitle(moriarty);
        getList();
       Random random = new Random();
        int t = random.nextInt(30);
        if (t == 12) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
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

    public void addbutton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View convertview = inflater.inflate(R.layout.custom_dialogue, null);
        builder.setView(convertview);
        builder.setTitle("Select Song to add to playlist");
        listView96 = convertview.findViewById(R.id.list_dialogue);
        tada = new myAdapter(getActivity().getApplicationContext(), ListElementsArrayList);
        listView96.setAdapter(tada);
        listView96.setSelector(R.drawable.rowbgselector);
        listView96.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView96.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Sonselectorlist.contains(listView96.getItemAtPosition(position))) {
                    Sonselectorlist.add(ListElementsArrayList.get(position));
                } else if (Sonselectorlist.size() != 0) {
                    Sonselectorlist.remove(ListElementsArrayList.get(position));
                }
            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < Sonselectorlist.size(); i++) {
                    if (ListElementsArrayList_songs.contains(Sonselectorlist.get(i))) {

                    } else
                        dBhandler.addNewSongstoplaylist(getContext(), Sonselectorlist.get(i), moriarty);
                }
                getList();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });
        builder.show();
    }

    public void removebutton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View convertview = inflater.inflate(R.layout.custom_dialogue, null);
        builder.setView(convertview);
        builder.setTitle("Select Song to Remove From playlist");
        listView98 = convertview.findViewById(R.id.list_dialogue);
        tada = new myAdapter(getActivity().getApplicationContext(), ListElementsArrayList_songs);
        listView98.setAdapter(tada);
        listView98.setSelector(R.drawable.rowbgselector);
        listView98.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView98.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Sonremoverlist.contains(listView98.getItemAtPosition(position))) {
                    Sonremoverlist.add((String) listView98.getItemAtPosition(position));
                } else if (Sonremoverlist.size() != 0) {
                    Sonremoverlist.remove(listView98.getItemAtPosition(position));
                }
            }
        });
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < Sonremoverlist.size(); i++) {
                    dBhandler.removeSongstoplaylist(getContext(), Sonremoverlist.get(i), moriarty);
                }
                getList();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void getList() {
        ListElementsArrayList_searching_2.clear();
        original.clear();
        ListElementsArrayList_searching_2.addAll(dBhandler.getSongsName(moriarty));
        original = (ArrayList<String>) ListElementsArrayList_songs;
        if (ListElementsArrayList_songs.size() == 0 || ListElementsArrayList_songs == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "No Song Found", Toast.LENGTH_LONG).show();
                    textView = new TextView(getContext());
                    textView.setId(0);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    textView.setGravity(1);
                    textView.setTextSize(15);
                    textView.setText("No Song Found");
                    leaves.addView(textView);
                    listView55.setAdapter(null);
                }

            });
        } else {
            theAdapter = new myAdapter(getContext(), ListElementsArrayList_searching_2);
            leaves.removeView(textView);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView55.setAdapter(theAdapter);
                }
            });
            listView55.setTextFilterEnabled(false);
            listView55.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("final.temopray.forimages", MODE_MULTI_PROCESS);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("fortheimages", ListElementsArrayList_songs.indexOf(listView55.getItemAtPosition(position)));
                    edit.putInt("forthesize", ListElementsArrayList_songs.size());
                    for (int i = 0; i < ListElementsArrayList_songs.size(); i++) {
                        edit.putString("#1234@" + i, ListElementsArrayList_songs.get(i));
                    }
                    edit.apply();
                    MainActivity.bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_Host, MainActivity.image, "IMAGE").commit();
                    svc = new Intent(getActivity(), playingbackgroundService.class);
                    svc.putExtra("positionideal", ListElementsArrayList_songs.indexOf(listView55.getItemAtPosition(position)));
                    svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) ListElementsArrayList_songs);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(svc);
                    }
                    Toast.makeText(getActivity().getApplicationContext(), parent.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setUpSearchView() {
        Searchview55.setOnQueryTextListener(this);
        Searchview55.setSubmitButtonEnabled(true);
        Searchview55.setQueryHint("Search Here");
    }

    public void clicklitsen() {
        Searchview55.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSearchView();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.allplaylist_list_addremovesongs, menu);
        MenuItem item = menu.findItem(R.id.serachview2);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        Searchview55 = (SearchView) item.getActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serachview2: {
                clicklitsen();
                getFilter();
                Searchview55.setIconified(false);
                break;
            }
            case R.id.allPlaylist_addsongs: {
                addbutton();
                break;
            }
            case R.id.allPlaylist_removesongs: {
                removebutton();
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filter.filter(query);
        Searchview55.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            filter.filter(null);
        }

        filter.filter(newText);
        return false;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new filterhere();
        }
        return filter;
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
                ListElementsArrayList_searching_2.clear();
                ListElementsArrayList_searching_2.addAll((Collection<? extends String>) results.values);

                theAdapter.notifyDataSetChanged();
            } else {
                ListElementsArrayList_searching_2.clear();
                ListElementsArrayList_searching_2.addAll(ListElementsArrayList_songs);

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        leaves = (ViewGroup) inflater.inflate(R.layout.fragment_allplaylist_list, null);
        listView55 = leaves.findViewById(R.id.listviewforplayList_songs);
        removebutton = leaves.findViewById(R.id.allPlaylist_removesongs);
        Searchview55 = leaves.findViewById(R.id.serachview2);
        return leaves;
    }
}