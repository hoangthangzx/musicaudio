package com.example.mixer;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arthenica.mobileffmpeg.FFmpeg;

import com.example.Home.HomeActivity;
import com.example.SuccesActivity;
import com.example.cutter.Cutaudio2Activity;
//import com.example.merge.MergeActivity;
import com.example.model.AudioFile;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityMixerBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogCreatBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogoutBinding;
import com.example.ultils.AudioUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Mixer2Activity extends AppCompatActivity implements MixerAdapter.OnAudioClickListener{
    ActivityMixerBinding binding;
    MixerAdapter mixerAdapter;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateWaveformRunnable;
    private boolean isPlaying = false;
    private String currentAudioUri = null;
    private String name= null ;
    private String size= null ;
    private String date= null ;
    private String dur= null;
    private String audioPath= null;
    private static final String TAG = "Mixer2Activity";
    private List<MediaPlayer> mediaPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer);
        setLocale(this);
        binding = ActivityMixerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        binding.recy.setLayoutManager(gridLayoutManager);


        mixerAdapter = new MixerAdapter(AudioUtils.getSelectedAudioFiles(), this,this);

        binding.music.setOnClickListener(v -> togglePlayPause());
        binding.recy.setAdapter(mixerAdapter);
        handler = new Handler(Looper.getMainLooper());
        binding.save.setOnClickListener(v -> {
//            binding.mai.setVisibility(View.GONE);
//            binding.loading.setVisibility(View.VISIBLE);
            showSaveDialog();

        });
        binding.imageView18.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            if(AudioUtils.getSelectedAudioFiles().size()>2){
                AudioUtils.removeSelectedAudioFile(currentAudioUri);
            }else {
                mixerAdapter.resetAllBackground();
                Toast.makeText(this, R.string.you_can_only_delete_when_there_are_at_least_3_files, Toast.LENGTH_SHORT).show();
            }
