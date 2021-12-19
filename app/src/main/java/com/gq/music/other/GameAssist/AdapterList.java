package com.gq.music.other.GameAssist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gq.music.R;
import com.gq.music.util.Icon;


import java.util.ArrayList;

public class AdapterList extends BaseAdapter {
  private ServiceFloating.Control control;
  private boolean isOval = false;
  private Context context;
  private Activity activity;
  private ArrayList<Assist> list;
  private MyInterface myInterface;

  public AdapterList(Context context, Activity activity, ArrayList<Assist> list, ServiceFloating.Control control, MyInterface myInterface){
    this.context = context;
    this.list = list;
    this.control = control;
    this.myInterface = myInterface;
    this.activity = activity;
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override //每一项的赋值
  public View getView(int position, View convertView, ViewGroup parent) {
    MyHolder holder = null;
    if(convertView==null){
      holder = new MyHolder();
      convertView = View.inflate(context, R.layout.adapter_assist_hero,null);
      holder.tv_name = convertView.findViewById(R.id.tv_name);
      holder.tv_openAll = convertView.findViewById(R.id.tv_openAll);
      holder.tv_openOval = convertView.findViewById(R.id.tv_openOval);
      holder.ic_edit = convertView.findViewById(R.id.ic_edit);
      convertView.setTag(holder);
    }else{
      holder = (MyHolder) convertView.getTag();
    }
    Assist assist = (Assist) list.get(position);
    holder.tv_name.setText(assist.name);
    ClickListener clickListener = new ClickListener(position);
    holder.tv_openAll.setOnClickListener(clickListener);
    holder.tv_openOval.setOnClickListener(clickListener);
    holder.ic_edit.setOnClickListener(clickListener);
    return convertView;
  }

  static class MyHolder {
    TextView tv_name;
    TextView tv_openAll;
    TextView tv_openOval;
    Icon ic_edit;
  }

  public interface MyInterface{
    void callback(int position);
  }

  class ClickListener implements View.OnClickListener {
    private int position;

    public ClickListener(int position){
      this.position = position;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
      GD.assist = list.get(position);
      myInterface.callback(position);//告诉activity当前position
      switch (v.getId()) {
        case R.id.tv_openAll:
          if (!GD.isMenu) {
            control.openMenu();
            GD.isMenu = true;
          }
          if (!isOval) {
            control.openOval();
            isOval = true;
          }
          break;
        case R.id.tv_openOval:
          if(!isOval){
            control.openOval();
            isOval = true;
          }
          break;
        case R.id.ic_edit:
          InputDialog dialog = new InputDialog(activity,1);
          dialog.setCancelable(false);
          dialog.show();
          break;
      }
      control.update();
    }
  }
}
