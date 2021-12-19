package com.gq.music.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.songList.DataSong;
import com.gq.music.util.Data;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentNetSong extends Fragment {
  
  private AdapterResult adapterResult;
  private ArrayList<DataSong> list = new ArrayList<DataSong>();
  private VM vm;

  public FragmentNetSong(VM vm){
    this.vm = vm;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    return inflater.inflate(R.layout.fragment_search_result,container,false);
  }

  @Override
  public void onStart() {
    super.onStart();
    if(adapterResult==null){
      adapterResult = new AdapterResult(getContext(),list,0,getActivity().getSupportFragmentManager());
      RecyclerView rcv_result = getView().findViewById(R.id.rcv_result);
      rcv_result.setLayoutManager(new LinearLayoutManager(getContext()));
      rcv_result.setAdapter(adapterResult);
      getResult(vm.getInput());
    }
  }

  @Subscribe
  public void onSearchNet(EventSearchNet e){
    getResult(vm.getInput());
  }

  void getResult(String keyword){
    list.clear();
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        adapterResult.notifyDataSetChanged();
      }
    });
    String url = Data.baseUrl+"/search?keywords="+keyword;
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
          String str_res = response.body().string();
          JSONObject obj_res = JSON.parseObject(str_res);
          JSONArray arr = obj_res.getJSONObject("result").getJSONArray("songs");
          for(int i=0;i<arr.size();i++){
            JSONObject item = arr.getJSONObject(i);
            DataSong dataSong = new DataSong();
            dataSong.id = item.getString("id");
            dataSong.name = item.getString("name");
            JSONArray arr_author = item.getJSONArray("artists");
            String str_author = "";
            for(int j=0;j<arr_author.size();j++){
              str_author =  str_author +  "/" +  arr_author.getJSONObject(j).getString("name") ;
            }
            dataSong.author = str_author.substring(1);
            list.add(dataSong);
          }
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              adapterResult.notifyDataSetChanged();
            }
          });
        }catch(Exception e){System.out.println("异常："+e);}
      }
    };
    Request.get(url,cb);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }
}
