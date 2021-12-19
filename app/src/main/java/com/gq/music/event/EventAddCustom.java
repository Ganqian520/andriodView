package com.gq.music.event;

public class EventAddCustom {
  public int[] location;
  public int index;
  public EventAddCustom(int[] location,int index){
    this.location = location;
    this.index = index;
  }
}
