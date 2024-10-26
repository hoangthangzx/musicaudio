package com.example.merge;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.SuccesActivity;
import com.example.mixer.selecadd;
import com.example.model.AudioFile;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityMergeBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogCreatBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogoutBinding;
import com.example.ultils.AudioUtils;
import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MergeActivity2 extends AppCompatActivity implements MergeAdapter.OnAudioClickListener{
    ActivityMergeBinding binding;
    MergeAdapter mergeAdapter;
    //    private Wave_form_merge waveFormMerge;
    private Handler handler = new Handler();
    private Runnable updateThumbRunnable;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ArrayList<String> audioFileNames = new ArrayList<>();
    private String audiopathmerge;
    private File tam;
    private File Dir;
    private  String a;
    private String currentPath;
    ArrayList<AudioFile> audioFilesList;
    private boolean play;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLocale(this);
        binding = ActivityMergeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        binding.recy.setLayoutManager(new GridLayoutManager(this, 1));

        audioFilesList= AudioUtils.getSelectedAudioFiles();
        Log.e("audioFilesList", "audioFilesList: " + audioFilesList);
        boolean isMerge = getIntent().getBooleanExtra("isMerge", false);
        // Setup MediaPlayer and adapter
        mediaPlayer = new MediaPlayer();
        mergeAdapter = new MergeAdapter(audioFilesList, this,this);
        binding.recy.setAdapter(mergeAdapter);
        ItemTouchHelper.Callback callback = new MergeActivity2.ItemTouchHelperCallback(mergeAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.recy);
        enabalfalse();
        if (isMerge) {
            ArrayList<AudioFile> newAudioFiles = AudioUtils.getSelectedAudioFiles();
            mergeAdapter.updateData();
        }
        binding.music.setOnClickListener(v ->{

            binding.waveformSeekBar.setEnabled(true);
            binding.cong.setEnabled(true);
            binding.chu.setEnabled(true);
            mergeAdapter.cleanup();
            playAudio(a);
            play=true;
        });

        binding.waveformSeekBar.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
            if (fromUser && mediaPlayer != null) {
                int newPosition = (int) (progress * mediaPlayer.getDuration() / 100f);
                mediaPlayer.seekTo(newPosition);
            }
        });
        binding.waveformSeekBar.setWaveWidth(Utils.dp(this, 1));
        binding.waveformSeekBar.setWaveGap(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveMinHeight(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveCornerRadius(Utils.dp(this, 2));
        binding.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        binding.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(this, R.color.white2));
        binding.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(this, R.color.progress_start));
        binding.cong.setOnClickListener(v -> adjustPlaybackPosition(-15));
        binding.chu.setOnClickListener(v -> adjustPlaybackPosition(15));
        binding.save.setOnClickListener(v->{
            cleanupTempFiles();

            showSaveDialog();
        });
        binding.imageView5.setOnClickListener(v -> {
            mergeAdapter.cleanup();
            showExitDialog();
        });

        binding.add.setOnClickListener(v->{
            Intent mixer = new Intent(MergeActivity2.this, selecadd.class);
            mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mixer.putExtra("key", "merger");
            startActivity(mixer);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                binding.music.setImageResource(R.drawable.paush);
                mediaPlayer.pause(); // Tạm dừng nhạc khi ứng dụng vào nền
            }
        });
        cleanuptam();
        convertAndMergetam(audioFilesList);
        binding.xoay.setOnClickListener(v->{
            binding.waveformSeekBar.setSampleFrom(a);
            binding.loadAni.setVisibility(View.GONE);
            binding.waveformSeekBar.setVisibility(View.VISIBLE);
            binding.xoay.setVisibility(View.GONE);
        });
    }
    public void playAudio(String audioPath) {
        try {
            // Nếu mediaPlayer đang phát một file khác
            if (mediaPlayer != null) {
                if (currentPath != null && currentPath.equals(audioPath)) {
                    // Nếu là file hiện tại - xử lý pause/resume
                    if (isPlaying) {
                        binding.music.setImageResource(R.drawable.paush);

                        pauseAudio();
                        if (updateThumbRunnable != null) {
                            handler.removeCallbacks(updateThumbRunnable);
                        }
                    } else {
                        resumeAudio();
                        binding.music.setImageResource(R.drawable.playxanh);
                        startUpdatingThumb();
                    }
                    return;
                } else {

                    stopAndReleasePlayer();
                }
            }

            // Tạo và phát file mới
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            currentPath = audioPath;
            binding.music.setImageResource(R.drawable.playxanh);
            startUpdatingThumb();
            // Thiết lập listener khi phát xong
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                binding.music.setImageResource(R.drawable.paush);
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi phát audio", Toast.LENGTH_SHORT).show();
        }
    }
    private void pauseAudio() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            // Có thể thêm code cập nhật UI ở đây
        }
    }

    private void resumeAudio() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            // Có thể thêm code cập nhật UI ở đây
        }
    }

    private void stopAndReleasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            currentPath = null;
        }
    }

    // Phương thức để lấy trạng thái phát
    public boolean isPlaying() {
        return isPlaying;
    }

    // Phương thức để lấy vị trí hiện tại
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    // Phương thức để lấy tổng thời gian
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    public void release() {
        stopAndReleasePlayer();
    }
  public void loadwaform(){
        binding.waveformSeekBar.setVisibility(View.GONE);
        binding.xoay.setVisibility(View.VISIBLE);
  }
    public void close(){
        binding.waveformSeekBar.setVisibility(View.VISIBLE);
        binding.xoay.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<AudioFile> newAudioFiles = AudioUtils.getSelectedAudioFiles();
        mergeAdapter.updateData();
    }

    @Override
    public void onAudioDelete(String audioUri) {
   mergeAdapter.cleanup();

   binding.music.setImageResource(R.drawable.paush);
        binding.waveformSeekBar.setVisibility(View.GONE);
        binding.loadAni.setVisibility(View.VISIBLE);
        binding.cong.setEnabled(false);
        binding.chu.setEnabled(false);
        play=false;
        handler.removeCallbacks(updateThumbRunnable);
        convertAndMergetam(audioFilesList);

    }

    @Override
    public void onclick() {
        binding.waveformSeekBar.setEnabled(false);
        binding.cong.setEnabled(false);
        binding.chu.setEnabled(false);
        binding.waveformSeekBar.setProgress(0);
        binding.music.setImageResource(R.drawable.paush);
        play=false;
        if (updateThumbRunnable != null) {
            handler.removeCallbacks(updateThumbRunnable);
        }
stopAndReleasePlayer();
    }

    @Override
    public void swap() {
        binding.music.setImageResource(R.drawable.paush);
        binding.waveformSeekBar.setVisibility(View.GONE);
        binding.loadAni.setVisibility(View.VISIBLE);
        binding.cong.setEnabled(false);
        binding.chu.setEnabled(false);
        play=false;
        handler.removeCallbacks(updateThumbRunnable);
        convertAndMergetam(audioFilesList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mergeAdapter.cleanup();
        }
        binding.music.setImageResource(R.drawable.paush);
    }
    private void enabaltrue(){
        binding.music.setEnabled(true);
        binding.cong.setEnabled(true);
        binding.chu.setEnabled(true);
        binding.waveformSeekBar.setEnabled(true);
    }
    private void enabalfalse(){
        binding.waveformSeekBar.setEnabled(false);
        binding.music.setEnabled(false);
        binding.cong.setEnabled(false);
        binding.chu.setEnabled(false);
    }

    private void convertToStandardFormat(String inputPath, String outputPath, Runnable onComplete) {
        // Enhanced conversion command with standardized audio parameters
        String[] cmd = {
                "-i", inputPath,           // Input file
                "-acodec", "pcm_s16le",   // Convert to PCM 16-bit
                "-ar", "44100",           // Sample rate 44.1kHz
                "-ac", "2",               // Stereo output
                "-y",                     // Overwrite output file
                outputPath
        };

        FFmpeg.executeAsync(cmd, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.i("FFmpeg", "Conversion to standard format succeeded: " + outputPath);
                onComplete.run();
            } else {
                Log.e("FFmpeg", "Conversion failed with returnCode: " + returnCode);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error converting audio file", Toast.LENGTH_SHORT).show();
                    binding.main.setVisibility(View.VISIBLE);
                    binding.loading.setVisibility(View.GONE);
                });
            }
        });
    }
    @Override
    public void onItemMoveCompleted(int fromPosition, int toPosition) {
        binding.loadAni.setVisibility(View.GONE);
        binding.waveformSeekBar.setVisibility(View.GONE);
binding.xoay.setVisibility(View.VISIBLE);
    }
    private void convertAndMergeAudioFiles2(ArrayList<AudioFile> audioFilesList, String newName) {
        String a = newName;
        Dir = new File(getExternalFilesDir(null), "TempAudio");
        if (!Dir.exists()) {
            Dir.mkdirs();
        }

        // Use LinkedHashMap to maintain insertion order
        LinkedHashMap<String, String> convertedFiles = new LinkedHashMap<>();
        AtomicInteger conversionCount = new AtomicInteger(0);
        int totalFiles = audioFilesList.size();

        // Show loading state
        binding.main.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);

        for (int i = 0; i < audioFilesList.size(); i++) {
            AudioFile audioFile = audioFilesList.get(i);
            String inputPath = audioFile.getUri();
            String name = audioFile.getName();
            // Add index prefix to ensure order is maintained
            String outputPath = new File(Dir, String.format("%03d_converted_%s.wav", i, System.currentTimeMillis()))
                    .getAbsolutePath();

            // Convert to WAV format with standardized parameters
            convertToStandardFormat(inputPath, outputPath, () -> {
                convertedFiles.put(name, outputPath);
                if (conversionCount.incrementAndGet() == totalFiles) {
                    // Once all files are converted, proceed with merging in order
                    ArrayList<String> orderedPaths = new ArrayList<>();

                    // Add files in the original order
                    for (AudioFile originalFile : audioFilesList) {
                        String convertedPath = convertedFiles.get(originalFile.getName());
                        if (convertedPath != null) {
                            orderedPaths.add(convertedPath);
                        }
                    }

                    mergeAudioFilesUsingFFmpeg2(orderedPaths, a);
                }
            });
        }
    }

    private void mergeAudioFilesUsingFFmpeg2(ArrayList<String> convertedFilePaths, String Names) {
        if (convertedFilePaths.size() < 2) {
            Toast.makeText(this, "Need at least 2 files to merge", Toast.LENGTH_SHORT).show();
            return;
        }

        File outputDir = new File(getExternalFilesDir(null), "MergeAudio");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String outputFormat = "mp3";
        String outputPath = new File(outputDir, Names + "." + outputFormat).getAbsolutePath();

        // Create file list maintaining order
        File fileList = new File(Dir, "filelist.txt");
        try {
            FileWriter writer = new FileWriter(fileList);
            for (String path : convertedFilePaths) {
                writer.write("file '" + path + "'\n");
            }
            writer.close();
        } catch (IOException e) {
            Log.e("MergeActivity", "Error creating file list: " + e.getMessage());
            return;
        }

        String[] cmd;
        if (outputFormat.equals("mp3")) {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileList.getAbsolutePath(),
                    "-c:a", "libmp3lame",
                    "-b:a", "320k",
                    "-y",
                    outputPath
            };
        } else if (outputFormat.equals("m4a")) {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileList.getAbsolutePath(),
                    "-c:a", "aac",
                    "-b:a", "256k",
                    "-y",
                    outputPath
            };
        } else {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileList.getAbsolutePath(),
                    "-acodec", "copy",
                    "-y",
                    outputPath
            };
        }

        FFmpeg.executeAsync(cmd, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.i("FFmpeg", "Merging succeeded: " + outputPath);
                audiopathmerge = outputPath;

                runOnUiThread(() -> {
                    binding.waveformSeekBar.setSampleFrom(audiopathmerge);
                    cleanupTempFiles();

                    Intent successIntent = new Intent(MergeActivity2.this, SuccesActivity.class);
                    successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    successIntent.putExtra("outputAudioPath", outputPath);
                    startActivity(successIntent);

                    binding.main.setVisibility(View.VISIBLE);
                    binding.loading.setVisibility(View.GONE);
                });
            } else {
                Log.e("FFmpeg", "Merging failed with returnCode: " + returnCode);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error merging audio files", Toast.LENGTH_SHORT).show();
                    binding.main.setVisibility(View.VISIBLE);
                    binding.loading.setVisibility(View.GONE);
                });
            }

            // Clean up the file list
            fileList.delete();
        });
    }
    private void cleanupTempFiles() {
        if (Dir != null && Dir.exists()) {
            File[] files = Dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
    private void cleanuptam() {
        if (tam != null && tam.exists()) {
            File[] files = tam.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
    private int currentAudioIndex = 0; // Chỉ mục cho file âm thanh hiện tại


    private void updatePlayPauseIcon() {
        if (isPlaying) {
            binding.music.setImageResource(R.drawable.playxanh);
        } else {
            binding.music.setImageResource(R.drawable.paush); // Đổi thành icon phát
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        updatePlayPauseIcon(); // Đảm bảo cập nhật icon khi MediaPlayer được giải phóng
    }


    private void adjustPlaybackPosition(int seconds) {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int newPosition = currentPosition + (seconds * 1000); // Convert seconds to milliseconds

            // Ensure new position is within bounds
            if (newPosition < 0) {
                newPosition = 0;
            } else if (newPosition > mediaPlayer.getDuration()) {
                newPosition = mediaPlayer.getDuration();
            }

            mediaPlayer.seekTo(newPosition); // Update the MediaPlayer's position
            binding.waveformSeekBar.setProgress((int) ((float) newPosition / mediaPlayer.getDuration() * 100)); // Update seek bar
        }
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
                    }

//                    updateCurrentTimeDisplay(currentPosition);

                    handler.postDelayed(this, 50);
                }
            };
            handler.post(updateThumbRunnable);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (updateThumbRunnable != null) {
            handler.removeCallbacks(updateThumbRunnable);
        }

        cleanupTempFiles();
        cleanuptam();
    }
    private static class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final MergeAdapter mAdapter;

        public ItemTouchHelperCallback(MergeAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Not implemented as isItemViewSwipeEnabled() returns false
        }
    }
    private void convertAndMergetam(ArrayList<AudioFile> audioFilesList) {
        tam = new File(getExternalFilesDir(null), "a");
        if (!tam.exists()) {
            tam.mkdirs();
        }

        // Use LinkedHashMap to maintain insertion order
        LinkedHashMap<String, String> convertedFiles = new LinkedHashMap<>();
        AtomicInteger conversionCount = new AtomicInteger(0);
        int totalFiles = audioFilesList.size();

        for (int i = 0; i < audioFilesList.size(); i++) {
            AudioFile audioFile = audioFilesList.get(i);
            String inputPath = audioFile.getUri();
            String name = audioFile.getName();
            // Add index prefix to ensure order is maintained
            String outputPath = new File(tam, String.format("%03d_converted_%s.wav", i, System.currentTimeMillis()))
                    .getAbsolutePath();
            final int index = i;

            // Convert to WAV format with standardized parameters
            convertToStandardtam(inputPath, outputPath, () -> {
                convertedFiles.put(name, outputPath);
                if (conversionCount.incrementAndGet() == totalFiles) {
                    // Once all files are converted, proceed with merging in order
                    ArrayList<String> orderedPaths = new ArrayList<>();
                    ArrayList<String> orderedNames = new ArrayList<>();

                    // Add files in the original order
                    for (AudioFile originalFile : audioFilesList) {
                        String convertedPath = convertedFiles.get(originalFile.getName());
                        if (convertedPath != null) {
                            orderedPaths.add(convertedPath);
                            orderedNames.add(originalFile.getName());
                        }
                    }

                    tam(orderedPaths, orderedNames);
                }
            });
        }
    }

    private void convertToStandardtam(String inputPath, String outputPath, Runnable onComplete) {
        // Enhanced conversion command with standardized audio parameters
        String[] cmd = {
                "-i", inputPath,           // Input file
                "-acodec", "pcm_s16le",   // Convert to PCM 16-bit
                "-ar", "44100",           // Sample rate 44.1kHz
                "-ac", "2",               // Stereo output
                "-y",                     // Overwrite output file
                outputPath
        };

        FFmpeg.executeAsync(cmd, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.i("FFmpeg", "Conversion to standard format succeeded: " + outputPath);
                onComplete.run();
            } else {
                Log.e("FFmpeg", "Conversion failed with returnCode: " + returnCode);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error converting audio file", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void tam(ArrayList<String> convertedFilePaths, ArrayList<String> audioFileNames) {
        if (convertedFilePaths.size() < 2) {
            Toast.makeText(this, "Need at least 2 files to merge", Toast.LENGTH_SHORT).show();
            return;
        }

        File outputDir = new File(getExternalFilesDir(null), "tamaudio");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String outputFormat = "mp3";
        String outputPath = new File(outputDir, "merged_" +
                System.currentTimeMillis() + "." + outputFormat).getAbsolutePath();

        // Create file list maintaining order
        File fileList = new File(tam, "filelist.txt");
        try {
            FileWriter writer = new FileWriter(fileList);
            for (String path : convertedFilePaths) {
                writer.write("file '" + path + "'\n");
            }
            writer.close();
        } catch (IOException e) {
            Log.e("MergeActivity", "Error creating file list: " + e.getMessage());
            return;
        }

        String[] cmd;
        if (outputFormat.equals("mp3")) {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileList.getAbsolutePath(),
                    "-c:a", "libmp3lame",
                    "-b:a", "320k",
                    "-y",
                    outputPath
            };
        } else if (outputFormat.equals("m4a")) {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileList.getAbsolutePath(),
                    "-c:a", "aac",
                    "-b:a", "256k",
                    "-y",
                    outputPath
            };
        } else {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", fileList.getAbsolutePath(),
                    "-acodec", "copy",
                    "-y",
                    outputPath
            };
        }

        FFmpeg.executeAsync(cmd, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                binding.loadAni.setVisibility(View.GONE);
                binding.waveformSeekBar.setVisibility(View.VISIBLE);
                Log.i("FFmpeg", "Merging succeeded: " + outputPath);
                a = outputPath;
                runOnUiThread(() -> {
                    enabaltrue();
                    binding.waveformSeekBar.setSampleFrom(outputPath);
                    cleanuptam();
                });
            } else {
                enabaltrue();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error merging audio files", Toast.LENGTH_SHORT).show();
                });
            }
            fileList.delete();
        });
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


        dialogBinding.clearIcon.setOnClickListener(v -> dialogBinding.editText.setText(""));
        dialogBinding.textView3.setOnClickListener(v -> dialog.dismiss());
        applyGradien2(dialogBinding.textView2);
        applyGradientToSaveText(dialogBinding.textView3);
        dialogBinding.textView2.setOnClickListener(v -> {
            String newName = dialogBinding.editText.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show();
                return;
            }
            int a=AudioUtils.getSelectedAudioFiles().size();
            if(a>1) {
                binding.main.setVisibility(View.GONE);
                binding.loading.setVisibility(View.VISIBLE);
                binding.music.setImageResource(R.drawable.paush);
                mediaPlayer.pause();
                binding.loading.post(() -> {
                    convertAndMergeAudioFiles2(audioFilesList,newName);
                });
                dialog.dismiss();
            }else {
                Toast.makeText(this, "Cần ít nhất 2 file âm thanh để trộn.", Toast.LENGTH_SHORT).show();
            }

        });

        dialog.show();
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
            if (updateThumbRunnable != null) {
                handler.removeCallbacks(updateThumbRunnable);
            }
            AudioUtils.clearSelectedAudioFiles();
            finish();
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

}
