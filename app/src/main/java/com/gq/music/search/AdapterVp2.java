package com.gq.music.search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AdapterVp2 extends FragmentStateAdapter {

  private ArrayList<Fragment> list = new ArrayList<Fragment>();

  public AdapterVp2(@NonNull FragmentActivity fragmentActivity,VM vm) {
    super(fragmentActivity);
    list.add(new FragmentNetSong(vm));
    list.add(new FragmentDouSong(vm));
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    return list.get(position);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }
}
