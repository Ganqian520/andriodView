package com.gq.music.songList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.event.EventSongsChange;
import com.gq.music.service.EventFMNext;
import com.gq.music.songsMenu.EventSpecialList;
import com.gq.music.songsMenu.FragmentMenu;
import com.gq.music.util.Data;
import com.gq.music.util.SPutil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class FragmentList extends Fragment {
  private RecyclerView rv_song;
  private AdapterListSong adapterListSong;
  private SwipeRefreshLayout id_refresh;
  private SPutil sp;
  private String id;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fg_menusong, new FragmentMenu(),"fg_menusong").commitAllowingStateLoss();
    return inflater.inflate(R.layout.fragment_list_song,container,false); //绑定布局
  }

  @Override
  public void onStart() {
    super.onStart();
    if(rv_song==null){
      id_refresh = getView().findViewById(R.id.id_refresh);
      rv_song = getView().findViewById(R.id.rv_song);
      rv_song.setLayoutManager(new LinearLayoutManager(getContext()));
      adapterListSong = new AdapterListSong(getContext(),getView().findViewById(R.id.rcv_out));
      rv_song.setAdapter(adapterListSong);
      id_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          if(Data.originNode==0){
            getData(id);
          }else{
            getDou(id);
          }
        }
      });
      sp = new SPutil(getContext());
      EventBus.getDefault().register(this);
    }
  }

  @Subscribe  //监听歌单变化   上面的menu比较快吧 初始化时就能收到监听？
  public void onEvent(EventSongsChange event){
    Data.list.clear();
    setState();
    id = event.dataSongs.id;
    if(Data.originNode==0){ //网易的歌曲列表获取
      if(!getDataLocal(id)){
        getData(id);
      }
    }else{  //抖音
      if(!getDouLocal(event.dataSongs.id)){
        getDou(id);
      }
    }
  }

  @Subscribe  //私人fm模式，日推
  public void onFM(EventSpecialList e){
    if(e.flag==0){
      getDataFM();
    }else if(e.flag==1){
      getDataDay();
    }
  }

  @Subscribe  //私人fm下一首
  public void onFMNext(EventFMNext e){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {

        adapterListSong.notifyDataSetChanged();
      }
    });
  }

  void getDataDay(){

  }

  void getDataFM(){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Data.list.clear();
        adapterListSong.notifyDataSetChanged();
      }
    });
    String url = Data.baseUrl+"/personal_fm";
    Callback callback = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {System.out.println(""+"请求fm:"+e);}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
          String str_res = response.body().string();
          System.out.println(""+str_res);
          JSONObject jobj = JSON.parseObject(str_res);
          JSONArray arr = jobj.getJSONArray("data");
          for(int i=0;i<arr.size();i++){
            JSONObject item = arr.getJSONObject(i);
            DataSong dataSong = new DataSong();
            dataSong.name = item.getString("name");
            dataSong.id = item.getString("id");
            dataSong.img = item.getString("picUrl");
            JSONArray arr1 = item.getJSONArray("artists");
            String str_author = "";
            for(int j=0;j<arr1.size();j++){
              str_author =  str_author +  "/" +  arr1.getJSONObject(j).getString("name") ;
            }
            dataSong.author = str_author.substring(1);
            Data.list_fm.add(dataSong);
          }
          Data.list.add(Data.list_fm.get(0));
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              adapterListSong.notifyDataSetChanged();
            }
          });
        }catch(Exception e){System.out.println("异常："+e);}
      }
    };
    Request.get(url,callback);
  }

  boolean getDataLocal(String id) {
    String str = sp.getString(id);
    if(str.equals("")){
      return false;
    }else {
      JSONArray arr = JSONArray.parseArray(str);
      for(int i=0;i<arr.size();i++){
        DataSong dataSong = JSONObject.toJavaObject(arr.getJSONObject(i),DataSong.class);
        Data.list.add(dataSong);
      }
      adapterListSong.notifyDataSetChanged();
      return true;
    }
  }

  private void getData(String id) {
    String url = Data.baseUrl + "/playlist/detail?id=" + id;
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getContext(),"网络异常"+e.toString(),Toast.LENGTH_LONG).show();
            id_refresh.setRefreshing(false);
          }
        });
      }
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try {
          JSONObject res = JSONObject.parseObject(response.body().string());
          if(res.getJSONObject("playlist")!=null){
            JSONArray arr = res.getJSONObject("playlist").getJSONArray("tracks");
            Data.list.clear();
            for(int i=0;i<arr.size();i++){
              DataSong dataSong = new DataSong();
              JSONObject obj = arr.getJSONObject(i);
              JSONArray arr1 = obj.getJSONArray("ar");
              String str_author = "";
              for(int j=0;j<arr1.size();j++){
                str_author =  str_author +  "/" +  arr1.getJSONObject(j).getString("name") ;
              }
              dataSong.author = str_author.substring(1);
              dataSong.name = obj.getString("name");
              dataSong.id = obj.getString("id");
              dataSong.img = obj.getJSONObject("al").getString("picUrl");
              dataSong.duration = obj.getInteger("dt");
              Data.list.add(dataSong);
            }
            String str = JSON.toJSONString(Data.list);
            sp.putString(id,str);
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                adapterListSong.notifyDataSetChanged();
                id_refresh.setRefreshing(false);
              }
            });
          }else {
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(getContext(),"未获取到数据"+res.toString(),Toast.LENGTH_LONG).show();
                id_refresh.setRefreshing(false);
              }
            });
            System.out.println("未获取到数据"+res.toString());
          }
        }catch (Exception e){System.out.println("捕获到异常"+e);}
      }
    };
    Request.get(url,cb);
  }

  boolean getDouLocal(String id){
    String str = sp.getString("songlist_dou");
    if(str.equals("")){
      return false;
    }else{
      JSONArray arr = JSONArray.parseArray(str);
      for(int i=0;i<arr.size();i++){
        JSONObject obj = arr.getJSONObject(i);
        if(id.equals("0")){ //全部
          Data.list.add(JSONObject.toJavaObject(obj,DataSong.class));
        }else if(id.equals("1")){ //纯音乐
          if(obj.getString("tag").equals("1")){
            Data.list.add(JSONObject.toJavaObject(obj,DataSong.class));
          }
        }else if(id.equals("2")){ //人声
          if(obj.getString("tag").equals("2")){
            Data.list.add(JSONObject.toJavaObject(obj,DataSong.class));
          }
        }
      }
      adapterListSong.notifyDataSetChanged();
      return true;
    }
  }

  void getDou(String id){
    Map map = new HashMap();
    map.put("action","getDouYin");
    Callback cb = new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {}
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          String str = response.body().string();
          JSONObject obj = JSON.parseObject(str);
          if(obj.get("message").equals("成功")) {
            JSONArray list = obj.getJSONArray("data");
            Data.list.clear();
            for(int i=0;i<list.size();i++){
              DataSong dataSong = new DataSong();
              JSONObject obj2 = list.getJSONObject(i);
              dataSong.name = obj2.getString("name");
              dataSong.tag = obj2.getString("tag");
              dataSong.musicUrl = obj2.getJSONObject("res").getString("musicUrl");
              dataSong.duration = obj2.getJSONObject("res").getInteger("musicDuration");
              dataSong.img = obj2.getJSONObject("res").getString("musicCover");
              dataSong.author = obj2.getJSONObject("res").getString("musicAuthor");
              Data.list.add(dataSong);
              String str2 = JSON.toJSONString(Data.list);
              sp.putString("songlist_dou",str2);
            }
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                adapterListSong.notifyDataSetChanged();
                id_refresh.setRefreshing(false);
              }
            });
          }
        }catch (Exception e){
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(getContext(),"捕获到异常"+e.toString(),Toast.LENGTH_LONG).show();
              id_refresh.setRefreshing(false);
            }
          });
          System.out.println("捕获到异常"+e);
        }
      }
    };
    Request.myPost(map, cb);
  }

  void setState(){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        adapterListSong.notifyDataSetChanged();
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
