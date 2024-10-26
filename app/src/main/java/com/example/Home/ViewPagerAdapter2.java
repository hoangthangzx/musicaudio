package com.example.Home;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fragment.EqualizerFragment;
import com.example.fragment.SpeedFragment;
import com.example.fragment.VoiceChangerFragment;
import com.example.fragment.VolumeFragment;
import com.example.fragment.Mp3cutterFragment;
import com.example.fragment.MergeFragment;
import com.example.fragment.MixerFragment;
import com.example.myaudio.OnAudioActionListener;

public class ViewPagerAdapter2 extends FragmentStateAdapter {
    private OnAudioActionListener audioActionListener;

    public ViewPagerAdapter2(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Here, you could pass the audio action listener to each fragment if needed
        switch (position) {
            case 0:
                return new Mp3cutterFragment();
            case 1:
                return new MergeFragment();
            case 2:
                return new MixerFragment();
            case 3:
                return new SpeedFragment();
            case 4:
                return new VolumeFragment();
            case 5:
                return new EqualizerFragment();
            case 6:
                return new VoiceChangerFragment();
            default:
                return new Mp3cutterFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }



}
