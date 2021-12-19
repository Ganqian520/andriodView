package com.gq.music.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.databinding.ActivitySearchBinding;
import com.gq.music.util.Data;
import com.gq.music.util.Util;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivitySearch extends AppCompatActivity {

  private VM vm;
  private AdapterGuess adapterGuess;
  private ArrayList<String> list_hot = new ArrayList<String>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    init();
    Util.tranStatusBar(this);
    EventBus.getDefault().register(this);
    initHot();
  }

  @Subscribe
  public void onKeyword(EventKeyword e){
    this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        adapterGuess.notifyDataSetChanged();
      }
    });
  }

  void init(){
    vm = new VM();
    adapterGuess = new AdapterGuess(vm,this);
    ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
    binding.setVm(vm);
    binding.rcvGuess.setAdapter(adapterGuess);

    @SuppressLint("WrongViewCast") TabLayout tabLayout = findViewById(R.id.tl);
    ViewPager2 viewPager2 = findViewById(R.id.vp2);
    viewPager2.setAdapter(new AdapterVp2(this,vm));
    String[] list_tab = {"网易","抖音"};
    new TabLayoutMediator(tabLayout, viewPager2,
      (tab, position) -> tab.setText(list_tab[position])
    ).attach();
    //监听键盘确认点击
    EditText editText = findViewById(R.id.et);
    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        InputMethodManager imm = (InputMethodManager) editText.getContext()
          .getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
        vm.setFlag_state(2);
        EventBus.getDefault().post(new EventSearchNet());
        return true;
      }
    });

  }
  //输入框的叉
  public void clear(View view){
    vm.setInput("");
  }
  //获取热搜数据，绑定适配器，设置点击监听
  void initHot(){
    Activity that = this;
    GridView gv = findViewById(R.id.gv);
    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        vm.keyword = list_hot.get(position);
        vm.isFromHot = true;
        vm.setInput(list_hot.get(position));
        vm.setFlag_state(2);
        EventBus.getDefault().post(new EventSearchNet());
      }
    });
    String url = Data.baseUrl+"/search/hot/detail";
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
          String str_res = response.body().string();
          JSONObject obj_res = JSON.parseObject(str_res);
          JSONArray arr = obj_res.getJSONArray("data");
          for(int i=0;i<arr.size();i++){
            list_hot.add(arr.getJSONObject(i).getString("searchWord"));
          }
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              gv.setAdapter(new AdapterHot(that,list_hot));
            }
          });
        }catch(Exception e){System.out.println("异常："+e);}
      }
    };
    Request.get(url,cb);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Data.flagPlay = 0;
    EventBus.getDefault().unregister(this);
  }
}