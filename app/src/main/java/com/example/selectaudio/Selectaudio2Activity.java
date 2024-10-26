package com.example.selectaudio;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.Manifest;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Home.HomeActivity;
import com.example.equazer.equazerActivity;
//import com.example.merge.MergeActivity;
import com.example.merge.MergeActivity2;
import com.example.mixer.MixerActivity;
import com.example.model.AudioFile;
import com.example.model.Mp3File;
import com.example.speed.SpeedActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivitySelectaudio2Binding;
import com.example.volume.VolumeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Selectaudio2Activity extends AppCompatActivity implements selectaudio2Adapter.OnSelectionChangedListener{
    ActivitySelectaudio2Binding binding;
    private selectaudio2Adapter mp3Adapter;
    private selectaudioclickAdapter clickAdapter;
    private RecyclerView recySelectedAudios, recyAllAudios;
    private MutableLiveData<List<Mp3File>> selectedAudios = new MutableLiveData<>();
    private static final String TAG = "Selectaudio2Activity";
    private List<Mp3File> allAudioFiles; // Store all audio files
    private List<Mp3File> filteredAudioFiles; // Store filtered audio files
    private List<Mp3File> file; // Store filtered audio files
    private AlertDialog permissionDialog;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100; // You can choose any number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivitySelectaudio2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");

        // Initialize selected audios RecyclerView (recyselect)
        recySelectedAudios = findViewById(R.id.recyselect);
        allAudioFiles = getAllAudioFiles(this);
        mp3Adapter = new selectaudio2Adapter(allAudioFiles, this, value, selectedAudios);
        clickAdapter = new selectaudioclickAdapter(new ArrayList<>(), this, value, selectedAudios, mp3Adapter);
        recySelectedAudios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recySelectedAudios.setAdapter(clickAdapter);

        selectedAudios.observe(this, new Observer<List<Mp3File>>() {
            @Override
            public void onChanged(List<Mp3File> selectedFiles) {
                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    file=selectedFiles;
                    binding.constraintLayout12.setVisibility(View.VISIBLE);
                    binding.noitem.setVisibility(View.GONE);
                    if (selectedFiles.isEmpty()){
                        binding.next.setVisibility(View.GONE);
                    }else { binding.next.setVisibility(View.VISIBLE);

                    }
                }
                else {
binding.noitem.setVisibility(View.VISIBLE);
                    binding.next.setVisibility(View.GONE);
//                    binding.constraintLayout12.setVisibility(View.GONE);
                }
                Log.d(TAG, "Selected Audio Files: " + selectedFiles.size());
                clickAdapter.updateSelectedFiles(selectedFiles);
            }
        });


        recyAllAudios = findViewById(R.id.recy);
        recyAllAudios.setLayoutManager(new LinearLayoutManager(this));
        recyAllAudios.setAdapter(mp3Adapter);
        binding.next.setOnClickListener(v -> {
            if(file.size() > 0){
                Intent nextIntent;
                switch (value) {
                    case "speed":
                        nextIntent = new Intent(this, SpeedActivity.class);
                        break;
                    case "mixer":
                        nextIntent = new Intent(this, MixerActivity.class);
                        break;
                    case "valume":
                        nextIntent = new Intent(this, VolumeActivity.class);
                        break;
                    case "merger":
                        nextIntent = new Intent(this, MergeActivity2.class);
                        break;
                    default:
                        nextIntent = new Intent(this, equazerActivity.class);
                        break;
                }

                List<Mp3File> selectedFiles = selectedAudios.getValue();
                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    ArrayList<AudioFile> audioFilesList = new ArrayList<>();

                    for (Mp3File file : selectedFiles) {
                        audioFilesList.add(new AudioFile(
                                file.getPath(),        // URI
                                file.getName(),        // File Name
                                file.getSize(),        // File Size
                                file.getDuration(),    // File Duration
                                file.getDate()));      // File Date

                        Log.d(TAG, "Selected Audio URI: " + file.getPath());
                    }

                    ArrayList<String> audioPaths = new ArrayList<>();
                    for (Mp3File file : selectedFiles) {
                        audioPaths.add(file.getPath());
                        // Log details for debugging
                        Log.d(TAG, "Selected Audio Path for Mixer: " + file.getPath());
                    }
                    // Add the ArrayList of String paths to the intent
                    nextIntent.putStringArrayListExtra("audio_paths", audioPaths);
                    nextIntent.putParcelableArrayListExtra("audio_files_list", audioFilesList);

                    Log.d(TAG, "Total selected audio files: " + audioFilesList.size());
                } else {
                    Log.d(TAG, "No audio files selected");
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nextIntent);


            }else {

            }

        });
        binding.back.setOnClickListener(v->{
            Intent mp3cutter = new Intent(Selectaudio2Activity.this, HomeActivity.class);
            mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mp3cutter);
            finish();
        });
        filteredAudioFiles = new ArrayList<>(allAudioFiles);
        binding.imageView2.setOnClickListener(v -> {
            binding.imageView2.setVisibility(View.GONE);
            binding.textView2.setVisibility(View.GONE);
            binding.search.setVisibility(View.VISIBLE);
        });
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
                filterAudioFiles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.imageView7.setOnClickListener(v -> {
            binding.editText.setText(""); // Clear the text in the EditText

        });
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
//        Log.d(TAG, "onResume: ");
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
            allAudioFiles = getAllAudioFiles(this);
            Log.d(TAG, "file: "+allAudioFiles.size());
            mp3Adapter = new selectaudio2Adapter(allAudioFiles, this, value, selectedAudios);

            clickAdapter = new selectaudioclickAdapter(new ArrayList<>(), this, value, selectedAudios, mp3Adapter);
            recySelectedAudios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recySelectedAudios.setAdapter(clickAdapter);

            recyAllAudios.setLayoutManager(new LinearLayoutManager(this));
            recyAllAudios.setAdapter(mp3Adapter);

