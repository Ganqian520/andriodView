package com.gq.pianoWindow;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.gq.music.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogMenu extends Dialog {

  private Context context;
  private TextView id_diaoHao;
  private SeekBar id_seekBar;
  private TextView id_name;
  private EditText id_editText;
  private RecyclerView id_rcv;
  private AdapterRcv adapterRcv;
  private HashMap<String,String> mapSong = new HashMap<String, String>();
  private ArrayList<Map> list =  new ArrayList<Map>();
  private String[] list_diaoHao = {"C大调","#C大调","D大调","#D大调","E大调","F大调","#F大调","G大调","#G大调","A大调","#A大调","B大调"
              ,"c小调" ,"#c小调" ,"d小调" ,"e小调" ,"#e小调" ,"f小调" ,"#f小调" ,"g小调" ,"a小调" ,"#a小调" ,"b小调" ,"#b小调"};

  public DialogMenu(@NonNull Context context) {
    super(context,R.style.DialogPianoWindow);
    View view = LayoutInflater.from(getContext()).inflate(R.layout.other_piano_window_dialog,null);
    super.setContentView(view);
    this.context = context;
    getData();
    initBinding();
  }

  @Subscribe
  public void onNameChange(HashMap map){
    id_editText.post(new Runnable() {
      @Override
      public void run() {
        id_editText.setText((String)map.get("name"));
      }
    });
  }

  void initBinding(){
    findViewById(R.id.id_save).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String str = JSON.toJSONString(GD.song);
        GD.mySQLite.updatePiano(mapSong.get("time"),str,mapSong.get("name"));
        adapterRcv.notifyDataSetChanged();
        Toast.makeText(context,"保存成功",Toast.LENGTH_LONG).show();
      }
    });
    findViewById(R.id.id_new).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        GD.song.clear();
        String str = String.valueOf(System.currentTimeMillis());
        HashMap map = new HashMap();
        map.put("name",mapSong.get("name"));
        map.put("time",str);
        list.add(map);
        GD.mySQLite.addPiano(mapSong.get("name"),"", str);
        adapterRcv.notifyDataSetChanged();
        Toast.makeText(context,"新建成功",Toast.LENGTH_LONG).show();
      }
    });
    id_rcv = findViewById(R.id.id_rcv);
    adapterRcv = new AdapterRcv(context,list);
    id_rcv.setLayoutManager(new LinearLayoutManager(context));
    id_rcv.setAdapter(adapterRcv);
    id_name = findViewById(R.id.id_name);
    id_editText = findViewById(R.id.id_editText);
    id_editText.setText(mapSong.get("name"));
    id_editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        mapSong.replace("name",s.toString());
      }
      @Override
      public void afterTextChanged(Editable s) {}
    });
    id_diaoHao = findViewById(R.id.id_diaHao);
    id_diaoHao.setText(list_diaoHao[GD.dioHao-1]);
    id_seekBar = findViewById(R.id.id_seekBar);
    id_seekBar.setProgress(GD.dioHao-1);
    id_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        id_diaoHao.setText(list_diaoHao[progress]);
        GD.dioHao = progress + 1;
      }
      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {}
      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {}
    });
  }

  void getData(){
    list.clear();
    Map res = GD.mySQLite.getPiano("",1);
    list = (ArrayList<Map>) res.get("songs");
    try{
      HashMap map = (HashMap) list.get(0);
      mapSong = (HashMap<String, String>) map.clone();
    }catch(Exception e){System.out.println("异常："+e);}
  }

  @Override
  public void show() {
    super.show();
    EventBus.getDefault().register(this);
    Window dialogWindow = this.getWindow();
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
    int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;
    lp.width = (int) (width*0.5);
    lp.height = (int) (height*1);
    lp.gravity = Gravity.LEFT;
    dialogWindow.setAttributes(lp);
  }

  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);
    EventBus.getDefault().post(new GD()); //发送关闭通知
    super.onStop();
  }
}
