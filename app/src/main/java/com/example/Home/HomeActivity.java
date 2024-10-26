package com.example.Home;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {

        }).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabUI(position);
            }
        });
        binding.Home.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(0);  // Thay đổi vị trí của ViewPager sang tab 0
            updateTabUI(0);
        });

        binding.audiototex.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(1);  // Thay đổi vị trí của ViewPager sang tab 1
            updateTabUI(1);
        });

        updateTabUI(0);
    }

    private void updateTabUI(int position) {
        switch (position) {
            case 0:
                binding.ichome.setBackgroundResource(R.drawable.navication);
                binding.home.setImageResource(R.drawable.home);
                binding.my.setImageResource(R.drawable.myaudio);
                binding.icmy.setBackground(null);
                binding.audio.setTextColor(Color.parseColor("#B3FFFFFF"));
                binding.voidchan.setTextColor(getResources().getColor(R.color.white, null));
                break;
            case 1:
                binding.ichome.setBackground(null);
                binding.home.setImageResource(R.drawable.homenhat);
                binding.my.setImageResource(R.drawable.myideadam);
                binding.icmy.setBackgroundResource(R.drawable.navication);
                binding.voidchan.setTextColor(Color.parseColor("#B3FFFFFF"));
                binding.audio.setTextColor(getResources().getColor(R.color.white, null));

                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}
