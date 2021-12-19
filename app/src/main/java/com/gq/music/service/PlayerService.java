package com.gq.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gq.music.event.EventPlayProgress;
import com.gq.music.event.EventSongChange;
import com.gq.music.songList.DataSong;
import com.gq.music.util.Data;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerService extends Service {

  private MediaPlayer mediaPlayer;
  private TimerTask task;
  private Timer timer;
  private ArrayList<DataSong> list;

  public PlayerService(){}

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return new MusicControl();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mediaPlayer = new MediaPlayer();
    Data.mediaPlayer = mediaPlayer;
    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {  //播放完毕回调
        next1(mediaPlayer);
      }
    });
    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {  //缓冲进度回调
      @Override
      public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d("buffer更新", String.valueOf(percent));
      }
    });
    timer = new Timer();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  void initTask(){
    task = new TimerTask() {
      @Override
      public void run() {
        int currentSecond = (int) ((float)mediaPlayer.getCurrentPosition()/1000);
        EventBus.getDefault().post(new EventPlayProgress(currentSecond));
      }
    };
    timer.scheduleAtFixedRate(task,0,1000);
  }

  public void next1(MediaPlayer mediaPlayer){
    try{
      if(Data.flagPlay==0 || Data.flagPlay==1){
        if(Data.list_custom.size()==0){
          if(Data.mode==0){ //顺序
            Data.index = Data.index==Data.list.size()-1 ? 0 : Data.index+1;
            Data.control.play();
          }else if(Data.mode==1){  //随机
            Data.index = new Random().nextInt(Data.list.size());
            Data.control.play();
          }else{  //单曲
            mediaPlayer.start();
          }
        }else{
          Data.control.play();
        }
      }else if(Data.flagPlay==2){ //私人fm模式的下一首
        Data.list_custom.remove(0);
        Data.list.clear();
        Data.list.add(Data.list_fm.get(0));
        EventBus.getDefault().post(new EventFMNext());
        Data.control.play();
      }
    }catch(Exception e){System.out.println("异常 下一首："+e);}
  }

  public class MusicControl extends Binder {
    public void play() {
      try{
        String url;
        String proxyUrl = "";
        DataSong dataSong;
        if(Data.flagPlay==0){
          if(Data.list_custom.size()==0){
            dataSong = Data.list.get(Data.index);
            if(Data.originNode==0){
              url = "http://music.163.com/song/media/outer/url?id=" + dataSong.id;
            }else{
              url = dataSong.musicUrl;
            }
          }else{
            dataSong = Data.list_custom.get(0);
            if(Data.originNode==0){
              url = "http://music.163.com/song/media/outer/url?id=" + dataSong.id;
            }else{
              url = dataSong.musicUrl;
            }
            Data.list_custom.remove(0);
          }
          Data.dataSong = dataSong;
          proxyUrl = Data.proxy.getProxyUrl(url);
        }else if(Data.flagPlay==1){ //搜索
          proxyUrl = "http://music.163.com/song/media/outer/url?id=" + Data.dataSong.id;
        }else if(Data.flagPlay==2){ //私人fm
          System.out.println(""+"play");
          Data.dataSong = Data.list_custom.get(0);
          proxyUrl = "http://music.163.com/song/media/outer/url?id=" + Data.dataSong.id;
        }
        EventBus.getDefault().post(new EventSongChange(Data.dataSong));
        Data.duration = 0;  //重置总时长
        mediaPlayer.reset();
        try {
          mediaPlayer.setDataSource(proxyUrl);
        } catch (IOException e) {
          e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
            Data.duration = (int) Math.round((float)mediaPlayer.getDuration()/1000);
          }
        });
        initTask();
      }catch(Exception e){System.out.println("异常 播放："+e);}
    }
    public void pause(){
      mediaPlayer.pause();
      task.cancel();
    }
    public void resume(){
      mediaPlayer.start();
      initTask();
    }
    public void last(){

    }
    public void next(){
      next1(mediaPlayer);
    }
    public void stop(){
      mediaPlayer.stop();
    }
    public void seekTo(int ms){
      mediaPlayer.seekTo(ms);
    }
  }
}
