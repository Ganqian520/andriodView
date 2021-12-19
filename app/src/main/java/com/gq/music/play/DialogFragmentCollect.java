package com.gq.music.play;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.songsMenu.DataSongs;
import com.gq.music.util.Data;
import com.gq.music.util.SPutil;

import java.util.ArrayList;

public class DialogFragmentCollect extends DialogFragment {

  private RecyclerView recyclerView;
  private AdapterCollect adapterCollect;

  private ArrayList<DataSongs> list = new ArrayList<DataSongs>();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogCollect);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_collect,null);
  }

  @Override
  public void onStart() {
    super.onStart();
    Window window = getDialog().getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = (int) (Data.w*0.9);
    lp.height = (int) (Data.h*0.5);
    lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
    window.setAttributes(lp);

    getData();
    initBinding();
  }

  void initBinding(){
    recyclerView = getView().findViewById(R.id.rcv);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapterCollect = new AdapterCollect(getContext(),list,getActivity());
    recyclerView.setAdapter(adapterCollect);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapterCollect));
    itemTouchHelper.attachToRecyclerView(recyclerView);
  }

  void getData(){
    SPutil sp  = new SPutil(getContext());
    String str = sp.getString("songsList");
    list.clear();
    JSONArray arr = JSONArray.parseArray(str);
    for(int i=0;i<arr.size();i++){
      DataSongs dataSongs = JSONObject.toJavaObject((JSON) arr.get(i),DataSongs.class);
      list.add(dataSongs);
    }
  }



}
