package com.thursday.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.thursday.musicplayer.MainActivity.AlbumPathList;
import static com.thursday.musicplayer.MainActivity.ArtistPathList;

public class Artist extends Fragment implements AdapterView.OnItemClickListener, Filterable, SearchView.OnQueryTextListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    ViewGroup truet;
    SearchView searchview25;
    public List<String> ListElementsArrayList2;
    public List<String> ListElementsArrayListforsongtimtleinartist = new ArrayList<>();
    TextView textView;
    public List<String> ArtistImagefilter = new ArrayList<>();
    public ArrayList<String> original = new ArrayList<>();
    public ArrayList<String> filteredlist = new ArrayList<>();
    imagingAdapter theAdapter;
    RecyclerView recyclerView;
    Filter filtera;
    public List<String> ListElementsArrayList4 = new ArrayList<>();

    public Artist() {
    }

    public static Artist newInstance(String param1, String param2) {
        Artist fragment = new Artist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getList();
        setHasOptionsMenu(true);
    }

    public void getList() {
        ListElementsArrayList2 = MainActivity.ArtistPathList;
        ListElementsArrayList4.clear();
        ArtistImagefilter.clear();
        ArtistImagefilter.addAll(MainActivity.ImageSongPathListforArtists);
        original = (ArrayList<String>) ListElementsArrayList2;
        ListElementsArrayList4.addAll(ListElementsArrayList2);
        if (ListElementsArrayList2.size() == 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), "No Song Found", Toast.LENGTH_LONG).show();
                    textView = new TextView(getActivity());
                    textView.setId(0);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    textView.setGravity(1);
                    textView.setTextSize(15);
                    textView.setText("No Song Found");
                    truet.addView(textView);
                }

            });
        } else {
            theAdapter = new imagingAdapter(getActivity().getApplicationContext(), this, ListElementsArrayList4, ArtistImagefilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        truet = (ViewGroup) inflater.inflate(R.layout.fragment_artist, null);
        recyclerView = truet.findViewById(R.id.recyclerview_artist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(theAdapter);
        return truet;
    }

    public void gettingsongsforartist(String Artist) {
        ContentResolver contentResolver;
        contentResolver = getContext().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(getContext(), "Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(getContext(), "No Music Found on SD Card.", Toast.LENGTH_LONG);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Playlists.ARTIST);
            do {
                String Path = cursor.getString(path);
                String SongTitle = cursor.getString(Title);
                String Albama = cursor.getString(artist);
                if (new File(Path).exists() && Albama.equals(Artist)) {
                    ListElementsArrayListforsongtimtleinartist.add(SongTitle);
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gettingsongsforartist(ArtistPathList.get(position));
        Bundle bandle = new Bundle();
        bandle.putString("Named", "Artist");
        bandle.putStringArrayList("#1234", (ArrayList<String>) ListElementsArrayListforsongtimtleinartist);
        MainActivity.listforplaylist.setArguments(bandle);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_Host, MainActivity.listforplaylist, "listforplaylist").addToBackStack(null).commit();
    }

    public void clicklitsen() {
        searchview25.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSearchView();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.searchbarmenu, menu);
        MenuItem item = menu.findItem(R.id.serachview2);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchview25 = (SearchView) item.getActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serachview2: {
                clicklitsen();
                searchview25.setIconified(false);
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
        filtera.filter(query);
        searchview25.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            filtera.filter(null);
        }

        filtera.filter(newText);
        return false;

    }

    private void setUpSearchView() {
        searchview25.setOnQueryTextListener(this);
        searchview25.setSubmitButtonEnabled(true);
        searchview25.setQueryHint("Search Here");
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filtera == null) {
            filtera = new filterhere();
        }
        return filtera;
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

                    ArtistImagefilter.clear();
                    int i = 0;
                    ArrayList<String> results = new ArrayList<String>();
                    for (String g : original) {
                        if (g.toLowerCase().contains(constraint.toString())) {
                            results.add(g);
                            ArtistImagefilter.add(MainActivity.ImageSongPathListforArtists.get(i));
                        }
                        i++;
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
                ListElementsArrayList4.clear();
                ListElementsArrayList4.addAll((Collection<? extends String>) results.values);
                theAdapter.notifyDataSetChanged();
            } else {
                ListElementsArrayList4.clear();
                ListElementsArrayList4.addAll(ListElementsArrayList2);
                theAdapter.notifyDataSetChanged();
            }


            }
    }
}