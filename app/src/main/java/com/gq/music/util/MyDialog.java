package com.gq.music.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyDialog extends Dialog {
  private Button bt_cancel;
  private Button bt_confirm;
  private EditText et_url;
  private EditText et_name;
  private RadioGroup id_rg;
  private int tag = 1;
  private String shareUrl = "";
  private String name = "未命名";

  private Context myContext;
  private Activity activity;

  public MyDialog(@NonNull Context context) {
    super(context, R.style.myDialog);
    setContentView(R.layout.layout_dialog);
    myContext = context;
    this.activity = activity;
    this.activity = (Activity) context;
  }

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("dialog start");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ClipboardManager clipboard = (ClipboardManager) myContext.getSystemService(Context.CLIPBOARD_SERVICE);
    try{
      shareUrl = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
    }catch(Exception e){System.out.println("异常：剪切板为空"+e);}
    initBinding();
  }

  @Override
  public void show() {
    super.show();
  }

  @Override
  public void cancel() {
    super.cancel();
  }

  void initBinding(){
    bt_cancel = findViewById(R.id.bt_cancel);
    bt_confirm = findViewById(R.id.bt_confirm);
    et_name = findViewById(R.id.et_name);
    et_url = findViewById(R.id.et_url);
    et_url.setText(shareUrl);
    id_rg = findViewById(R.id.id_rg);
    id_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
          case R.id.id_rb_1:
            tag = 1;
            break;
          case R.id.id_rb_2:
            tag = 2;
            break;
        }
      }
    });
    bt_cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cancel();
      }
    });
    bt_confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Map map = new HashMap();
        map.put("action","addDouYin");
        map.put("share",shareUrl);
        map.put("name",name);
        map.put("tag",tag);
        findViewById(R.id.pb_wait).setVisibility(View.VISIBLE);
        bt_confirm.setVisibility(View.GONE);
        Callback cb = new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {

          }
          @Override
          public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            System.out.println(str);
            activity.runOnUiThread(() -> {
              try {
                JSONObject obj = JSON.parseObject(str);
                if (obj.getString("message").equals("添加成功")){
                  System.out.println("toast");
                  Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
                }
              }catch (Exception e){
                Toast.makeText(getContext(),"添加失败："+str+e,Toast.LENGTH_SHORT).show();
              }
              findViewById(R.id.pb_wait).setVisibility(View.GONE);
              bt_confirm.setVisibility(View.VISIBLE);
            });
          }
        };
        Request.myPost(map, cb);
      }
    });
    et_url.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        shareUrl = s.toString();
      }
      @Override
      public void afterTextChanged(Editable s) {}
    });
    et_name.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        name = s.toString();
      }
      @Override
      public void afterTextChanged(Editable s) {}
    });
  }
}
