package com.example;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Home.HomeActivity;
import com.example.interect.InterectActivity;
import com.example.model.TextShaderSpan;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityPermissonBinding;

public class PermissonActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;
    private ActivityPermissonBinding binding;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityPermissonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);
//        applyGradientToSaveText(b);
        // Kiểm tra quyền lưu trữ
        checkPermissions();
        applyGradientToText();
        // Thiết lập UI cho các quyền
        updatePermissionUI();

        binding.Storage.setOnClickListener(v -> requestStoragePermission());
        binding.screenpremissions.setOnClickListener(v -> requestNotificationPermission());

        binding.con.setOnClickListener(v -> {
            saveValueToPreferences("true");
            acHome();
        });
        startPermissionUpdateLoop();
        TextView saveTextView = findViewById(R.id.con);
        applyGradientToSaveText(saveTextView);
    }
    private void applyGradientToText() {
        // Part 1: "Allow"
        String allowText = getString(R.string.allow);
        // Part 2: "Audio Editor"
        String audioEditorText = getString(R.string.audio_editor);
        // Part 3: "to access photos, media, and files on your device"
        String accessText = getString(R.string.to_access_photos_media_and_files_on_your_device);

        SpannableStringBuilder spannable = new SpannableStringBuilder();

        // Part 1: Solid color for "Allow"
        SpannableString allowSpan = new SpannableString(allowText + " ");
        allowSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, allowText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(allowSpan);

        // Part 2: Gradient for "Audio Editor"
        SpannableString audioEditorSpan = new SpannableString(audioEditorText + " ");

        // Create the gradient shader
        Shader textShader = new LinearGradient(0, 0, 0, binding.permissionTextView.getTextSize(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color
                        Color.parseColor("#14D2E6")  // Bottom color
                },
                new float[]{0.5f, 1f}, Shader.TileMode.CLAMP);

        // Apply the shader using the custom TextShaderSpan
        audioEditorSpan.setSpan(new TextShaderSpan(textShader), 0, audioEditorText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(audioEditorSpan);

        // Part 3: Solid color for the rest of the text
        SpannableString accessSpan = new SpannableString(accessText);
        accessSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, accessText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(accessSpan);

        // Set the spannable text to the TextView
        binding.permissionTextView.setText(spannable);

        // Force redraw
        binding.permissionTextView.invalidate();
    }
    private void checkPermissions() {
        boolean storagePermissionGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.textViewStorage.setText(R.string.music_and_audio);
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        boolean notificationsPermissionGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
;

        binding.Storage.setImageResource(storagePermissionGranted ? R.drawable.sw : R.drawable.swithfalse);
        binding.screenpremissions.setImageResource(notificationsPermissionGranted ? R.drawable.sw : R.drawable.swithfalse);

    }

    private void updatePermissionUI() {
        // Kiểm tra và ẩn/hiển thị các phần tử UI dựa trên phiên bản Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.c1.setVisibility(View.VISIBLE);
            binding.c2.setVisibility(View.VISIBLE);
            applyGradientToText();
        } else {
//            binding.permissionTextView.setText(R.string.storages);
            applyGradientToText();
            binding.c1.setVisibility(View.VISIBLE);
            binding.cardView2.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
//            binding.permissionTextView.setText(R.string.storages);
            applyGradientToText();
            binding.c1.setVisibility(View.VISIBLE);
            binding.cardView2.setVisibility(View.GONE);
        }
    }

    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Màu trên
                        Color.parseColor("#14D2E6")  // Màu dưới
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 cho 20% trên, 1f cho 80% dưới

        textView.getPaint().setShader(textShader);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void checkPermissions1() {
        boolean storagePermissionGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.textViewStorage.setText(R.string.music_and_audio);
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        boolean notificationsPermissionGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        ;

        binding.Storage.setImageResource(storagePermissionGranted ? R.drawable.sw : R.drawable.swithfalse);
        binding.screenpremissions.setImageResource(notificationsPermissionGranted ? R.drawable.sw : R.drawable.swithfalse);

    }
    private void saveValueToPreferences(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("permisson", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedpermisson", value);
        editor.apply();
    }

    private void acHome() {
        Intent intent = new Intent(PermissonActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startPermissionUpdateLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePermissionStates();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updatePermissionStates() {
        checkPermissions();
    }

//    private void requestStoragePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_EXTERNAL_STORAGE
//                    },
//                    STORAGE_PERMISSION_REQUEST_CODE);
//        }
//    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Hiện dialog ngay khi từ chối lần đầu
                showPermissionDeniedDialog(getString(R.string.quyen));
            }
        }

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Hiện dialog ngay khi từ chối lần đầu
                showPermissionDeniedDialog(getString(R.string.quyen));
            }
        }
    }

    // Không cần kiểm tra shouldShowSettingsDialog nữa vì ta muốn hiện dialog ngay
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (!hasPermission(permission)) {
            // Request quyền trực tiếp, không cần kiểm tra shouldShowSettingsDialog
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void showPermissionDeniedDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_permisson, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        TextView btnSettings = dialogView.findViewById(R.id.yes);
        TextView no = dialogView.findViewById(R.id.no);
        btnSettings.setOnClickListener(v -> {
            openAppSettings();
            dialog.dismiss();
        });
        no.setOnClickListener(v -> dialog.dismiss());

        applyGradientToSaveText(no);
        dialog.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}
