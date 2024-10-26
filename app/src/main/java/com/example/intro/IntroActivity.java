package com.example.intro;



import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.Home.HomeActivity;
import com.example.interect.InterectActivity;
import com.example.PermissonActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityIntroBinding;
import com.example.ultils.SystemUtils;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.File;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;
    private int[] images = {R.drawable.intro1, R.drawable.intro2, R.drawable.intro3};
    private String[] texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);
        SystemUtils.setLocale(this);
        applyGradientToSaveText(binding.nextButton);
        // Initialize the texts array inside onCreate()
        texts = new String[]{
              getString(R.string.best_audio_editor),
               getString(R.string.easy_to_cut_music),
                getString(R.string.magic_of_voice_changing_effects)
        };
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);

        // Set up the adapter and link the dotsIndicator to viewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, images, texts);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);

        // Set click listener for the Next button
        binding.nextButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < images.length - 1) {
                // Move to the next item if not the last page
                viewPager.setCurrentItem(currentItem + 1);
            } else {
                // If it's the last page, transition to the appropriate activity
                transitionToNextActivity();
            }
        });
        File outputDirectory = new File(getExternalFilesDir(null), "Speed");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs(); // Tạo thư mục "Speed" nếu nó chưa tồn tại
        }

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
    private void transitionToNextActivity() {
        // Access SharedPreferences for permission and interaction status
        SharedPreferences sharedPreferencesPermisson = getSharedPreferences("permisson", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesInterect = getSharedPreferences("Interect", Context.MODE_PRIVATE);

        // Retrieve stored values
        String savedPermisson = sharedPreferencesPermisson.getString("savedpermisson", "false");
        String savedInterect = sharedPreferencesInterect.getString("savedInterect", "false");

        Intent intent;

        // Determine which activity to navigate to based on saved preferences
        if ("true".equals(savedPermisson) && "true".equals(savedInterect)) {
            intent = new Intent(IntroActivity.this, HomeActivity.class);
        } else if ("true".equals(savedPermisson)) {
            intent = new Intent(IntroActivity.this, HomeActivity.class);
        } else {
            intent = new Intent(IntroActivity.this, PermissonActivity.class);
        }

        // Start the chosen activity and finish the current one
        startActivity(intent);
        finish();
    }
}