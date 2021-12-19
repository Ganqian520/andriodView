package com.gq.music.other.GameAssist;

public class Assist implements Cloneable {
  public String name;
  public int w;
  public int h;
  public int w2;
  public int y;
  public Assist(){  //为了fastjson加一个无参构造函数

  }
  public Assist(int h,int w,int w2,int y){
    this.h = h;
    this.w = w;
    this.w2 = w2;
    this.y = y;
  }
  public Assist clone() throws CloneNotSupportedException{
    return (Assist) super.clone();
  }
}
