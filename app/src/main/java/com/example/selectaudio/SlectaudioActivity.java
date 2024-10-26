package com.example.selectaudio;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Home.HomeActivity;
import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivitySlectaudioBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.widget.TextView;

public class SlectaudioActivity extends AppCompatActivity {
    private Mp3Adapter mp3Adapter;
    private RecyclerView recyclerView;
ActivitySlectaudioBinding binding;
    private List<Mp3File> mp3Files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivitySlectaudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        recyclerView = findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");

        // Initialize mp3Files and ensure it's not null
        mp3Files = getAllAudioFiles(this);
        if (mp3Files == null) {
            mp3Files = new ArrayList<>(); // Ensure it’s at least an empty list
        }

        mp3Adapter = new Mp3Adapter(mp3Files, this, value);
        recyclerView.setAdapter(mp3Adapter);

        binding.imageView2.setOnClickListener(v -> {
            binding.imageView2.setVisibility(View.GONE);
            binding.textView2.setVisibility(View.GONE);
            binding.search.setVisibility(View.VISIBLE);
        });
        binding.imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editText.setText(""); // Clear the text in the EditText
            }
        });
        // Setup search functionality
        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    binding.imageView7.setVisibility(View.VISIBLE);
                } else {
                    binding.imageView7.setVisibility(View.GONE);
                }
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        binding.back.setOnClickListener(v->{
            Intent mp3cutter = new Intent(SlectaudioActivity.this, HomeActivity.class);
            mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mp3cutter);
            finish();
        });
    }
    private void filter(String text) {
        List<Mp3File> filteredList = new ArrayList<>();

        if (mp3Files != null) { // Check if mp3Files is not null
            for (Mp3File file : mp3Files) {
                if (file.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(file);
                }
            }
        }
        if (filteredList.isEmpty()) {
            // Show selecnull if no results are found
            binding.selectnull.setVisibility(View.VISIBLE);
        } else {
            // Hide selecnull if results are found
            binding.selectnull.setVisibility(View.GONE);

        }

        mp3Adapter.updateList(filteredList);
    }
    private void checkPermissions() {
        boolean storagePermissionGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        if (!storagePermissionGranted) {
            binding.selectnull.setVisibility(View.VISIBLE);
            showPermissionDeniedDialog();
        } else {
            binding.selectnull.setVisibility(View.GONE);
        }
    }

    private void showPermissionDeniedDialog() {
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

        recyclerView = findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");

        // Initialize mp3Files and ensure it's not null
        mp3Files = getAllAudioFiles(this);
        if (mp3Files == null) {
            mp3Files = new ArrayList<>(); // Ensure it’s at least an empty list
        }

        mp3Adapter = new Mp3Adapter(mp3Files, this, value);
        recyclerView.setAdapter(mp3Adapter);
        mp3Adapter.notifyDataSetChanged();
        checkPermissions();
    }



    public List<Mp3File> getAllAudioFiles(Context context) {
        List<Mp3File> audioFiles = new ArrayList<>();

        // Lấy audio từ bộ nhớ ngoài
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        audioFiles.addAll(getAudioFilesFromUri(context, externalUri));

        // Lấy audio từ bộ nhớ trong
        Uri internalUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        audioFiles.addAll(getAudioFilesFromUri(context, internalUri));

        return audioFiles;
    }

    private List<Mp3File> getAudioFilesFromUri(Context context, Uri uri) {
        List<Mp3File> audioFiles = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";  // Chỉ tìm các tệp âm nhạc
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);

        if (cursor != null) {
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String path = cursor.getString(pathColumn);
                long durationMs = cursor.getLong(durationColumn);
                long sizeBytes = cursor.getLong(sizeColumn);
                long dateAddedSecs = cursor.getLong(dateColumn);

                String duration = formatDuration(durationMs);
                String size = formatSize(sizeBytes);
                String date = formatDate(dateAddedSecs);

                audioFiles.add(new Mp3File(name, path, duration, size, date));
            }
            cursor.close();
        }

        return audioFiles;
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
        return sdf.format(new Date(dateAddedSecs * 1000));  // Chuyển từ giây sang mili giây
    }

}