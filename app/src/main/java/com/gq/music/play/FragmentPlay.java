package com.gq.music.play;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.event.EventSongChange;
import com.gq.music.spectrum.FragmentSpectrum;
import com.gq.music.spectrum.FragmentSpectrumWeb;
import com.gq.music.util.Data;
import com.gq.music.util.MySQLite;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentPlay extends Fragment implements View.OnClickListener{

  private LinearLayout ll_middle_line;
  private RelativeLayout rl_lrc;
  private TextView tv_time;
  TextViewCommentsLoop my_tv;
  private ViewLyric viewLyric;
  private MySQLite db;
  private String str_comments;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_play,container,false);
  }
  @Override
  public void onStart() {
    super.onStart();
//    System.out.println("start");
    if(db==null){
      initBinding();
      getActivity().getSupportFragmentManager().beginTransaction()
        .add(R.id.fl_spectrum,new FragmentSpectrum(),"fl_spectrum").commitAllowingStateLoss();
      getActivity().getSupportFragmentManager().beginTransaction()
        .add(R.id.fl_spectrum_web,new FragmentSpectrumWeb(),"fl_spectrum_web").commitAllowingStateLoss();
    }
  }
  @Override
  public void onResume() {
    super.onResume();
    if(my_tv!=null){
      my_tv.start();
    }
//    System.out.println("lyric resume");
    EventBus.getDefault().register(this);
    if(Data.dataSong!=null){
      db = new MySQLite(getContext());
      getData(Data.dataSong.id);
      getData_comments(Data.dataSong.id);
    }
  }
  @Override
  public void onPause() {
    super.onPause();
    my_tv.stop();
//    System.out.println("lyric pause");
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void onSongChange(EventSongChange event){
    String id = event.dataSong.id;
    getData(id);
    getData_comments(id);
  }

  void initBinding(){
    View view = getView();
    my_tv = getView().findViewById(R.id.my_tv);
    tv_time = view.findViewById(R.id.tv_time);
    viewLyric = view.findViewById(R.id.view_lyric);
    view.findViewById(R.id.ic_collect).setOnClickListener(this);
    view.findViewById(R.id.ic_seek).setOnClickListener(this);
    initListener();
  }

  void getData(String id){
    if(Data.originNode==0){
      String res = db.getLyric(id);
      if(res!="notFound"){  //数据库中有歌词
        handleLyric(res);
      }else{
        String url = Data.baseUrl + "/lyric?id=" + id;
        Callback cb = new Callback() {
          @Override
          public void onFailure(@NotNull Call call, @NotNull IOException e) {System.out.println(e);}
          @Override
          public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            JSONObject res = JSONObject.parseObject(response.body().string());
            String str_lyric = res.getJSONObject("lrc").getString("lyric");
            db.addLyric(id,str_lyric);
            handleLyric(str_lyric);
          }
        };
        Request.get(url,cb);
      }
    }
  }

  void handleLyric(String str_lyric){
    ArrayList<DataLyric> list = new ArrayList<DataLyric>();
    String[] _list = str_lyric.split("\n");
    for(int i=0;i<_list.length;i++){
      String item = _list[i];
      DataLyric dataLyric = new DataLyric();
      try{
        if(item.substring(9,10).equals("]")){
          dataLyric.content = item.substring(10);
        }else{
          dataLyric.content = item.substring(11);
        }
        if(dataLyric.content.equals("")){
          continue;
        }
        dataLyric.time_str = item.substring(1,6);
        int minute = Integer.parseInt(item.substring(1,3));
        int second = (int) Math.round(Double.parseDouble(item.substring(4,8)));
        dataLyric.time_second = minute*60+second;
      }catch (Exception e){
        dataLyric.time_second = 0;
      }
      list.add(dataLyric);
    }
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        viewLyric.setList(list);
      }
    });
  }

  void initListener(){

  }

  void getData_comments(String id){
    String url = Data.baseUrl+"/comment/music?id="+id;
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {System.out.println(e);}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try {
          JSONObject res = JSONObject.parseObject(response.body().string());
          JSONArray arr = res.getJSONArray("hotComments");
          str_comments = "";
          for(int i=0;i<arr.size();i++){
            JSONObject obj = arr.getJSONObject(i);
//            DataComment dataComment = new DataComment();
//            dataComment.nick = obj.getJSONObject("user").getString("nickname");
//            dataComment.content = obj.getString("content");
//            list_comments.add(dataComment);
            str_comments = str_comments + obj.getJSONObject("user").getString("nickname") +"："
              +obj.getString("content")+"           ";
          }
          str_comments = str_comments.replaceAll("\r|\n","");
        }catch (Exception e){}
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            my_tv.setText(str_comments);
            my_tv.start();
          }
        });
      }
    };
    Request.get(url,cb);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.ic_collect:
        DialogFragmentCollect dialogFragmentCollect = new DialogFragmentCollect();
        dialogFragmentCollect.show(getParentFragmentManager(),"1");
        break;
      case R.id.ic_seek:

        break;
    }
  }
}