//            removeCurrentAudio();
            mixerAdapter.resetAnimation();
            mixerAdapter.setPlaying(false);
            binding.constraintLayout19.setVisibility(View.GONE);
            cleanupMediaPlayers();
            mixerAdapter.resetAllBackground();
        });
        binding.imageView16.setOnClickListener(v -> {
            binding.constraintLayout19.setVisibility(View.GONE);
            mixerAdapter.resetAllBackground();
            Intent mp3cutter = new Intent(Mixer2Activity.this, SlectaudioActivity.class);
            mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mp3cutter.putExtra("key", "mixer");
            mp3cutter.putExtra("outputAudioPath", currentAudioUri);
            mp3cutter.putExtra("audioname",name);
            mp3cutter.putExtra("audiodur", dur);
            mp3cutter.putExtra("date", date);
            mp3cutter.putExtra("size",size);
            startActivity(mp3cutter);

        });

        binding.imageView19.setOnClickListener(v->{
            binding.constraintLayout19.setVisibility(View.GONE);
            mixerAdapter.resetAllBackground();
            cleanupMediaPlayers();
            Intent mp3cutter = new Intent(Mixer2Activity.this, Cutaudio2Activity.class);
            mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mp3cutter.putExtra("key", "mixer");

            mp3cutter.putExtra("outputAudioPath", currentAudioUri);
            mp3cutter.putExtra("audioname",name);
            mp3cutter.putExtra("audiodur", dur);
            mp3cutter.putExtra("date", date);
            mp3cutter.putExtra("size",size);
            startActivity(mp3cutter);

        });
        binding.add.setOnClickListener(v->{
            mixerAdapter.resetAllBackground();
            binding.constraintLayout19.setVisibility(View.GONE);
            if(AudioUtils.getSelectedAudioFiles().size()<5){
                cleanupMediaPlayers();
                Intent mixer = new Intent(Mixer2Activity.this, selecadd.class);
                mixer.putExtra("key", "mixer");
                mixer.putExtra("clear", "false");
                startActivity(mixer);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    binding.music.setImageResource(R.drawable.paush);
                    mediaPlayer.pause();
                }
            }
            else {
                Toast.makeText(this, "You can only select up to 5 files.", Toast.LENGTH_SHORT).show();
            };

        });
        binding.imageView5.setOnClickListener(v->{
            showExitDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mixerAdapter.updateList();
    }

    private void togglePlayPause() {
        mixerAdapter.resetAllBackground();
        binding.constraintLayout19.setVisibility(View.GONE);
        if (!isPlaying) {
            binding.music.setImageResource(R.drawable.playxanh);
            // Starting playback
            if (mediaPlayers.isEmpty()) {
                playAllAudioFiles(AudioUtils.getSelectedAudioFiles());
            } else {
                // Resume existing players
                for (MediaPlayer player : mediaPlayers) {
                    if (!player.isPlaying()) {
                        // Resume from where it was paused
                        int pausedPosition = player.getCurrentPosition();
                        player.seekTo(pausedPosition);
                        player.start();
                    }
                }
                // Resume animations
                mixerAdapter.setPlaying(false);
                mixerAdapter.resumeAnimation();
            }

            isPlaying = true;
        } else {
            // Pausing playback
            for (MediaPlayer player : mediaPlayers) {
                if (player.isPlaying()) {
                    // Store the current playback position
                    int currentPos = player.getCurrentPosition();
                    player.pause();
                }
            }
            // Pause animations
            mixerAdapter.setPlaying(false);
            mixerAdapter.pauseAnimation();
            binding.music.setImageResource(R.drawable.paush);
            isPlaying = false;
        }
    }

    private void playAllAudioFiles(ArrayList<AudioFile> audioFiles) {
        // Clear existing players if any
        cleanupMediaPlayers();
        // Reset adapter animations
        mixerAdapter.resetAnimation();

        // Create new MediaPlayers for each audio file
        for (AudioFile audioFile : audioFiles) {
            MediaPlayer player = new MediaPlayer();
            try {
                player.setDataSource(audioFile.getUri());
                player.prepare();
                player.start();
                mediaPlayers.add(player);

                // Set up completion listener
                player.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayers.remove(mp);

                    // Check if all players are finished
                    if (mediaPlayers.isEmpty()) {
                        binding.music.setImageResource(R.drawable.paush);
                        isPlaying = false;
                        mixerAdapter.resetAnimation();
                        mixerAdapter.setPlaying(false);

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!mediaPlayers.isEmpty()) {
            isPlaying = true;
            mixerAdapter.setPlaying(true);
        }
    }

    // Add this helper method for cleanup
    private void cleanupMediaPlayers() {
        for (MediaPlayer player : mediaPlayers) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.release();
            }
        }
        mediaPlayers.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mixer = new Intent(Mixer2Activity.this, HomeActivity.class);
        mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mixer.putExtra("key", "mixer");
        startActivity(mixer);
        cleanupMediaPlayers();
    }


    private String formatDuration(long durationMs) {
        long seconds = (durationMs / 1000) % 60;
        long minutes = (durationMs / (1000 * 60)) % 60;
        long hours = durationMs / (1000 * 60 * 60);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private String formatSize(long sizeBytes) {
        return String.format(Locale.getDefault(), "%.2f MB", sizeBytes / (1024.0 * 1024.0));
    }

    private String formatDate(long dateAddedSecs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date(dateAddedSecs));
    }



    private boolean convertToMp3(String inputPath, String outputPath) {
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add("-i");
        commandList.add(inputPath);
        commandList.add("-acodec");
        commandList.add("libmp3lame");
        commandList.add("-q:a");
        commandList.add("2");
        commandList.add(outputPath);

        int returnCode = FFmpeg.execute(commandList.toArray(new String[0]));
        return returnCode == RETURN_CODE_SUCCESS;
    }

    private String getOutputPath(String fileName) {
        File outputDir = new File(getExternalFilesDir(null), "MixedAudio");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return new File(outputDir, fileName).getAbsolutePath();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
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
        String currentName = name != null ? name : "";
        dialogBinding.editText.setText(currentName);
        dialogBinding.editText.setSelection(currentName.length());

        dialogBinding.clearIcon.setOnClickListener(v -> dialogBinding.editText.setText(""));
        dialogBinding.textView3.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.textView2.setOnClickListener(v -> {
            String newName = dialogBinding.editText.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show();
                return;
            }
int a=AudioUtils.getSelectedAudioFiles().size();
            if(a>1) {
                binding.mai.setVisibility(View.GONE);
                binding.loading.setVisibility(View.VISIBLE);
                binding.loading.post(() -> {
                    mixAudioFiles(newName);
                });
                dialog.dismiss();
            }else {
                showToast("Cần ít nhất 2 file âm thanh để trộn.");
            }

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
                        AudioUtils.clearSelectedAudioFiles();
      finish();
        });
        dialogBinding.no.setOnClickListener(v->{
            dialog.dismiss();
        });



        dialog.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        for (MediaPlayer player : mediaPlayers) {
            if (player.isPlaying()) {
                // Store the current playback position
                int currentPos = player.getCurrentPosition();
                player.pause();
            }
        }
        // Pause animations
        mixerAdapter.setPlaying(false);
        mixerAdapter.pauseAnimation();
        binding.music.setImageResource(R.drawable.paush);
        isPlaying = false;
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
    private void mixAudioFiles(String outputFileName) {
        new AudioMixingTask().execute(outputFileName);
    }



    @Override
    public void onAudioClick() {
        binding.constraintLayout19.setVisibility(View.VISIBLE);
    }

    @Override
    public void onclick() {
        binding.constraintLayout19.setVisibility(View.VISIBLE);
            currentAudioUri =mixerAdapter.getPath();

        Log.d(TAG, "onclick: "+currentAudioUri);
            name = mixerAdapter.getName();
            dur = mixerAdapter.getDur();
            size = mixerAdapter.getSize();
            date = mixerAdapter.getDate();
            if (currentAudioUri==null){
                binding.constraintLayout19.setVisibility(View.GONE);
            }else {

                for (MediaPlayer player : mediaPlayers) {
                    if (player.isPlaying()) {
                        // Store the current playback position
                        int currentPos = player.getCurrentPosition();
                        player.pause();
                    }
                }
                binding.music.setImageResource(R.drawable.paush);
                isPlaying = false;
            }
    }

    private class AudioMixingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            // Hiển thị loading indicator
            binding.loading.setVisibility(View.VISIBLE);
            binding.mai.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String outputFileName = params[0];
            ArrayList<AudioFile> audioFilesList = AudioUtils.getSelectedAudioFiles();
            if (audioFilesList == null || audioFilesList.size() < 2) {
                return "At least 2 audios are required to mix";
            }

            ArrayList<String> convertedAudioPaths = new ArrayList<>();
            ArrayList<String> originalFileNames = new ArrayList<>();

            // Tạo thư mục tạm thời để lưu các file đã chuyển đổi
            File tempDir = new File(getCacheDir(), "TempAudio");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            for (int i = 0; i < audioFilesList.size(); i++) {
                AudioFile audioFile = audioFilesList.get(i);
                String inputPath = audioFile.getUri();
                if (inputPath != null && !inputPath.isEmpty()) {
                    String outputPath = new File(tempDir, "converted_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
                    if (convertToMp3(inputPath, outputPath)) {
                        convertedAudioPaths.add(outputPath);

                        // Lấy tên file gốc (không bao gồm đường dẫn và phần mở rộng)
                        String originalFileName = new File(inputPath).getName();
                        originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                        originalFileNames.add(originalFileName);

                        Log.d(TAG, "Converted and added audio path: " + outputPath);
                    } else {
                        Log.e(TAG, "Failed to convert audio file: " + inputPath);
                    }
                } else {
                    Log.e(TAG, "Invalid audio path for file: " + audioFile);
                }
            }

            if (convertedAudioPaths.size() < 2) {
                return "Cần ít nhất 2 file âm thanh hợp lệ để trộn.";
            }

            if (convertedAudioPaths.size() > 2) {
                outputFileName += "_and_" + (convertedAudioPaths.size() - 2) + "_more";
            }
            outputFileName += ".mp3";

            String outputPath = getOutputPath(outputFileName);
            Log.d(TAG, "Final output path: " + outputPath);

            ArrayList<String> commandList = new ArrayList<>();
            commandList.add("-y");

            // Thêm input cho mỗi file đã chuyển đổi
            for (String path : convertedAudioPaths) {
                commandList.add("-i");
                commandList.add(path);
            }

            // Thêm filter complex để trộn âm thanh
            StringBuilder filterComplex = new StringBuilder();
            for (int i = 0; i < convertedAudioPaths.size(); i++) {
                filterComplex.append("[").append(i).append(":a]");
            }
            filterComplex.append("amix=inputs=").append(convertedAudioPaths.size())
                    .append(":duration=longest:dropout_transition=2");

            commandList.add("-filter_complex");
            commandList.add(filterComplex.toString());

            // Thêm encoder và output
            commandList.add("-c:a");
            commandList.add("libmp3lame");
            commandList.add("-q:a");
            commandList.add("2");
            commandList.add(outputPath);

            Log.d(TAG, "FFmpeg command: " + commandList);

            int returnCode = FFmpeg.execute(commandList.toArray(new String[0]));

            // Xóa các file tạm thời
            for (String path : convertedAudioPaths) {
                new File(path).delete();
            }
            tempDir.delete();

            if (returnCode == RETURN_CODE_SUCCESS) {
                return outputPath;
            } else {
                return "Không thể trộn âm thanh. Mã lỗi: " + returnCode;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            binding.loading.setVisibility(View.GONE);
            binding.mai.setVisibility(View.VISIBLE);

            if (result.startsWith("Không thể trộn âm thanh") || result.startsWith("Cần ít nhất")) {
                showToast(result);
            } else {
                showToast("Trộn âm thanh thành công. Đã lưu tại: " + result);
                Intent mp3cutter = new Intent(Mixer2Activity.this, SuccesActivity.class);
                mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mp3cutter.putExtra("outputAudioPath", result);
                startActivity(mp3cutter);
            }
        }
    }
//    private void mixAudioFiles(String outputFileName) {
//        ArrayList<AudioFile> audioFilesList = AudioUtils.getSelectedAudioFiles();
//        if (audioFilesList != null && audioFilesList.size() > 1) {
//            ArrayList<String> convertedAudioPaths = new ArrayList<>();
//            ArrayList<String> originalFileNames = new ArrayList<>();
//
//            // Tạo thư mục tạm thời để lưu các file đã chuyển đổi
//            File tempDir = new File(getCacheDir(), "TempAudio");
//            if (!tempDir.exists()) {
//                tempDir.mkdirs();
//            }
//
//            for (int i = 0; i < audioFilesList.size(); i++) {
//                AudioFile audioFile = audioFilesList.get(i);
//                String inputPath = audioFile.getUri();
//                if (inputPath != null && !inputPath.isEmpty()) {
//                    String outputPath = new File(tempDir, "converted_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
//                    if (convertToMp3(inputPath, outputPath)) {
//                        convertedAudioPaths.add(outputPath);
//
//                        // Lấy tên file gốc (không bao gồm đường dẫn và phần mở rộng)
//                        String originalFileName = new File(inputPath).getName();
//                        originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
//                        originalFileNames.add(originalFileName);
//
//                        Log.d(TAG, "Converted and added audio path: " + outputPath);
//                    } else {
//                        Log.e(TAG, "Failed to convert audio file: " + inputPath);
//                    }
//                } else {
//                    Log.e(TAG, "Invalid audio path for file: " + audioFile);
//                }
//            }
//
//            if (convertedAudioPaths.size() < 2) {
//                showToast("Cần ít nhất 2 file âm thanh hợp lệ để trộn.");
//                return;
//            }
//
//            if (convertedAudioPaths.size() > 2) {
//                outputFileName += "_and_" + (convertedAudioPaths.size() - 2) + "_more";
//            }
//            outputFileName += ".mp3";
//
//            String outputPath = getOutputPath(outputFileName);
//            Log.d(TAG, "Final output path: " + outputPath);
//
//            ArrayList<String> commandList = new ArrayList<>();
//            commandList.add("-y");
//
//            // Thêm input cho mỗi file đã chuyển đổi
//            for (String path : convertedAudioPaths) {
//                commandList.add("-i");
//                commandList.add(path);
//            }
//
//            // Thêm filter complex để trộn âm thanh
//            StringBuilder filterComplex = new StringBuilder();
//            for (int i = 0; i < convertedAudioPaths.size(); i++) {
//                filterComplex.append("[").append(i).append(":a]");
//            }
//            filterComplex.append("amix=inputs=").append(convertedAudioPaths.size())
//                    .append(":duration=longest:dropout_transition=2");
//
//            commandList.add("-filter_complex");
//            commandList.add(filterComplex.toString());
//
//            // Thêm encoder và output
//            commandList.add("-c:a");
//            commandList.add("libmp3lame");
//            commandList.add("-q:a");
//            commandList.add("2");
//            commandList.add(outputPath);
//
//            Log.d(TAG, "FFmpeg command: " + commandList);
//
//            // Thực thi lệnh FFmpeg trong một luồng nền
//            new Thread(() -> {
//                int returnCode = FFmpeg.execute(commandList.toArray(new String[0]));
//                runOnUiThread(() -> {
//                    if (returnCode == RETURN_CODE_SUCCESS) {
//
//                        showToast("Trộn âm thanh thành công. Đã lưu tại: " + outputPath);
//                        Intent mp3cutter = new Intent(Mixer2Activity.this, SuccesActivity.class);
//                        mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mp3cutter.putExtra("outputAudioPath",outputPath);
//                        startActivity(mp3cutter);
//
//                        binding.mai.setVisibility(View.VISIBLE);
//                        binding.loading.setVisibility(View.GONE);
//                    } else {
//                        showToast("Không thể trộn âm thanh. Mã lỗi: " + returnCode);
//                        Log.e(TAG, "FFmpeg execution failed with return code: " + returnCode);
//                    }
//                    // Xóa các file tạm thời
//                    for (String path : convertedAudioPaths) {
//                        new File(path).delete();
//                    }
//                    tempDir.delete();
//                });
//            }).start();
//        } else {
//            showToast("Cần ít nhất 2 file âm thanh để trộn.");
//        }
//    }
}