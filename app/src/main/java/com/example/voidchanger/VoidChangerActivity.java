package com.example.voidchanger;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.SuccesActivity;
import com.example.mixer.MixerActivity;
import com.example.model.voidchangerItem;
import com.example.selectaudio.Selectaudio2Activity;
import com.example.selectaudio.SlectaudioActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityVoidChangerBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogCreatBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogoutBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.media.audiofx.Equalizer;
import android.media.MediaScannerConnection;

public class VoidChangerActivity extends AppCompatActivity {
    private ActivityVoidChangerBinding binding;
    private voidchangerAdapter voidchangerAdapter;
    private String audioPath;
    private String audioname;
    private String audiotime;
    private ExoPlayer exoPlayer;
    private Equalizer equalizer;
    private float currentSpeed = 1.0f;  // Default speed
    private float currentPitch = 1.0f;   // Default pitch
    private Handler handler = new Handler();
    private Runnable updateThumbRunnable;



    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityVoidChangerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);
        // Initialize ExoPlayer
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        // Setup RecyclerView with GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        binding.recy.setLayoutManager(gridLayoutManager);
        voidchangerAdapter = new voidchangerAdapter(this, getItems(), this::onItemClicked);
        binding.recy.setAdapter(voidchangerAdapter);

        audioPath = getIntent().getStringExtra("audioPath");
        if (audioPath != null) {
            setupExoPlayer();
            binding.waveformSeekBar.setSampleFrom(audioPath);
        } else {
            Log.e("AudiotexActivity", "audioPath is null.");
        }

        binding.waveformSeekBar.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
            if (fromUser && exoPlayer != null) {
                int newPosition = (int) (progress * exoPlayer.getDuration() / 100f);
                exoPlayer.seekTo(newPosition);
                updateCurrentTimeDisplay(newPosition);
            }
        });

        // Set OnSeekBarChangeListener for SeekBarSetSizeThumb4
        binding.sbhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && exoPlayer != null) {
                    int newPosition = (int) (progress * exoPlayer.getDuration() / 100f);
                    exoPlayer.seekTo(newPosition);
                    binding.waveformSeekBar.setProgress(progress);
                    updateCurrentTimeDisplay(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Restart updating when the user stops dragging
                startUpdatingThumb();
            }
        });
        binding.music.setOnClickListener(v -> {
            playAudio();
        });

        audioname = getIntent().getStringExtra("audioname");
        audiotime = getIntent().getStringExtra("audiodur");
        updateSongTitle(audioname);
        binding.time2.setText(audiotime);
        binding.textView3.setOnClickListener(v -> {

            showSaveDialog();
        });

        binding.waveformSeekBar.setWaveWidth(Utils.dp(this, 1));
        binding.waveformSeekBar.setWaveGap(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveMinHeight(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveCornerRadius(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        binding.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(this, R.color.white2));
        binding.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(this, R.color.progress_start));
        binding.imageView5.setOnClickListener(v->{
              finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();  // Pause the audio
            binding.music.setImageResource(R.drawable.paush);
        }
    }

    private void startUpdatingThumb() {
        // Kiểm tra xem exoPlayer có khác null và đang phát hay không
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            updateThumbRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        int currentPosition = (int) exoPlayer.getCurrentPosition();
                        int duration = (int) exoPlayer.getDuration();
                        if (duration > 0) {
                            float progress = (float) currentPosition / duration * 100;
                            binding.waveformSeekBar.setProgress((int) progress);
                            binding.sbhz.setProgress((int) progress);
                        }

                        // Cập nhật hiển thị thời gian hiện tại
                        updateCurrentTimeDisplay(currentPosition);
                    } catch (Exception e) {
                        Log.e("VoidChangerActivity", "Error updating thumb position: " + e.getMessage());
                    } finally {
                        // Đặt lịch để gọi lại hàm này sau 50ms
                        handler.postDelayed(this, 50);
                    }
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

    private void onItemClicked(String itemName) {
        switch (itemName.toLowerCase()) {
            case "normal":
                binding.textView3.setVisibility(View.GONE);
                changeVoice(1.0f, 1.0f);
                break;
            case "baby":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(1.3f, 2.0f);
                break;
            case "male":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(0.9f, 0.7f);
                break;
            case "female":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(1.1f, 1.4f);
                break;
            case "alien":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(0.8f, 1.6f);
                break;
            case "robot":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(0.9f, 0.5f);
                break;
            case "minions":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(1.2f, 2.5f);  // Tốc độ nhanh, pitch rất cao
                break;
            case "chipmunk":
                binding.textView3.setVisibility(View.VISIBLE);
                changeVoice(1.5f, 2.8f);  // Tốc độ rất nhanh, pitch cực kỳ cao
                break;
            default:
                binding.textView3.setVisibility(View.GONE);
                // Nếu không chọn loại giọng nào, giữ nguyên tốc độ và pitch
                changeVoice(1.0f, 1.0f);
                break;
        }
    }

    private void changeVoice(float speed, float pitch) {
        currentSpeed = speed;  // Set current speed
        currentPitch = pitch;  // Set current pitch

        PlaybackParameters playbackParameters = new PlaybackParameters(speed, pitch);
        exoPlayer.setPlaybackParameters(playbackParameters);

        // Optionally, you can play the audio again to apply new parameters
        playAudio2();
    }
    private void playAudio2() {
        if (exoPlayer != null) {
                exoPlayer.play(); // Play will respect the current playback parameters
                startUpdatingThumb();
                binding.music.setImageResource(R.drawable.playxanh);

        }
    }
    private void playAudio() {
        if (exoPlayer != null) {
            if (exoPlayer.isPlaying()) {
                // If music is playing, pause it
                exoPlayer.pause();
                binding.music.setImageResource(R.drawable.paush);
            } else {
                exoPlayer.play(); // Play will respect the current playback parameters
                startUpdatingThumb();
                binding.music.setImageResource(R.drawable.playxanh);
            }
        }
    }
    private void setupExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(this).build(); // Khởi tạo ExoPlayer
        try {
            Uri audioUri = Uri.parse(audioPath); // Chuyển đổi audioPath thành Uri
            MediaItem mediaItem = MediaItem.fromUri(audioUri); // Tạo MediaItem từ Uri

            exoPlayer.setMediaItem(mediaItem); // Đặt MediaItem làm nguồn phát
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE); // Chế độ lặp bài hiện tại
            exoPlayer.prepare(); // Chuẩn bị ExoPlayer

            // Lắng nghe sự kiện khi bài nhạc phát xong
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == ExoPlayer.STATE_ENDED) {
                        binding.music.setImageResource(R.drawable.paush);
                        // Đặt SeekBar về đầu
                        binding.waveformSeekBar.setProgress(0);
                        binding.sbhz.setProgress(0);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveModifiedAudio(String name) {

        String inputAudioPath = audioPath;
        File voichangerDir = new File(getExternalFilesDir(null), "Voichanger");
        if (!voichangerDir.exists()) {
            voichangerDir.mkdirs();
        }

        String originalFileName = new File(inputAudioPath).getName();
        String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String originalFileExtension = getFileExtension(inputAudioPath);

        String outputFileName = generateUniqueFileName(voichangerDir, name, originalFileExtension);
        String outputAudioPath = new File(voichangerDir, outputFileName).getAbsolutePath();
        File outputFile = new File(outputAudioPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        String codec;
        String bitrate;

        switch (originalFileExtension.toLowerCase()) {
            case "mp3":
                codec = "libmp3lame";
                bitrate = "192k";
                break;
            case "wav":
                codec = "pcm_s16le";
                bitrate = "";
                break;
            case "aac":
                codec = "aac";
                bitrate = "192k";
                break;
            case "ogg":
                codec = "libvorbis";
                bitrate = "192k";
                break;
            case "m4a":
                codec = "aac";
                bitrate = "192k";
                break;
            default:
                codec = "libmp3lame";
                bitrate = "192k";
                break;
        }

        List<String> commandList = new ArrayList<>();
        commandList.add("-y");
        commandList.add("-i");
        commandList.add(inputAudioPath);
        commandList.add("-filter:a");

        StringBuilder filterBuilder = new StringBuilder();
        Log.d("AudioSpeed", "Current speed before saving: " + currentSpeed);
        // Xử lý tốc độ
        if (currentSpeed != 1.0f) {
            filterBuilder.append("atempo=").append(currentSpeed);
        }

        // Xử lý pitch
        if (currentPitch != 1.0f) {
            if (filterBuilder.length() > 0) {
                filterBuilder.append(",");
            }
            // Sử dụng pitch hợp lệ
            filterBuilder.append("asetrate=").append((int)(currentPitch * 44100));
        }

        // Nếu không có bộ lọc nào được áp dụng, sử dụng bộ lọc anull
        if (filterBuilder.length() == 0) {
            filterBuilder.append("anull");
        }

        commandList.add(filterBuilder.toString());
        commandList.add("-c:a");
        commandList.add(codec);
        if (!bitrate.isEmpty()) {
            commandList.add("-b:a");
            commandList.add(bitrate);
        }
        commandList.add(outputAudioPath);

        String[] ffmpegCommand = commandList.toArray(new String[0]);

        FFmpeg.executeAsync(ffmpegCommand, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                runOnUiThread(() -> {

                    Intent mp3cutter = new Intent(VoidChangerActivity.this, SuccesActivity.class);
                    mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mp3cutter.putExtra("key", "mp3cutter");

                    mp3cutter.putExtra("outputAudioPath", outputAudioPath);
                    mp3cutter.putExtra("outputtime", audiotime);
                    startActivity(mp3cutter);
//
//                    Toast.makeText(this, "Audio file saved as: " + outputFileName, Toast.LENGTH_LONG).show();

                    MediaScannerConnection.scanFile(this, new String[]{outputAudioPath}, null, null);
                    binding.main.setVisibility(View.VISIBLE);
                    binding.loading.setVisibility(View.GONE);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.failed_to_save_audio_file, Toast.LENGTH_SHORT).show();
                });
                Log.e("FFmpeg", "Error: " + returnCode);
            }
        });
    }


    private String generateUniqueFileName(File directory, String baseName, String extension) {
        String fileName = baseName + "." + extension;
        File file = new File(directory, fileName);
        int count = 1;

        while (file.exists()) {
            fileName = baseName + "_" + count + "." + extension;
            file = new File(directory, fileName);
            count++;
        }

        return fileName;
    }

    private String getFileExtension(String filePath) {
        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i + 1);
        }
        return extension;
    }

    private void updateSongTitle(String songTitle) {
        // Nếu độ dài vượt quá 20 ký tự thì cắt bớt và thêm "..."
        if (songTitle.length() > 20) {
            songTitle = songTitle.substring(0, 20) + "...";
        }
        // Cập nhật TextView với tên bài hát
        binding.textView17.setText(songTitle);}



    private List<voidchangerItem> getItems() {
        List<voidchangerItem> items = new ArrayList<>();
        items.add(new voidchangerItem(R.drawable.voichan, "Normal"));
        items.add(new voidchangerItem(R.drawable.baby, "Baby"));
        items.add(new voidchangerItem(R.drawable.male, "Male"));
        items.add(new voidchangerItem(R.drawable.female, "Female"));
        items.add(new voidchangerItem(R.drawable.alien, "Alien"));
        items.add(new voidchangerItem(R.drawable.robot, "Robot"));
        items.add(new voidchangerItem(R.drawable.minions, "Minions"));
        items.add(new voidchangerItem(R.drawable.chipmunk, "Chipmunk"));
        return items;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release(); // Giải phóng tài nguyên khi activity bị hủy
            exoPlayer = null; // Đặt exoPlayer về null để tránh lỗi IllegalStateException
        }
        handler.removeCallbacks(updateThumbRunnable);
        stopUpdatingThumb();
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
            exoPlayer.pause();
            String newName = dialogBinding.editText.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.main.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);

            binding.loading.post(() ->  saveModifiedAudio(newName));
            closeKeyboard(v);
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
            Intent mixer = new Intent(VoidChangerActivity.this, SlectaudioActivity.class);
            mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mixer.putExtra("key", "voidchanger");
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
