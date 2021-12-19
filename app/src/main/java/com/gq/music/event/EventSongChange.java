package com.gq.music.event;

import com.gq.music.songList.DataSong;

public class EventSongChange {
  public DataSong dataSong;
  public EventSongChange(DataSong dataSong){
    this.dataSong = dataSong;
  }
}
