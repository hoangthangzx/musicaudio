package com.example.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customview.CircularProgressBar;

import com.example.model.AudioFile;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogDeleteAllBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogMyidiaBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogReanameBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogsetAsRingtoneBinding;
import com.example.ultils.Untils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Mp3Adapterfrm extends RecyclerView.Adapter<Mp3Adapterfrm.Mp3ViewHolder> {
    private ArrayList<AudioFile> audioFiles;
    private Context context;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateProgressRunnable;
    private OnAudioClickListener audioClickListener;

    public interface OnAudioClickListener {
        void onAudioDelete(String a);
    }
    public void setOnAudioClickListener(OnAudioClickListener listener) {
        this.audioClickListener = listener;
        Log.d("Mp3Adapterfrm", "Audio click listener set");
    }


    public Mp3Adapterfrm(ArrayList<AudioFile> audioFiles, Context context) {
        this.audioFiles = audioFiles;
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public Mp3Adapterfrm.Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merge, parent, false);
        return new Mp3Adapterfrm.Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mp3Adapterfrm.Mp3ViewHolder holder, int position) {
        AudioFile audioFile = audioFiles.get(position);
holder.tab.setVisibility(View.GONE);
        String name = audioFile.getName();
        if (name.length() > 20) {
            // Cắt tên và thêm dấu ba chấm
            name = name.substring(0, 20) + "...";
        }
        holder.nameTextView.setText(name);
        holder.durationTextView.setText(audioFile.getDuration());
        holder.sizeTextView.setText(audioFile.getSize());
        holder.dateTextView.setText(audioFile.getDate());
        // Thêm sự kiện nhấp vào checkbox
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v,position);
            }
        });

        holder.progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPlayingPosition == position) {
                    if (isPlaying) {
                        pauseAudio();

                        holder.progressBar.setPaused(true);
                    } else {
                        resumeAudio();
                        holder.progressBar.setPaused(false);
                    }
                } else {
                    stopCurrentAudio();
                    playNewAudio(audioFile.getUri(), position);
                    holder.progressBar.setPaused(false);
                }
            }
        });

        // Reset progress for all items
        holder.progressBar.setProgress(0);
        holder.progressBar.setPaused(true);

        // Update progress for the currently playing item
        if (currentPlayingPosition == position && isPlaying) {
            updateProgressBar(holder.progressBar);
        }
        holder.progressBar.setPaused(currentPlayingPosition != position || !isPlaying);
    }
    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    private void playNewAudio(String audioFilePath, int position) {
        stopCurrentAudio();

        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            currentPlayingPosition = position;
            notifyDataSetChanged(); // Update all items to reflect the new playing state
            startProgressUpdate();

            // Set OnCompletionListener to clear media when audio finishes
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Clear media and reset progress
                    stopCurrentAudio();
                    notifyItemChanged(position); // Update the UI for the finished item
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            stopProgressUpdate();
        }
    }

    private void resumeAudio() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            startProgressUpdate();
        }
    }

    void stopCurrentAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        isPlaying = false;
        stopProgressUpdate();
        if (currentPlayingPosition != -1) {
            notifyItemChanged(currentPlayingPosition);
        }
        currentPlayingPosition = -1;
    }

    private void startProgressUpdate() {
        stopProgressUpdate(); // Ensure we don't have multiple runnables
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPlayingPosition != -1 && isPlaying && mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();
                    int progress = (int) (((float) currentPosition / totalDuration) * 100);

                    // Update the progress bar for the currently playing item
                    notifyItemChanged(currentPlayingPosition);

                    handler.postDelayed(this, 10); // Update every 100ms
                }
            }
        };
        handler.post(updateProgressRunnable);
    }

    private void stopProgressUpdate() {
        if (updateProgressRunnable != null) {
            handler.removeCallbacks(updateProgressRunnable);
        }
    }
    private void updateProgressBar(CircularProgressBar progressBar) {
        if (mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int totalDuration = mediaPlayer.getDuration();
            int progress = (int) (((float) currentPosition / totalDuration) * 100);
            progressBar.setProgress(progress);
        }
    }
    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, durationTextView, sizeTextView, dateTextView;
        LinearLayout linear;
        ImageView checkbox,tab;
        CircularProgressBar progressBar;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            linear = itemView.findViewById(R.id.item);
            nameTextView = itemView.findViewById(R.id.name);
            durationTextView = itemView.findViewById(R.id.duration);
            sizeTextView = itemView.findViewById(R.id.size);
            dateTextView = itemView.findViewById(R.id.date);
            progressBar = itemView.findViewById(R.id.progressBar);
            checkbox = itemView.findViewById(R.id.checkbox);
            tab= itemView.findViewById(R.id.tab);
        }
    }
    private void showDialog(View anchorView, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogMyidiaBinding dialogBinding = DialogMyidiaBinding.inflate(inflater);

        // Ensure system UI is shown before showing the popup window
        Untils.showSystemUI((AppCompatActivity) context, true);

        PopupWindow popupWindow = new PopupWindow(dialogBinding.getRoot(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setElevation(100);

        // Set click listeners for buttons inside the popup window
        dialogBinding.share.setOnClickListener(v -> {
            shareFile(position);
            popupWindow.dismiss();
        });
        dialogBinding.delete.setOnClickListener(v -> {
            showDeleteDialog(position);
            popupWindow.dismiss();
        });
        dialogBinding.edit.setOnClickListener(v -> {
            showRenameDialog(position);
            popupWindow.dismiss();
        });
        dialogBinding.ringtone.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    showSetRingtoneDialog(position);
                    popupWindow.dismiss();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else {
                showSetRingtoneDialog(position);
                popupWindow.dismiss();
            }
        });

        // Calculate the position for showing the PopupWindow
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        // Get anchor view's location on screen
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);

        // Calculate x and y position for PopupWindow
        int x = screenWidth - popupWindow.getContentView().getMeasuredWidth() - dpToPx(190); // 20dp from the right
        int y = location[1] - anchorView.getHeight() / 2;

        // Show the popup window at the calculated location
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y + dpToPx(25));

        // Reapply system UI visibility after showing the popup
        popupWindow.setOnDismissListener(() -> {
            Untils.showSystemUI((AppCompatActivity) context, true);
        });
    }
