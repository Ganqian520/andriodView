package com.gq.music.search;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.BR;
import com.gq.music.api.Request;
import com.gq.music.util.Data;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VM extends BaseObservable {

  private String input = "";
  private int flag_state = 0;//0:输入空，显示排行 1:显示备选词 2：搜所结果

  public boolean isFromHot = false; //输入框的变化是否由点击热榜引起
  public ArrayList<String> list_keyword = new ArrayList<String>();
  public String keyword;

  //监听输入框变化，获取预测词
  public void onTextChanged(CharSequence text){
    input = text.toString();
    if(input.equals("")){
      setFlag_state(0);
    }else if(!isFromHot){
      setFlag_state(1);
      getGuess(list_keyword);
    }
    isFromHot = false;
  }

  void getGuess(ArrayList<String> list){
    list.clear();
    String url = Data.baseUrl+"/search/suggest?keywords="+input+"&type=mobile";
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {System.out.println(e);}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
          String str_res = response.body().string();
          JSONObject obj_res = JSON.parseObject(str_res);
          JSONArray arr = obj_res.getJSONObject("result").getJSONArray("allMatch");
          for(int i=0;i<arr.size();i++){
            list.add(arr.getJSONObject(i).getString("keyword"));
          }
          EventBus.getDefault().post(new EventKeyword());
        }catch(Exception e){System.out.println("异常："+e);}
      }
    };
    Request.get(url,cb);
  }

  @Bindable
  public String getInput(){
    return input;
  }
  public void setInput(String input){
    this.input = input;
    notifyPropertyChanged(BR.input);
  }
  @Bindable
  public int getFlag_state(){
    return flag_state;
  }
  public void setFlag_state(int flag_state){
    this.flag_state = flag_state;
    notifyPropertyChanged(BR.flag_state);
  }
}
