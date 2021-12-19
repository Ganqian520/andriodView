package com.gq.music.play;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.event.EventPlayProgress;
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

public class DialogFragmentPlay extends DialogFragment implements View.OnClickListener {

  //歌词
  private RecyclerView rcv_lyric;
  private MyScrollView myScrollView;
  private View v_bank1,v_bank2;
  private AdapterRecLyric adapterRecLyric;
  private int h;  //scrollview的高度
  private boolean isUserSlide = false;  //是否人为滑动
  private int currentLyric = 0; //  当前歌词项
  private final ArrayList<DataLyric> list = new ArrayList<DataLyric>();
  private MySQLite db;
  private int oldIndex = 0; //上次的焦点歌词序号
  private int newIndex = 0;
  //评论跑马灯
  private String str_comments;
  private HorizontalScrollView rel;
  private int w_rel;
  private int w_comment;
  private TranslateAnimation translateAnimation;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE,R.style.DialogPlay); //这里才生效
    db = new MySQLite(getContext());
  }

  @Nullable
  @Override //每次打开都会执行,也就是重新加载布局文件，但是动态添加的view还在？？
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_play,null);
  }

  @Override
  public void onStart() {
    super.onStart();
    Window window = getDialog().getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = (int) Data.w;
    lp.height = (int) (Data.h+Data.sbH);
    window.setAttributes(lp);
//    Util.tranStatusBar(getActivity());
  }

  @Override
  public void onResume() {
    super.onResume();
    System.out.println(""+"df resume");
    initBinding();
//      getActivity().getSupportFragmentManager().beginTransaction()
//        .add(R.id.fl_spectrum,new FragmentSpectrum(),"fl_spectrum").commitAllowingStateLoss();
//      getActivity().getSupportFragmentManager().beginTransaction()
//        .add(R.id.fl_spectrum_web,new FragmentSpectrumWeb(),"fl_spectrum_web").commitAllowingStateLoss();
    if(Data.dataSong!=null&&list.size()==0){
      System.out.println(""+"获取数据");
      getData(Data.dataSong.id);
      getData_comments(Data.dataSong.id);
    }
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    System.out.println(""+"df destroy");
  }

  @Override
  public void onCancel(@NonNull DialogInterface dialog) {
    super.onCancel(dialog);
    System.out.println(""+"cancel");
  }

  @Subscribe
  public void onProgressChanged(EventPlayProgress event){
    for(int i=0;i<list.size();i++){
      if(event.currentSecond == list.get(i).time_second){
        newIndex = i;
        break;
      }
    }
    if (newIndex != oldIndex){
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          myScrollView.mScrollTo(0, newIndex *100);
        }
      });
      oldIndex = newIndex;
    }
  }


  void initBinding(){
    View view = getView();
    view.findViewById(R.id.ic_collect).setOnClickListener(this);
    rel = view.findViewById(R.id.rel);
    rcv_lyric = view.findViewById(R.id.rcv_lyric);
    myScrollView = view.findViewById(R.id.myScrollView);
    v_bank1 = view.findViewById(R.id.v_bank1);
    v_bank2 = view.findViewById(R.id.v_bank2);
    rcv_lyric.setLayoutManager(new LinearLayoutManager(getContext()));
    adapterRecLyric = new AdapterRecLyric(getContext(),list);
    rcv_lyric.setAdapter(adapterRecLyric);
    initListener();
  }

  void getData(String id){
    String url = Data.baseUrl + "/lyric?id=" + id;
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {System.out.println(e);}
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        JSONObject res = JSONObject.parseObject(response.body().string());
        String str_lyric = res.getJSONObject("lrc").getString("lyric");
        handleLyric(str_lyric);
      }
    };
    Request.get(url,cb);
  }

  void handleLyric(String str_lyric){
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
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          rcv_lyric.getLayoutParams().height = list.size()*100;
          adapterRecLyric.notifyDataSetChanged();
        }
      });
    }
  }

  void initListener(){
    rcv_lyric.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
          case MotionEvent.ACTION_DOWN:
            isUserSlide = true;
            break;
          case MotionEvent.ACTION_UP:
            isUserSlide = false;
            break;
        }
        return false;
      }
    });
    myScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
      @Override
      public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int temp = (int)(((float) scrollY)/100);
        if(Data.currentLyric!=temp){
          Data.currentLyric = temp;
          adapterRecLyric.notifyDataSetChanged();
        }
        if(isUserSlide){

        }
      }
    });
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
            str_comments = str_comments + obj.getJSONObject("user").getString("nickname") +"："
              +obj.getString("content")+"           ";
          }
          str_comments = str_comments.replaceAll("\r|\n","");
        }catch (Exception e){}
        updateView();
      }
    };
    Request.get(url,cb);
  }

  void updateView(){
    w_rel = rel.getWidth();
    TextView tv = new TextView(getContext());
    HorizontalScrollView.LayoutParams lp =
      new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lp.gravity = Gravity.TOP | Gravity.LEFT;
    tv.setLayoutParams(lp);
    tv.setGravity(Gravity.CENTER);
    tv.setTextColor(Color.WHITE);
    tv.setMaxLines(1);
    tv.setText(str_comments);
    tv.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
    w_comment = tv.getMeasuredWidth();
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        rel.removeAllViews();
        rel.addView(tv);
        ani(tv);
      }
    });
  }

  void ani(View v){
    translateAnimation = new TranslateAnimation
      (Animation.ABSOLUTE,w_rel,Animation.ABSOLUTE,-w_comment-w_rel,
        Animation.ABSOLUTE,0,Animation.ABSOLUTE,0);
    int duration = (int) ((w_comment+w_rel/2)*5);
    translateAnimation.setDuration(duration);
    translateAnimation.setInterpolator(new LinearInterpolator());
    translateAnimation.setRepeatCount(ValueAnimator.INFINITE);
    translateAnimation.setRepeatMode(ValueAnimator.RESTART);
    v.startAnimation(translateAnimation);
    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        System.out.println("动画end");
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.ic_collect:
        DialogFragmentCollect dialogFragmentCollect = new DialogFragmentCollect();
        dialogFragmentCollect.show(getParentFragmentManager(),"1");
        break;
    }
  }
}