//    private void shareFile(int position) {
//        AudioFile audioFile = audioFiles.get(position);
//        String sourceFilePath = audioFile.getUri();
//        File sourceFile = new File(sourceFilePath);
//
//        // Ensure system UI is visible before proceeding with the share dialog
//        Untils.showSystemUI((AppCompatActivity) context, true);
//
//        if (!sourceFile.exists()) {
//            Toast.makeText(context, "File không tồn tại", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            // Tạo file tạm thời trong thư mục cache của ứng dụng
//            File tempFile = File.createTempFile("audio_share_", ".mp3", context.getCacheDir());
//
//            // Copy nội dung từ file gốc sang file tạm thời
//            copyFile(sourceFile, tempFile);
//
//            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", tempFile);
//
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("audio/*");
//            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Listen for when the sharing activity is closed, to hide the system UI again
//            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file âm thanh"));
//
//            // Xóa file tạm thời sau một khoảng thời gian
//            new Handler().postDelayed(() -> {
//                if (tempFile.exists()) {
//                    tempFile.delete();
//                }
//            }, 60000); // Xóa sau 1 phút
//
//        } catch (IOException e) {
//            Toast.makeText(context, "Không thể chia sẻ file này", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//
//        // Reapply system UI changes after the share dialog is dismissed
//        new Handler().postDelayed(() -> {
//            Untils.showSystemUI((AppCompatActivity) context, true); // Adjust this to hide UI again
//        }, 500); // Slight delay to ensure smooth transition
//    }
//private void shareFile(int position) {
//    AudioFile audioFile = audioFiles.get(position);
//    String sourceFilePath = audioFile.getUri();
//    File sourceFile = new File(sourceFilePath);
//
//    Untils.showSystemUI((AppCompatActivity) context, true);
//
//    if (!sourceFile.exists()) {
//        Toast.makeText(context, "File không tồn tại", Toast.LENGTH_SHORT).show();
//        return;
//    }
//
//    try {
//        // Lấy tên file và định dạng từ file gốc
//        String originalFileName = sourceFile.getName();
//        String fileExtension = "";
//        int lastDotIndex = originalFileName.lastIndexOf(".");
//
//        if (lastDotIndex > 0) {
//            fileExtension = originalFileName.substring(lastDotIndex); // Lấy cả dấu chấm
//            originalFileName = originalFileName.substring(0, lastDotIndex); // Tên file không có extension
//        }
//
//        // Tạo file tạm thời với tên gốc
//        File tempFile = File.createTempFile(
//                originalFileName + "_share_", // prefix với tên file gốc
//                fileExtension,               // giữ nguyên định dạng gốc
//                context.getCacheDir()
//        );
//
//        // Copy nội dung từ file gốc sang file tạm thời
//        copyFile(sourceFile, tempFile);
//
//        Uri fileUri = FileProvider.getUriForFile(
//                context,
//                context.getApplicationContext().getPackageName() + ".provider",
//                tempFile
//        );
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType(getMimeType(fileExtension)); // Lấy mime type dựa trên extension
//        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file âm thanh"));
//
//        // Xóa file tạm thời sau một khoảng thời gian
//        new Handler().postDelayed(() -> {
//            if (tempFile.exists()) {
//                tempFile.delete();
//            }
//        }, 60000); // Xóa sau 1 phút
//
//    } catch (IOException e) {
//        Toast.makeText(context, "Không thể chia sẻ file này", Toast.LENGTH_SHORT).show();
//        e.printStackTrace();
//    }
//
//    new Handler().postDelayed(() -> {
//        Untils.showSystemUI((AppCompatActivity) context, true);
//    }, 500);
//}
private void shareFile(int position) {
    AudioFile audioFile = audioFiles.get(position);
    String sourceFilePath = audioFile.getUri();
    File sourceFile = new File(sourceFilePath);

    Untils.showSystemUI((AppCompatActivity) context, true);

    if (!sourceFile.exists()) {
        Toast.makeText(context, "File không tồn tại", Toast.LENGTH_SHORT).show();
        return;
    }

    try {
        // Lấy tên file gốc
        String originalFileName = sourceFile.getName();

        // Tạo file tạm thời với đúng tên file gốc
        File tempFile = new File(context.getCacheDir(), originalFileName);

        // Đảm bảo file tạm thời không tồn tại
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Copy nội dung từ file gốc sang file tạm thời
        copyFile(sourceFile, tempFile);

        Uri fileUri = FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                tempFile
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // Lấy mime type dựa trên tên file gốc
        shareIntent.setType(getMimeType(originalFileName.substring(originalFileName.lastIndexOf("."))));
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file âm thanh"));

        // Xóa file tạm thời sau một khoảng thời gian
        new Handler().postDelayed(() -> {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }, 60000); // Xóa sau 1 phút

    } catch (IOException e) {
        Toast.makeText(context, "Không thể chia sẻ file này", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    new Handler().postDelayed(() -> {
        Untils.showSystemUI((AppCompatActivity) context, true);
    }, 500);
}
    // Helper method để lấy MIME type dựa trên extension
    private String getMimeType(String extension) {
        switch (extension.toLowerCase()) {
            case ".mp3":
                return "audio/mpeg";
            case ".wav":
                return "audio/wav";
            case ".ogg":
                return "audio/ogg";
            case ".m4a":
                return "audio/mp4";
            case ".aac":
                return "audio/aac";
            default:
                return "audio/*";
        }
    }
    // Phương thức để copy file
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
    private void showSetRingtoneDialog(int position) {
        AudioFile audioFile = audioFiles.get(position);
        File file = new File(String.valueOf(audioFile.getUri()));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogsetAsRingtoneBinding binding = DialogsetAsRingtoneBinding.inflate(LayoutInflater.from(context));
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        AtomicInteger a = new AtomicInteger();
        Untils.showSystemUI((AppCompatActivity) context, true);
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
                setAsRingtone(audioFile, RingtoneManager.TYPE_RINGTONE);
                dialog.dismiss();
            } else if (a.get() == 2) {
                setAsRingtone(audioFile, RingtoneManager.TYPE_NOTIFICATION);
                dialog.dismiss();
            } else if (a.get() == 3) {
                setAsRingtone(audioFile, RingtoneManager.TYPE_ALARM);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Vui lòng chọn loại nhạc chuông", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    private void setAsRingtone(AudioFile audioFile, int type) {
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

        String filePath = audioFile.getUri(); // Đường dẫn từ bộ nhớ trong ứng dụng

        if (filePath == null || filePath.isEmpty()) {
            Log.e("SetAsRingtone", "File path is null or empty.");
            Toast.makeText(context, "Invalid file path", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sao chép file nhạc ra thư mục công cộng (Ringtones)
        String externalFileName = "custom_ringtone.mp3"; // Đặt tên file mong muốn
        boolean copySuccess = copyFileToExternalStorage(filePath, externalFileName);
        if (!copySuccess) {
            Log.e("SetAsRingtone", "Failed to copy file to external storage.");
            Toast.makeText(context, "Failed to copy file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đường dẫn đến file đã sao chép
        String externalFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/" + externalFileName;
        File file = new File(externalFilePath);

        if (!file.exists() || !file.canRead()) {
            Log.e("SetAsRingtone", "File not found or not readable: " + externalFilePath);
            Toast.makeText(context, "File not found or not readable", Toast.LENGTH_SHORT).show();
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
            Cursor cursor = context.getContentResolver().query(
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
                context.getContentResolver().update(newUri, values, null, null);
            } else {
                // File doesn't exist, insert it
                newUri = context.getContentResolver().insert(contentUri, values);
            }

            if (cursor != null) {
                cursor.close();
            }

            if (newUri == null) {
                Log.e("SetAsRingtone", "Failed to insert or update audio file in MediaStore.");
                Toast.makeText(context, "Failed to set ringtone", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the new URI as the default ringtone/alarm/notification
            RingtoneManager.setActualDefaultRingtoneUri(context, type, newUri);
            Toast.makeText(context, "Ringtone set successfully: " + title, Toast.LENGTH_SHORT).show();

        } catch (SecurityException se) {
            Log.e("SetAsRingtone", "Lacking WRITE_SETTINGS permission: " + se.getMessage());
            handleWriteSettingsPermission();

        } catch (Exception e) {
            Log.e("SetAsRingtone", "Error setting ringtone: " + e.getMessage());
            Toast.makeText(context, "Error setting ringtone", Toast.LENGTH_SHORT).show();
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
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        }
    }

    private void showRenameDialog(int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        DialogReanameBinding renameBinding = DialogReanameBinding.inflate(inflater);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialogTheme);
        builder.setView(renameBinding.getRoot());
        Untils.showSystemUI((AppCompatActivity) context, true);
        AlertDialog renameDialog = builder.setCancelable(false).create();

        if (renameDialog.getWindow() != null) {
            renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            renameDialog.getWindow().setGravity(Gravity.CENTER);
            renameDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Populate the dialog with current file info
        AudioFile audioFile = audioFiles.get(position);
        String currentName = audioFile.getName();
        renameBinding.editText.setText(currentName);
        renameBinding.editText.setSelection(currentName.length());
        renameBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {

                    renameBinding.clearIcon.setVisibility(View.VISIBLE);
                } else {
                    renameBinding.clearIcon.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        renameBinding.clearIcon.setOnClickListener(v -> renameBinding.editText.setText(""));

        if (renameDialog.getWindow() != null) {
            renameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        renameBinding.textView2.setOnClickListener(v -> {
            String newName = renameBinding.editText.getText().toString().trim();

            // Get the file extension (e.g., ".mp3")
            String extension = currentName.substring(currentName.lastIndexOf('.'));

            if (!newName.isEmpty() && !newName.equals(currentName)) {
                // Ensure the new file name contains the original extension
                if (!newName.endsWith(extension)) {
                    newName += extension;
                }

                File oldFile = new File(audioFile.getUri());
                File newFile = new File(oldFile.getParent(), newName);

                if (oldFile.renameTo(newFile)) {
                    audioFile.setName(newName);
                    audioFile.setUri(newFile.getAbsolutePath());
                    notifyItemChanged(position);
                    renameDialog.dismiss();
                    Toast.makeText(context, "File renamed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show();
                }
            } else if (newName.equals(currentName)) {
                renameDialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            }
        });

        applyGradientToSaveText(renameBinding.textView3);
        renameBinding.textView3.setOnClickListener(v -> renameDialog.dismiss());

        // Show the dialog
        renameDialog.show();
    }

    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color (20%)
                        Color.parseColor("#14D2E6")  // Bottom color (80%)
                },
                new float[]{0.2f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }
    private void showDeleteDialog(int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        DialogDeleteAllBinding dialogBinding = DialogDeleteAllBinding.inflate(inflater);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialogTheme);
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder
                .setCancelable(false)
                .create();

        Untils.showSystemUI((AppCompatActivity) context, true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Ensure the dialog appears in the center
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(layoutParams);
        }

        dialogBinding.yes.setOnClickListener(v -> {
            AudioFile audioFile = audioFiles.get(position);
            File fileToDelete = new File(audioFile.getUri());

            if (fileToDelete.delete()) {
                audioFiles.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, audioFiles.size());
                Toast.makeText(context, R.string.file_deleted_successfully, Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
             String a="a" ;
                if (audioClickListener != null) {
                    audioClickListener.onAudioDelete(a);
                } else {
                    Log.e("TAG", "Audio click listener is not set");
                }
            } else {
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });
        applyGradientToSaveText( dialogBinding.no);
        dialogBinding.no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}