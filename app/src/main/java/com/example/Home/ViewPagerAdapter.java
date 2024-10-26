package com.example.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Here, you could pass the audio action listener to each fragment if needed
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MyidiaFragment();
            default:
                return new MyidiaFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }




}
