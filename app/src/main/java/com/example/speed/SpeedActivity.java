package com.example.speed;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.SuccesallActivity;
import com.example.model.AudioFile;
import com.example.model.Speed;
import com.example.selectaudio.Selectaudio2Activity;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivitySpeedBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SpeedActivity extends AppCompatActivity implements speedAdapter.OnAudioControlListener {
    private ExoPlayer exoPlayer;
    private ActivitySpeedBinding binding;
    private speedAdapter speedAdapter;
    private String currentlyPlayingUri = null;
    private ArrayList<Speed> speedList;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateProgressRunnable;
    private static final float MIN_SPEED = 0.5f;
    private static final float MAX_SPEED = 5.0f;
    private static final int DEFAULT_SPEED_PROGRESS = 110; // 1.0x speed
    private static final int MAX_PROGRESS = 600; // Maximum progress for 3.0x speed
    private ArrayList<String> savedAudioPaths = new ArrayList<>();
   private  int speedaudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivitySpeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        Intent intent = getIntent();
        ArrayList<AudioFile> audioFiles = intent.getParcelableArrayListExtra("audio_files_list");

        speedList = new ArrayList<>();
        for (AudioFile audioFile : audioFiles) {
            Speed speed = new Speed(audioFile.getUri(), audioFile.getName(), audioFile.getDuration());
            speedList.add(speed);
        }

        speedAdapter = new speedAdapter(speedList, this, this);
        binding.recy.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recy.setAdapter(speedAdapter);

        binding.imageView5.setOnClickListener(v ->  {
            Intent a = new Intent(SpeedActivity.this, Selectaudio2Activity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            a.putExtra("key", "valume");
            startActivity(a);
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                exoPlayer.stop();
                speedAdapter.setCurrentlyPlayingUri(null);
            }
finish();
        });

        initializeExoPlayer();
        setupSaveButton();
        binding.thumb.setMax(MAX_PROGRESS);
        binding.thumb.setProgress(DEFAULT_SPEED_PROGRESS);
        binding.thumb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Map the progress to a valid speed range (0.1f - 5.0f)
                float speed = 0.1f + (progress / 100.0f);
                updatePlaybackSpeed(speed);
                speedaudio = progress;
                setSpeedByUri(currentlyPlayingUri,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    long currentPosition = exoPlayer.getCurrentPosition();
                    long duration = exoPlayer.getDuration();
                    if (duration > 0) {
                        int progress = (int) (((float) currentPosition / duration) * 100);
                        if (currentlyPlayingUri != null) {
//                            speedAdapter.updatePlaybackProgress(currentlyPlayingUri, progress);
                        }
                    }
                    handler.postDelayed(this, 16); // ~60fps for smooth updates
                }
            }
        };
        handler.post(updateProgressRunnable);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
        speedAdapter.resetAllToPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        ArrayList<AudioFile> audioFiles = intent.getParcelableArrayListExtra("audio_files_list");

        speedList = new ArrayList<>();
        for (AudioFile audioFile : audioFiles) {
            Speed speed = new Speed(audioFile.getUri(), audioFile.getName(), audioFile.getDuration());
            speedList.add(speed);
        }

        speedAdapter = new speedAdapter(speedList, this, this);
        binding.recy.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recy.setAdapter(speedAdapter);
        speedAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        Intent mixer = new Intent(SpeedActivity.this, Selectaudio2Activity.class);
        mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mixer.putExtra("key", "speed");
        startActivity(mixer);

    }
    private void updatePlaybackSpeed(float speed) {
        // Clamp speed value between 0.1f and 5.0f
        float clampedSpeed = Math.max(0.1f, Math.min(speed, 5.0f));
        Log.d("TAG", "updatePlaybackSpeed: "+clampedSpeed);
        // Set the playback speed using the clamped value
        PlaybackParameters params = new PlaybackParameters(clampedSpeed);
        exoPlayer.setPlaybackParameters(params);
    }

    private void initializeExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
//                    stopProgressUpdates();
                    currentlyPlayingUri = null;
                    speedAdapter.setCurrentlyPlayingUri(null);
                    speedAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setupSpeedSeekBar() {
        binding.thumb.setMax(MAX_PROGRESS);
binding.thumb.setProgress(DEFAULT_SPEED_PROGRESS);
binding.thumb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float speed = (float) progress / 100;
                    updatePlaybackSpeed(speed);
                    setSpeedByUri(currentlyPlayingUri, progress);
                    int percent = (progress * 100) / MAX_PROGRESS;
                    // Update UI to show current speed

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupSaveButton() {
        binding.save.setOnClickListener(v -> {
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                exoPlayer.pause();
            }
            speedAdapter.resetAllToPause();
            binding.main.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.post(this::saveLoading);
        });
    }
    private void setSpeedByUri(String uri, int speedProgress) {
        for (Speed speed : speedList) {
            if (speed.getUri().equals(uri)) {
                speed.setSpeed(speedProgress);
                Log.d("SpeedUpdate", "Set speed for URI: " + uri + " to: " + speedProgress);
                return;
            }
        }
        Log.e("SpeedUpdate", "No speed found for URI: " + uri);
    }

    private void playAudio(String speed) {
        setupProgressUpdates();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
        }
