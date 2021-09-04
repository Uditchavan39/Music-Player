package com.thursday.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.thursday.musicplayer.MainActivity.*;
import static com.thursday.musicplayer.MainActivity.svc;
import static com.thursday.musicplayer.R.drawable.ic_baseline_favorite_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_favorite_border_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_music_note_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_pause_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_play_arrow_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_repeat_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_repeat_one_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_shuffle_24;
import static com.thursday.musicplayer.playingbackgroundService.builder;
import static com.thursday.musicplayer.playingbackgroundService.music;
import static com.thursday.musicplayer.playingbackgroundService.notificationManager;
import static com.thursday.musicplayer.playingbackgroundService.wakeLock;

public class PlaySong2 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String CHANNEL_ID_1 = "Channel1";
    public static final String Channel_name = "FirstChannel";

    public PlaySong2() {
    }

    public static PlaySong2 newInstance(String param1, String param2) {
        PlaySong2 fragment = new PlaySong2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ViewGroup root;
    Button play_pause;
    Button Favourite_buttont;
    Button NextPlay;
    Button PreviousPlay;
    int position = 0;
    public SeekBar seekbar;
    int position_z;
   public static boolean playwholelist = false;
    TextView songname;
    ImageView album_art;
   public static boolean Shuffle = false;
    Button controler;
    BroadcastReceiver updateUIReceiver;
  public static  boolean RepeatSameSong = true;
    public List playsong2list = new ArrayList();
    Timer myTimer = new Timer();
private AdView mAdviewed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("final.temopray.forimages", MODE_MULTI_PROCESS);
        position = sharedPreferences.getInt("fortheimages", 0);
        int size = sharedPreferences.getInt("forthesize", 0);
        playsong2list.clear();

        for (int i = 0; i < size; i++) {
            playsong2list.add(sharedPreferences.getString("#1234@" + i, ListElementsArrayList.get(0)));
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hello.Action");
        updateUIReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String name = intent.getStringExtra("imaged");
                position = (playsong2list.indexOf(name));
                Getimageofsong();
                Textnamesetter();
                if (music.isPlaying())
                    play_pause.setBackgroundResource(ic_baseline_pause_24);
                isitinfavouriteornot();
            }
        };
        getContext().registerReceiver(updateUIReceiver, filter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(updateUIReceiver);
        myTimer.cancel();
        if (!getActivity().isDestroyed())
            wakeLock.acquire();
        if(mAdviewed!=null)
            mAdviewed.destroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       Random random=new Random();
       int t=random.nextInt(5);
       if(t==2) {
           mAdviewed.loadAd(adRequest);
       }
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(false);
        isitinfavouriteornot();
        Getimageofsong();
        Textnamesetter();
        SharedPreferences buttonpreferneces = getActivity().getSharedPreferences("looper single list", MODE_MULTI_PROCESS);
        Shuffle = buttonpreferneces.getBoolean("Shuffle", false);
        RepeatSameSong = buttonpreferneces.getBoolean("RepeatSameSong", true);
        playwholelist = buttonpreferneces.getBoolean("playwholelist", false);
        if (Shuffle == true) {
            controler.setBackgroundResource(ic_baseline_shuffle_24);
        }
        if (RepeatSameSong == true) {
            controler.setBackgroundResource(ic_baseline_repeat_one_24);
        }
        if (playwholelist == true) {
            controler.setBackgroundResource(ic_baseline_repeat_24);
        }
        if (music.isPlaying()) {
            play_pause.setBackgroundResource(ic_baseline_pause_24);
        } else {
            play_pause.setBackgroundResource(ic_baseline_play_arrow_24);
        }

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (music != null && music.isPlaying()) {
                            seekbar.setProgress(musiccurrentposition());
                            seekbar.setMax(music.getDuration());
                            play_pause.setBackgroundResource(ic_baseline_pause_24);

                        } else
                            play_pause.setBackgroundResource(ic_baseline_play_arrow_24);
                    }
                });
            }
        }, 0, 500);
        seekbar.setMax(music.getDuration());
        seekbar.setProgress(music.getCurrentPosition());
        music.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                music.seekTo(seekBar.getProgress());
            }
        });
        //  ------------------------------------------------------------------------------------------------
        Random random = new Random();
        int t = random.nextInt(30);
        if (t == 22) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
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


    public void Textnamesetter() {
        songname.setText((CharSequence) playsong2list.get(position));
    }

    public static Uri geturi(int position, ArrayList playsong2list) {
        position = ListElementsArrayList.indexOf(playsong2list.get(position));
        String album_id = MainActivity.ImageSongPathList.get(position);
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(album_id));
        return uri;
    }

    private int musiccurrentposition() {
        int t = 0;
        if (music.isPlaying()) {
            t = music.getCurrentPosition();
        }
        return t;
    }

    private void Getimageofsong() {
        Glide.with(getContext())
                .asBitmap()
                .placeholder(ic_baseline_music_note_24)
                .load(geturi(position, (ArrayList) playsong2list))
                .into(album_art);
    }

    public void previousplay(View view) {
        position = ((position - 1) < 0) ? (playsong2list.size() - 1) : (position - 1);
        svc = new Intent(getActivity(), playingbackgroundService.class);
        svc.putExtra("positionideal", position);
        svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) playsong2list);
        getActivity().startService(svc);
        position_z = position;
        Getimageofsong();
        play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        Textnamesetter();
        //      showNotification();
    }

    public void Nextplay(View view) {
        position = ((position + 1) % playsong2list.size());
        svc = new Intent(getActivity(), playingbackgroundService.class);
        svc.putExtra("positionideal", position);
        svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) playsong2list);
        getActivity().startService(svc);
        Getimageofsong();
        play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        Textnamesetter();
        //    showNotification();
    }

    public void PlayPause(View view) {
        seekbar.setMax(music.getDuration());
        if (music.isPlaying()) {
            music.pause();
            play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);

        } else {
            music.start();
            play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);

        }
        playingbackgroundService.showNotification(getActivity().getApplicationContext(), position, (ArrayList) playsong2list);
        notificationManager.notify(856913, builder.build());
    }


    public void AddOrRemoveFavourite(View view) {
        SharedPreferences preferences = getActivity().getSharedPreferences("com.Thursday.FavouriteSharedPref", MODE_MULTI_PROCESS);
        if (preferences.contains((String.valueOf(ListElementsArrayList.indexOf(playsong2list.get(position)))))) {
            SharedPreferences.Editor remover = preferences.edit();
            remover.remove(String.valueOf(ListElementsArrayList.indexOf(playsong2list.get(position))));
            remover.apply();
            Favourite_buttont.setBackgroundResource(ic_baseline_favorite_border_24);
        } else {
            SharedPreferences.Editor adder = preferences.edit();
            adder.putString((String.valueOf(ListElementsArrayList.indexOf(playsong2list.get(position)))), String.valueOf(playsong2list.get(position)));
            adder.apply();
            Favourite_buttont.setBackgroundResource(ic_baseline_favorite_24);
        }
    }

    public void isitinfavouriteornot() {

        SharedPreferences preferences = getActivity().getSharedPreferences("com.Thursday.FavouriteSharedPref", MODE_MULTI_PROCESS);
        if (preferences.contains(String.valueOf(ListElementsArrayList.indexOf(playsong2list.get(position))))) {
            Favourite_buttont.setBackgroundResource(ic_baseline_favorite_24);
        } else Favourite_buttont.setBackgroundResource(ic_baseline_favorite_border_24);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_play_song2, null);
        play_pause = root.findViewById(R.id.Play_btn);
        Favourite_buttont = root.findViewById(R.id.Favourite_buttont);
        seekbar = root.findViewById(R.id.seekbar);
        album_art = root.findViewById(R.id.album_art);
        songname = root.findViewById(R.id.Songname);
        mAdviewed = root.findViewById(R.id.adviewed);
        controler = root.findViewById(R.id.controler);
        NextPlay = root.findViewById(R.id.Next_play);
        PreviousPlay = root.findViewById(R.id.previous_play);
        play_pause.setOnClickListener(this::PlayPause);
        NextPlay.setOnClickListener(this::Nextplay);
        PreviousPlay.setOnClickListener(this::previousplay);
        Favourite_buttont.setOnClickListener(this::AddOrRemoveFavourite);
        controler.setOnClickListener(this::Radiostatechecker);
        return root;
    }

    public void Radiostatechecker(View view) {
        SharedPreferences buttonpreferneces = getActivity().getSharedPreferences("looper single list", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = buttonpreferneces.edit();
        if (Shuffle == true && playwholelist == false && RepeatSameSong == false) {
            editor.putBoolean("Shuffle", false);
            editor.putBoolean("playwholelist", true);
            editor.putBoolean("RepeatSameSong", false);
            editor.apply();
            Shuffle = false;
            playwholelist = true;
            RepeatSameSong = false;
            Toast.makeText(getContext(), "Play All", Toast.LENGTH_SHORT).show();

            controler.setBackgroundResource(R.drawable.ic_baseline_repeat_24);
        } else if (Shuffle == false && playwholelist == true && RepeatSameSong == false) {
            editor.putBoolean("Shuffle", false);
            editor.putBoolean("playwholelist", false);
            editor.putBoolean("RepeatSameSong", true);
            editor.apply();
            Shuffle = false;
            playwholelist = false;
            RepeatSameSong = true;
            Toast.makeText(getContext(), "Repeat", Toast.LENGTH_SHORT).show();

            controler.setBackgroundResource(R.drawable.ic_baseline_repeat_one_24);
        } else if (Shuffle == false && playwholelist == false && RepeatSameSong == true) {
            editor.putBoolean("Shuffle", true);
            editor.putBoolean("playwholelist", false);
            editor.putBoolean("RepeatSameSong", false);
            editor.apply();
            Shuffle = true;
            playwholelist = false;
            RepeatSameSong = false;
            Toast.makeText(getContext(), "Shuffle", Toast.LENGTH_SHORT).show();

            controler.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);
        }

    }
}