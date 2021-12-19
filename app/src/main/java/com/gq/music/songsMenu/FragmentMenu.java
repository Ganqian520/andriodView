package com.gq.music.songsMenu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.Login.DataLogin;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.event.EventSongsChange;
import com.gq.music.util.Data;
import com.gq.music.util.SPutil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentMenu extends Fragment implements View.OnClickListener {
  private ArrayList<DataSongs> list = new ArrayList<DataSongs>();
  private LinearLayout ll_songs;
  private SPutil sp;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_songsmenu,container,false);
  }

  @Override
  public void onStart() {
    super.onStart();
    if(sp==null){
      sp = new SPutil(getContext());
      initBinding();
      if(!getLoaclData()){
        getData();
      }
    }
  }

  void initBinding(){
    View v = getView();
    ll_songs = v.findViewById(R.id.ll_songs);
    v.findViewById(R.id.tv_dou).setOnClickListener(this);
    v.findViewById(R.id.tv_net).setOnClickListener(this);
    v.findViewById(R.id.tv_fm).setOnClickListener(this);
  }

  void getData(){ //网易云 在线
    String str = sp.getString("dataLogin");
    DataLogin dataLogin = JSONObject.toJavaObject(JSONObject.parseObject(str),DataLogin.class);
    String url = Data.baseUrl + "/user/playlist?uid=" + dataLogin.uid;
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String str = response.body().string();
        JSONArray arr = JSONObject.parseObject(str).getJSONArray("playlist");
        list.clear();
        for (int i = 0; i < arr.size(); i++) {
          DataSongs dataSongs = new DataSongs();
          if (!arr.getJSONObject(i).getBoolean("subscribed")) {
            dataSongs.name = arr.getJSONObject(i).getString("name");
            dataSongs.id = arr.getJSONObject(i).getString("id");
            list.add(dataSongs);
          }
        }
        list.get(0).name = "我喜欢";
        updateView();
        String str1 = JSON.toJSONString(list);
        sp.putString("songsList",str1);
      }
    };
    Request.get(url,cb);
  }

  boolean getLoaclData(){ //网易云 本地
    String str = sp.getString("songsList");
    if(str.equals("")){
      return false;
    }
    list.clear();
    JSONArray arr = JSONArray.parseArray(str);
    for(int i=0;i<arr.size();i++){
      DataSongs dataSongs = JSONObject.toJavaObject((JSON) arr.get(i),DataSongs.class);
      list.add(dataSongs);
    }
    updateView();  //更新列表视图
    return true;
  }

  void getDou(){
    Data.originNode = 1;
    list.clear();
    DataSongs dataSongs1 = new DataSongs();
    DataSongs dataSongs2 = new DataSongs();
    DataSongs dataSongs3 = new DataSongs();
    dataSongs1.name = "全部";
    dataSongs1.id = "0";
    dataSongs2.name = "纯音乐";
    dataSongs2.id = "1";
    dataSongs3.name = "人声";
    dataSongs3.id = "2";
    list.add(dataSongs1);
    list.add(dataSongs2);
    list.add(dataSongs3);
    updateView();
  }

  void updateView(){  //根据当前歌单列表更新视图
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ll_songs.removeAllViews();
        for(int i=0;i<list.size();i++){
          TextView tv = new TextView(getContext());
          tv.setText(list.get(i).name);
          RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
          params.height = 100;
          params.width = 190;
          tv.setLayoutParams(params);
          tv.setGravity(Gravity.CENTER);
          tv.setMaxLines(1);
          tv.setTextColor(Color.WHITE);
          int finalI = i;
          tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              EventBus.getDefault().post(new EventSongsChange(list.get(finalI))); //点击歌单项后通知更新
            }
          });
          ll_songs.addView(tv);
        }
        EventBus.getDefault().post(new EventSongsChange(list.get(0)));  //切换大菜单时通知更新
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.tv_dou:
        getDou();
        break;
      case R.id.tv_net:
        Data.originNode = 0;
        if(!getLoaclData()){
          getData();
        }
        break;
      case R.id.tv_fm:
        Data.flagPlay = 2;
        EventBus.getDefault().post(new EventSpecialList(0));
        break;
    }
  }
}
