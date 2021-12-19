package com.gq.music.components;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gq.music.databinding.FragmentDrawerBinding;
import com.gq.music.other.GameAssist.ActivityGame;
import com.gq.music.Login.ActivityLogin;
import com.gq.music.R;
import com.gq.music.event.EventLogin;
import com.gq.music.other.lyric3d.ActivityLyric;
import com.gq.music.other.naked3d.ActivityNaked3d;
import com.gq.music.other.pianoWindow.ActivityPianoWindow;
import com.gq.music.util.SPutil;
import com.gq.video.ActivityIndexVideo;
import com.gq.webview.ActivityWebDouMusic;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

//侧滑栏
public class FragmentDrawer extends Fragment implements View.OnClickListener {
  private SPutil sp;
  private TextView bt_assist;
  private TextView bt_login;
  private FragmentDrawerBinding binding;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
//    return inflater.inflate(R.layout.fragment_drawer,container,false);
    binding = FragmentDrawerBinding.inflate(inflater,container,false);
    return binding.getRoot();
  }

  @Override
  public void onStart() {
    super.onStart();
    sp = new SPutil(getContext());
    init();
  }
  void init(){
    binding.btMusicDou.setOnClickListener(v->{
      startActivity(new Intent(getContext(), ActivityWebDouMusic.class));
    });
    binding.btVideo.setOnClickListener(v -> {
      startActivity(new Intent(getContext(), ActivityIndexVideo.class));
    });
    bt_assist = getView().findViewById(R.id.bt_assist);
    bt_assist.setOnClickListener(this);
    bt_login = getView().findViewById(R.id.bt_login);
    bt_login.setOnClickListener(this);
    getView().findViewById(R.id.bt_naked3d).setOnClickListener(this);
    getView().findViewById(R.id.bt_lyric).setOnClickListener(this);
    getView().findViewById(R.id.bt_piano_window).setOnClickListener(this);
  }

  @Subscribe
  public void onEvent(EventLogin event){
    Toast.makeText(getContext(),"登录成功",Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.bt_assist:
        Intent intent = new Intent(getContext(), ActivityGame.class);
        getContext().startActivity(intent);
        break;
      case R.id.bt_login:
        Intent intent1 = new Intent(getContext(), ActivityLogin.class);
        startActivity(intent1);
        break;
      case R.id.bt_naked3d:
        startActivity(new Intent(getContext(), ActivityNaked3d.class));
        break;
      case R.id.bt_lyric:
        startActivity(new Intent(getContext(), ActivityLyric.class));
        break;
      case R.id.bt_piano_window:
        startActivity(new Intent(getContext(), ActivityPianoWindow.class));
        break;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }
}
