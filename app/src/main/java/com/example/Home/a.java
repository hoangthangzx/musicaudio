//package com.example.Home;
//
//
//import static com.example.ultils.SystemUtils.setLocale;
//import static com.example.ultils.Untils.showSystemUI;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//
//import com.example.SettingActivity;
//import com.example.mixer.SelectActivity;
//import com.example.model.HomeItem;
//import com.example.myaudio.MyaudioActivity;
//import com.example.selectaudio.Selectaudio2Activity;
//import com.example.selectaudio.SlectaudioActivity;
//import com.example.st046_audioeditorandmusiceditor.R;
//import com.example.st046_audioeditorandmusiceditor.databinding.ActivityHomeBinding;
//import com.example.st046_audioeditorandmusiceditor.databinding.RateDialogBinding;
//import com.example.ultils.AudioUtils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class HomeActivity extends AppCompatActivity {
//    ActivityHomeBinding binding;
//    private boolean isDialogShowing = false;
//    File tempDir;
//private HomeAdapter homeAdapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setLocale(this);
//        binding = ActivityHomeBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        showSystemUI(this, true);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        binding.recy.setLayoutManager(gridLayoutManager);
//
//        AudioUtils.clearSelectedAudioFiles();
//        homeAdapter = new HomeAdapter(this, getItems(), this::onItemClicked);
//        binding.recy.setAdapter(homeAdapter);
//
//        binding.imageView2.setOnClickListener(v->{
//            Intent a = new Intent(com.example.Home.a.this, SettingActivity.class);
//            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(a);
//        });
//        tempDir = new File(getExternalFilesDir(null), "TempAudio");
//        binding.icmy.setOnClickListener(v->{
//            Intent a = new Intent(com.example.Home.a.this, MyaudioActivity.class);
//            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(a);
//            finish();
//        });
//        isDialogShowing = false;
//        cleanupTempFiles();
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
//    @Override
//    protected void onResume() {
//        super.onResume();
//        AudioUtils.clearSelectedAudioFiles();
//    }
//
//    // This method is triggered when an item is clicked
//    private void onItemClicked(String itemName) {
//        Intent intent = null;
//        switch (itemName.toLowerCase()) {
//            case "mp3cutter":
//                Intent mp3cutter = new Intent(a.this, SlectaudioActivity.class);
//                mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                mp3cutter.putExtra("key", "mp3cutter");
//                mp3cutter.putExtra("key2", "1");
//                startActivity(mp3cutter);
//
//                break;
//            case "merger":
//                Intent merger = new Intent(a.this, SelectActivity.class);
//                merger.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                merger.putExtra("key2", "1");
//                merger.putExtra("key", "merger");
//                startActivity(merger);
//
//                break;
//            case "mixer":
//                Intent mixer = new Intent(a.this, SelectActivity.class);
//                mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                mixer.putExtra("key", "mixer");
//                mixer.putExtra("key2", "1");
//                startActivity(mixer);
//                break;
//            case "speed":
//                Intent speed = new Intent(a.this, Selectaudio2Activity.class);
//                speed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                speed.putExtra("key", "speed");
//                startActivity(speed);
//
//                break;
//            case "equalizer":
//                Intent equalizer = new Intent(a.this, SlectaudioActivity.class);
//                equalizer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                equalizer.putExtra("key", "equalizer");
//                startActivity(equalizer);
//
//                break;
//            case "valume":
//                Intent valume = new Intent(a.this, Selectaudio2Activity.class);
//                valume.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                valume.putExtra("key", "valume");
//                startActivity(valume);
//
//                break;
//            case "voidchanger":
//                Intent voidchanger = new Intent(a.this, SlectaudioActivity.class);
//                voidchanger.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                voidchanger.putExtra("key", "voidchanger");
//                startActivity(voidchanger);
//
//                break;
//            case "audiotex":
//                Intent audiotex = new Intent(a.this, SlectaudioActivity.class);
//                audiotex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                audiotex.putExtra("key", "audiotex");
//                startActivity(audiotex);
//
//                break;
//            default:
//                intent = null;
//                break;
//        }
//
//        if (intent != null) {
//            startActivity(intent);
//        }
//    }
//    private List<HomeItem> getItems() {
//        List<HomeItem> items = new ArrayList<>();
//        items.add(new HomeItem(R.drawable.cutterxanh,"mp3cutter", getString(R.string.mp3cutter)));
//        items.add(new HomeItem(R.drawable.merge,"merger", getString(R.string.merger)));
//        items.add(new HomeItem(R.drawable.mixer,"mixer",  getString(R.string.mixer)));
//        items.add(new HomeItem(R.drawable.speed,"speed",  getString(R.string.speed)));
//        items.add(new HomeItem(R.drawable.equalizer,"equalizer",  getString(R.string.equalizer)));
//        items.add(new HomeItem(R.drawable.valume,"valume",  getString(R.string.volume)));
//        items.add(new HomeItem(R.drawable.voidchanger,"voidchanger",  getString(R.string.voice_changer)));
//        items.add(new HomeItem(R.drawable.audiototex,"audiotex",  getString(R.string.audio_to_text)));
//
//        return items;
//    }
//    @Override
//    public void onBackPressed() {
//        SharedPreferences shared = getSharedPreferences("setting", Context.MODE_PRIVATE);
//        String savedSetting = shared.getString("savedsetting", "false");
//
//        if ("true".equals(savedSetting)) {
//            finishAffinity();
//            super.onBackPressed();
//        } else {
//            SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
//            int backPressCount = sharedPreferences.getInt("back_press_count", 0);
//
//            // Save the updated back press count
//            save();
//
//            // Check if the back press count is odd
//            if (backPressCount % 2 == 0) {
//                // Show the rate dialog if it's not already showing
//                finishAffinity();
//                super.onBackPressed();
//            } else {
//                if (!isDialogShowing) {
//                    showdialograte();
//                }
//
//            }
//        }
//
//    }
//
//    private void showdialograte() {
//        // Set the flag to true indicating the dialog is showing
//        isDialogShowing = true;
//        setLocale(this);
//        View dimView = new View(this);
//        dimView.setBackgroundColor(Color.parseColor("#A19AA6"));
//        dimView.setAlpha(0.6f);
//
//        // Add the overlay view to the root view
//        ViewGroup rootView = findViewById(android.R.id.content);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        rootView.addView(dimView, layoutParams);
//
//        RateDialogBinding binding = RateDialogBinding.inflate(getLayoutInflater());
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(binding.getRoot());
//        AlertDialog dialog = builder.create();
//dialog.setCancelable(false);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//        binding.titleTextView.setSelected(true);
//        dialog.setCanceledOnTouchOutside(false);
//        binding.ratingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> {
//            updateIconAndText(binding, Math.round(rating));
//        });
//        binding.subtitleTextView.setSelected(true);
//        binding.rateButton.setOnClickListener(v -> {
//            float rating = binding.ratingBar.getRating();
//
//            if (rating == 0) {
//                Toast.makeText(this, R.string.please_select_a_rating_before_submitting, Toast.LENGTH_SHORT).show();
//            } else {
//                finishAffinity();
//                Toast.makeText(this, R.string.thank_you_for_your_rating, Toast.LENGTH_SHORT).show();
//                rootView.removeView(dimView);
//                saveValueToPreferences("true");
//                dialog.dismiss();
//            }
//        });
//
//        binding.exitButton.setOnClickListener(v -> {
//            finishAffinity();
//            rootView.removeView(dimView);
//            dialog.dismiss();
//        });
//
//        dialog.setOnDismissListener(dialogInterface -> {
//            rootView.removeView(dimView);
//            isDialogShowing = false;  // Reset the flag when the dialog is dismissed
//        });
//
//        dialog.show();
//    }
//    private void saveValueToPreferences(String value) {
//        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("savedsetting", value);
//        editor.apply();
//    }
//    private void save() {
//        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        int backPressCount = sharedPreferences.getInt("back_press_count", 0);
//        backPressCount++;
//        editor.putInt("back_press_count", backPressCount);
//        editor.apply();
//    }
//
//    private void updateIconAndText(RateDialogBinding binding, int rating) {
//        setLocale(this);
//        switch (rating) {
//            case 1:
//                binding.emojiImageView.setImageResource(R.drawable.icon2);
//                binding.titleTextView.setText(R.string.oh_no);
//                binding.subtitleTextView.setText(R.string.please_give_us_some_feedback);
//                break;
//            case 2:
//                binding.emojiImageView.setImageResource(R.drawable.ic3);
//                binding.titleTextView.setText(R.string.oh_no);
//                binding.subtitleTextView.setText(R.string.please_give_us_some_feedback);
//                break;
//            case 3:
//                binding.emojiImageView.setImageResource(R.drawable.ic4);
//                binding.titleTextView.setText(R.string.oh_no);
//                binding.subtitleTextView.setText(R.string.please_give_us_some_feedback);
//                break;
//            case 4:
//                binding.emojiImageView.setImageResource(R.drawable.ic5);
//                binding.titleTextView.setText(R.string.we_love_you_too);
//                binding.subtitleTextView.setText(R.string.thanks_for_your_feedback);
//                break;
//            case 5:
//                binding.emojiImageView.setImageResource(R.drawable.ic6);
//                binding.titleTextView.setText(R.string.we_love_you_too);
//                binding.subtitleTextView.setText(R.string.thanks_for_your_feedback);
//                break;
//            default:
//                binding.emojiImageView.setImageResource(R.drawable.ic0);
//                binding.titleTextView.setText(R.string.do_you_like_the_app);
//                binding.subtitleTextView.setText(R.string.let_us_know_your_experience);
//                break;
//        }
//
//    }
//
//}