//
//        Uri uri = Uri.parse(speed.getUri());
        MediaItem mediaItem = MediaItem.fromUri(speed);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        currentlyPlayingUri = speed;
        speedAdapter.setCurrentlyPlayingUri(currentlyPlayingUri);
        setupProgressUpdates();

        // Set the speed based on the current seekbar progress
        float currentSpeed = binding.thumb.getProgress() / 100f;
        updatePlaybackSpeed(currentSpeed);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED || state == Player.STATE_IDLE) {
                    speedAdapter.resetAllToPause();
                }
            }
        });
    }

    private void setupProgressUpdates() {
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null) {

                    long currentPosition = exoPlayer.getCurrentPosition();
                    long duration = exoPlayer.getDuration();
                    if (duration > 0) {
                        int progress = (int) (((float) currentPosition / duration) * 100);
                        if (currentlyPlayingUri != null) {
                            speedAdapter.updatePlaybackProgress(currentlyPlayingUri, progress);
                        }
                    }
                    handler.postDelayed(this, 16); // ~60fps for smooth updates
                }
            }
        };
        handler.post(updateProgressRunnable);
    }

//    private void stopProgressUpdates() {
//        if (handler != null && updateProgressRunnable != null) {
//            handler.removeCallbacks(updateProgressRunnable);
//        }
//    }
    @Override
    public void onAudioPauseClick(Speed speed) {
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();  // Pause the audio
//            stopProgressUpdates();  // Stop progress updates
            speedAdapter.setCurrentlyPlayingUri(null);  // Clear playing URI in adapter
        }
    }


    @Override
    public void onAudioPlayClick(Speed speed) {
        setupProgressUpdates();
        if (exoPlayer != null) {
            exoPlayer.release();  // Release any previously playing media
        }

        try {
            exoPlayer = new ExoPlayer.Builder(this).build();
            MediaItem mediaItem = MediaItem.fromUri(speed.getUri());
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();  // Start playing the audio
            setupProgressUpdates();
            currentlyPlayingUri = speed.getUri();  // Update currently playing URI
            speedAdapter.setCurrentlyPlayingUri(speed.getUri());  // Update adapter state

            // Log currently playing URI and its corresponding volume
            Log.d("AudioPlayClick", "Currently playing URI: " + currentlyPlayingUri);
            Log.d("AudioPlayClick", "Volume (volumedb) for this URI: " + speed.getSpeed());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAudioPlayPauseClick(String speed) {
        if (exoPlayer == null) {
            initializeExoPlayer();
        }
        if (exoPlayer.isPlaying() && speed.equals(currentlyPlayingUri)) {
            speedAdapter.resetAllToPause();
            ((Activity) this).runOnUiThread(() -> {
                exoPlayer.pause();
            });


        } else if (speed.equals(currentlyPlayingUri)) {
            exoPlayer.play();

            setupProgressUpdates();

        } else {

            playAudio(speed);
            setupProgressUpdates();

        }
        setupProgressUpdates();
        speedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlaybackPositionChanged(Speed speed, int progress) {
        setupProgressUpdates();
        if (exoPlayer != null && speed.getUri().equals(currentlyPlayingUri)) {
            long duration = exoPlayer.getDuration();
            long newPosition = (progress * duration) / 100;
            exoPlayer.seekTo(newPosition);
            setupProgressUpdates();
        }
    }

    @Override
    public void onSpeedChanged(Speed speed, int speedProgress) {
        speed.setSpeed(speedProgress);
        if (speed.getUri().equals(currentlyPlayingUri)) {
            float newSpeed = speedProgress / 100f;
            updatePlaybackSpeed(newSpeed);
        }
        setupProgressUpdates();
    }

    private void saveLoading() {
        saveModifiedAudio(speedList);
    }

    private void saveModifiedAudio(ArrayList<Speed> speedList) {
        new Thread(() -> {
            ArrayList<String> savedAudioPaths = new ArrayList<>();
            for (Speed speed : speedList) {
                int progress = speedaudio;
                float speedFactor = progress / 100f;
                Log.d("TAG", "saveModifiedAudio: " + progress);
                System.out.println("Processing file: " + speed.getName() + " with speed factor: " + speedFactor);

                File originalFile = new File(speed.getUri());
                String originalFileName = originalFile.getName();
                File outputDirectory = getOutputPath();
                File outputFile = new File(outputDirectory, originalFileName);
                int count = 1;

                while (outputFile.exists()) {
                    String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                    String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                    outputFile = new File(outputDirectory, fileNameWithoutExtension + "_" + count + extension);
                    count++;
                }

                try {
                    boolean success = processAndSaveAudio(speed.getUri(), outputFile.getAbsolutePath(), speedFactor);
                    if (success) {
                        savedAudioPaths.add(outputFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(SpeedActivity.this, "Error saving modified audio for: " + originalFileName + " - " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            runOnUiThread(() -> {
                for (String path : savedAudioPaths) {
                    Log.d("Saved Audio Path", path);
                }

                Intent mp3cutter = new Intent(SpeedActivity.this, SuccesallActivity.class);
                mp3cutter.putExtra("key", "speed");
                mp3cutter.putStringArrayListExtra("outputAudioPaths", savedAudioPaths);
                startActivity(mp3cutter);

                binding.main.setVisibility(View.VISIBLE);
                binding.loading.setVisibility(View.GONE);

                if (exoPlayer != null) {
                    exoPlayer.release();
                    exoPlayer = null;
                }

                speedAdapter.clearData();
                handler.removeCallbacksAndMessages(null);
            });
        }).start();
    }

    private File getOutputPath() {
        File outputDir = new File(getExternalFilesDir(null), "Speed"); // Correct the directory name

        // Create the directory if it doesn't exist
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir; // Return the directory File
    }
    private boolean processAndSaveAudio(String inputPath, String outputPath, float speedFactor) {
        try {
            // Calculate the atempo filter value
            // Note: FFmpeg's atempo filter accepts values between 0.5 and 2.0
            // For values outside this range, we need to chain multiple atempo filters
            String atempoFilter = getAtempoFilter(speedFactor);

            // Get the audio format
            String format = getAudioFormat(inputPath);
            if (format.equals("unknown")) {
                System.err.println("Unsupported audio format: " + inputPath);
                return false;
            }

            // Prepare FFmpeg command
            String[] cmd = {
                    "-i", inputPath,
                    "-filter:a", atempoFilter,
                    "-c:a", getAudioCodec(format),
                    "-y", // Overwrite output file if it exists
                    outputPath
            };

            // Execute FFmpeg command
            int rc = FFmpeg.execute(cmd);
            if (rc == RETURN_CODE_SUCCESS) {
                System.out.println("FFmpeg process completed successfully for file: " + outputPath);
                return true;
            } else {
                System.err.println("FFmpeg process failed with return code: " + rc + " for file: " + outputPath);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("FFmpeg processing error: " + e.getMessage());
            return false;
        }
    }
    private String getAtempoFilter(float speedFactor) {
        if (speedFactor == 1.0f) {
            return "atempo=1.0";
        }

        StringBuilder filterBuilder = new StringBuilder();
        float remainingFactor = speedFactor;
        int maxIterations = 100; // Prevent infinite loops
        int iterations = 0;

        while ((remainingFactor < 0.5f || remainingFactor > 5.0f) && iterations < maxIterations) {
            if (remainingFactor < 0.5f) {
                filterBuilder.append("atempo=0.5,");
                remainingFactor /= 0.5f;
            } else {
                filterBuilder.append("atempo=2.0,");
                remainingFactor /= 5.0f;
            }
            iterations++;
        }

        if (iterations == maxIterations) {
            // Handle extreme cases
            return "atempo=1.0"; // Or throw an exception
        }

        filterBuilder.append("atempo=").append(String.format(Locale.US, "%.2f", remainingFactor));
        return filterBuilder.toString();
    }
    private String getAudioFormat(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        switch (extension) {
            case ".mp3":
                return "mp3";
            case ".wav":
                return "wav";
            case ".aac":
                return "aac";
            case ".m4a":
                return "m4a";
            case ".ogg":
                return "ogg";
            case ".flac":
                return "flac";
            default:
                return "unknown";
        }
    }

    private String getAudioCodec(String format) {
        switch (format) {
            case "mp3":
                return "libmp3lame";
            case "aac":
            case "m4a":
                return "aac";
            case "ogg":
                return "libvorbis";
            case "flac":
                return "flac";
            default:
                return "copy"; // Use "copy" for formats like wav or unknown formats
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        speedAdapter.clearData();
        handler.removeCallbacksAndMessages(null);
    }
}