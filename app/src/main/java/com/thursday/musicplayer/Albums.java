package com.thursday.musicplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.RelativeDateTimeFormatter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.TestLooperManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.thursday.musicplayer.MainActivity.AlbumPathList;
import static com.thursday.musicplayer.MainActivity.svc;

public class Albums extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, Filterable {
    ViewGroup branch;
    public List<String> ListElementsArrayList2;
    public List<String> ListElementsArrayListforsongtimtleinalbum = new ArrayList<>();
    public ArrayList<String> original = new ArrayList<>();
    public ArrayList<String> filteredlist = new ArrayList<>();
    TextView textView;
    imagingAdapter theAdapter;
    RecyclerView recyclerView;
    SearchView searchView21;
    filterhere filterd;
    public List<String> ListElementsArrayList4 = new ArrayList<>();
    public List<String> AlbumImagefilter = new ArrayList<>();

    public Albums() {
    }

    public static Albums newInstance(String param1, String param2) {
        Albums fragment = new Albums();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        getList();
        setHasOptionsMenu(true);
    }

    public void getList() {
        ListElementsArrayList2 = MainActivity.AlbumPathList;
        ListElementsArrayList4.clear();
        AlbumImagefilter.clear();
        AlbumImagefilter.addAll(MainActivity.ImageSongPathListforAlbums);
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
                    branch.addView(textView);
                }

            });
        } else {
            theAdapter = new imagingAdapter(getActivity().getApplicationContext(), this, ListElementsArrayList4, AlbumImagefilter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        branch = (ViewGroup) inflater.inflate(R.layout.fragment_albums, null);
        recyclerView = branch.findViewById(R.id.recyclerview_albums);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(theAdapter);
        return branch;
    }

    public void gettingsongsforalbums(String Album) {
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
            int album = cursor.getColumnIndex(MediaStore.Audio.Playlists.ALBUM);
            do {
                String Path = cursor.getString(path);
                String SongTitle = cursor.getString(Title);
                String Albama = cursor.getString(album);
                if (new File(Path).exists() && Albama.equals(Album)) {
                    ListElementsArrayListforsongtimtleinalbum.add(SongTitle);
                  }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gettingsongsforalbums(AlbumPathList.get(position));
        Bundle bandle = new Bundle();
        bandle.putString("Named", "Album");
        bandle.putStringArrayList("#1234", (ArrayList<String>) ListElementsArrayListforsongtimtleinalbum);
        MainActivity.listforplaylist.setArguments(bandle);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_Host, MainActivity.listforplaylist, "listforplaylist").addToBackStack(null).commit();
    }

    public void clicklitsen() {
        searchView21.setOnSearchClickListener(new View.OnClickListener() {
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
        searchView21 = (SearchView) item.getActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serachview2: {
                clicklitsen();
                searchView21.setIconified(false);
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
        filterd.filter(query);
        searchView21.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            filterd.filter(null);
        }

        filterd.filter(newText);
        return false;

    }

    private void setUpSearchView() {
        searchView21.setOnQueryTextListener(this);
        searchView21.setSubmitButtonEnabled(true);
        searchView21.setQueryHint("Search Here");
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filterd == null) {
            filterd = new filterhere();
        }
        return filterd;
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

                    AlbumImagefilter.clear();
                    int i = 0;
                    ArrayList<String> results = new ArrayList<String>();
                    for (String g : original) {
                        if (g.toLowerCase().contains(constraint.toString())) {
                            results.add(g);
                            AlbumImagefilter.add(MainActivity.ImageSongPathListforAlbums.get(i));
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