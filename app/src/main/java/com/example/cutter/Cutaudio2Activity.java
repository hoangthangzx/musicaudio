package com.example.cutter;



import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.SuccesActivity;
import com.example.customview.CutaudioAudio;
import com.example.customview.WaveformViewcutter;
//import com.example.merge.MergeActivity;
import com.example.merge.MergeActivity2;
import com.example.mixer.Mixer2Activity;
import com.example.model.AudioFile;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityCutaudio2Binding;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityCutaudioBinding;
import com.example.ultils.AudioUtils;
import com.example.ultils.Untils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cutaudio2Activity extends AppCompatActivity {
    private static final String TAG = "CutaudioActivity";
    private ActivityCutaudio2Binding binding;
    private WaveformSeekBar seekbar;
    private Custombar cutaudioAudio;
    private float audioDurationInMs ;
    private static final int NUM_COLUMNS = 80;
    private MediaPlayer mediaPlayer;
    private String audioPath;
    private Handler handler = new Handler();
    private Runnable updateThumbRunnable;
    private float total=10000;
private String key;
    private void startUpdatingThumb() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            updateThumbRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        if (duration > 0) {
                            float progress = (float) currentPosition / duration;
                            cutaudioAudio.updateThumbPosition(progress);
                        }
                        handler.postDelayed(this, 50);
                    }
                }
            };
            handler.post(updateThumbRunnable);
        }

    }
    // Dừng cập nhật thumb khi nhạc dừng
    private void stopUpdatingThumb() {
        if (updateThumbRunnable != null) {
            handler.removeCallbacks(updateThumbRunnable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCutaudio2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Untils.showSystemUI(this , true);
        audioPath = getIntent().getStringExtra("outputAudioPath");
        String audioname = getIntent().getStringExtra("audioname");
        String audiodur = getIntent().getStringExtra("audiodur");
        String date = getIntent().getStringExtra("date");
        String size = getIntent().getStringExtra("size");
        AudioFile audioFile = new AudioFile(audioPath, audioname, size, audiodur, date);

        // Create an ArrayList to hold AudioFile objects
        ArrayList<AudioFile> audioFilesList = new ArrayList<>();
        audioFilesList.add(audioFile);  // Add the AudioFile object to the list
        binding.duration.setText(audiodur);
        binding.textView15.setText(audiodur);
        if (audioname.length() > 30) {
            audioname = audioname.substring(0, 30) + "...";
        }
        binding.name.setText(audioname);
        binding.date.setText(date);
        binding.size.setText(size);
        audioDurationInMs = convertTimeToMilliseconds(audiodur);
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
            if(total<1000){
                Toast.makeText(this, R.string.minimum_cutting_time_1_s, Toast.LENGTH_SHORT).show();
            }else {
                binding.main.setVisibility(View.GONE);
                binding.loading.setVisibility(View.VISIBLE);
                binding.loading.post(() -> saveAudioInBackground());
            }
        });
        binding.music.setOnClickListener(v->{
            playAudio();
        });
        startUpdatingTime();
        audioDurationInMs = convertTimeToMilliseconds(audiodur);
        int   totalDuration = (int) audioDurationInMs;


        seekbar = binding.waveformView;
        seekbar.setSampleFrom(audioPath);
        binding.reload.setOnClickListener(v->{
            recreate();
            binding.reload.setVisibility(View.GONE);
        });
        binding.imageView5.setOnClickListener(v->{
            finish();
        });
        cutaudioAudio.setListener(new Custombar.OnThumbPosition() {
            @Override
            public void onThumbPositionChanged(float position) {

            }
            @Override
            public void onSliderChanged(float progress) {
                binding.reload.setVisibility(View.VISIBLE);
                float startTime = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
                float endTime = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();
                seekbar.setAudioDuration(totalDuration+2500, startTime, endTime);
                Log.d("AudioSeekBar", "Tổng thời gian: " + totalDuration + ", Thời gian bắt đầu: " +startTime+ ", Thời gian kết thúc: " +endTime);
                seekbar.isActivated();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    binding.music.setImageResource(R.drawable.paush);
                    stopUpdatingThumb();
                }
            }

        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void startUpdatingTime() {
        handler.post(updateTimeRunnable);
    }

    // Dừng cập nhật thời gian khi không cần thiết
    private void stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable);
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                // Lấy startTime và endTime từ cutaudioAudio
                float startTime = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
                float endTime = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();
total= endTime-startTime;
                if (startTime <= 0) {
                    binding.textView14.setText("00:00");
                } else {

                    binding.textView14.setText(formatTime((int) startTime));
                }

                if (endTime <= 0) {
                    String audiodur = getIntent().getStringExtra("audiodur");
                    binding.time2.setText(audiodur != null ? audiodur : "00:00");
                } else {
                    binding.time2.setText(formatTime((int) endTime));
                }

                // Đặt lại thời gian chạy của Handler sau 1 giây
                handler.postDelayed(this, 1000);
            }
        }
    };

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void saveAudioInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            saveAudio();
        });
    }

    private void saveAudio() {
        float startTimeInMs = cutaudioAudio.getStartThumb() * mediaPlayer.getDuration();
        float endTimeInMs = cutaudioAudio.getEndThumb() * mediaPlayer.getDuration();

        // Chuẩn bị thư mục
        File outputDir = new File(getExternalFilesDir(null), "/tam");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String audioname = getIntent().getStringExtra("audioname");

        // Tạo tên file mới nếu file đã tồn tại
        String finalFileName = generateUniqueFileName(outputDir, audioname);
        File outputFile = new File(outputDir, finalFileName);

        // Chuẩn bị command để cut audio
        String[] cmd;
        String audioFormat = getAudioFormat(audioPath);
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

        try {
            FFmpeg.execute(cmd);

            long trimmedDuration = (long) (endTimeInMs - startTimeInMs);

            // Thu thập thông tin file sau khi lưu thành công
            String filePath = outputFile.getPath();
            String fileNameSaved = outputFile.getName();
            String fileSize = String.valueOf(outputFile.length() / 1024) + " KB";
            String audioDuration = formatDuration(trimmedDuration);
            String fileDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            AudioFile audioFile = new AudioFile(filePath, fileNameSaved, fileSize, audioDuration, fileDate);
            AudioUtils.removeSelectedAudioFile(audioPath);
            AudioUtils.addSelectedAudioFile(audioFile);

            runOnUiThread(() -> {
                key = getIntent().getStringExtra("key");
                if ("mixer".equals(key)) {
//                    Intent mp3cutter = new Intent(Cutaudio2Activity.this, Mixer2Activity.class);
//                    mp3cutter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(mp3cutter);
                    finish();
                }
                if ("merge".equals(key)) {
//                    Intent mp3cutter = new Intent(Cutaudio2Activity.this, MergeActivity2.class);
//                    mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    mp3cutter.putExtra("isMerge", true);
//                    startActivity(mp3cutter);
                    finish();
                }

                stopUpdatingThumb();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error saving file", e);
            runOnUiThread(() -> Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show());
        }
    }


    // Thêm phương thức mới để tạo tên file độc nhất
    private String generateUniqueFileName(File directory, String originalFileName) {
        String nameWithoutExtension;
        String extension;

        // Tách phần tên và phần mở rộng
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            nameWithoutExtension = originalFileName.substring(0, lastDotIndex);
            extension = originalFileName.substring(lastDotIndex);
        } else {
            nameWithoutExtension = originalFileName;
            extension = "";
        }

        // Thử tên file gốc trước
        File file = new File(directory, originalFileName);
        if (!file.exists()) {
            return originalFileName;
        }

        // Nếu file đã tồn tại, thêm *1
        String newFileName = nameWithoutExtension + "*1" + extension;
        file = new File(directory, newFileName);
        if (!file.exists()) {
            return newFileName;
        }

        // Nếu vẫn tồn tại, tăng số lên cho đến khi tìm được tên độc nhất
        int counter = 2;
        while (file.exists()) {
            newFileName = nameWithoutExtension + "*" + counter + extension;
            file = new File(directory, newFileName);
            counter++;
        }

        return newFileName;
    }


    private String formatDuration(long durationInMs) {
        int minutes = (int) (durationInMs / 1000 / 60);
        int seconds = (int) (durationInMs / 1000 % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
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

                mediaPlayer.seekTo((int) startTime);

                mediaPlayer.start();
                startUpdatingThumb();

                binding.music.setImageResource(R.drawable.playxanh);

                mediaPlayer.setOnSeekCompleteListener(mp -> {
                    if (mediaPlayer.getCurrentPosition() >= endTime) {
                        mediaPlayer.pause();
                        stopUpdatingThumb();
                        mediaPlayer.seekTo((int) startTime); // Reset to start position
                        binding.music.setImageResource(R.drawable.paush); // Update icon
                    }
                });
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
                    // Cập nhật icon thành paush khi nhạc phát xong
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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopUpdatingTime();
        stopUpdatingThumb();
    }
//    public void decodeMP3(String audioPath) {
//        MediaExtractor extractor = new MediaExtractor();
//        MediaCodec codec = null;
//
//        try {
//            File file = new File(audioPath);
//            if (!file.exists()) {
//                Log.e(TAG, "File does not exist: " + audioPath);
//                return;
//            }
//
//            extractor.setDataSource(new FileInputStream(file).getFD());
//
//            int numTracks = extractor.getTrackCount();
//            MediaFormat format = null;
//            for (int i = 0; i < numTracks; i++) {
//                format = extractor.getTrackFormat(i);
//                String mime = format.getString(MediaFormat.KEY_MIME);
//                Log.d(TAG, "MIME type: " + mime);
//
//                if (mime.startsWith("audio/")) {
//                    extractor.selectTrack(i);
//                    break;
//                }
//            }
//
//            if (format == null) {
//                Log.e(TAG, "No audio track found in the file.");
//                return;
//            }
//
//            String mimeType = format.getString(MediaFormat.KEY_MIME);
//            codec = MediaCodec.createDecoderByType(mimeType);
//            codec.configure(format, null, null, 0);
//            codec.start();
//
//            ByteBuffer[] inputBuffers = codec.getInputBuffers();
//            ByteBuffer[] outputBuffers = codec.getOutputBuffers();
//            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//            boolean isEOS = false;
//            long timeoutUs = 10000;
//
//            // Lấy thời gian phát của âm thanh
//            long durationUs = format.getLong(MediaFormat.KEY_DURATION); // Thời gian phát bằng micro giây
//            Log.d(TAG, "Audio duration (us): " + durationUs);
//
//            long segmentDurationUs = durationUs / NUM_COLUMNS; // Thời gian cho mỗi cột (micro giây)
//
//            float[] segmentAmplitudes = new float[NUM_COLUMNS]; // Lưu giá trị độ cao mỗi cột
//            int[] segmentSampleCounts = new int[NUM_COLUMNS];   // Đếm số lượng mẫu trong mỗi cột
//
//            long currentSegmentEndTimeUs = segmentDurationUs; // Thời gian kết thúc của cột đầu tiên
//
//            while (!isEOS) {
//                int inputBufferIndex = codec.dequeueInputBuffer(timeoutUs);
//                if (inputBufferIndex >= 0) {
//                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                    int sampleSize = extractor.readSampleData(inputBuffer, 0);
//
//                    if (sampleSize < 0) {
//                        codec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                        isEOS = true;
//                    } else {
//                        long presentationTimeUs = extractor.getSampleTime();
//                        codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
//                        extractor.advance();
//                    }
//                }
//
//                int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs);
//                if (outputBufferIndex >= 0) {
//                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//                    byte[] chunk = new byte[bufferInfo.size];
//                    outputBuffer.get(chunk);
//                    outputBuffer.clear();
//
//                    short[] audioShorts = convertBytesToShorts(chunk);
//                    long presentationTimeUs = bufferInfo.presentationTimeUs;
//
//                    // Tính toán âm lượng cho từng đoạn tương ứng với mỗi cột
//                    int currentSegmentIndex = (int) (presentationTimeUs / segmentDurationUs);
//                    currentSegmentIndex = Math.min(currentSegmentIndex, NUM_COLUMNS - 1); // Đảm bảo không vượt quá số cột
//
//                    for (short audioShort : audioShorts) {
//                        segmentAmplitudes[currentSegmentIndex] += Math.abs(audioShort);
//                        segmentSampleCounts[currentSegmentIndex]++;
//                    }
//
//                    codec.releaseOutputBuffer(outputBufferIndex, false);
//                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                    outputBuffers = codec.getOutputBuffers();
//                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                    MediaFormat newFormat = codec.getOutputFormat();
//                    Log.d(TAG, "Output format changed: " + newFormat);
//                }
//            }
//
//            // Tính toán giá trị trung bình của độ cao cho từng cột
//            for (int i = 0; i < NUM_COLUMNS; i++) {
//                if (segmentSampleCounts[i] > 0) {
//                    segmentAmplitudes[i] /= segmentSampleCounts[i]; // Trung bình độ lớn cho mỗi cột
//                }
//            }
//
//            // Cập nhật UI với dữ liệu âm thanh
//            runOnUiThread(() -> audioCutterView.setAudioData(segmentAmplitudes));
//
//        } catch (IOException e) {
//            Log.e(TAG, "Failed to decode MP3: " + e.getMessage());
//        } finally {
//            if (codec != null) {
//                codec.stop();
//                codec.release();
//            }
//            extractor.release();
//        }
//    }
//
//    private short[] convertBytesToShorts(byte[] bytes) {
//        short[] shorts = new short[bytes.length / 2];
//        for (int i = 0; i < shorts.length; i++) {
//            shorts[i] = (short) ((bytes[i * 2] & 0xFF) | (bytes[i * 2 + 1] << 8));
//        }
//        return shorts;
//    }
}
