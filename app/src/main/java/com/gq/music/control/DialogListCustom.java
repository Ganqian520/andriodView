package com.gq.music.control;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gq.music.R;
import com.gq.music.util.Data;
import com.gq.music.util.Icon;

public class DialogListCustom extends Dialog {

  private RecyclerView rcv_want;
  private Adpter adpter;
  private Context context;

  public DialogListCustom(@NonNull Context context) {
    super(context, R.style.DialogFromTop);
    this.context = context;
    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_list_want,null);
    super.setContentView(view);
    initBinding();
  }

  void initBinding(){
    rcv_want = findViewById(R.id.rcv_want);
    rcv_want.setLayoutManager(new LinearLayoutManager(getContext()));
    adpter = new Adpter();
    rcv_want.setAdapter(adpter);
  }

  @Override
  public void show() {
    super.show();
    Window dialogWindow = this.getWindow();
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
    lp.width = (int) (displayMetrics.widthPixels*0.9);
    lp.height = (int) (displayMetrics.heightPixels*0.5);
    lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
    dialogWindow.setAttributes(lp);
  }

  class Adpter extends RecyclerView.Adapter<Adpter.Holder>{

    @NonNull
    @Override
    public Adpter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.adapter_want,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Adpter.Holder holder, int position) {
      holder.tv_index.setText((Data.list_custom.size()-position)+"");
      holder.tv_name.setText(Data.list_custom.get(position).name);
      holder.tv_author.setText(Data.list_custom.get(position).author);
      holder.ic_move.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Data.list_custom.remove(position);
          adpter.notifyItemRemoved(position);
          adpter.notifyItemRangeChanged(position,Data.list_custom.size()); //刷新后面的序号
        }
      });
    }

    @Override
    public int getItemCount() {
      return Data.list_custom.size();
    }

    class Holder extends RecyclerView.ViewHolder{
      private TextView tv_index;
      private TextView tv_name;
      private TextView tv_author;
      private Icon ic_move;
      public Holder(@NonNull View itemView) {
        super(itemView);
        tv_index = itemView.findViewById(R.id.tv_index);
        tv_name = itemView.findViewById(R.id.tv_name);
        ic_move = itemView.findViewById(R.id.ic_move);
        tv_author = itemView.findViewById(R.id.tv_author);
      }
    }
  }

}
