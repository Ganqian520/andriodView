package com.gq.music.event;

import com.gq.music.songsMenu.DataSongs;

public class EventSongsChange {
  public DataSongs dataSongs;
  public EventSongsChange(DataSongs dataSongs){
    this.dataSongs = dataSongs;
  }
}
