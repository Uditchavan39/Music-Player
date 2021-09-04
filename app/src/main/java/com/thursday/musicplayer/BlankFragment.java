package com.thursday.musicplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;

import static android.content.Context.LOCATION_SERVICE;
import static com.thursday.musicplayer.MainActivity.adRequest;
import static com.thursday.musicplayer.playingbackgroundService.music;

public class BlankFragment extends Fragment {
    ViewGroup testing;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static final String TAG = "AudioFxDemo";
    private VeticalSeekbar bar;
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private Equalizer mEqualizer;
    private LinearLayout mLinearLayout;
    Spinner spinner_preset;
    private String[] music_style;
    boolean mSpinnerinitialized = false;

    private  AdView mAdView;

    public BlankFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
  if(mAdView!=null)
    mAdView.destroy();
    }

    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setupEqualizerFxAndUI();
        setingPreset();
        mEqualizer.setEnabled(true);
        Random random=new Random();
        int t=random.nextInt(5);
        if(t==1)
        mAdView.loadAd(adRequest);

        super.onViewCreated(view, savedInstanceState);
    }


    private void setupEqualizerFxAndUI() {

        mEqualizer = new Equalizer(0, music.getAudioSessionId());
        short bands = mEqualizer.getNumberOfBands();
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];
        for (short i = 0; i < bands; i++) {
            final short band = i;
            TextView freqTextView = new TextView(getActivity());
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setTextColor(Color.BLACK);
            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000) + " Hz");
            LinearLayout row = new LinearLayout(getActivity());
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            row.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 900);
            layoutParams.setMargins(0, 10, 0, 20);
            layoutParams.gravity=Gravity.CENTER_HORIZONTAL;

            bar = new VeticalSeekbar(getActivity());
            bar.setLayoutParams(layoutParams);
            bar.setId(band);
            bar.setThumb(getResources().getDrawable(R.drawable.ic_baseline_circle_24));
            bar.setMax(maxEQLevel - minEQLevel);
            bar.updateThumb();
            bar.setProgress(mEqualizer.getBandLevel(band));
            bar.offsetLeftAndRight(3);
            bar.setPadding(0, 10, 0, 10);
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                    bar.updateThumb();
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            row.addView(bar);
            row.addView(freqTextView);
            mLinearLayout.addView(row);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        testing = (ViewGroup) inflater.inflate(R.layout.fragment_blank, null);
        mLinearLayout = testing.findViewById(R.id.mlayout);
        spinner_preset = testing.findViewById(R.id.Spinner_preset);
        mAdView = testing.findViewById(R.id.adView);
        return testing;

    }

    public void setingPreset() {
        short m = mEqualizer.getNumberOfPresets();
        music_style = new String[m];
        for (int i = 0; i < m; i++) {
            music_style[i] = mEqualizer.getPresetName((short) i);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, music_style);
        spinner_preset.setAdapter(spinnerArrayAdapter);
        spinner_preset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mSpinnerinitialized) {
                    mSpinnerinitialized = true;
                    return;
                } else {
                    mEqualizer.usePreset((short) position);
                    short mini = mEqualizer.getBandLevelRange()[0];
                    for (short t = 0; t < mEqualizer.getNumberOfBands(); t++) {
                        VeticalSeekbar temp = testing.findViewById(t);
                        short to = mEqualizer.getBandLevel(t);
                        temp.setProgress(to - mini);
                        temp.updateThumb();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}