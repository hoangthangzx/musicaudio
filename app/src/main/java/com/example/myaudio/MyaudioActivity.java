package com.example.myaudio;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Home.HomeActivity;
import com.example.SettingActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityMyaudioBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyaudioActivity extends AppCompatActivity implements OnAudioActionListener{
ActivityMyaudioBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityMyaudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);
        binding.imageView2.setOnClickListener(v->{
            Intent a = new Intent(MyaudioActivity.this, SettingActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(a);
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.mp3_cutter);
                            break;
                        case 1:
                            tab.setText(R.string.merge);
                            break;
                        case 2:
                            tab.setText(R.string.mixer);
                            break;
                        case 3:
                            tab.setText(R.string.speed);
                            break;
                        case 4:
                            tab.setText(R.string.volume);
                            break;
                        case 5:
                            tab.setText(R.string.equalizer);
                            break;
                        case 6:
                            tab.setText(R.string.voice_changer);
                            break;
                    }
                }).attach();

    }

    @Override
    public void onStopAudio() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}