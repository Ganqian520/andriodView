package com.gq.music.control;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gq.music.R;
import com.gq.music.event.EventAddCustom;
import com.gq.music.event.EventIsPlay;
import com.gq.music.event.EventPlayProgress;
import com.gq.music.event.EventSongChange;
import com.gq.music.util.Data;
import com.gq.music.util.Icon;
import com.gq.music.service.PlayerService;
import com.gq.music.util.MyAnimation;
import com.gq.music.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static android.content.Context.BIND_AUTO_CREATE;

public class FragmentControl extends Fragment {

  private Icon bt_play,bt_next,bt_last,bt_list,bt_mode;
  private TextView tv_time_left,tv_time_right,tv_name;
  private SeekBar sb_control;
  private boolean isUserSeek = false;
  private int currentSecond;

  private PlayerService.MusicControl control;
  private ServiceConnection conn = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      control = (PlayerService.MusicControl) service;
      Data.control = control;
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {}
  };

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    return inflater.inflate(R.layout.fragment_control,container,false);
  }

  @Override
  public void onStart() {
    super.onStart();
    init(getView());
    Intent intent = new Intent(getContext(), PlayerService.class);
    getContext().bindService(intent,conn,BIND_AUTO_CREATE);   //绑定fragment与service
  }

  //监听进度更新
  @Subscribe
  public void onEvent(EventPlayProgress event){
    currentSecond = event.currentSecond;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if(Data.duration!=0){
          tv_time_right.setText(Util.transTime(Data.duration));
          sb_control.setMax(Data.duration);
          if(!isUserSeek){
            sb_control.setProgress(event.currentSecond);
          }
        }
      }
    });
  }
  //监听歌曲变化
  @Subscribe
  public void onSongChange(EventSongChange event){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        tv_time_right.setText(Util.transTime(Data.duration));
        tv_name.setText(Data.dataSong.name+"——"+Data.dataSong.author);
      }
    });
  }
  //监听自定义队列的添加,抖动列表图标
  @Subscribe
  public void onAddCustom(EventAddCustom event){
    MyAnimation.aniRock(bt_list);
  }
  //监听歌曲的播放暂停
  @Subscribe
  public void onIsPlay(EventIsPlay event){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if(event.isPlay){
          bt_play.setText(R.string.pause);
        }else{
          bt_play.setText(R.string.play);
        }
      }
    });
  }

  void init(View view){
    OnClick click = new OnClick();
    tv_name = view.findViewById(R.id.tv_name);
    tv_name.setSelected(true);
    bt_play = view.findViewById(R.id.bt_play);
    bt_next = view.findViewById(R.id.bt_next);
    bt_last = view.findViewById(R.id.bt_last);
    bt_list = view.findViewById(R.id.bt_list);
    bt_mode = view.findViewById(R.id.bt_mode);
    sb_control = view.findViewById(R.id.sb_control);
    tv_time_left = view.findViewById(R.id.tv_time_left);
    tv_time_right = view.findViewById(R.id.tv_time_right);
    bt_play.setOnClickListener(click);
    bt_next.setOnClickListener(click);
    bt_last.setOnClickListener(click);
    bt_list.setOnClickListener(click);
    bt_mode.setOnClickListener(click);
    sb_control.setOnSeekBarChangeListener(new OnSeek());
    sb_control.setProgress(0);
  }

  public class OnSeek implements SeekBar.OnSeekBarChangeListener{

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      tv_time_left.setText(Util.transTime(currentSecond));
      if(isUserSeek){
        control.seekTo(progress*1000);
        tv_time_left.setText(Util.transTime(progress));
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      isUserSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      isUserSeek = false;
    }
  }

  public class OnClick implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()){
        case R.id.bt_play:
          if(Data.isPlay){
            control.pause();
            bt_play.setText(getResources().getString(R.string.play));
          }else{
            control.resume();
            bt_play.setText(getResources().getString(R.string.pause));
          }
          Data.isPlay = ! Data.isPlay;
          break;
        case R.id.bt_last:
          control.last();
          break;
        case R.id.bt_next:
          control.next();
          break;
        case R.id.bt_list:
          DialogListCustom dialogListCustom = new DialogListCustom(getContext());
          dialogListCustom.show();
          break;
        case R.id.bt_mode:
          Data.mode++;
          if(Data.mode==3){
            Data.mode = 0;
          }
          switch (Data.mode) {
            case 0:
              bt_mode.setText(getResources().getString(R.string.shunxv));
              Toast.makeText(getContext(),"顺序播放",Toast.LENGTH_SHORT).show();
              break;
            case 1:
              bt_mode.setText(getResources().getString(R.string.suiji));
              Toast.makeText(getContext(),"随机播放",Toast.LENGTH_SHORT).show();
              break;
            case 2:
              bt_mode.setText(getResources().getString(R.string.danqu));
              Toast.makeText(getContext(),"单曲循环",Toast.LENGTH_SHORT).show();
              break;
          }
          break;
      }
    }
  }
}
