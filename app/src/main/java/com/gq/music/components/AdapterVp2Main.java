package com.gq.music.components;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gq.music.play.FragmentPlay;
import com.gq.music.songList.FragmentList;

import java.util.ArrayList;

public class AdapterVp2Main extends FragmentStateAdapter {
  ArrayList<Fragment> fragments = new ArrayList<Fragment>();

  public AdapterVp2Main(@NonNull FragmentActivity fragmentActivity) {
    super(fragmentActivity);
    fragments.add(new FragmentList());
    fragments.add(new FragmentPlay());
    fragments.add(new FragmentDescribe());
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    return fragments.get(position);
  }

  @Override
  public int getItemCount() {
    return fragments.size();
  }

}
