package com.gq.music.util;

import android.media.MediaPlayer;

import com.danikula.videocache.HttpProxyCacheServer;
import com.gq.music.Login.DataLogin;
import com.gq.music.service.PlayerService;
import com.gq.music.songList.DataSong;

import java.util.ArrayList;

public class Data {

  public static DataLogin dataLogin;

  public static double h;  //屏幕宽高,高没有加状态栏
  public static double w;
  public static double sbH; //状态栏高度

//  public static final String baseUrl = "https://autumnfish.cn";
  public static final String baseUrl = "http://cloud-music.pl-fe.cn";
//  public static final String baseUrl = "http://192.168.1.100:3000";

  public static ArrayList<DataSong> list = new ArrayList<DataSong>();  //歌曲列表
  public static ArrayList<DataSong> list_custom = new ArrayList<DataSong>();//自定义列表
  public static ArrayList<DataSong> list_fm = new ArrayList<DataSong>();  //私人fm
  public static int index = -1; //当前播放序号
  public static int currentLyric = 0;//当前歌词序号
  public static int duration = 0;   //音乐总时长，ms
  public static int mode = 0;//0顺序 1随机 2单曲
  public static boolean isPlay = false; //播放状态
  public static DataSong dataSong;  //当前歌曲对象实体
  public static int originNode  = 0;  //0网易云，1抖音
  public static int flagPlay = 0; //0常用列表，1搜索，2私人fm

  public static PlayerService.MusicControl control; //播放控制器
  public static MediaPlayer mediaPlayer;  //播放器实例

  public static HttpProxyCacheServer proxy; //播放代理

}
