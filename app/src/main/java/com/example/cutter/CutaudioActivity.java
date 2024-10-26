package com.example.cutter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.SuccesActivity;
import com.example.selectaudio.SlectaudioActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityCutaudioBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogCreatBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogoutBinding;
import com.example.ultils.Untils;
import com.example.voidchanger.VoidChangerActivity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CutaudioActivity extends AppCompatActivity implements Custombar.OnclickA {
    private static final String TAG = "CutaudioActivity";
    private ActivityCutaudioBinding binding;
    private WaveformSeekBar seekbar;
    private Custombar cutaudioAudio;
    private float audioDurationInMs ;
    private static final int NUM_COLUMNS = 80;
    private MediaPlayer mediaPlayer;
    private String audioPath;
    private Handler handler = new Handler();
    private Runnable updateThumbRunnable;
private float total=1000;
private boolean a=true;
    private void startUpdatingThumb() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            updateThumbRunnable = new Runnable() {

                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();

                        // Kiểm tra giá trị currentPosition và duration hợp lệ
                        if (currentPosition < 0 || duration <= 0) {
                            handler.removeCallbacks(updateThumbRunnable);
                            return;
                        }

                        if (duration > 0) {
                            float progress = (float) currentPosition / duration;
                            cutaudioAudio.updateThumbPosition(progress);
                        }

                        // Tiếp tục cập nhật sau 50ms
                        handler.postDelayed(this, 50);
                    } else {
                        // Nếu mediaPlayer không tồn tại hoặc không phát nữa, dừng handler
                        handler.removeCallbacks(updateThumbRunnable);
                    }
                }
            };

            // Bắt đầu cập nhật thumb
            handler.post(updateThumbRunnable);
        } else {
            // Nếu mediaPlayer là null hoặc không đang phát, dừng handler
            handler.removeCallbacks(updateThumbRunnable);
        }
    }
    private void stopUpdatingThumb() {
        if (updateThumbRunnable != null) {
            handler.removeCallbacks(updateThumbRunnable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCutaudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Untils.showSystemUI(this , true);
        audioPath = getIntent().getStringExtra("audioPath");
        String audioname = getIntent().getStringExtra("audioname");
        String audiodur = getIntent().getStringExtra("audiodur");
        String date = getIntent().getStringExtra("date");
        String size = getIntent().getStringExtra("size");

        binding.duration.setText(audiodur);
        binding.textView15.setText(audiodur);
        if (audioname.length() > 30) {
            audioname = audioname.substring(0, 30) + "...";
        }
        binding.name.setText(audioname);
        binding.date.setText(date);
        binding.size.setText(size);
        audioDurationInMs = convertTimeToMilliseconds(audiodur);
//      String  time1=formatTime(audiodur);
        binding.time2.setText(audiodur);
        Log.d(TAG, "Audio duration: " + audioDurationInMs + " ms");

        if (audioPath == null) {
            Log.e(TAG, "No audio path provided");
            Toast.makeText(this, "No audio file selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cutaudioAudio = binding.audioSeekBar;
        if (cutaudioAudio == null) {

        } else {
            setupMediaPlayer();
            cutaudioAudio.setMediaPlayer(mediaPlayer);
            Log.d(TAG, "CutaudioAudio initialized successfully");
            cutaudioAudio.setAudioDuration(audioDurationInMs);

        }


        binding.save.setOnClickListener(v -> {
            String totaltime = binding.textView15.getText().toString();
//            totaltime.equals(timeend)
            String timeend = binding.time2.getText().toString();
                if (a) {
                    Toast.makeText(this, R.string.cut1, Toast.LENGTH_SHORT).show();
                } else {
                    if (total < 1000) {
                        Toast.makeText(this, R.string.minimum_cutting_time_1_s, Toast.LENGTH_SHORT).show();
                    } else {
                        showSaveDialog();
                    }
                }
        });


        binding.music.setOnClickListener(v->{
            playAudio();
        });
//        startUpdatingTime();
        audioDurationInMs = convertTimeToMilliseconds(audiodur);
        int   totalDuration = (int) audioDurationInMs;

        binding.cong1.setOnClickListener(v -> {
            restartAudio();
            String cong1 = getIntent().getStringExtra("audiodur");
            Log.d("onCreate", "Original Time: " + cong1);

            // Tách phút và giây
            String[] parts = cong1.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);

            // Chuyển đổi thành tổng số giây
            int totalSeconds = minutes * 60 + seconds;

            // Trừ đi 1 giây
            totalSeconds -= 1;

            // Đảm bảo không giảm dưới 0
            if (totalSeconds < 0) {
                totalSeconds = 0;
            }

            // Tính toán phút và giây mới
            int newMinutes = totalSeconds / 60;
            int newSeconds = totalSeconds % 60;

            // Định dạng lại chuỗi thời gian
            String formattedTime = String.format("%02d:%02d", newMinutes, newSeconds);
            if (formattedTime.equals(binding.textView14.getText().toString())) {
                binding.chu2.setImageResource(R.drawable.chu1mo);
            } else {
                binding.chu2.setImageResource(R.drawable.min);
            }
            Log.d(TAG, "onCreate: "+cong1);
            cutaudioAudio.adjustStartThumb(1000); // Tăng 1 giây
            seekbar.invalidate();
            binding.chu1.setImageResource(R.drawable.min);
            loadformwave();
        });
        binding.chu1.setOnClickListener(v -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cutaudioAudio.adjustStartThumb(-1000); // Giảm 1 giây
                    seekbar.invalidate();
                }
            });
            binding.cong1.setImageResource(R.drawable.max);
            if ("00:00".equals(binding.textView14.getText().toString())) {
                binding.chu1.setImageResource(R.drawable.chu1mo);
            } else {
                binding.chu1.setImageResource(R.drawable.min);
            }
            loadformwave();

        });
        binding.cong2.setOnClickListener(v -> {
            binding.chu2.setImageResource(R.drawable.min);
            cutaudioAudio.adjustEndThumb(1000); // Tăng 1 giây
            seekbar.invalidate();
            String cong2 = getIntent().getStringExtra("audiodur");
            if (cong2.equals(binding.time2.getText().toString())) {
                binding.cong2.setImageResource(R.drawable.cong1mo);
            } else {
                binding.cong2.setImageResource(R.drawable.max);
            }
            loadformwave();
        });

        binding.chu2.setOnClickListener(v -> {
            binding.cong2.setImageResource(R.drawable.max);
            cutaudioAudio.adjustEndThumb(-1000); // Giảm 1 giây
            seekbar.invalidate();

            if ("00:00".equals(binding.time2.getText().toString())) {
                binding.chu2.setImageResource(R.drawable.chu1mo);
            } else {
                binding.chu2.setImageResource(R.drawable.min);
            }
            loadformwave();
        });
        binding.chu3.setOnClickListener(v -> {
            cutaudioAudio.adjustProgressThumb(15000); // Tăng 1 giây
            seekbar.invalidate();
        });

        binding.cong3.setOnClickListener(v -> {
            cutaudioAudio.adjustProgressThumb(-15000); // Giảm 1 giây
            seekbar.invalidate();
        });