mp3Adapter.notifyDataSetChanged();
        checkPermissions();
    }
    private void loadAudioFiles() {
        String value = getIntent().getStringExtra("key");
        allAudioFiles = getAllAudioFiles(this); // Only call if permissions are granted
        mp3Adapter = new selectaudio2Adapter(allAudioFiles, this, value, selectedAudios);
        clickAdapter = new selectaudioclickAdapter(new ArrayList<>(), this, value, selectedAudios, mp3Adapter);
        recySelectedAudios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recySelectedAudios.setAdapter(clickAdapter);
    }

    // Helper method to check if storage permissions are granted
    private boolean arePermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    private void filterAudioFiles(String query) {

        query = query.toLowerCase().trim();
        filteredAudioFiles.clear(); // Xóa danh sách để chuẩn bị cho kết quả mới

        if (query.isEmpty()) {
            // Nếu không có truy vấn, thêm tất cả file vào filteredAudioFiles
            filteredAudioFiles.addAll(allAudioFiles);
        } else {
            // Lặp qua allAudioFiles để kiểm tra tên file
            for (Mp3File file : allAudioFiles) {
                if (file.getName().toLowerCase().contains(query)) {
                    // Nếu file khớp với truy vấn, thêm vào filteredAudioFiles
                    filteredAudioFiles.add(file);
                }
            }
        }

        updateUIForSearchResults();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with accessing audio files
                    Log.d(TAG, "Storage permission granted");
                    loadAudioFiles(); // Load audio files or call your method to access them
                } else {
                    Log.d(TAG, "Storage permission denied");
                    binding.selectnull.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    private void updateUIForSearchResults() {
        if (filteredAudioFiles.isEmpty()) {
            binding.selectnull.setVisibility(View.VISIBLE);
//            binding.recy.setVisibility(View.GONE);
        } else {
            binding.selectnull.setVisibility(View.GONE);
            binding.recy.setVisibility(View.VISIBLE);
        }

        // Update the adapter's data and notify it
        mp3Adapter.updateList(filteredAudioFiles);
        mp3Adapter.notifyDataSetChanged();
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

                // Định dạng thời lượng và kích thước để dễ đọc
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

    @Override
    public void onSelectionChanged(int selectedCount) {
        Log.d(TAG, "onSelectionChanged: "+selectedCount);

    }
}