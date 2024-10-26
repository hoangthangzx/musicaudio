package com.example.equazer;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.example.ultils.Untils.showSystemUI;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;

import com.arthenica.mobileffmpeg.FFmpeg;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.SuccesActivity;
import com.example.customview.SeekBarSetSizeThumb;
//import com.example.merge.MergeActivity;
import com.example.model.HomeItem;
import com.example.selectaudio.SlectaudioActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityEquazerBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogCreatBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogoutBinding;
import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class equazerActivity extends AppCompatActivity {
    ActivityEquazerBinding binding;
    equazeradapter equazeradapter;
    private String audioPath;
    private String name;
    private String time;
    private Equalizer equalizer;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateThumbRunnable;
    private short[] currentBandLevels = new short[]{0, 0, 0, 0, 0};
    SeekBarSetSizeThumb equazer5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSystemUI(this, true);
        binding = ActivityEquazerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        binding.recy.setLayoutManager(gridLayoutManager);

        equazeradapter = new equazeradapter(this, getItems(), this::onItemClicked);
        binding.recy.setAdapter(equazeradapter);

        audioPath = getIntent().getStringExtra("audioPath");
        time = getIntent().getStringExtra("audiodur");
        name = getIntent().getStringExtra("audioname");

        binding.load.setEnabled(false);
        binding.imageView10.setOnClickListener(v -> {
            playAudio();
        });
        updateSongTitle(name);
binding.time2.setText(time);
        currentBandLevels = new short[]{0, 0, 0, 0, 0};
        if (audioPath != null) {
            setupMediaPlayer(); // Initialize MediaPlayer
            setupEqualizer(); // Initialize Equalizer after MediaPlayer
            setupSeekBars();
        } else {
            Log.e("equazerActivity", "audioPath is null.");
        }
        binding.waveformSeekBar.setSampleFrom(audioPath);
        binding.waveformSeekBar.setWaveWidth(Utils.dp(this, 1));
        binding.waveformSeekBar.setWaveGap(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveMinHeight(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveCornerRadius(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        binding.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(this, R.color.white2));
        binding.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(this, R.color.progress_start));
        equazeradapter.setSelectedItemName("Custom");
binding.load.setOnClickListener(v->{
    currentBandLevels = new short[]{0, 0, 0, 0, 0};
    equazeradapter.setSelectedItemName("Custom");
    binding.thumb.setProgress(0);
    setEqualizerBandLevels(currentBandLevels);
    updateSeekBarProgress(currentBandLevels);
    loadfale();
});
        binding.waveformSeekBar.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
            if (fromUser && mediaPlayer != null) {
                int newPosition = (int) (progress * mediaPlayer.getDuration() / 100f);
                mediaPlayer.seekTo(newPosition);
                updateCurrentTimeDisplay(newPosition);
            }
        });


        binding.textView3.setOnClickListener(v->{
            showSaveDialog();
        });
        binding.imageView5.setOnClickListener(v->{
            stopUpdatingThumb();
//            showExitDialog();
            Intent mixer = new Intent(equazerActivity.this, SlectaudioActivity.class);
            mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mixer.putExtra("key", "equalizer");
            startActivity(mixer);
        });
        // Set OnSeekBarChangeListener for SeekBarSetSizeThumb4
        binding.thumb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


    }
    private void load(){
        binding.load.setEnabled(true);
        binding.load.setImageResource(R.drawable.load);
    }
    private void loadfale(){
        binding.load.setEnabled(false);
        binding.load.setImageResource(R.drawable.loadmo);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            binding.imageView10.setImageResource(R.drawable.paush);
            mediaPlayer.pause(); // Tạm dừng nhạc khi ứng dụng vào nền
        }
    }
    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogCreatBinding dialogBinding = DialogCreatBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        applyGradien2(dialogBinding.textView2);
        applyGradientToSaveText(dialogBinding.textView3);
        dialogBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    dialogBinding.textView2.setBackgroundResource(R.drawable.corlorbo);
                    dialogBinding.clearIcon.setVisibility(View.VISIBLE);
                } else {
                  dialogBinding.clearIcon.setVisibility(View.GONE);
                    dialogBinding.textView2.setBackgroundResource(R.drawable.creatnull);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        String currentName = name != null ? name : "audio";
//        dialogBinding.editText.setText("");
//        dialogBinding.editText.setSelection(currentName.length());

        dialogBinding.clearIcon.setOnClickListener(v -> dialogBinding.editText.setText(""));
        dialogBinding.textView3.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.textView2.setOnClickListener(v -> {
            String newName = dialogBinding.editText.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.main.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.post(() ->
                    applyEqualizerAndSaveAudio(audioPath, newName,currentBandLevels)
            );


            dialog.dismiss();
        });

        dialog.show();
    }
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogoutBinding dialogBinding = DialogoutBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
//        applyGradientToSaveText(dialogBinding.tex1);
        applyGradientToSaveText(dialogBinding.tex2);

        dialogBinding.yes.setOnClickListener(v->{
            Intent mixer = new Intent(equazerActivity.this, SlectaudioActivity.class);
            mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mixer.putExtra("key", "mixer");
            startActivity(mixer);

        });
        dialogBinding.no.setOnClickListener(v->{
            dialog.dismiss();
        });



        dialog.show();
    }
    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color (20%)
                        Color.parseColor("#14D2E6")  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }
    private void applyGradien(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#80FFFFFF"), // Top color (20%)
                        Color.parseColor("#80FFFFFF")  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }
    private void applyGradien2(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#FFFFFF"), // Top color (20%)
                        Color.parseColor("#FFFFFF")  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }
    private void applyEqualizerAndSaveAudio(String inputPath, String outputName, short[] bandLevels) {
        Log.d("Equalizer", "Band Levels: " + Arrays.toString(bandLevels));
        String fileExtension = getFileExtension(inputPath);
        Log.d("FFmpeg", "Input file extension: " + fileExtension);

        // Determine codec and output file extension based on the input file type
        String codec;
        String outputExtension;

        switch (fileExtension.toLowerCase()) {
            case "mp3":
                codec = "libmp3lame";
                outputExtension = ".mp3";
                break;
            case "aac":
                codec = "aac";
                outputExtension = ".aac";
                break;
            case "wav":
                codec = "pcm_s16le";
                outputExtension = ".wav";
                break;
            case "m4a":
                codec = "aac";
                outputExtension = ".m4a";
                break;
            case "flac":
                codec = "flac";
                outputExtension = ".flac";
                break;
            case "ogg":
                codec = "libvorbis";
                outputExtension = ".ogg";
                break;
            case "wma":
                codec = "wmav2";
                outputExtension = ".wma";
                break;
            case "opus":
                codec = "libopus";
                outputExtension = ".opus";
                break;
            case "amr":
                codec = "libopencore_amrnb";
                outputExtension = ".amr";
                break;
            default:
                Log.e("FFmpeg", "Unsupported audio format: " + fileExtension);
                return;
        }

        outputName = generateUniqueFileName(outputName, outputExtension);
        String outputPath = getOutputPath(outputName, outputExtension);
        Log.d("FFmpeg", "Output path: " + outputPath);

        // Construct the equalizer filter
        String filter = constructEqualizerFilter(bandLevels);
        Log.d("FFmpeg", "filter: " + filter);
        // Build the FFmpeg command
        String[] cmd = {"-i", inputPath, "-filter:a", filter, "-c:a", codec, "-b:a", "192k", outputPath};
        Log.d("FFmpeg", "FFmpeg command: " + Arrays.toString(cmd));

        // Execute the FFmpeg command asynchronously
        FFmpeg.executeAsync(cmd, (executionId, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Log.i("FFmpeg", "Command execution completed successfully.");
                Intent mp3cutter = new Intent(equazerActivity.this, SuccesActivity.class);
                mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mp3cutter.putExtra("key", "mp3cutter");

                mp3cutter.putExtra("outputAudioPath", outputPath);
                startActivity(mp3cutter);
                binding.main.setVisibility(View.VISIBLE);
                binding.loading.setVisibility(View.GONE);
            } else {
                Log.e("FFmpeg", "Command execution failed with rc=" + returnCode);
            }
        });
    }
    private boolean doesFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private String generateUniqueFileName(String baseName, String extension) {
        int counter = 1;  // Khởi tạo bộ đếm
        String uniqueName = baseName;

        while (doesFileExist(getOutputPath(uniqueName, extension))) {
            uniqueName = baseName + "_" + counter;
            counter++;
        }

        return uniqueName;
    }

    private String constructEqualizerFilter(short[] bandLevels) {
        double[] centerFreqs = {60, 230, 910, 3600, 14000}; // Only 5 bands for the equalizer
        StringBuilder filter = new StringBuilder();

        for (int i = 0; i < bandLevels.length; i++) {
            if (i > 0) filter.append(",");
            filter.append("equalizer=f=").append(centerFreqs[i]).append(":width_type=o:width=1:g=").append(bandLevels[i]);
        }

        return filter.toString();
    }

    private String getFileExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf('.') + 1);
    }

    // Construct output file path based on file type
    private String getOutputPath(String outputName, String extension) {
        // Define the directory where you want to save the output file
        File outputDir = new File(getExternalFilesDir(null), "EqualizerAudio");

        // Create the directory if it doesn't exist
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Append the outputName and the correct file extension
        return new File(outputDir, outputName + extension).getAbsolutePath();
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void setupEqualizer() {
        if (mediaPlayer != null) {
            int sessionId = mediaPlayer.getAudioSessionId(); // Get session ID from MediaPlayer
            equalizer = new Equalizer(0, sessionId);
            equalizer.setEnabled(true);

            int numBands = equalizer.getNumberOfBands();
            for (short i = 0; i < numBands; i++) {
                equalizer.setBandLevel(i, (short) 0); // Initialize band levels to 0
            }
        }
    }
    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Audio đã phát hết
                stopUpdatingThumb();
                binding.imageView10.setImageResource(R.drawable.paush); // Hoặc cập nhật icon khác
            }
        });
        try {
            Uri audioUri = Uri.parse(audioPath);
            mediaPlayer.setDataSource(this, audioUri); // Set the Uri as the data source
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setEqualizerBandLevels(short[] levels) {
        int numBands = equalizer.getNumberOfBands();
        for (short i = 0; i < numBands && i < levels.length; i++) {
            equalizer.setBandLevel(i, levels[i]);
        }
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                // Pause the audio
                mediaPlayer.pause();

                stopUpdatingThumb();
                binding.imageView10.setImageResource(R.drawable.paush);
            } else {
                // Play the audio
                mediaPlayer.start();
                startUpdatingThumb();
                // Update the UI to show "Pause" icon
                binding.imageView10.setImageResource(R.drawable.playxanh);
            }
        }
    }

    private void onItemClicked(String itemName) {
        Log.d("EquazerActivity", "Selected item: " + itemName);
        switch (itemName.toLowerCase()) {
            case "custom":
                currentBandLevels = new short[]{0, 0, 0, 0, 0}; // Flat response
                loadfale();
                break;
            case "dance":
                currentBandLevels = new short[]{10, 8, 5, 0, -5};
                load();
                break;
            case "r&b":
                // Warm low end and smooth mids, less emphasis on highs
                currentBandLevels = new short[]{7, 6, 4, 2, -3};
                load();
                break;
            case "rock":
                // Boosts lows and highs, slightly cuts mids
                currentBandLevels = new short[]{10, 5, 0, 5, 10};
                load();
                break;
            case "pop":
                currentBandLevels = new short[]{5, 4, 5, 7, 6};
                load();
                break;
            case "hip hop":
                currentBandLevels = new short[]{12, 8, 3, -2, -5};
                load();
                break;
            case "classical":
                currentBandLevels = new short[]{0, 2, 5, 7, 10};
                load();
                break;
            case "bass":
                currentBandLevels = new short[]{15, 10, 0, -5, -10};
                load();
                break;
            case "jazz":
                currentBandLevels = new short[]{4, 6, 8, 4, 0};
                load();
                break;
            default:
                itemName="custom";
                currentBandLevels = new short[]{0, 0, 0, 0, 0}; // Default flat response
                break;
        }
        setEqualizerBandLevels(currentBandLevels);

        updateSeekBarProgress(currentBandLevels);

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(audioPath));
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            playAudio(); // Start playback
        }
    }
    private void updateSeekBarProgress(short[] bandLevels) {
     
        // SeekBar max range should be set to 30 (i.e., -15 to +15 mapped to 0 to 30)
        binding.sbhz.setMax(30); // Make sure max is 30
        binding.sbkhz.setMax(30);
        binding.sb910hz.setMax(30);
        binding.kHz1.setMax(30);
        binding.kHz2.setMax(30);

        int sbkhz=bandLevels[1] + 15;
        int sb910hz= bandLevels[2] + 15;
        int kHz1= bandLevels[3] + 15;
        int kHz2= bandLevels[4] + 15;
        binding.sbhz.setProgress(bandLevels[0] + 15);
        binding.sbkhz.setProgress(sbkhz);
        binding.sb910hz.setProgress(sb910hz);
        binding.kHz1.setProgress(kHz1);
        binding.kHz2.setProgress(kHz2);
    }
    private void setupSeekBars() {
        // SeekBar for 60 Hz
        binding.sbhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
       
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                load();
                int dbValue = progress - 15; // Calculate dB value from SeekBar progress

                binding.textView8.setText(dbValue + " dB"); // Update dB display
                currentBandLevels[0] = (short) dbValue; // Update currentBandLevels for 60 Hz

                if (equalizer != null) {
                    short band = 0; // 60 Hz band
                    equalizer.setBandLevel(band, (short) (dbValue * 100)); // Update equalizer
                }
                equazeradapter.setSelectedItemName("Custom");
                equazeradapter.notifyDataSetChanged();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // SeekBar for 230 Hz
        binding.sbkhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                load();
                int dbValue = progress - 15;
                binding.textkhzt.setText(dbValue + " dB");
                currentBandLevels[1] = (short) dbValue; // Update currentBandLevels for 230 Hz

                if (equalizer != null) {
                    short band = 1; // 230 Hz band
                    equalizer.setBandLevel(band, (short) (dbValue * 100));
                }
                equazeradapter.setSelectedItemName("Custom");
                equazeradapter.notifyDataSetChanged();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.sb910hz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                load();

                int dbValue = progress - 15;
                binding.textkhz36t.setText(dbValue + " dB");
                equazeradapter.setSelectedItemName("Custom");

                if (equalizer != null) {
                    short band = 2; // 910 Hz band
                    equalizer.setBandLevel(band, (short) (dbValue * 100));
                }
                equazeradapter.notifyDataSetChanged();
                currentBandLevels[2] = (short) dbValue; // Update currentBandLevels for 910 Hz
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // SeekBar for 3.60 kHz
        binding.kHz1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                load();
                int dbValue = progress - 15;
                binding.texhz910.setText(dbValue + " dB");

                currentBandLevels[3] = (short) dbValue; // Update currentBandLevels for 3.60 kHz

                if (equalizer != null) {
                    short band = 3; // 3.60 kHz band
                    equalizer.setBandLevel(band, (short) (dbValue * 100));
                }

                equazeradapter.setSelectedItemName("Custom");
                equazeradapter.notifyDataSetChanged();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // SeekBar for 14 kHz
        binding.kHz2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                load();
                int dbValue = progress - 15;
                binding.textkhz90.setText(dbValue + " dB");
                currentBandLevels[4] = (short) dbValue; // Update currentBandLevels for 14 kHz

                if (equalizer != null) {
                    short band = 4; // 14 kHz band
                    equalizer.setBandLevel(band, (short) (dbValue * 100));
                }
                equazeradapter.setSelectedItemName("Custom");
                equazeradapter.notifyDataSetChanged();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private List<HomeItem> getItems() {
        List<HomeItem> items = new ArrayList<>();
        items.add(new HomeItem(R.drawable.custom, "Custom","Custom"));
        items.add(new HomeItem(R.drawable.dance, "Dance","Dance"));
        items.add(new HomeItem(R.drawable.rb, "R&B","R&B"));
        items.add(new HomeItem(R.drawable.rock, "Rock","Rock"));
        items.add(new HomeItem(R.drawable.pop, "Pop","Pop"));
        items.add(new HomeItem(R.drawable.hiphop, "Hip Hop","Hip Hop"));
        items.add(new HomeItem(R.drawable.classial, "Classical","Classical"));
        items.add(new HomeItem(R.drawable.bass, "Bass","Bass"));
        items.add(new HomeItem(R.drawable.jazz, "Jazz","Jazz"));
        return items;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingThumb();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
        handler.removeCallbacksAndMessages(null);
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
                        binding.thumb.setProgress((int) progress);  // Update the SeekBarSetSizeThumb4
                    }

                    updateCurrentTimeDisplay(currentPosition);
                    handler.postDelayed(this, 50);
                }
            };
            handler.post(updateThumbRunnable);
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
    private void updateSongTitle(String songTitle) {
        // Nếu độ dài vượt quá 20 ký tự thì cắt bớt và thêm "..."
        if (songTitle.length() > 25) {
            songTitle = songTitle.substring(0, 25) + "...";
        }
        binding.textView7.setText(songTitle);
    }
}
