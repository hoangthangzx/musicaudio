package com.example.audiotex;

import static com.example.ultils.SystemUtils.setLocale;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityAudiotexBinding;
import com.example.ultils.Untils;

import java.io.IOException;

import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
public class AudiotexActivity extends AppCompatActivity {
    ActivityAudiotexBinding binding;
    private MediaPlayer mediaPlayer;
    private String audioPath;
    private String audioname;
    private String audiotime;
    private Handler handler = new Handler();
    private Runnable updateThumbRunnable;
    private long lastToastTime = 0;
    private static final long TOAST_DELAY = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudiotexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
Untils.showSystemUI(this, true);
        setLocale(this);
        audioPath = getIntent().getStringExtra("audioPath");

        if (audioPath != null) {
            setupMediaPlayer();
            binding.waveformSeekBar.setSampleFrom(audioPath);
        } else {
            Log.e("AudiotexActivity", "audioPath is null.");
        }

        binding.music.setOnClickListener(v -> {
            playAudio();
        });
        binding.waveformSeekBar.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
            if (fromUser && mediaPlayer != null) {
                int newPosition = (int) (progress * mediaPlayer.getDuration() / 100f);
                mediaPlayer.seekTo(newPosition);
                updateCurrentTimeDisplay(newPosition);
            }
        });

        // Set OnSeekBarChangeListener for SeekBarSetSizeThumb4
        binding.sbhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    int newPosition = (int) (progress * mediaPlayer.getDuration() / 100f);
                    mediaPlayer.seekTo(newPosition);
                    binding.waveformSeekBar.setProgress(progress);
                    updateCurrentTimeDisplay(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optionally stop updating the waveform while dragging
                stopUpdatingThumb();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Restart updating when the user stops dragging
                startUpdatingThumb();
            }
        });

        try {
            // Read MP3 file
            Mp3File mp3file = new Mp3File(audioPath);
            if (mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                String lyrics = id3v2Tag.getLyrics(); // Get lyrics from metadata

                if (lyrics != null && !lyrics.isEmpty()) {
                    // Display lyrics in TextView
                    binding.tex.setText(lyrics);
                } else {
                    binding.tex.setText(R.string.no_lyrics_found_in_this_mp3_file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.tex.setText("Error reading MP3 file.");
        }

        // Waveform SeekBar configuration
        binding.waveformSeekBar.setWaveWidth(Utils.dp(this, 1));
        binding.waveformSeekBar.setWaveGap(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveMinHeight(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveCornerRadius(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        binding.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(this, R.color.white2));
        binding.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(this, R.color.progress_start));

        binding.tex.setMovementMethod(new ScrollingMovementMethod());
        binding.imageView5.setOnClickListener(v -> {
            finish();
        });

        // Get additional data
        audioname = getIntent().getStringExtra("audioname");
        audiotime = getIntent().getStringExtra("audiodur");
        binding.textView17.setText(audioname);
        binding.time2.setText(audiotime);
        binding.imageView8.setOnClickListener(v -> {
            // Kiểm tra thời gian giữa các lần hiển thị Toast
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastToastTime < TOAST_DELAY) {
                return;
            }
            lastToastTime = currentTime;

            String lyrics = binding.tex.getText().toString();
            if (!lyrics.isEmpty()) {
                // Copy vào clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Lyrics", lyrics);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.lyrics_c_sao_ch_p, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.kh_ng_c_lyrics_sao_ch_p, Toast.LENGTH_SHORT).show();
            }
        });

        updateSongTitle(audioname);
    }
    private void startUpdatingThumb() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            updateThumbRunnable = new Runnable() {
                @Override
                public void run() {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    if (duration > 0) {
                        float progress = (float) currentPosition / duration * 100;
                        binding.waveformSeekBar.setProgress((int) progress);
                        binding.sbhz.setProgress((int) progress);  // Update the SeekBarSetSizeThumb4
                    }

                    updateCurrentTimeDisplay(currentPosition);
                    handler.postDelayed(this, 50);
                }
            };
            handler.post(updateThumbRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopUpdatingThumb();
            binding.music.setImageResource(R.drawable.paush);
        }
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                stopUpdatingThumb();
                binding.music.setImageResource(R.drawable.paush);
            } else {
                mediaPlayer.start();
                startUpdatingThumb();
                binding.music.setImageResource(R.drawable.playxanh);
            }
        }
    }

    private void stopUpdatingThumb() {
        if (updateThumbRunnable != null) {
            handler.removeCallbacks(updateThumbRunnable);
        }
    }

    private void updateCurrentTimeDisplay(int currentPosition) {
        String currentTime = formatTime(currentPosition);
        binding.time1.setText(currentTime);
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopUpdatingThumb();
    }
    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            Uri audioUri = Uri.parse(audioPath);
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                binding.music.setImageResource(R.drawable.paush);
                stopUpdatingThumb();
                binding.waveformSeekBar.setProgress(0);
                binding.sbhz.setProgress(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSongTitle(String songTitle) {
        // Nếu độ dài vượt quá 20 ký tự thì cắt bớt và thêm "..."
        if (songTitle.length() > 20) {
            songTitle = songTitle.substring(0, 20) + "...";
        }
        // Cập nhật TextView với tên bài hát
        binding.textView17.setText(songTitle);
    }



}