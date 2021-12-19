package com.gq.music.other.GameAssist;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gq.music.R;
import com.gq.music.util.SPutil;


public class ServiceFloating extends Service {

  private SPutil spUtil;

  private WindowManager windowManager;
  private WindowManager.LayoutParams layoutParamsMenu;
  private WindowManager.LayoutParams layoutParamsOval;
  private WindowManager.LayoutParams layoutParamsText;
  private LayoutInflater inflater;
  private View layout_floating_menu;
  private View layout_floating_oval;
  private TextView tv_show;
  private CanvasAssist canvas_ssist;

  private TextView tv_isSync;
  private TextView tv_numH;
  private TextView tv_numW;
  private TextView tv_numY;
  private TextView tv_numW2;
  private TextView tv_name;

  private float h = GD.w;
  private float w = GD.h+GD.sbH;

  @Override
  public void onCreate() {
    super.onCreate();
    spUtil = new SPutil(this);
    inflater = LayoutInflater.from(getApplicationContext());
    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    initFloatMenu();
    initFloatOval();
    initFloatText();
  }
  //点击保存时给activity发广播通知
  public void send(){
    Intent intent = new Intent();
    intent.setAction("location.reports");
    sendBroadcast(intent);
  }
  @SuppressLint("ClickableViewAccessibility")
  void initFloatMenu(){
    layout_floating_menu = inflater.inflate(R.layout.layout_floating_menu, null);
    SeekBar.OnSeekBarChangeListener listener = new SeekListener();
    View.OnTouchListener listenerTouch = new TouchListener();
    View.OnClickListener listenerClick = new ClickListener();
    SeekBar sb_height = layout_floating_menu.findViewById(R.id.sb_height);
    SeekBar sb_width = layout_floating_menu.findViewById(R.id.sb_width);
    SeekBar sb_y = layout_floating_menu.findViewById(R.id.sb_y);
    SeekBar sb_secondOval = layout_floating_menu.findViewById(R.id.sb_secondOval);
    TextView tv_move = layout_floating_menu.findViewById(R.id.tv_move);
    TextView tv_ok = layout_floating_menu.findViewById(R.id.tv_ok);
    TextView tv_close = layout_floating_menu.findViewById(R.id.tv_close);
    tv_name = layout_floating_menu.findViewById(R.id.tv_name);
    tv_isSync = layout_floating_menu.findViewById(R.id.tv_isSync);
    tv_numH = layout_floating_menu.findViewById(R.id.tv_numH);
    tv_numW = layout_floating_menu.findViewById(R.id.tv_numW);
    tv_numY = layout_floating_menu.findViewById(R.id.tv_numY);
    tv_numW2 = layout_floating_menu.findViewById(R.id.tv_numW2);

    sb_height.setOnSeekBarChangeListener(listener);
    sb_height.setProgress(GD.assist.h);
    sb_width.setOnSeekBarChangeListener(listener);
    sb_width.setProgress(GD.assist.w);
    sb_y.setOnSeekBarChangeListener(listener);
    sb_y.setProgress(GD.assist.y);
    sb_secondOval.setOnSeekBarChangeListener(listener);
    sb_secondOval.setProgress(GD.assist.w2);
    tv_move.setOnTouchListener(listenerTouch);
    tv_ok.setOnClickListener(listenerClick);
    tv_close.setOnClickListener(listenerClick);
    tv_isSync.setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.h_add).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.h_cut).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.w_add).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.w_cut).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.y_add).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.y_cut).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.w2_add).setOnClickListener(listenerClick);
    layout_floating_menu.findViewById(R.id.w2_cut).setOnClickListener(listenerClick);

    layoutParamsMenu = new WindowManager.LayoutParams();
    // 设置图片格式，效果为背景透明
    layoutParamsMenu.format =PixelFormat.TRANSPARENT;
    //26版本以上不用这个会报错
    layoutParamsMenu.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    //  默认为none，只有悬浮浮窗能点击
    layoutParamsMenu.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    //默认为全屏 layout宽高设置无效
    layoutParamsMenu.width = (int) (GD.h*0.8);
    layoutParamsMenu.height = (int) (GD.w/2);
    //定位方式
    layoutParamsMenu.gravity = Gravity.TOP | Gravity.LEFT ;
    //位置,设置了定位才生效否则居中
    layoutParamsMenu.x = 0;
    layoutParamsMenu.y = 0;
  }

  /*
  * 布局起点x会让出信号栏位置，艹,
  * 键盘无法弹出，小返回键失效
  * */
  void initFloatOval(){
    layout_floating_oval = inflater.inflate(R.layout.layout_floating_oval, null);
    canvas_ssist = layout_floating_oval.findViewById(R.id.canvas_assist);
    layoutParamsOval = new WindowManager.LayoutParams();
    layoutParamsOval.gravity = Gravity.LEFT;
    layoutParamsOval.x = 0; //
    layoutParamsOval.width = (int) ((int) GD.h+GD.sbH);
    layoutParamsOval.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE //不许点击，就能穿透下层view
      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;  // 不限制布局宽高
    layoutParamsOval.format = PixelFormat.TRANSPARENT;
    layoutParamsOval.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
  }

  void initFloatText(){
    tv_show = new TextView(this);
    tv_show.setGravity(Gravity.CENTER);
    tv_show.setTextColor(Color.YELLOW);
    tv_show.setTextSize(20);
    tv_show.setTypeface(Typeface.DEFAULT_BOLD);
    layoutParamsText = new WindowManager.LayoutParams();
    layoutParamsText.height = 100;
    layoutParamsText.width = (int) GD.h;
    layoutParamsText.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; //不许点击，就能穿透下层view
    layoutParamsText.format = PixelFormat.TRANSPARENT;
    layoutParamsText.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    layoutParamsText.gravity = Gravity.TOP | Gravity.LEFT ;
    layoutParamsText.y = 0;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return new Control();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }


  class Control extends Binder {
    public void openMenu(){
      windowManager.addView(layout_floating_menu, layoutParamsMenu);
    }
    public void openOval(){
      windowManager.addView(layout_floating_oval,layoutParamsOval);
      windowManager.addView(tv_show,layoutParamsText);
    }
    public void removeMenu(){
      windowManager.removeView(layout_floating_menu);
    }
    public void update(){
      tv_numH.setText(GD.assist.h+"");
      tv_numW.setText(GD.assist.w+"");
      tv_numW2.setText(GD.assist.w2+"");
      tv_numY.setText(GD.assist.y+"");
      tv_name.setText(GD.assist.name);
      tv_show.setText(GD.textShow);
      canvas_ssist.redraw();
    }
  }

  class ClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()){
        case R.id.tv_ok:
          send();
          windowManager.removeView(layout_floating_menu);
          GD.isMenu = false;
          break;
        case R.id.tv_isSync:
          GD.isAsync = ! GD.isAsync;
          String str = GD.isAsync ? "高宽同步:是" : "高宽同步:否";
          tv_isSync.setText(str);
          break;
        case R.id.tv_close:
          windowManager.removeView(layout_floating_menu);
          GD.isMenu = false;
          break;
        case R.id.h_add:
          GD.assist.h++;
          tv_numH.setText(GD.assist.h+"");
          break;
        case R.id.h_cut:
          GD.assist.h--;
          tv_numH.setText(GD.assist.h+"");
          break;
        case R.id.w_add:
          GD.assist.w++;
          tv_numW.setText(GD.assist.w+"");
          break;
        case R.id.w_cut:
          GD.assist.w--;
          tv_numW.setText(GD.assist.w+"");
          break;
        case R.id.y_add:
          GD.assist.y++;
          tv_numY.setText(GD.assist.y+"");
          break;
        case R.id.y_cut:
          GD.assist.y--;
          tv_numY.setText(GD.assist.y+"");
          break;
        case R.id.w2_add:
          GD.assist.w2++;
          tv_numW2.setText(GD.assist.w2+"");
          break;
        case R.id.w2_cut:
          GD.assist.w2--;
          tv_numW2.setText(GD.assist.w2+"");
          break;
      }
      if(canvas_ssist!=null){
        canvas_ssist.redraw();
      }
    }
  }

  class SeekListener implements SeekBar.OnSeekBarChangeListener {
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      switch (seekBar.getId()){
        case R.id.sb_height:
          GD.assist.h = progress;
          tv_numH.setText(GD.assist.h+"");
          if(GD.isAsync){
            GD.assist.w = (int) (GD.assist.h/GD.ratioHW);
            tv_numW.setText(GD.assist.w+"");
          }
          break;
        case R.id.sb_width:
          GD.assist.w = progress;
          tv_numW.setText(GD.assist.w+"");
          if(GD.isAsync){
            GD.assist.h = (int) (GD.assist.w*GD.ratioHW);
            tv_numH.setText(GD.assist.h+"");
          }
          break;
        case R.id.sb_y:
          GD.assist.y = progress;
          tv_numY.setText(GD.assist.y+"");
          break;
        case R.id.sb_secondOval:
          GD.assist.w2 = progress;
          tv_numW2.setText(GD.assist.w2+"");
          break;
      }
      if(canvas_ssist!=null){
        canvas_ssist.redraw();
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
  }

  class TouchListener implements View.OnTouchListener {
    private int x , y;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()){
        case MotionEvent.ACTION_DOWN:
          x = (int) event.getRawX();
          y = (int) event.getRawY();
          break;
        case MotionEvent.ACTION_MOVE:
          int nowX = (int) event.getRawX();
          int nowY = (int) event.getRawY();
          int movedX = nowX - x;
          int movedY = nowY - y;
          x = nowX;
          y = nowY;
          layoutParamsMenu.x = layoutParamsMenu.x + movedX;
          layoutParamsMenu.y = layoutParamsMenu.y + movedY;
          windowManager.updateViewLayout(layout_floating_menu, layoutParamsMenu);
          break;
        default:
          break;
      }
      return true;
    }
  }

}
