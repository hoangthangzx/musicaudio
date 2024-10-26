package com.example.Home;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.FragmentMyidiaBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyidiaFragment extends Fragment {

    private FragmentMyidiaBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentMyidiaBinding.inflate(inflater, container, false);

        // Set Locale and show system UI
        setLocale(requireContext());
        showSystemUI((AppCompatActivity) requireActivity(), true);

        // Set up ViewPager with Adapter and TabLayout
        ViewPagerAdapter2 adapter = new ViewPagerAdapter2(requireActivity());
        binding.viewPager.setAdapter(adapter);

        // Attach the TabLayoutMediator to synchronize TabLayout with ViewPager
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

        // Return the root view for the fragment
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
