package com.thursday.musicplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.thursday.musicplayer.MainActivity.svc;


public class SearchSong extends Fragment implements SearchView.OnQueryTextListener, Filterable {
    private static final String ARG_PARAM1 = null;
    private static final String ARG_PARAM2 = null;
    public List<String> ListElementsArrayList2;
    public ArrayList<String> original = new ArrayList<>();
    public ArrayList<String> filteredlist = new ArrayList<>();
    private filterhere filter;
    public SearchView searchview2;
    ViewGroup root;
    myAdapter theAdapter;
    ListView listView1;
    TextView textView;
    public List<String> ListElementsArrayList3 = new ArrayList<>();

    public SearchSong() {
    }

    public static SearchSong newInstance(String param1, String param2) {
        SearchSong fragment = new SearchSong();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.bottomNavigationView.getMenu().getItem(0).setChecked(true);
        getList();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Song List");
    }

    public void getList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ListElementsArrayList2 = MainActivity.ListElementsArrayList;
                ListElementsArrayList3.clear();
                original = (ArrayList<String>) ListElementsArrayList2;
                ListElementsArrayList3.addAll(ListElementsArrayList2);
                if (ListElementsArrayList2.size() == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity().getApplicationContext(), "No Song Found", Toast.LENGTH_LONG).show();
                            textView = new TextView(getActivity());
                            textView.setId(0);
                            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            textView.setGravity(1);
                            textView.setTextSize(25);
                            textView.setText("No Song Found");
                            root.addView(textView);
                        }

                    });
                } else {
                    theAdapter = new myAdapter(getActivity().getApplicationContext(), ListElementsArrayList3);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView1.setAdapter(theAdapter);
                        }
                    });
                    listView1.setTextFilterEnabled(false);
                    listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("final.temopray.forimages", MODE_MULTI_PROCESS);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putInt("fortheimages", ListElementsArrayList2.indexOf(listView1.getItemAtPosition(position)));
                            edit.putInt("forthesize", ListElementsArrayList2.size());
                            for (int i = 0; i < ListElementsArrayList2.size(); i++) {
                                edit.putString("#1234@" + i, ListElementsArrayList2.get(i));
                            }
                            edit.apply();
                            MainActivity.bottomNavigationView.getMenu().getItem(1).setChecked(true);
                            setHasOptionsMenu(false);
                            getParentFragmentManager().beginTransaction().replace(R.id.fragment_Host, MainActivity.image, "IMAGE").commit();
                            svc = new Intent(getActivity(), playingbackgroundService.class);
                            svc.putExtra("positionideal", ListElementsArrayList2.indexOf(listView1.getItemAtPosition(position)));
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

    private void setUpSearchView() {
        searchview2.setOnQueryTextListener(this);
        searchview2.setSubmitButtonEnabled(true);
        searchview2.setQueryHint("Search Here");
    }

    public void clicklitsen() {
        searchview2.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSearchView();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_search_song, null);
        listView1 = root.findViewById(R.id.listview1);
        searchview2 = root.findViewById(R.id.serachview2);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.searchbarmenu, menu);
        MenuItem item = menu.findItem(R.id.serachview2);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchview2 = (SearchView) item.getActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serachview2: {
                clicklitsen();
                searchview2.setIconified(false);
                getFilter();
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
        searchview2.clearFocus();
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
                ListElementsArrayList3.clear();
                ListElementsArrayList3.addAll((Collection<? extends String>) results.values);

                theAdapter.notifyDataSetChanged();
            } else {
                ListElementsArrayList3.clear();
                ListElementsArrayList3.addAll(ListElementsArrayList2);
                theAdapter.notifyDataSetChanged();
            }

        }
    }
}