package com.example;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.Home.HomeActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogSuccesBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogsetAsRingtoneBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class SuccesActivity extends AppCompatActivity {
    DialogSuccesBinding binding;
    private MediaPlayer mediaPlayer;
    private String AudioPath;
    private boolean isPlaying = false; // Để kiểm tra trạng thái phát nhạc
    private boolean isSeeking = false; // Kiểm tra khi người dùng đang kéo SeekBar
    private Handler handler = new Handler();  // Create a handler object
    private Runnable transitionRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = DialogSuccesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        String outputAudioPath = getIntent().getStringExtra("outputAudioPath");
        AudioPath=outputAudioPath;
        Log.e("SetAsRingtone", "File not found or not readable: " + outputAudioPath);
        if (outputAudioPath != null) {
            // Create a MediaMetadataRetriever object
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(outputAudioPath); // Set the audio file path

            String audioTitle = new File(outputAudioPath).getName(); // Get the file name as the title

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration = Long.parseLong(durationStr);

            // Convert duration to minutes and seconds
            long minutes = (duration / 1000) / 60;
            long seconds = (duration / 1000) % 60;
           String a= formatDuration(duration);
            updateAudioTitle(audioTitle);
binding.textView11.setText(a);

            // Display the title and time (optional, replace with your actual implementation)
            System.out.println("Audio Title: " + audioTitle);
            System.out.println("Duration: " + minutes + " min " + seconds + " sec");

            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (outputAudioPath != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputAudioPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        binding.imageView11.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    binding.imageView11.setImageResource(R.drawable.a);
                    // Nếu đang phát nhạc thì tạm dừng
                    mediaPlayer.pause();
                    isPlaying = false;
                    stop();
                } else {
                    // Nếu đang dừng thì phát nhạc
                    mediaPlayer.start();
                    isPlaying = true;
                    binding.imageView11.setImageResource(R.drawable.c);
                    // Cập nhật SeekBar và thời gian nhạc
                    updateSeekBar();
                    start();
                }
            }
            mediaPlayer.setOnCompletionListener(mp -> {
                binding.imageView11.setImageResource(R.drawable.a);
                stop();

                isPlaying = false;
            });

        });

        binding.sbhz.setMax(mediaPlayer.getDuration());


        // Lắng nghe sự thay đổi của SeekBar
        binding.sbhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Nếu người dùng kéo SeekBar, phát nhạc từ thời gian tương ứng
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
//                    binding.textView11.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tạm dừng cập nhật khi người dùng kéo SeekBar
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Khi người dùng thả SeekBar, tiếp tục cập nhật SeekBar
                isSeeking = false;
                if (mediaPlayer != null && isPlaying) {
                    mediaPlayer.start(); // Phát nhạc tiếp nếu đang trong trạng thái phát
                }
                updateSeekBar(); // Cập nhật lại SeekBar sau khi thả
            }
        });
        binding.ringtone.setOnClickListener(v->{
            showSetRingtoneDialog();
        });
        binding.imageView5.setOnClickListener(v->{
            finish();
        });
        binding.home.setOnClickListener(v->{
            Intent intent = new Intent(SuccesActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
        });
        binding.constraintLayout21.setOnClickListener(v->{
            shareFile();
        });
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
    private void cancelTransition() {
        handler.removeCallbacks(transitionRunnable); // Cancel the runnable
    }
    private void showSetRingtoneDialog() {
        String outputAudioPath = getIntent().getStringExtra("outputAudioPath");
        File file = new File(outputAudioPath);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogsetAsRingtoneBinding binding = DialogsetAsRingtoneBinding.inflate(LayoutInflater.from(this));
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        AtomicInteger a = new AtomicInteger();

        binding.Phoneringtone.setOnClickListener(v ->{
            binding.check1.setImageResource(R.drawable.radiotrue);
            binding.check2.setImageResource(R.drawable.radiofalse);
            binding.check3.setImageResource(R.drawable.radiofalse);
            a.set(1);
        });
        binding.smsringtone.setOnClickListener(v ->{
            binding.check1.setImageResource(R.drawable.radiofalse);
            binding.check2.setImageResource(R.drawable.radiotrue);
            binding.check3.setImageResource(R.drawable.radiofalse);
            a.set(2);
        });
        binding.Alarm.setOnClickListener(v ->{
            binding.check1.setImageResource(R.drawable.radiofalse);
            binding.check2.setImageResource(R.drawable.radiofalse);
            binding.check3.setImageResource(R.drawable.radiotrue);
            a.set(3);
        });

        binding.no.setOnClickListener(v -> dialog.dismiss());
        binding.yes.setOnClickListener(v -> {
            if (a.get() == 1) {
                setAsRingtone(outputAudioPath, RingtoneManager.TYPE_RINGTONE);
                dialog.dismiss();
            } else if (a.get() == 2) {
                setAsRingtone(outputAudioPath, RingtoneManager.TYPE_NOTIFICATION);
                dialog.dismiss();
            } else if (a.get() == 3) {
                setAsRingtone(outputAudioPath, RingtoneManager.TYPE_ALARM);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng chọn loại nhạc chuông", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    private void shareFile() {
        String outputAudioPath = getIntent().getStringExtra("outputAudioPath");
        String sourceFilePath = outputAudioPath;
        File sourceFile = new File(sourceFilePath);

        if (!sourceFile.exists()) {
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Lấy tên file gốc
            String originalFileName = sourceFile.getName();

            // Tạo file tạm thời với đúng tên file gốc
            File tempFile = new File(getCacheDir(), originalFileName);

            // Đảm bảo file tạm thời không tồn tại
            if (tempFile.exists()) {
                tempFile.delete();
            }

            // Copy nội dung từ file gốc sang file tạm thời
            copyFile(sourceFile, tempFile);

            Uri fileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", tempFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Đặt flag để xóa file tạm thời sau khi chia sẻ
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            this.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file âm thanh"));

            // Xóa file tạm thời sau một khoảng thời gian
            new Handler().postDelayed(() -> {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }, 60000); // Xóa sau 1 phút

        } catch (IOException e) {
            Toast.makeText(this, "Không thể chia sẻ file này", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void copyFile(File sourceFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
    private void setAsRingtone(String audioFile, int type) {
        String title;
        switch (type) {
            case RingtoneManager.TYPE_RINGTONE:
                title = "Phone Ringtone";
                break;
            case RingtoneManager.TYPE_NOTIFICATION:
                title = "Notification Sound";
                break;
            case RingtoneManager.TYPE_ALARM:
                title = "Alarm Sound";
                break;
            default:
                title = "Ringtone";
        }

        String filePath = audioFile; // Đường dẫn từ bộ nhớ trong ứng dụng

        if (filePath == null || filePath.isEmpty()) {
            Log.e("SetAsRingtone", "File path is null or empty.");
            Toast.makeText(this, "Invalid file path", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sao chép file nhạc ra thư mục công cộng (Ringtones)
        String externalFileName = "custom_ringtone.mp3"; // Đặt tên file mong muốn
        boolean copySuccess = copyFileToExternalStorage(filePath, externalFileName);
        if (!copySuccess) {
            Log.e("SetAsRingtone", "Failed to copy file to external storage.");
            Toast.makeText(this, "Failed to copy file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đường dẫn đến file đã sao chép
        String externalFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/" + externalFileName;
        File file = new File(externalFilePath);

        if (!file.exists() || !file.canRead()) {
            Log.e("SetAsRingtone", "File not found or not readable: " + externalFilePath);
            Toast.makeText(this, "File not found or not readable", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, type == RingtoneManager.TYPE_RINGTONE);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, type == RingtoneManager.TYPE_NOTIFICATION);
        values.put(MediaStore.Audio.Media.IS_ALARM, type == RingtoneManager.TYPE_ALARM);
        values.put(MediaStore.Audio.Media.DATA, externalFilePath);

        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = null;

        try {
            // Check if the file already exists in MediaStore
            Cursor cursor = this.getContentResolver().query(
                    contentUri,
                    new String[] { MediaStore.Audio.Media._ID },
                    MediaStore.Audio.Media.DATA + "=?",
                    new String[] { externalFilePath },
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // File exists, update it
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                newUri = ContentUris.withAppendedId(contentUri, id);
                this.getContentResolver().update(newUri, values, null, null);
            } else {
                // File doesn't exist, insert it
                newUri = this.getContentResolver().insert(contentUri, values);
            }

            if (cursor != null) {
                cursor.close();
            }

            if (newUri == null) {
                Log.e("SetAsRingtone", "Failed to insert or update audio file in MediaStore.");
                Toast.makeText(this, "Failed to set ringtone", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the new URI as the default ringtone/alarm/notification
            RingtoneManager.setActualDefaultRingtoneUri(this, type, newUri);
            Toast.makeText(this, "Ringtone set successfully: " + title, Toast.LENGTH_SHORT).show();

        } catch (SecurityException se) {
            Log.e("SetAsRingtone", "Lacking WRITE_SETTINGS permission: " + se.getMessage());
            handleWriteSettingsPermission();

        } catch (Exception e) {
            Log.e("SetAsRingtone", "Error setting ringtone: " + e.getMessage());
        }
    }
    private boolean copyFileToExternalStorage(String sourcePath, String destFileName) {
        File sourceFile = new File(sourcePath);
        File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), destFileName);

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            return true; // Sao chép thành công
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Sao chép thất bại
        }
    }

    // Helper method to handle WRITE_SETTINGS permission request
    private void handleWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                this.startActivity(intent);
            }
        }
    }
    private Runnable updateSeekBarRunnable;

    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            // Đặt giá trị tối đa cho SeekBar dựa trên tổng thời lượng của audio
            binding.sbhz.setMax(mediaPlayer.getDuration());

            // Tạo Runnable để cập nhật SeekBar theo thời gian thực
            updateSeekBarRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && isPlaying) {
                        int currentPosition = mediaPlayer.getCurrentPosition();

                        binding.sbhz.setProgress(currentPosition);

                        handler.postDelayed(this, 100);
                    }
                }
            };

            handler.post(updateSeekBarRunnable);
        }
    }

    private void stopUpdatingSeekBar() {
        if (handler != null && updateSeekBarRunnable != null) {
            // Hủy cập nhật khi tạm dừng hoặc kết thúc nhạc
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private ObjectAnimator rotateAnimator; // Biến toàn cục cho ObjectAnimator

    private void start() {
        ImageView imageView = binding.imageView3;
        rotateAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotateAnimator.setDuration(2000);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.start();
    }

    private void stop() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.end();  // Dừng animation
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
cancelTransition();
    }
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void updateAudioTitle(String audioTitle) {
        if (audioTitle.length() > 30) {
            String truncatedTitle = audioTitle.substring(0, 30) + "...";
            binding.textView20.setText(truncatedTitle);
        } else {
            binding.textView20.setText(audioTitle);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.imageView11.setImageResource(R.drawable.a);
        mediaPlayer.pause();
        isPlaying = false;
        stop();
    }
}