binding.imageView5.setOnClickListener(v->{
    showExitDialog();
});
binding.imageView15.setOnClickListener(v->{
    a=true;
    if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
        // Pause the music if already playing
        mediaPlayer.pause();
        stopUpdatingThumb();
        binding.music.setImageResource(R.drawable.paush);
    }
    recreate();
    binding.imageView15.setVisibility(View.GONE);
});

        seekbar = binding.waveformView;
        seekbar.setSampleFrom(audioPath);

        cutaudioAudio.setListener(new Custombar.OnThumbPosition() {
            @Override
            public void onThumbPositionChanged(float position) {

            }
            @Override
            public void onSliderChanged(float progress) {
                a=false;
                binding.imageView15.setVisibility(View.VISIBLE);
                loadformwave();

            }
        });

        cutaudioAudio.setOnclickA(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void saveAudio(String name) {
        float startTimeInMs = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
        float endTimeInMs = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();
        Log.e(TAG, "start " + startTimeInMs);
        Log.e(TAG, "end " + endTimeInMs);

        File originalFile = new File(audioPath);
        String originalName = originalFile.getName();
        String fileExtension = getFileExtension(originalName);
        String audioFormat = getAudioFormat(audioPath);

        File outputDir = new File(getExternalFilesDir(null), "/Music/Mp3cutter");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Tạo tên file độc nhất
        String baseFileName = name;
        String fileName = baseFileName + fileExtension;
        File outputFile = new File(outputDir, fileName);
        int counter = 1;

        // Kiểm tra xem file đã tồn tại chưa
        while (outputFile.exists()) {
            fileName = baseFileName + " (" + counter + ")" + fileExtension;
            outputFile = new File(outputDir, fileName);
            counter++;
        }

        String[] cmd;
        if (audioFormat.equals("unknown")) {
            cmd = new String[]{
                    "-i", audioPath,
                    "-ss", String.valueOf(startTimeInMs / 1000),
                    "-to", String.valueOf(endTimeInMs / 1000),
                    "-c:a", "libmp3lame",
                    "-q:a", "2",
                    outputFile.getPath()
            };
        } else {
            cmd = new String[]{
                    "-i", audioPath,
                    "-ss", String.valueOf(startTimeInMs / 1000),
                    "-to", String.valueOf(endTimeInMs / 1000),
                    "-c:a", getEncoder(audioFormat),
                    outputFile.getPath()
            };
        }
String op=outputFile.getPath();
        new Thread(() -> {
            try {
                FFmpeg.execute(cmd);
                runOnUiThread(() -> {
                    Intent mp3cutter = new Intent(CutaudioActivity.this, SuccesActivity.class);
                    mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mp3cutter.putExtra("key", "mp3cutter");
                    mp3cutter.putExtra("outputAudioPath", op);
                    startActivity(mp3cutter);

                    binding.main.setVisibility(View.VISIBLE);
                    binding.loading.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error saving file", e);
                runOnUiThread(() -> {
                    Toast.makeText(CutaudioActivity.this, "Error saving file", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return ".mp3"; // Default to .mp3 if no extension is found
    }

    private String getAudioFormat(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        switch (extension) {
            case ".mp3": return "mp3";
            case ".wav": return "wav";
            case ".aac": return "aac";
            case ".m4a": return "m4a";
            case ".ogg": return "ogg";
            case ".flac": return "flac";
            default: return "unknown";
        }
    }

    private String getEncoder(String format) {
        switch (format) {
            case "mp3": return "libmp3lame";
            case "wav": return "pcm_s16le";
            case "aac": return "aac";
            case "m4a": return "aac";
            case "ogg": return "libvorbis";
            case "flac": return "flac";
            default: return "libmp3lame";
        }
    }
//    private void saveAudio(String name) {
//        float startTimeInMs = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
//        float endTimeInMs = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();
//        Log.e(TAG, "start " + startTimeInMs);
//        Log.e(TAG, "end " + endTimeInMs);
//
//        File originalFile = new File(audioPath);
//        String originalName = originalFile.getName();
//        String fileExtension = getFileExtension(originalName);
//        String audioFormat = getAudioFormat(audioPath);
//
//        String defaultFileName = "cut_" + System.currentTimeMillis();
//        String fileName = name + fileExtension;
//
//        File outputDir = new File(getExternalFilesDir(null), "/Music/Mp3cutter");
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();
//        }
//        File outputFile = new File(outputDir, fileName);
//
//        String[] cmd;
//        if (audioFormat.equals("unknown")) {
//            cmd = new String[]{
//                    "-i", audioPath,
//                    "-ss", String.valueOf(startTimeInMs / 1000),
//                    "-to", String.valueOf(endTimeInMs / 1000),
//                    "-c:a", "libmp3lame",
//                    "-q:a", "2",
//                    outputFile.getPath()
//            };
//        } else {
//            cmd = new String[]{
//                    "-i", audioPath,
//                    "-ss", String.valueOf(startTimeInMs / 1000),
//                    "-to", String.valueOf(endTimeInMs / 1000),
//                    "-c:a", getEncoder(audioFormat),
//                    outputFile.getPath()
//            };
//        }
//
//        new Thread(() -> {
//            try {
//                FFmpeg.execute(cmd);
//                runOnUiThread(() -> {
//                    Intent mp3cutter = new Intent(CutaudioActivity.this, SuccesActivity.class);
//                    mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    mp3cutter.putExtra("key", "mp3cutter");
//                    mp3cutter.putExtra("outputAudioPath", outputFile.getPath());
//                    startActivity(mp3cutter);
//
//                    binding.main.setVisibility(View.VISIBLE);
//                    binding.loading.setVisibility(View.GONE);
//                });
//
//            } catch (Exception e) {
//                Log.e(TAG, "Error saving file", e);
//                runOnUiThread(() -> {
//                    Toast.makeText(CutaudioActivity.this, "Error saving file", Toast.LENGTH_SHORT).show();
//                });
//            }
//        }).start();
//    }
//
//    private String getFileExtension(String fileName) {
//        int lastDotIndex = fileName.lastIndexOf(".");
//        if (lastDotIndex > 0) {
//            return fileName.substring(lastDotIndex);
//        }
//        return ".mp3"; // Default to .mp3 if no extension is found
//    }
//
//    private String getAudioFormat(String filePath) {
//        String extension = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
//        switch (extension) {
//            case ".mp3": return "mp3";
//            case ".wav": return "wav";
//            case ".aac": return "aac";
//            case ".m4a": return "m4a";
//            case ".ogg": return "ogg";
//            case ".flac": return "flac";
//            default: return "unknown";
//        }
//    }
//
//    private String getEncoder(String format) {
//        switch (format) {
//            case "mp3": return "libmp3lame";
//            case "wav": return "pcm_s16le";
//            case "aac": return "aac";
//            case "m4a": return "aac";
//            case "ogg": return "libvorbis";
//            case "flac": return "flac";
//            default: return "libmp3lame";
//        }
//    }
    private void playAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                // Pause the music if already playing
                mediaPlayer.pause();
                stopUpdatingThumb();
                binding.music.setImageResource(R.drawable.paush);
            } else {
                // Play the music only within the specified time range
                float startTime = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
                float endTime = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();

                // Seek to the start time
                mediaPlayer.seekTo((int) startTime);

                // Start playing the audio
                mediaPlayer.start();
                startUpdatingThumb();

                // Update the play/pause icon
                binding.music.setImageResource(R.drawable.playxanh);

                // Stop the music when the endTime is reached
                mediaPlayer.setOnSeekCompleteListener(mp -> {
                    if (mediaPlayer.getCurrentPosition() >= endTime) {
                        mediaPlayer.pause();
                        stopUpdatingThumb();
                        mediaPlayer.seekTo((int) startTime);
                        binding.music.setImageResource(R.drawable.paush);
                    }
                });

                // Continuously check if the current position has reached the endTime
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) { // Kiểm tra mediaPlayer khác null
                            if (mediaPlayer.getCurrentPosition() >= endTime) {
                                mediaPlayer.pause();
                                stopUpdatingThumb();
                                mediaPlayer.seekTo((int) startTime); // Reset về thời điểm bắt đầu
                                binding.music.setImageResource(R.drawable.paush);
                            } else {
                                handler.postDelayed(this, 100); // Tiếp tục kiểm tra mỗi 100ms
                            }
                        } else {
                            // Nếu mediaPlayer là null, dừng handler hoặc thêm logic để xử lý.
                            handler.removeCallbacks(this);
                        }
                    }
                }, 100);

            }
        }
    }
    // Method to restart the audio from the beginning
    private void restartAudio() {
        if (mediaPlayer != null) {
            float startTime = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();

            // Seek to the start time (beginning of the audio)
            mediaPlayer.seekTo((int) startTime);

            // Start playing the audio
            mediaPlayer.start();
            startUpdatingThumb();

            // Update the play/pause icon
            binding.music.setImageResource(R.drawable.playxanh);

            // Continuously check if the current position has reached the endTime
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    float endTime = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();

                    if (mediaPlayer != null) { // Ensure mediaPlayer is not null
                        if (mediaPlayer.getCurrentPosition() >= endTime) {
                            // Pause when the end is reached
                            mediaPlayer.pause();
                            stopUpdatingThumb();
                            mediaPlayer.seekTo((int) startTime); // Reset to the beginning
                            binding.music.setImageResource(R.drawable.paush);
                        } else {
                            handler.postDelayed(this, 100); // Continue checking every 100ms
                        }
                    } else {
                        // If mediaPlayer is null, stop the handler
                        handler.removeCallbacks(this);
                    }
                }
            }, 100);
        }
    }

    private void setupMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                Uri audioUri = Uri.parse(audioPath);
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
                cutaudioAudio.setMediaPlayer(mediaPlayer);
                startUpdatingThumb();

                // Lắng nghe sự kiện khi nhạc phát xong
                mediaPlayer.setOnCompletionListener(mp -> {
                    binding.music.setImageResource(R.drawable.paush);
                    stopUpdatingThumb(); // Dừng cập nhật tiến trình
                });
            } catch (IOException e) {
                Log.e(TAG, "Error setting up MediaPlayer", e);
                Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private float convertTimeToMilliseconds(String time) {
        try {
            String[] parts = time.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60 + seconds) * 1000f; // Convert to milliseconds
        } catch (Exception e) {
            Log.e(TAG, "Invalid time format: " + time, e);
            return 0;
        }
    }


