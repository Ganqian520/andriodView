package com.gq.music.search;

import androidx.fragment.app.FragmentManager;

import com.gq.music.play.DialogFragmentPlay;
import com.gq.music.songList.DataSong;
import com.gq.music.util.Data;

public class Handler {
  private FragmentManager fragmentManager;
  public Handler(FragmentManager fragmentManager){
    this.fragmentManager = fragmentManager;
  }
  public void play(DataSong song){
    DialogFragmentPlay dialogFragmentPlay;
    dialogFragmentPlay = new DialogFragmentPlay();
    dialogFragmentPlay.show(fragmentManager,"tag");
    Data.flagPlay = 1;
    Data.dataSong = song;
    Data.control.play();
  }
}
