package com.example;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.Home.HomeActivity;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogAllFileBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SuccesallActivity extends AppCompatActivity {
    DialogAllFileBinding binding;
    private MediaPlayer mediaPlayer;
    private String outputAudioPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = DialogAllFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);


        binding.imageView5.setOnClickListener(v->{
            finish();
        });
        binding.addnull.setOnClickListener(v->{
            shareFiles();
        });
        binding.home.setOnClickListener(v->{
            Intent intent = new Intent(SuccesallActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
//    private void shareFiles() {
//        // Lấy danh sách đường dẫn từ Intent
//        ArrayList<String> outputAudioPaths = getIntent().getStringArrayListExtra("outputAudioPaths");
//        for (String path : outputAudioPaths) {
//            Log.d("Received Audio Path", path); // Log từng đường dẫn
//        }
//        if (outputAudioPaths == null || outputAudioPaths.isEmpty()) {
//            Toast.makeText(this, "Không có file để chia sẻ", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Tạo một danh sách Uri để chứa các Uri của file
//        ArrayList<Uri> fileUris = new ArrayList<>();
//
//        for (String filePath : outputAudioPaths) {
//            File sourceFile = new File(filePath);
//
//            if (!sourceFile.exists()) {
//                Toast.makeText(this, "File không tồn tại: " + filePath, Toast.LENGTH_SHORT).show();
//                continue; // Bỏ qua file này nếu nó không tồn tại
//            }
//
//            try {
//                // Tạo file tạm thời trong thư mục cache của ứng dụng
//                File tempFile = File.createTempFile("audio_share_", ".mp3", this.getCacheDir());
//
//                // Copy nội dung từ file gốc sang file tạm thời
//                copyFile(sourceFile, tempFile);
//
//                Uri fileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", tempFile);
//                fileUris.add(fileUri); // Thêm Uri vào danh sách
//
//            } catch (IOException e) {
//                Toast.makeText(this, "Không thể chia sẻ file này: " + filePath, Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
//
//        // Chia sẻ các file đã lưu
//        if (!fileUris.isEmpty()) {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            shareIntent.setType("audio/*");
//            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            this.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file âm thanh"));
//
//            // Xóa file tạm thời sau một khoảng thời gian (nên được thực hiện trong vòng lặp ở trên)
//            new Handler().postDelayed(() -> {
//                for (Uri uri : fileUris) {
//                    File tempFile = new File(uri.getPath());
//                    if (tempFile.exists()) {
//                        tempFile.delete();
//                    }
//                }
//            }, 60000); // Xóa sau 1 phút
//        }
//    }
private void shareFiles() {
    // Lấy danh sách đường dẫn từ Intent
    ArrayList<String> outputAudioPaths = getIntent().getStringArrayListExtra("outputAudioPaths");
    for (String path : outputAudioPaths) {
        Log.d("Received Audio Path", path);
    }
    if (outputAudioPaths == null || outputAudioPaths.isEmpty()) {
        Toast.makeText(this, "Không có file để chia sẻ", Toast.LENGTH_SHORT).show();
        return;
    }

    // Tạo một danh sách Uri để chứa các Uri của file
    ArrayList<Uri> fileUris = new ArrayList<>();

    for (String filePath : outputAudioPaths) {
        File sourceFile = new File(filePath);

        if (!sourceFile.exists()) {
            Toast.makeText(this, "File không tồn tại: " + filePath, Toast.LENGTH_SHORT).show();
            continue;
        }

        try {
            // Lấy tên file gốc
            String originalFileName = sourceFile.getName();

            // Tạo file tạm thời trong thư mục cache của ứng dụng với tên file gốc
            File tempFile = new File(getCacheDir(), originalFileName);

            // Nếu file đã tồn tại, xóa nó
            if (tempFile.exists()) {
                tempFile.delete();
            }

            // Copy nội dung từ file gốc sang file tạm thời
            copyFile(sourceFile, tempFile);

            Uri fileUri = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".provider",
                    tempFile);
            fileUris.add(fileUri);

        } catch (IOException e) {
            Toast.makeText(this, "Không thể chia sẻ file này: " + filePath, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Chia sẻ các file đã lưu
    if (!fileUris.isEmpty()) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("audio/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file âm thanh"));

        // Xóa file tạm thời sau một khoảng thời gian
        new Handler().postDelayed(() -> {
            for (Uri uri : fileUris) {
                File tempFile = new File(uri.getPath());
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }, 60000); // Xóa sau 1 phút
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

