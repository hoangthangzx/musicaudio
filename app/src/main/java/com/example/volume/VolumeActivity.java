package com.example.volume;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
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
import com.example.model.Valume;
import com.example.selectaudio.Selectaudio2Activity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityVolumeBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

public class VolumeActivity extends AppCompatActivity implements VolumeAdapter.OnAudioControlListener {
    private MediaPlayer mediaPlayer;
    private ActivityVolumeBinding binding;
    private Equalizer equalizer;
    private VolumeAdapter mp3Adapter;
    private String currentlyPlayingUri=null;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateProgressRunnable;
    ArrayList<Valume> valumeList;
    private ArrayList<String> savedAudioPaths = new ArrayList<>();
private int valumeaudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityVolumeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        Intent intent = getIntent();
        ArrayList<AudioFile> audioFiles = intent.getParcelableArrayListExtra("audio_files_list");

        valumeList = new ArrayList<>();
        Valume valume = null;
        for (AudioFile audioFile : audioFiles) {
            valume = new Valume(audioFile.getUri(), audioFile.getName(), audioFile.getDuration());
            valumeList.add(valume);
        }
//        for (Valume v : valumeList) {
//            ValumeUntils.getInstance().addValume(v);
//        }
//        valumeList.clear();
//        valumeList=ValumeUntils.getInstance().getValumeList();
        mp3Adapter = new VolumeAdapter(valumeList, this, this);
        binding.recy.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recy.setAdapter(mp3Adapter);
        binding.imageView5.setOnClickListener(v -> {
            Intent valumec = new Intent(VolumeActivity.this, Selectaudio2Activity.class);
            valumec.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            valumec.putExtra("key", "valume");
            startActivity(valumec);
        });

        binding.valume.setMax(500);
        binding.valume.setProgress(0);

