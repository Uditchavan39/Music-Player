package com.thursday.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllPlaylist extends Fragment implements AdapterView.OnItemClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public AllPlaylist() {
    }

    ViewGroup fruit;
    private DBhandler dBhandler;
    Button createplaylist;
    public static String m_Text;
    ArrayList<String> plalist_list;
    public List<String> ListElementsArrayListforsongtimtleinalbum = new ArrayList<>();
    TextView textView;
    Recycler_playlist_adapter theAdapter;
    RecyclerView recyclerView;
    PopupMenu popupMenu;
    public List<String> ListElementsArrayList4 = new ArrayList<>();

    public static AllPlaylist newInstance(String param1, String param2) {
        AllPlaylist fragment = new AllPlaylist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forrecreation();
    }

    public void forrecreation() {
        dBhandler = new DBhandler(getActivity().getApplicationContext());
        plalist_list = dBhandler.getPlaylistsName();
        ListElementsArrayList4.clear();
        ListElementsArrayList4.addAll(plalist_list);
      }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ListElementsArrayList4.size() == 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), "Playlists not Found", Toast.LENGTH_LONG).show();
                    textView = new TextView(getActivity());
                    textView.setId(0);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    textView.setGravity(1);
                    textView.setTextSize(15);
                    textView.setText("Playlist not Found");
                    fruit.addView(textView);
                }

            });
        } else {
            theAdapter = new Recycler_playlist_adapter(getActivity().getApplicationContext(), this, ListElementsArrayList4);
            fruit.removeView(textView);
        }
    }

    public void createplaylist(View view) {
        final EditText editText = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(editText);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setTitle("Create Playlist")
                .setMessage("Enter the Name of Playlist")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = editText.getText().toString();
                        if (!m_Text.isEmpty()) {
                            dBhandler.createplaylist(m_Text);
                            forrecreation();
                            onStart();
                        } else {
                            Toast.makeText(getContext(), "Enter Valid Playlist Name.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                })
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fruit = (ViewGroup) inflater.inflate(R.layout.fragment_all_playlist, null);
        createplaylist = fruit.findViewById(R.id.createplaylist);
        createplaylist.setOnClickListener(this::createplaylist);
        recyclerView = fruit.findViewById(R.id.recyclerview_allplaylist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new Recycler_playlist_adapter(getActivity().getApplicationContext(),this,ListElementsArrayList4));
        return fruit;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == R.id.tripledot) {
            onClick(view, plalist_list.get(position));
        } else {
            Bundle bandle = new Bundle();
            bandle.putString("recycler", plalist_list.get(position));
            MainActivity.Allplaylist_list.setArguments(bandle);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_Host, MainActivity.Allplaylist_list, "listforplaylist").addToBackStack(null).commit();
        }
    }

    public void onClick(View v, String name) {
        popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.recycler_menu, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.RemoveFromPlaylist);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                dBhandler.RemovePlaylistName(getContext(), name);
                forrecreation();
                recyclerView.setAdapter(theAdapter);
                return true;
            }
        });
        popupMenu.show();
    }

}