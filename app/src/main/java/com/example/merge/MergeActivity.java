//package com.example.merge;
//
//import static com.example.ultils.SystemUtils.setLocale;
//import static com.example.ultils.Untils.showSystemUI;
//
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.LinearGradient;
//import android.graphics.Shader;
//import android.graphics.drawable.ColorDrawable;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.arthenica.mobileffmpeg.Config;
//import com.arthenica.mobileffmpeg.FFmpeg;
//import com.example.Home.HomeActivity;
//import com.example.SuccesActivity;
//import com.example.mixer.MixerActivity;
//import com.example.mixer.SelectActivity;
//import com.example.model.AudioFile;
//import com.example.st046_audioeditorandmusiceditor.R;
//import com.example.st046_audioeditorandmusiceditor.databinding.ActivityMergeBinding;
//import com.example.st046_audioeditorandmusiceditor.databinding.DialogCreatBinding;
//import com.example.st046_audioeditorandmusiceditor.databinding.DialogoutBinding;
//import com.example.ultils.AudioUtils;
//import com.masoudss.lib.utils.Utils;
//import com.masoudss.lib.utils.WaveGravity;
//
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class MergeActivity extends AppCompatActivity implements MergeAdapter.OnAudioClickListener{
//    ActivityMergeBinding binding;
//    MergeAdapter mergeAdapter;
////    private Wave_form_merge waveFormMerge;
//    private Handler handler = new Handler();
//    private Runnable updateThumbRunnable;
//    private MediaPlayer mediaPlayer;
//    private boolean isPlaying = false;
//private File Dir;
//    private String audiopathmerge;
//    private File tempDir;
//private String name ;
//private   ArrayList<AudioFile> audioFilesList;
//
//    public void setName(String name) {
//        this.name = name;
//    }
//private boolean a;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setLocale(this);
//        binding = ActivityMergeBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        showSystemUI(this, true);
//
//        binding.recy.setLayoutManager(new GridLayoutManager(this, 1));
//        Intent intent = getIntent();
//        audioFilesList = intent.getParcelableArrayListExtra("audio_files_list");
//
//        if (audioFilesList != null && !audioFilesList.isEmpty()) {
//            for (AudioFile audioFile : audioFilesList) {
//                Log.d("RemoveAudioFile", "Name: " + audioFile.getName() +
//                        ", URI: " + audioFile.getUri() +
//                        ", Size: " + audioFile.getSize() +
//                        ", Duration: " + audioFile.getDuration() +
//                        ", Date: " + audioFile.getDate());
//            }
//        } else {
//
//        }
//            AudioUtils.saveSelectedAudioFiles(audioFilesList);
//        ArrayList<AudioFile> savedAudioFiles = AudioUtils.getSelectedAudioFiles();
//        if (savedAudioFiles != null && !savedAudioFiles.isEmpty()) {
//            for (AudioFile audioFile : savedAudioFiles) {
//                Log.d("savedAudioFiles", "Name: " + audioFile.getName() +
//                        ", URI: " + audioFile.getUri() +
//                        ", Size: " + audioFile.getSize() +
//                        ", Duration: " + audioFile.getDuration() +
//                        ", Date: " + audioFile.getDate());
//            }
//        } else {
//            Log.d("savedAudioFiles", "No audio files were saved.");
//        }
//
//        mediaPlayer = new MediaPlayer();
//        mergeAdapter = new MergeAdapter(AudioUtils.getSelectedAudioFiles(), this,mediaPlayer,this::onAudioDelete);
//        binding.recy.setAdapter(mergeAdapter);
//        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mergeAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(binding.recy);
//
//        ArrayList<AudioFile> a = AudioUtils.getSelectedAudioFiles();
//            convertAndMergeAudioFiles(a);
//
//        binding.music.setOnClickListener(v -> {
//            if (    AudioUtils.getSelectedAudioFiles().size() >1){
//                playAudio();
//            } {
//                for (AudioFile audioFile : audioFilesList) {
//             playAudio2(audioFile.getUri() );
//                }
//            }
//
//        });
//
//        binding.waveformSeekBar.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
//            if (fromUser && mediaPlayer != null) {
//                int newPosition = (int) (progress * mediaPlayer.getDuration() / 100f);
//                mediaPlayer.seekTo(newPosition);
//            }
//        });
//        binding.waveformSeekBar.setWaveWidth(Utils.dp(this, 1));
//        binding.waveformSeekBar.setWaveGap(Utils.dp(this, 2));
//        binding.waveformSeekBar.setWaveMinHeight(Utils.dp(this, 2));
//        binding.waveformSeekBar.setWaveCornerRadius(Utils.dp(this, 2));
//        binding.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
//        binding.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(this, R.color.white2));
//        binding.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(this, R.color.progress_start));
//        binding.cong.setOnClickListener(v -> adjustPlaybackPosition(-15));
//        binding.chu.setOnClickListener(v -> adjustPlaybackPosition(15));
//binding.save.setOnClickListener(v->{
//showSaveDialog();
//});
//        binding.imageView5.setOnClickListener(v -> {
//            showExitDialog();
//        });
//binding.add.setOnClickListener(v->{
//    AudioUtils.saveSelectedAudioFiles(audioFilesList);
//    ArrayList<AudioFile> saved = AudioUtils.getSelectedAudioFiles();
//    if (saved != null && !saved.isEmpty()) {
//        for (AudioFile audioFile : saved) {
//            Log.d("savedAudioFiles", "Name: " + audioFile.getName() +
//                    ", URI: " + audioFile.getUri() +
//                    ", Size: " + audioFile.getSize() +
//                    ", Duration: " + audioFile.getDuration() +
//                    ", Date: " + audioFile.getDate());
//        }
//    } else {
//        Log.d("savedAudioFiles", "No audio files were saved.");
//    }
//    Intent mixer = new Intent(MergeActivity.this, SelectActivity.class);
//    mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    mixer.putExtra("key", "merger");
//    startActivity(mixer);
//    mediaPlayer.stop();
//});
//
//    }
//
//    @Override
//    public void onItemMoveCompleted(int fromPosition, int toPosition) {
//
//    }
//
//    @Override
//    public void onAudioDelete(String audioUri) {
//        Intent intent = getIntent();
//        ArrayList<AudioFile> audioFilesList = intent.getParcelableArrayListExtra("audio_files_list");
//        if (AudioUtils.getSelectedAudioFiles().size() >1){
//            convertAndMergeAudioFiles(audioFilesList);
//        } {
//            for (AudioFile audioFile : audioFilesList) {
//                String inputPath = audioFile.getUri();
//                binding.waveformSeekBar.setSampleFrom(inputPath);
//                };
//            }
//
//    }
//
//    private static class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
//        private final MergeAdapter mAdapter;
//
//        public ItemTouchHelperCallback(MergeAdapter adapter) {
//            mAdapter = adapter;
//        }
//
//        @Override
//        public boolean isLongPressDragEnabled() {
//            return true;
//        }
//
//        @Override
//        public boolean isItemViewSwipeEnabled() {
//            return false;
//        }
//
//        @Override
//        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//            return makeMovementFlags(dragFlags, 0);
//        }
//
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                              RecyclerView.ViewHolder target) {
//
//            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//            return true;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            // Not implemented as isItemViewSwipeEnabled() returns false
//        }
//    }
//    private interface a{
//        void swipa(String a);
//    }
//    private void convertAndMergeAudioFiles2(ArrayList<AudioFile> audioFilesList) {
//        Dir = new File(getExternalFilesDir(null), "a");
//        if (!Dir.exists()) {
//            Dir.mkdirs();
//        }
//
//        ArrayList<String> convertedFilePaths = new ArrayList<>();
//        AtomicInteger conversionCount = new AtomicInteger(0);
//
//        for (AudioFile audioFile : audioFilesList) {
//            String inputPath = audioFile.getUri();
//            String outputPath = new File(tempDir, "converted_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
//            convertToMp3(inputPath, outputPath, () -> {
//                convertedFilePaths.add(outputPath);
//                if (conversionCount.incrementAndGet() == audioFilesList.size()) {
//                    mergeAudioFilesUsingFFmpeg2(convertedFilePaths);
//                }
//            });
//        }
//    }
//    private void playAudio() {
//        if (audiopathmerge == null || audiopathmerge.isEmpty()) {
//            Log.e("MergeActivity", "Audio path is null or empty");
//            return; // Do nothing if the audio path is invalid
//        }
//
//        File audioFile = new File(audiopathmerge);
//        if (!audioFile.exists()) {
//            Log.e("MergeActivity", "Audio file does not exist: " + audiopathmerge);
//            return; // Exit if the audio file does not exist
//        }
//
//        // Check if MediaPlayer is null or in an error state
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
//                Log.e("MergeActivity", "MediaPlayer Error: what=" + what + ", extra=" + extra);
//                releaseMediaPlayer(); // Release the MediaPlayer on error
//                return true; // Handle the error
//            });
//
//            // Set up a listener for when the audio finishes playing
//            mediaPlayer.setOnCompletionListener(mp -> {
//                binding.music.setImageResource(R.drawable.paush);
//                Log.i("MergeActivity", "Playback completed.");
//                resetMediaPlayer(); // Reset the MediaPlayer to the default state
//            });
//        }
//
//        try {
//            if (!isPlaying) {
//                if (!mediaPlayer.isPlaying()) {
//                    mediaPlayer.reset(); // Reset the player before setting the data source
//                    mediaPlayer.setDataSource(audiopathmerge); // Set the data source only once
//                    mediaPlayer.prepare(); // Prepare the media player only once
//                }
//                mediaPlayer.start(); // Start playback from the current position
//                isPlaying = true; // Update playback state
//                startUpdatingThumb(); // Start updating the UI for the thumb position
//                updatePlayPauseIcon(); // Update UI icon
//            } else {
//                mediaPlayer.pause(); // Pause if already playing
//                isPlaying = false; // Update playback state
//                updatePlayPauseIcon(); // Update UI icon
//            }
//        } catch (IOException e) {
//            Log.e("MergeActivity", "Error preparing or starting MediaPlayer: " + e.getMessage());
//            releaseMediaPlayer(); // Release the MediaPlayer on exception
//        }
//    }
//
//    private void resetMediaPlayer() {
//        if (mediaPlayer != null) {
//            mediaPlayer.reset(); // Reset the MediaPlayer
//            isPlaying = false; // Set playing state to false
//            updatePlayPauseIcon(); // Update UI icon to reflect the stopped state
//            // Optionally, you may want to clear the audio path or set it to a default state
//        }
//    }
//
//
//    private void updatePlayPauseIcon() {
//        if (isPlaying) {
//         binding.music.setImageResource(R.drawable.play);
//        } else {
//            binding.music.setImageResource(R.drawable.paush); // Đổi thành icon phát
//        }
//    }
//    private void mergeAudioFilesUsingFFmpeg2(ArrayList<String> convertedFilePaths) {
//        if (convertedFilePaths.size() < 2) return;
//        String outputPath = getOutputPath(name);
//        Log.d("MergeActivity", "Output path: " + outputPath);
//
//        StringBuilder concatCommand = new StringBuilder("-y -i \"concat:");
//        for (int i = 0; i < convertedFilePaths.size(); i++) {
//            concatCommand.append(convertedFilePaths.get(i));
//            if (i < convertedFilePaths.size() - 1) {
//                concatCommand.append("|");
//            }
//        }
//        concatCommand.append("\" -acodec copy ").append(outputPath);
//
//        FFmpeg.executeAsync(concatCommand.toString(), (executionId, returnCode) -> {
//            if (returnCode == Config.RETURN_CODE_SUCCESS) {
//                Log.i("FFmpeg", "Merging succeeded.");
//                audiopathmerge = outputPath;
//                binding.waveformSeekBar.setSampleFrom(audiopathmerge);
////                loadAudioData(audiopathmerge);
//                cleanupTempFilessave();
//                Intent mp3cutter = new Intent(MergeActivity.this, SuccesActivity.class);
//                mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                mp3cutter.putExtra("outputAudioPath",outputPath);
//                startActivity(mp3cutter);
//
//                binding.main.setVisibility(View.VISIBLE);
//                binding.loading.setVisibility(View.GONE);
//            } else {
//                Log.e("FFmpeg", "Merging failed with returnCode: " + returnCode);
//            }
//        });
//    }
//    private void adjustPlaybackPosition(int seconds) {
//        if (mediaPlayer != null) {
//            int currentPosition = mediaPlayer.getCurrentPosition();
//            int newPosition = currentPosition + (seconds * 1000); // Convert seconds to milliseconds
//
//            // Ensure new position is within bounds
//            if (newPosition < 0) {
//                newPosition = 0;
//            } else if (newPosition > mediaPlayer.getDuration()) {
//                newPosition = mediaPlayer.getDuration();
//            }
//
//            mediaPlayer.seekTo(newPosition); // Update the MediaPlayer's position
//            binding.waveformSeekBar.setProgress((int) ((float) newPosition / mediaPlayer.getDuration() * 100)); // Update seek bar
//        }
//    }
//    private void convertAndMergeAudioFiles(ArrayList<AudioFile> audioFilesList) {
//
//        tempDir = new File(getExternalFilesDir(null), "TempAudio");
//        if (!tempDir.exists()) {
//            tempDir.mkdirs();
//        }
//        cleanupTempFiles();
//        ArrayList<String> convertedFilePaths = new ArrayList<>();
//        AtomicInteger conversionCount = new AtomicInteger(0);
//
//        for (AudioFile audioFile : audioFilesList) {
//            String inputPath = audioFile.getUri();
//            String outputPath = new File(tempDir, "converted_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
//            convertToMp3(inputPath, outputPath, () -> {
//                convertedFilePaths.add(outputPath);
//                if (conversionCount.incrementAndGet() == audioFilesList.size()) {
//                    mergeAudioFilesUsingFFmpeg(convertedFilePaths);
//                }
//            });
//        }
//    }
//
//    private void convertToMp3(String inputPath, String outputPath, Runnable onComplete) {
//        String[] cmd = {"-i", inputPath, "-c:a", "libmp3lame", "-b:a", "192k", outputPath};
//        FFmpeg.executeAsync(cmd, (executionId, returnCode) -> {
//            if (returnCode == Config.RETURN_CODE_SUCCESS) {
//                Log.i("FFmpeg", "Conversion to MP3 succeeded: " + outputPath);
//                onComplete.run();
//            } else {
//                Log.e("FFmpeg", "Conversion to MP3 failed with returnCode: " + returnCode);
//            }
//        });
//    }
//
//    private void mergeAudioFilesUsingFFmpeg(ArrayList<String> convertedFilePaths) {
//        if (convertedFilePaths.size() < 2) return;
//
//        String outputPath = getOutputPathtmp();
//        Log.d("MergeActivity", "Output path: " + outputPath);
//
//        StringBuilder concatCommand = new StringBuilder("-y -i \"concat:");
//        for (int i = 0; i < convertedFilePaths.size(); i++) {
//            concatCommand.append(convertedFilePaths.get(i));
//            if (i < convertedFilePaths.size() - 1) {
//                concatCommand.append("|");
//            }
//        }
//        concatCommand.append("\" -acodec copy ").append(outputPath);
//
//        FFmpeg.executeAsync(concatCommand.toString(), (executionId, returnCode) -> {
//            if (returnCode == Config.RETURN_CODE_SUCCESS) {
//                Log.i("FFmpeg", "Merging succeeded.");
//                audiopathmerge = outputPath;
//                binding.waveformSeekBar.setSampleFrom(audiopathmerge);
////                loadAudioData(audiopathmerge);
//                cleanupTempFiles();
//            } else {
//                Log.e("FFmpeg", "Merging failed with returnCode: " + returnCode);
//            }
//        });
//    }
//    private void cleanupTempFilessave() {
//        if (Dir != null && Dir.exists()) {
//            File[] files = tempDir.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    file.delete();
//                }
//            }
//        }
//    }
//    private void cleanupTempFiles() {
//        if (tempDir != null && tempDir.exists()) {
//            File[] files = tempDir.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    file.delete();
//                }
//            }
//        }
//    }
//
//    private String getOutputPathtmp() {
//        File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "tmp");
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();
//        }
//        return new File(outputDir, "output.mp3").getAbsolutePath();
//    }
//    private String getOutputPath(String fileName) {
//        File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "MergeAudio");
//
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();  // Create the directory if it doesn't exist
//        }
//
//        // Use the passed file name instead of hardcoding "output.mp3"
//        File outputFile = new File(outputDir, fileName + ".mp3");
//
//        return outputFile.getAbsolutePath();  // Return the full file path
//    }
//
//    private String getOutputPathcheck() {
//        String fileName =name;
//        File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "MergeAudio");
//
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();  // Create the directory if it doesn't exist
//        }
//
//        // Use the passed file name and append ".mp3"
//        File outputFile = new File(outputDir, fileName + ".mp3");
//        if (outputFile.exists()) {
//            Toast.makeText(this, "File already exists!", Toast.LENGTH_SHORT).show();
//            a=false;
//        }
//        else {
//            a = true;
//
//        }
//
//        return outputFile.getAbsolutePath();  // Return the full file path
//    }
//
//    private void startUpdatingThumb() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            updateThumbRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    int duration = mediaPlayer.getDuration();
//
//                    if (duration > 0) {
//                        float progress = (float) currentPosition / duration * 100;
//                        binding.waveformSeekBar.setProgress((int) progress);
//                    }
//
////                    updateCurrentTimeDisplay(currentPosition);
//
//                    handler.postDelayed(this, 10);
//                }
//            };
//            handler.post(updateThumbRunnable);
//        }
//    }
//    private void releaseMediaPlayer() {
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        isPlaying = false;
//        updatePlayPauseIcon(); // Đảm bảo cập nhật icon khi MediaPlayer được giải phóng
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        if (updateThumbRunnable != null) {
//            handler.removeCallbacks(updateThumbRunnable);
//        }
//
//        mergeAdapter.cleanup();
//        cleanupTempFiles();
//    }
//    private void playAudio2(String audioPath) {
//   audiopathmerge=audioPath;
//        if (audiopathmerge == null || audiopathmerge.isEmpty()) {
//            Log.e("MergeActivity", "Audio path is null or empty");
//            return; // Do nothing if the audio path is invalid
//        }
//
//        File audioFile = new File(audiopathmerge);
//        if (!audioFile.exists()) {
//            Log.e("MergeActivity", "Audio file does not exist: " + audiopathmerge);
//            return; // Exit if the audio file does not exist
//        }
//
//        // Check if MediaPlayer is null or in an error state
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
//                Log.e("MergeActivity", "MediaPlayer Error: what=" + what + ", extra=" + extra);
//                releaseMediaPlayer(); // Release the MediaPlayer on error
//                return true; // Handle the error
//            });
//        }
//
//        try {
//            if (!isPlaying) {
//                if (!mediaPlayer.isPlaying()) {
//                    mediaPlayer.reset(); // Reset the player before setting the data source
//                    mediaPlayer.setDataSource(audiopathmerge); // Set the data source only once
//                    mediaPlayer.prepare(); // Prepare the media player only once
//                }
//                mediaPlayer.start(); // Start playback from the current position
//                isPlaying = true; // Update playback state
//                startUpdatingThumb(); // Start updating the UI for the thumb position
//                updatePlayPauseIcon(); // Update UI icon
//            } else {
//                mediaPlayer.pause(); // Pause if already playing
//                isPlaying = false; // Update playback state
//                updatePlayPauseIcon(); // Update UI icon
//            }
//        } catch (IOException e) {
//            Log.e("MergeActivity", "Error preparing or starting MediaPlayer: " + e.getMessage());
//            releaseMediaPlayer(); // Release the MediaPlayer on exception
//        }
//    }
////    private static class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
////        private final MergeAdapter mAdapter;
////
////        public ItemTouchHelperCallback(MergeAdapter adapter) {
////            mAdapter = adapter;
////        }
////
////        @Override
////        public boolean isLongPressDragEnabled() {
////            return true;
////        }
////
////        @Override
////        public boolean isItemViewSwipeEnabled() {
////            return false;
////        }
////
////        @Override
////        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
////            return makeMovementFlags(dragFlags, 0);
////        }
////
////        @Override
////        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
////                              RecyclerView.ViewHolder target) {
////            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
////            return true;
////        }
////
////        @Override
////        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
////
////        }
////    }
//
//    private void showSaveDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        DialogCreatBinding dialogBinding = DialogCreatBinding.inflate(getLayoutInflater());
//        builder.setView(dialogBinding.getRoot());
//
//        AlertDialog dialog = builder.create();
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        String currentName = name != null ? name : "";
//        dialogBinding.editText.setText(currentName);
//        dialogBinding.editText.setSelection(currentName.length());
//
//        dialogBinding.clearIcon.setOnClickListener(v -> dialogBinding.editText.setText(""));
//        dialogBinding.textView3.setOnClickListener(v -> dialog.dismiss());
//
//        dialogBinding.textView2.setOnClickListener(v -> {
//            String newName = dialogBinding.editText.getText().toString().trim();
//setName(newName);
//getOutputPathcheck();
//            if (newName.isEmpty()) {
//                Toast.makeText(this, "Please enter a valid file name", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(a){
//                binding.main.setVisibility(View.GONE);
//                binding.loading.setVisibility(View.VISIBLE);
//                cleanupTempFiles();
//                convertAndMergeAudioFiles2(audioFilesList);
//                dialog.dismiss();
//            }else {
//                dialog.dismiss();
//            }
//
//        });
//
//        dialog.show();
//    }
//    private void showExitDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        DialogoutBinding dialogBinding = DialogoutBinding.inflate(getLayoutInflater());
//        builder.setView(dialogBinding.getRoot());
//
//        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
////        applyGradientToSaveText(dialogBinding.tex1);
//        applyGradientToSaveText(dialogBinding.tex2);
//
//        dialogBinding.yes.setOnClickListener(v->{
//
//            Intent mixer = new Intent(MergeActivity.this, HomeActivity.class);
//            mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            mixer.putExtra("key", "mixer");
//            startActivity(mixer);
//finishAffinity();
//        });
//        dialogBinding.no.setOnClickListener(v->{
//            dialog.dismiss();
//        });
//
//
//        dialog.show();
//    }
//
//    private void applyGradientToSaveText(TextView textView) {
//        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
//                new int[]{
//                        Color.parseColor("#6573ED"), // Top color (20%)
//                        Color.parseColor("#14D2E6")  // Bottom color (80%)
//                },
//                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom
//
//        textView.getPaint().setShader(textShader);
//    }
//}
