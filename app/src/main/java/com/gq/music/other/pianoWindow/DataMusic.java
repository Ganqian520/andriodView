package com.gq.music.other.pianoWindow;

public class DataMusic implements Cloneable {
  public int indexX;  //在表格中的序号
  public int indexY;
  public int length; //占据几个八分音符表格
  public DataMusic(){

  }
  public DataMusic(int indexX,int indexY,int length){
    this.indexX = indexX;
    this.indexY = indexY;
    this.length = length;
  }
  public DataMusic clone()  {
    try {
      return (DataMusic) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }
}
