package com.thursday.musicplayer;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.thursday.musicplayer.MainActivity.svc;
import static com.thursday.musicplayer.PlaySong2.RepeatSameSong;
import static com.thursday.musicplayer.PlaySong2.Shuffle;
import static com.thursday.musicplayer.PlaySong2.playwholelist;
import static com.thursday.musicplayer.R.drawable.ic_baseline_pause_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_play_arrow_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_skip_next_24;
import static com.thursday.musicplayer.R.drawable.ic_baseline_skip_previous_24;


public class playingbackgroundService extends IntentService {
    public playingbackgroundService() {
        super("playingbackgroundService");
    }

    public static MediaPlayer music = new MediaPlayer();
    public static int poso;
    BroadcastReceiver myReceiver;
    IntentFilter receiver = new IntentFilter();
    static PowerManager.WakeLock wakeLock;
    private final List<String> playinglist = new ArrayList<>();
    private final List<String> shufflelist = new ArrayList<>();
    String TAG = "tagging";
    public static int t = 0;
    public static NotificationCompat.Builder builder;
    private final int NOTIFICATION_ID = 856913;

    public static NotificationManager notificationManager;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        receiverlistener();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    notificationManager.cancelAll();
    stopForeground(true);
    }

    public void receiverlistener() {
        receiver.addAction("playpause");
        receiver.addAction("next");
        receiver.addAction("previous");
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == "playpause") {
                    if (music != null && music.isPlaying()) {
                        music.pause();
                    } else if(music!=null){
                        music.start();
                    }

                } else if (intent.getAction() == "next") {
                    poso = ((poso + 1) % playinglist.size());
                    svc = new Intent(getApplicationContext(), playingbackgroundService.class);
                    svc.putExtra("positionideal", poso);
                    svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) playinglist);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getApplicationContext().startForegroundService(svc);
                    }

                } else if (intent.getAction() == "previous") {
                    poso = ((poso - 1) < 0) ? (playinglist.size() - 1) : (poso - 1);
                    svc = new Intent(getApplication(), playingbackgroundService.class);
                    svc.putExtra("positionideal", poso);
                    svc.putStringArrayListExtra("playinglist#4321", (ArrayList<String>) playinglist);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getApplicationContext().startForegroundService(svc);
                    }

                }
                showNotification(getApplicationContext(), poso, (ArrayList) playinglist);
            startForeground(856913,builder.build());
            }

        };
        getApplicationContext().registerReceiver(myReceiver, receiver);

    }

    public static void showNotification(Context getApplicationContext, int poso, ArrayList playinglist) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext.getContentResolver(), PlaySong2.geturi(poso, playinglist));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent playIntent = new Intent("playpause");
        Intent nextIntent = new Intent("next");
        Intent previousIntent = new Intent("previous");
        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext,
                0, new Intent(getApplicationContext, MainActivity.class), 0);
        int plus;
        if (music.isPlaying()) {
            plus = ic_baseline_pause_24;
        } else {
            plus = ic_baseline_play_arrow_24;
        }
        builder =
                new NotificationCompat.Builder(getApplicationContext)
                        .setSmallIcon(R.mipmap.icon_d)
                        .setContentTitle((CharSequence) playinglist.get(poso))
                        .setLargeIcon(bitmap)
                        .setOngoing(true)
                        .setContentIntent(contentPendingIntent)
                        .setContentText("Next: " + playinglist.get((poso + 1) % playinglist.size()))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0))
                        .addAction(ic_baseline_skip_previous_24, "previous", PendingIntent.getBroadcast(getApplicationContext, 12596, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .addAction(plus, "playpause", PendingIntent.getBroadcast(getApplicationContext, 125989, playIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .addAction(ic_baseline_skip_next_24, "next", PendingIntent.getBroadcast(getApplicationContext, 1259678, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT));

       notificationManager =
                (NotificationManager) getApplicationContext.getSystemService(NOTIFICATION_SERVICE);
        String ChannelId = "1562";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ChannelId, "Human Readable Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(ChannelId);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        poso = intent.getIntExtra("positionideal", 0);
        playinglist.clear();
        playinglist.addAll(intent.getStringArrayListExtra("playinglist#4321"));
        shufflelist.addAll(playinglist);
        Collections.shuffle(shufflelist);
        t = 1;
        int currentposition = intent.getIntExtra("currentposition", -1);
        playetheselectedsong(poso, currentposition);
        showNotification(getApplicationContext(), poso, (ArrayList) playinglist);
        startForeground(NOTIFICATION_ID, builder.build());
        return Service.START_NOT_STICKY;
    }

    public void playetheselectedsong(int position, int currentposition) {

        if (music != null && music.isPlaying()) {
            music.stop();
            music.reset();
            music.release();
        }


        if (Shuffle == true && t != 1) {
            Intent local = new Intent();
            local.putExtra("imaged", shufflelist.get(position));
            local.setAction("com.hello.Action");
            getApplication().sendBroadcast(local);
            position = MainActivity.ListElementsArrayList.indexOf(shufflelist.get(position));
            t = 0;
            SharedPreferences sharedPreferences = getSharedPreferences("final.temopray.forimages", MODE_MULTI_PROCESS);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("fortheimages", position);
            edit.apply();
            } else {
            Intent local = new Intent();
            local.putExtra("imaged", playinglist.get(position));
            local.setAction("com.hello.Action");
            getApplication().sendBroadcast(local);
            position = MainActivity.ListElementsArrayList.indexOf(playinglist.get(position));
            }
        Uri uri = Uri.parse(MainActivity.audioFilePathList.get(position));
        music = MediaPlayer.create(getApplicationContext(), uri);
        if (currentposition != -1) {
            music.seekTo(currentposition);
        }

        music.start();
        completelistenr();
if(Shuffle==false) {
    showNotification(getApplicationContext(), poso, (ArrayList) playinglist);
    startForeground(856913, builder.build());
}
else {
    showNotification(getApplicationContext(),poso, (ArrayList) shufflelist);
    startForeground(856913,builder.build());

}
    }

    public void completelistenr() {

        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (RepeatSameSong == true) {

                }
                if (playwholelist == true) {
                    poso = ((poso + 1) % playinglist.size());
                }
                if (Shuffle == true) {
                    poso = ((poso + 1) % playinglist.size());
                    Intent local = new Intent();
                    local.putExtra("imaged", shufflelist.get(poso));
                    local.setAction("com.hello.Action");
                    getApplication().sendBroadcast(local);
                    t = 0;
                }
                if (Shuffle==false) {
                    SharedPreferences sharedPreferences = getSharedPreferences("final.temopray.forimages", MODE_MULTI_PROCESS);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("fortheimages", poso);
                    edit.apply();
                    Intent local = new Intent();
                    local.putExtra("imaged", playinglist.get(poso));
                    local.setAction("com.hello.Action");
                    getApplication().sendBroadcast(local);
                    t = 1;
                }
                playetheselectedsong(poso, -1);
            }
        });
    }

    @Override
    public void onDestroy() {
if(music!=null) {
    music.stop();
    music.reset();
    music.release();
    music = null;
}
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        stopForeground(true);
    }

    @Override
    public void onLowMemory() {
        Toast.makeText(getApplicationContext(), "Memory is not Sufficient.", Toast.LENGTH_SHORT).show();
    }

}