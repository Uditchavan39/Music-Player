package com.thursday.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.thursday.musicplayer.playingbackgroundService.music;
import static com.thursday.musicplayer.playingbackgroundService.notificationManager;
import static com.thursday.musicplayer.playingbackgroundService.poso;
import static com.thursday.musicplayer.playingbackgroundService.wakeLock;

public class MainActivity extends AppCompatActivity {
    public static List<String> ListElementsArrayList = new ArrayList();
    ContentResolver contentResolver;

    Cursor cursor;
    public static Intent svc;
    Uri uri;
    Context context;
    public static ArrayList<String> audioFilePathList = new ArrayList<>();
    public static ArrayList<String> ImageSongPathList = new ArrayList<>();
    public static ArrayList<String> ImageSongPathListforAlbums = new ArrayList<>();
    public static ArrayList<String> ImageSongPathListforArtists = new ArrayList<>();
    public static ArrayList<String> AlbumPathList = new ArrayList<>();
    public static ArrayList<String> ArtistPathList = new ArrayList<>();
    public static final int RUNTIME_PERMISSION_CODE = 7;
    public static BottomNavigationView bottomNavigationView;
    public static Fragment Search = new SearchSong();
    public static Fragment Favourite = new Favourite();
    public static Fragment playlist = new PlayList();
    public static Fragment image = new imageview();
    public static Fragment listforplaylist = new listforplaylist();
    public static Fragment Allplaylist_list = new Allplaylist_list();
    public static AdRequest adRequest;
    public static InterstitialAd mInterstitialAd;

    @Override
    public void onBackPressed() {
        if (MainActivity.bottomNavigationView.getMenu().findItem(R.id.searchSong).isChecked()) {
            if (music == null)
                super.onBackPressed();
            else moveTaskToBack(true);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0 && !playlist.isAdded()) {
            getSupportFragmentManager().popBackStackImmediate();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Host, Search, "SEARCH").commit();
            MainActivity.bottomNavigationView.getMenu().findItem(R.id.searchSong).setChecked(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (music != null) {
            music.stop();
            music.reset();
            music.release();
            music = null;
        }
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        music = null;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-1364567192434570/8127706499", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        NavController navController = Navigation.findNavController(this, R.id.fragment_Host);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        context = getApplicationContext();
        AndroidRuntimePermission();
        ListElementsArrayList = GetAllMediaMp3Files();

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {

            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (item.isEnabled()) {
                    return;
                }

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.searchSong:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Host, Search, "SEARCH").commit();
                        return true;
                    case R.id.favourite:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Host, Favourite, "FAVOURITE").commit();
                        return true;
                    case R.id.imageView1:
                        if (music == null) {
                            SharedPreferences sharedPreferences = getSharedPreferences("com.thursday.position", MODE_MULTI_PROCESS);
                            int positon = sharedPreferences.getInt("positionideal", 0);
                            int currentposti = sharedPreferences.getInt("currentposition", 1);
                            svc = new Intent(getApplicationContext(), playingbackgroundService.class);
                            svc.putExtra("positionideal", positon);
                            svc.putExtra("currentposition", currentposti);
                            svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) ListElementsArrayList);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(svc);
                            }
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Host, image, "IMAGE").commit();

                            return true;
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Host, image, "IMAGE").commit();
                            return true;
                        }

                    case R.id.playList:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Host, playlist).commit();
                }
                return true;

            }
        });

    }


    public List<String> GetAllMediaMp3Files() {
        contentResolver = context.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(MainActivity.this, "Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(MainActivity.this, "No Music Found on SD Card.", Toast.LENGTH_LONG);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int albumart = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int album = cursor.getColumnIndex(MediaStore.Audio.Playlists.ALBUM);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {

                String SongTitle = cursor.getString(Title);
                String Path = cursor.getString(path);
                String Album = cursor.getString(album);
                String Artist = cursor.getString(artist);
                String albumar = cursor.getString(albumart);
                if (new File(Path).exists()) {
                    ListElementsArrayList.add(SongTitle);
                    audioFilePathList.add(Path);
                    ImageSongPathList.add(albumar);
                    if (!AlbumPathList.contains(Album)) {
                        ImageSongPathListforAlbums.add(albumar);
                        AlbumPathList.add(Album);
                    }
                    if (!ArtistPathList.contains(Artist)) {
                        ImageSongPathListforArtists.add(albumar);
                        ArtistPathList.add(Artist);
                    }
                }
            } while (cursor.moveToNext());
        }

        return ListElementsArrayList;

    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.thursday.position", MODE_MULTI_PROCESS);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("positionideal", poso);
        if (music != null)
            myEdit.putInt("currentposition", music.getCurrentPosition());
        myEdit.apply();

        super.onStop();
    }

    // Creating Runtime permission function.
    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    });

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            } else {

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case RUNTIME_PERMISSION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, this.getClass()));
                } else {
                    AndroidRuntimePermission();
                }
            }
        }
    }


    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final Fragment[] mFragments = new Fragment[]{
                new PlaySong2(),
                new BlankFragment(),
        };
        public final String[] mFragmentNames = new String[]{//Tabs names array
                "Playing",
                "Equalizer"
        };

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Nullable
        @Override
        public Parcelable saveState() {
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentNames[position];
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

    public static class PlaylistViewPagerAdapter extends FragmentStatePagerAdapter {
        private final Fragment[] mFragments = new Fragment[]{
                new AllPlaylist(),
                new Albums(),
                new Artist()
        };
        public final String[] mFragmentNames = new String[]{//Tabs names array
                "All Playlist",
                "Album",
                "Artist"
        };

        public PlaylistViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Nullable
        @Override
        public Parcelable saveState() {
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentNames[position];
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }
}
