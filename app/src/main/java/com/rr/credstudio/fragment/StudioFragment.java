package com.rr.credstudio.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.rr.credstudio.FragmentPresenter;
import com.rr.credstudio.MainActivity;
import com.rr.credstudio.R;
import com.rr.credstudio.databinding.ActivityMainBinding;
import com.rr.credstudio.databinding.FragmentStudioBinding;
import com.rr.credstudio.model.Studio;
import com.rr.credstudio.viewModel.ShareViewModel;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StudioFragment extends Fragment {
    private ShareViewModel model;
    private FragmentStudioBinding binding;
    private MediaPlayer myMediaPlayer;
    private Timer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_studio, container, false);
        binding.setStudioVMFrag(model);
        binding.setPresenter(new FragmentPresenter() {
            @Override
            public void onPlayClick(Studio studio) {
                playSong(studio);
            }
        });

        binding.fragSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(ShareViewModel.class);
        model.getSelected().observe(this, studio -> {
            binding.fragSeekbar.setProgress(0);
            binding.setStudioFrag(studio);
            releaseResources();
            //Song Name as toolbar title
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(studio.getSong());

            playSong(studio);
        });

    }

    public void playSong(Studio studio) {
        if (myMediaPlayer == null) {
            myMediaPlayer = new MediaPlayer();
            myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                myMediaPlayer.setDataSource(studio.getUrl());
                myMediaPlayer.prepareAsync(); // prepare async to not block main thread
                //binding.fragSeekbar.setMax(myMediaPlayer.getDuration());
            } catch (IOException e) {
                Toast.makeText(getActivity(), "mp3 not found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            //mp3 will be started after completion of preparing...
            myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer myMediaPlayer) {
                    mediaAction();
                }
            });

        } else {
            mediaAction();
        }
    }

    private void mediaAction() {
        //Toast.makeText(getActivity(), "Player " + (myMediaPlayer.isPlaying() ? "pause" : "start"), Toast.LENGTH_SHORT).show();
        binding.fragSeekbar.setMax(myMediaPlayer.getDuration());
        binding.fragSeekbar.getMax();
        if (myMediaPlayer.isPlaying()) {
            binding.fragImgPlay.setImageDrawable(getResources().getDrawable(R.mipmap.play, null));
            myMediaPlayer.pause();
        } else {
            myMediaPlayer.start();
            binding.fragImgPlay.setImageDrawable(getResources().getDrawable(R.mipmap.pause, null));
            startProgressSeekbar();
        }
    }

    private void startProgressSeekbar() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                binding.fragSeekbar.setProgress(myMediaPlayer.getCurrentPosition());
            }
        }, 0, 1000);
    }

    private void releaseResources() {
        if (timer != null)
            timer.cancel();
        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseResources();
    }
}