        binding.valume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(equalizer != null ) {
                        float gainInDb = (float) progress / 33;
                        updateEqualizerGain(gainInDb);
                    }
                    int percent = (progress * 100) / 100;
                        binding.textView11.setText(percent + "%");
                }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        binding.save.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mp3Adapter.resetAllToPause();
            }
            binding.main.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.post(() -> saveloading());
        });
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    int progress = (int) (((float) currentPosition / duration) * 100);
//                    mp3Adapter.updatePlaybackProgress(currentlyPlayingUri, progress);
                    handler.postDelayed(this, 100); // Update every 100ms
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent valume = new Intent(VolumeActivity.this, Selectaudio2Activity.class);
        valume.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        valume.putExtra("key", "valume");
        startActivity(valume);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        ArrayList<AudioFile> audioFiles = intent.getParcelableArrayListExtra("audio_files_list");

        valumeList = new ArrayList<>();
        Valume valume = null;
        for (AudioFile audioFile : audioFiles) {
            valume = new Valume(audioFile.getUri(), audioFile.getName(), audioFile.getDuration());
            valumeList.add(valume);
        }

        mp3Adapter = new VolumeAdapter(valumeList, this, this);
        binding.recy.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recy.setAdapter(mp3Adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mp3Adapter.resetAllToPause();
        }
    }

    @Override
    public void onPlaybackPositionChanged(Valume valume, int progress) {
        if (mediaPlayer != null && valume.getUri().equals(currentlyPlayingUri)) {
            int duration = mediaPlayer.getDuration();
            int newPosition = (progress * duration) / 100;
            mediaPlayer.seekTo(newPosition);
        }
    }
    private void saveloading() {
        saveModifiedAudio(mp3Adapter.getValumeList());

    }
    private void playAudio(Valume valume) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        stopProgressUpdates();

        try {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            mediaPlayer.setDataSource(valume.getUri());

            // Update UI immediately to show loading state
            currentlyPlayingUri = valume.getUri();
            mp3Adapter.setCurrentlyPlayingUri(currentlyPlayingUri);

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                new Thread(() -> {
                    initEqualizer();
                    runOnUiThread(() -> {

                        mp.start();
                        setupProgressUpdates();
                        float gainInDb = (float) binding.valume.getProgress() / 33;
                        updateEqualizerGain(gainInDb);
                    });
                }).start();

            });

            mediaPlayer.setOnCompletionListener(mp -> {

                stopProgressUpdates();
                currentlyPlayingUri = null;
                mp3Adapter.setCurrentlyPlayingUri(null);
                mp3Adapter.resetAllToPause();
                mp3Adapter.notifyDataSetChanged();
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_playing_audio, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupProgressUpdates() {
        // Stop any existing updates
        stopProgressUpdates();

        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        if (duration > 0) {
                            int progress = (int) (((float) currentPosition / duration) * 100);
                            // Update adapter with new progress
                            if (currentlyPlayingUri != null) {
                                mp3Adapter.updatePlaybackProgress(currentlyPlayingUri, progress);

                            }
                        }
                        // Schedule next update
                        handler.postDelayed(this, 100); // ~60fps for smooth updates
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // Start updates
        handler.post(updateProgressRunnable);
    }
    private File getOutputPath() {
        // Define the directory where you want to save the output file
        File outputDir = new File(getExternalFilesDir(null), "Volume"); // Correct the directory name

        // Create the directory if it doesn't exist
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir; // Return the directory File
    }


    private void stopProgressUpdates() {
        if (handler != null && updateProgressRunnable != null) {
            handler.removeCallbacks(updateProgressRunnable);
            updateProgressRunnable = null;
        }
    }
    @Override
    public void onAudioPlayClick(Valume valume) {
        mp3Adapter.setCurrentlyPlayingUri(valume.getUri());
        if (mediaPlayer != null) {
            mediaPlayer.release();  // Release any previously playing media
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(valume.getUri());
            mediaPlayer.prepare();
            mediaPlayer.start();

            currentlyPlayingUri = valume.getUri();  // Update currently playing URI
            mp3Adapter.setCurrentlyPlayingUri(valume.getUri());  // Update adapter state
            float gainInDb = (float) binding.valume.getProgress() / 33;
            updateEqualizerGain(gainInDb);
            setupProgressUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioPauseClick(Valume valume) {

    }


    @Override
    public void onAudioPlayPauseClick(Valume valume) {
        if (mediaPlayer != null && mediaPlayer.isPlaying() && valume.getUri().equals(currentlyPlayingUri)) {
            mp3Adapter.setPlaying(false); // Update adapter state
            mediaPlayer.pause();
            stopProgressUpdates(); // Stop the progress updates

            float gainInDb = (float) binding.valume.getProgress() / 33;
            updateEqualizerGain(gainInDb);
;
        } else if (mediaPlayer != null && valume.getUri().equals(currentlyPlayingUri)) {
            mp3Adapter.setPlaying(true);
            currentlyPlayingUri = valume.getUri();
            mediaPlayer.start();
            setupProgressUpdates();

            float gainInDb = (float) binding.valume.getProgress() / 33;
            updateEqualizerGain(gainInDb);

        } else {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mp3Adapter.setPlaying(true);
            playAudio(valume);
            currentlyPlayingUri = valume.getUri(); // Set the new URI

            float gainInDb = (float) binding.valume.getProgress() / 33;
            updateEqualizerGain(gainInDb);
        }
        mp3Adapter.notifyDataSetChanged(); // Refresh UI in the adapter
    }


    private void initEqualizer() {
        if (equalizer != null) {
            equalizer.release();
        }
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(true);
    }

    private void updateEqualizerGain(float gainInDb) {
        if (equalizer != null && equalizer.getEnabled()) {
            short numberOfBands = equalizer.getNumberOfBands();
            for (short i = 0; i < numberOfBands; i++) {
                equalizer.setBandLevel(i, (short) (gainInDb * 100));
            }
        } else {
            Log.e("Equalizer Error", "Equalizer chưa được khởi tạo hoặc không khả dụng.");
        }

    }



    @Override
    public void onVolumeChanged(Valume valume, int volume) {
//        valume.setVolume(volume);
        if (valume.getUri().equals(currentlyPlayingUri) && equalizer != null) {
            float gainInDb = (float) volume / 33;
            updateEqualizerGain(gainInDb);
        }
    }


    private void saveModifiedAudio(ArrayList<Valume> valumeList) {
        new Thread(() -> {
            ArrayList<String> savedAudioPaths = new ArrayList<>();
            for (Valume valume : valumeList) {
                float gainInDb = (float) binding.valume.getProgress() / 33;
                System.out.println("Processing file: " + valume.getName() + valume.getVolume() + " with gain: " + gainInDb + "dB");

                File originalFile = new File(valume.getUri());
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
                    boolean success = processAndSaveAudio(valume.getUri(), outputFile.getAbsolutePath(), gainInDb);
                    if (success) {
                        savedAudioPaths.add(outputFile.getAbsolutePath());
                    } else {
                        // Handle failure if necessary
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(VolumeActivity.this, getString(R.string.error_saving_modified_audio_for) + originalFileName + " - " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            runOnUiThread(() -> {
                for (String path : savedAudioPaths) {
                    Log.d("Saved Audio Path", path);
                }

                Intent mp3cutter = new Intent(VolumeActivity.this, SuccesallActivity.class);
                mp3cutter.putExtra("key", "Valume");
                mp3cutter.putStringArrayListExtra("outputAudioPaths", savedAudioPaths);
                startActivity(mp3cutter);

                binding.main.setVisibility(View.VISIBLE);
                binding.loading.setVisibility(View.GONE);

                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (equalizer != null) {
                    equalizer.release();
                    equalizer = null;
                }

                mp3Adapter.clearData();
                handler.removeCallbacksAndMessages(null);
            });
        }).start();
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

    private String[] getFFmpegParams(String format, String volumeFilter) {
        ArrayList<String> cmdList = new ArrayList<>();
        cmdList.add("-i");
        cmdList.add(INPUT_PLACEHOLDER); // Will be replaced with actual input path
        cmdList.add("-af");
        cmdList.add("volume=" + volumeFilter);
        cmdList.add("-map");
        cmdList.add("0:a");

        // Định cấu hình encoder và params tùy theo định dạng
        switch (format) {
            case "mp3":
                cmdList.add("-c:a");
                cmdList.add("libmp3lame");
                cmdList.add("-q:a");
                cmdList.add("2");
                break;
            case "wav":
                cmdList.add("-c:a");
                cmdList.add("pcm_s16le");
                break;
            case "aac":
                cmdList.add("-c:a");
                cmdList.add("aac");
                cmdList.add("-b:a");
                cmdList.add("192k");
                break;
            case "m4a":
                cmdList.add("-c:a");
                cmdList.add("aac");
                cmdList.add("-b:a");
                cmdList.add("192k");
                break;
            case "ogg":
                cmdList.add("-c:a");
                cmdList.add("libvorbis");
                cmdList.add("-q:a");
                cmdList.add("4");
                break;
            case "flac":
                cmdList.add("-c:a");
                cmdList.add("flac");
                cmdList.add("-compression_level");
                cmdList.add("5");
                break;
            default:
                // Mặc định sử dụng MP3 nếu không xác định được định dạng
                cmdList.add("-c:a");
                cmdList.add("libmp3lame");
                cmdList.add("-q:a");
                cmdList.add("2");
                break;
        }

        cmdList.add(OUTPUT_PLACEHOLDER); // Will be replaced with actual output path

        return cmdList.toArray(new String[0]);
    }

    private boolean processAndSaveAudio(String inputPath, String outputPath, float gainInDb) {
        try {
            // Tính toán giá trị volume filter
            String volumeFilter = String.format(Locale.US, "%.2f", Math.pow(10, gainInDb / 20.0));

            // Xác định định dạng file đầu vào
            String format = getAudioFormat(inputPath);
            if (format.equals("unknown")) {
                System.err.println("Unsupported audio format: " + inputPath);
                return false;
            }

            // Lấy các tham số FFmpeg dựa trên định dạng
            String[] cmdTemplate = getFFmpegParams(format, volumeFilter);

            // Tạo mảng lệnh cuối cùng với đường dẫn thực tế
            String[] cmd = new String[cmdTemplate.length];
            for (int i = 0; i < cmdTemplate.length; i++) {
                if (cmdTemplate[i].equals(INPUT_PLACEHOLDER)) {
                    cmd[i] = inputPath;
                } else if (cmdTemplate[i].equals(OUTPUT_PLACEHOLDER)) {
                    cmd[i] = outputPath;
                } else {
                    cmd[i] = cmdTemplate[i];
                }
            }

            // Thực thi lệnh FFmpeg
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

    // Constants for placeholder values
    private static final String INPUT_PLACEHOLDER = "##INPUT##";
    private static final String OUTPUT_PLACEHOLDER = "##OUTPUT##";
    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
        mp3Adapter.clearData();
        handler.removeCallbacksAndMessages(null);
    }

}