@Override
protected void onPause() {
    super.onPause();
    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
        mediaPlayer.pause();
        binding.music.setImageResource(R.drawable.paush);
        stopUpdatingThumb();
    }
}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopUpdatingThumb();
    }

    @Override
    public void onclick(String startThumb) {
//        Log.d(TAG, "onclick: "+startThumb);
//        Log.d(TAG, "onclick: "+time1);
        if (binding.textView14 != null) {
            binding.textView14.setText(startThumb);
        } else {
        }
    }

    @Override
    public void Onclickend(String Endthumb) {
  binding.time2.setText(Endthumb);
    }
    private String formatTime(float position) {
        int timeInMs = (int) position; // Directly use position as it's already in milliseconds
        int minutes = (timeInMs / 1000) / 60;
        int seconds = (timeInMs / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void loadformwave(){
    float startTime = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
    float endTime = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();

        total = endTime - startTime; // This should already be in milliseconds

        String a = formatTime(total);
        Log.d(TAG, "loadformwave: " + a);
        binding.textView15.setText(a);

    seekbar.setAudioDuration(mediaPlayer.getDuration()+3000, startTime, endTime);
    Log.d("AudioSeekBar", "Total duration: " + mediaPlayer.getDuration() + ", Start time: " + startTime + ", End time: " + endTime);

    seekbar.isActivated();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            binding.music.setImageResource(R.drawable.paush);
            stopUpdatingThumb();
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
                    applyGradien2(dialogBinding.textView2);
                    dialogBinding.textView2.setBackgroundResource(R.drawable.corlorbo);
                    dialogBinding.clearIcon.setVisibility(View.VISIBLE);
                } else {
                    dialogBinding.clearIcon.setVisibility(View.GONE);
                    applyGradien(dialogBinding.textView2);
                    dialogBinding.textView2.setBackgroundResource(R.drawable.creatnull);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        dialogBinding.editText.setText("");

        dialogBinding.clearIcon.setOnClickListener(v -> dialogBinding.editText.setText(""));
        dialogBinding.textView3.setOnClickListener(v -> dialog.dismiss());
        applyGradientToSaveText(dialogBinding.textView2);
        dialogBinding.textView2.setOnClickListener(v -> {

            String newName = dialogBinding.editText.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show();
                return;
            }
            closeKeyboard(v);
            if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
                // Pause the music if already playing
                mediaPlayer.pause();
                stopUpdatingThumb();
                binding.music.setImageResource(R.drawable.paush);
            }

            binding.music.setImageResource(R.drawable.paush);
            binding.main.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.post(() -> {
                Executors.newSingleThreadExecutor().execute(() -> saveAudio(newName));
            });
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

        applyGradientToSaveText(dialogBinding.tex2);

        dialogBinding.yes.setOnClickListener(v->{
            Intent mixer = new Intent(CutaudioActivity.this, SlectaudioActivity.class);
            mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mixer.putExtra("key", "mp3cutter");
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
    public void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
