package com.gq.pianoWindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CanvasPianoWindow extends View {

  private Paint paint;
  private Canvas canvas;
  private ScaleGestureDetector scaleDetector;
  private float startX_table,startY_table;
  private float oldTouchX,oldTouchY;   //
  private float factorX = 1,factorY = 1;  //缩放因子
  private float oldFactorX = 1,oldFactorY = 1;
  private float focusX,focusY; //缩放中心点
  private float h_music8 = 80,w_music8 = 60; //初始最小单位格子的宽高
  private float a_square = 100;  //左上角正方形的边长
  private float offsetX_origin = 0,offsetY_origin;  //画布原点偏移量,有正负
  private int music8_count = 8*50;  //初始八分音符(最小格子)的一排的个数

  private ArrayList<DataMusic> list = new ArrayList<DataMusic>();
  private float previousTime; //时间间隔短才触发up事件
  private boolean isHave = false; //该点击点是否已经有音乐块
  private boolean isMusicMenu = false;  //音乐块菜单是否已经画了
  private boolean isLenMove = false; //菜单是否移动中
  private boolean isPositionMove = false;
  private int indexMusic;  //当前焦点音乐序号
  private float r;    //菜单项
  private float del_x=0;
  private float del_y=0;
  private float scale_x=0;
  private float scale_y=0;
  private float move_x=0;
  private float move_y=0;
  private DataMusic dataMusic = new DataMusic(0,0,0); //接收变化前的音乐块

  private int diaoHao = 1; //大小调标志1~24


  public CanvasPianoWindow(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    GD.mySQLite = new MySQLite(getContext());
    list = GD.song; //浅拷贝
    Map res = GD.mySQLite.getPiano("",2);
    if (res.get("song")!=null&&!res.get("song").equals("")){
      JSONArray array = JSONArray.parseArray((String) res.get("song"));
      for(int i=0;i<array.size();i++){
        DataMusic dataMusic = JSONObject.toJavaObject(array.getJSONObject(i),DataMusic.class);
        GD.song.add(dataMusic);
      }
    }
    paint = new Paint();
    paint.setAntiAlias(true);
    offsetY_origin = -88*h_music8/2;
    initScaleGestureDetector();
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void onMessage(GD gd){
//    list = GD.song;
    diaoHao = GD.dioHao;
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    this.canvas = canvas;
    reInitAndCheck();
    drawMiddleBackGroud();
    drawMiddleMusic();
    drawMiddleMusicMenu();
    drawTopGraduation();
    drawLeftPiano();
    drawLeftTop();
  }
  //左上方块
  void drawLeftTop(){
    canvas.save();
    canvas.translate(-offsetX_origin,-offsetY_origin);
    Paint paintBg = new Paint();
    paintBg.setColor(0xff596267);
    canvas.drawRect(0,0,a_square,a_square,paintBg);
    drawTextCenter(a_square/2,a_square/2,"菜单",30,Color.WHITE);
    canvas.restore();
  }
  //左边 钢琴条
  void drawLeftPiano(){
    canvas.save();
    canvas.translate(-offsetX_origin,a_square);  //钢琴条只会上下移动，所以回复原点的x坐标
    //背景
    Paint paintWhite = new Paint();
    paintWhite.setColor(Color.WHITE);
    Paint paintBlack = new Paint();
    paintBlack.setColor(0xff2F4F4F);
    Paint paintDot = new Paint();
    paintDot.setTextSize(50);
    for(int i=0;i<88;i++){
      String strNum = (String) tf1(88-1-i).get("number");
      String strDot = "";
      int dots = (int) tf1(88-1-i).get("dots");
      if(strNum.charAt(0)=='#'){
        canvas.drawRect(0,i*h_music8,a_square,(i+1)*h_music8,paintBlack);
        drawTextCenter(a_square/2,i*h_music8+h_music8/2, strNum,30,Color.WHITE);
        if(dots>0){
          for(int j=0;j<dots;j++){
            strDot += "·";
          }
          drawTextCenter(a_square/2,i*h_music8+h_music8/2-20, strDot,60,Color.WHITE);
        }else if(dots<0) {
          for(int j=0;j<-dots;j++){
            strDot += "·";
          }
          drawTextCenter(a_square/2,i*h_music8+h_music8/2+20, strDot,60,Color.WHITE);
        }
      }else{
        canvas.drawRect(0,i*h_music8,a_square,(i+1)*h_music8,paintWhite);
        drawTextCenter(a_square/2,i*h_music8+h_music8/2, strNum,30,Color.BLACK);
        if(dots>0){
          for(int j=0;j<dots;j++){
            strDot += "·";
          }
          drawTextCenter(a_square/2,i*h_music8+h_music8/2-20, strDot,60,Color.BLACK);
        }else if(dots<0) {
          for(int j=0;j<-dots;j++){
            strDot += "·";
          }
          drawTextCenter(a_square/2,i*h_music8+h_music8/2+20, strDot,60,Color.BLACK);
        }
      }
      //线
      canvas.drawLine(0,i*h_music8,a_square,i*h_music8,paint);
    }
    canvas.restore();
  }
  //上边 时间轴
  void drawTopGraduation(){
    canvas.save();
    canvas.translate(a_square,-offsetY_origin);
    //背景
    Paint paint1 = new Paint();
    paint1.setColor(0xff708090);
    paint1.setStyle(Paint.Style.FILL);
    canvas.drawRect(0,0,music8_count*w_music8,a_square,paint1);
    //背景线

    //文字
    for(int i=0;i<music8_count/8;i++){
      drawTextCenter(i*w_music8*8+w_music8/2,a_square/2,i+1+"",30,Color.WHITE);
    }
    canvas.restore();
  }
  //中间 音乐块
  void drawMiddleMusic(){
    canvas.save();
    canvas.translate(a_square,a_square);
    Paint paintNormal = new Paint();
    paintNormal.setColor(0xff00ffff);
    Paint paintAlive = new Paint();
    paintAlive.setColor(0xff4682B4);
    for(int i=0;i<list.size();i++){
      DataMusic dataMusic = list.get(i);
      canvas.drawRect(dataMusic.indexX*w_music8,dataMusic.indexY*h_music8
        ,(dataMusic.indexX+dataMusic.length)*w_music8,(dataMusic.indexY+1)*h_music8,paintNormal);
    }
    if(isMusicMenu){
      DataMusic dataMusic = list.get(indexMusic);
      canvas.drawRect(dataMusic.indexX*w_music8,dataMusic.indexY*h_music8
        ,(dataMusic.indexX+dataMusic.length)*w_music8,(dataMusic.indexY+1)*h_music8,paintAlive);
    }
    canvas.restore();
  }
  //中间 音乐块菜单
  void drawMiddleMusicMenu(){
    canvas.save();
    canvas.translate(a_square,a_square);
    if(isMusicMenu){
      DataMusic dataMusic = list.get(indexMusic);
      float indexX = dataMusic.indexX;
      float indexY = dataMusic.indexY;
      float length = dataMusic.length;
      Paint paintMenu = new Paint();
      paintMenu.setColor(0xff4682B4);
      r = 50; //菜单圆球的半径
      float distance = 20;  //圆球离音乐块的间距
      del_x = (indexX+length)*w_music8-r;
      del_y = indexY*h_music8-r-distance;
      scale_x = (indexX+length)*w_music8+r+distance;
      scale_y = indexY*h_music8+h_music8/2;
      move_x = (indexX+length)*w_music8-r;
      move_y = (indexY+1)*h_music8+r+distance;
      canvas.drawCircle(del_x,del_y,r,paintMenu);
      canvas.drawCircle(scale_x,scale_y,r,paintMenu);
      canvas.drawCircle(move_x,move_y,r,paintMenu);
      drawTextCenter(del_x,del_y,"删",30,Color.WHITE);
      drawTextCenter(scale_x,scale_y,"长",30,Color.WHITE);
      drawTextCenter(move_x,move_y,"位",30,Color.WHITE);
    }
    canvas.restore();
  }
  //中间 背景
  void drawMiddleBackGroud(){
    canvas.save();
    canvas.translate(a_square,a_square);
    //横
    Paint paintWhite = new Paint();
    Paint paintBlack = new Paint();
    paintWhite.setColor(Color.rgb(52,68,78));
    paintBlack.setColor(Color.rgb(46,62,72));
    for(int i=0;i<88;i++){
      String str = (String) tf1(88-1-i).get("number");
      if(str.charAt(0)=='#'){
        canvas.drawRect(0,i*h_music8,music8_count*w_music8,(i+1)*h_music8,paintBlack);
      }else{
        canvas.drawRect(0,i*h_music8,music8_count*w_music8,(i+1)*h_music8,paintWhite);
      }
      canvas.drawLine(0,i*h_music8,music8_count*w_music8,i*h_music8,paint);
    }
    //竖
    for(int i=0;i<music8_count;i++){
      canvas.drawLine(i*w_music8,0,i*w_music8,88*h_music8,paint);
    }
    canvas.restore();
  }
  //初始原点位置,并检测是否越界
  void reInitAndCheck(){
    offsetY_origin = offsetY_origin>0 ? 0 : offsetY_origin;
    offsetY_origin = offsetY_origin<-(a_square+88*h_music8-canvas.getHeight()) ? -(a_square+88*h_music8-canvas.getHeight()) : offsetY_origin;
    offsetX_origin = offsetX_origin>0 ? 0 : offsetX_origin;
    if(w_music8*music8_count+offsetX_origin<canvas.getWidth()){
      music8_count += 10;
    }
    canvas.translate(offsetX_origin,offsetY_origin);
    w_music8 = w_music8<30 ? 30 : w_music8;
    w_music8 = w_music8>150 ? 150 : w_music8;
    h_music8 = h_music8<30 ? 30 : h_music8;
    h_music8 = h_music8>150 ? 150 : h_music8;
  }
  //处理手指点击
  void handleClick(float x,float y){
    if(x<a_square && y<a_square){
      DialogMenu dialogMenu = new DialogMenu(getContext());
      dialogMenu.show();
      return;
    }
    float x_table = x-offsetX_origin-a_square; //相对于表格原点的坐标
    float y_table = y-offsetY_origin-a_square;
    int indexX = (int) (x_table/w_music8);
    int indexY = (int) (y_table/h_music8);
    if(!isMusicMenu){ //如果当前没展开菜单
      isHave = false;
      for(int i=0;i<list.size();i++){
        DataMusic dataMusic = list.get(i);
        if(indexY==dataMusic.indexY && indexX>=dataMusic.indexX && indexX<dataMusic.indexX+dataMusic.length){
          isHave = true;
          indexMusic = i;
          this.dataMusic = dataMusic.clone();
          isMusicMenu = true; //打开菜单
          break;
        }
      }
      if(!isHave){
        list.add(new DataMusic(indexX,indexY,1));
      }
    }else { //如果菜单展开了
      if(isIn(x_table,y_table,del_x,del_y,2*r)){
        list.remove(indexMusic);
      }
      isMusicMenu = false;//关闭菜单
    }
  }
  //处理手指按下
  void handleDown(float x,float y){
    oldTouchX = x;
    oldTouchY = y;
    startX_table = x-offsetX_origin-a_square;
    startY_table = y-offsetY_origin-a_square;
    previousTime = System.currentTimeMillis()%100000;
    if(isMusicMenu && isIn(startX_table,startY_table,scale_x,scale_y,2*r)){
      isLenMove = true;
    }
    if(isMusicMenu && isIn(startX_table,startY_table,move_x,move_y,2*r)){
      isPositionMove = true;
    }
  }
  //处理手指移动
  void handleMove(float currentX,float currentY){
    float currentX_table = currentX-offsetX_origin-a_square; //相对表格原点
    float currentY_table = currentY-offsetY_origin-a_square;
    if(isLenMove){
      list.get(indexMusic).length = dataMusic.length + (int) ((currentX_table-startX_table)/w_music8);
      if (list.get(indexMusic).length<1){
        list.get(indexMusic).length = 1;
      }
    }else if(isPositionMove){
      list.get(indexMusic).indexX = dataMusic.indexX + (int) ((currentX_table-startX_table)/w_music8);
      list.get(indexMusic).indexY = dataMusic.indexY + (int) ((currentY_table-startY_table)/h_music8);
    }else {
      offsetX_origin += currentX-oldTouchX;
      offsetY_origin += currentY-oldTouchY;
    }
    oldTouchX = currentX;
    oldTouchY = currentY;
  }
  //处理手指抬起
  void handleUp(float x,float y){
    float delta = System.currentTimeMillis()%100000-previousTime;
    if(delta>0 && delta<100){
      handleClick(x,y);
    }
    if(isPositionMove || isLenMove){
      dataMusic = list.get(indexMusic).clone();
      isLenMove = false;
      isPositionMove = false;
    }
  }
  //移动手势监听
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if(event.getPointerCount()==1){
      switch (event.getAction()){
        case MotionEvent.ACTION_DOWN:
          handleDown(event.getX(),event.getY());
          break;
        case MotionEvent.ACTION_MOVE:
          handleMove(event.getX(),event.getY());
          break;
        case MotionEvent.ACTION_UP:
          handleUp(event.getX(),event.getY());
          break;
      }
      invalidate();
      return true;
    }else if(event.getPointerCount()==2){
      return scaleDetector.onTouchEvent(event);
    }
    return true;
  }
  //缩放手势监听
  void initScaleGestureDetector(){
    scaleDetector = new ScaleGestureDetector(getContext(),
      new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @SuppressLint("DefaultLocale")
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
          oldFactorX = factorX;
          factorX = detector.getCurrentSpanX()/detector.getPreviousSpanX();
          focusX = detector.getFocusX();
          w_music8 = w_music8*(1+factorX-oldFactorX);
          if(w_music8>30 && w_music8<150){
            offsetX_origin = focusX - (focusX - offsetX_origin)*(1+factorX-oldFactorX);
          }
          oldFactorY = factorY;
          factorY = detector.getCurrentSpanY()/detector.getPreviousSpanY();
          focusY = detector.getFocusY();
          h_music8 = h_music8*(1+factorY-oldFactorY);
          if(h_music8>30 && h_music8<150){
            offsetY_origin = focusY - (focusY - offsetY_origin)*(1+factorY-oldFactorY);
          }
          invalidate();
          return super.onScale(detector);
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
          factorX = 1;
          factorY = 1;
          return super.onScaleBegin(detector);
        }
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
          super.onScaleEnd(detector);
        }
      }
    );
  }
  //根据中心点绘制文字
  void drawTextCenter ( float centerX, float centerY, String text,int textSize,int color) {
    Paint paint = new Paint();
    paint.setColor(color);
    paint.setTextSize(textSize);
    float textWidth = paint.measureText(text);
    Paint.FontMetrics fontMetrics = paint.getFontMetrics();
    float baselineY = centerY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
    canvas.drawText(text, centerX - textWidth / 2, baselineY, paint);
  }
  //根据边长，矩形中心，判断是否入界
  boolean isIn(float x,float y,float centerX,float centerY,float a){
    if(x>centerX-a/2 && x<centerX+a/2 && y>centerY-a/2 && y<centerY+a/2){
      return true;
    }else {
      return false;
    }
  }
  //根据0~77返回对应简谱音符,数字，上下点
  HashMap tf1(int index){
    HashMap map = new HashMap();
    index += 10;
    index -= diaoHao%12-1;    //这里根据调号偏移
    int remainder = index%12;
    int quotient = (int) (((float)index)/12);
    if(remainder==0){
      remainder = 12;
      quotient -= 1;
    }
    if(diaoHao>12){
      remainder += 12;
    }
    map.put("number",tf2(remainder));
    map.put("dots",-4+quotient);
    return map;
  }
  //根据1-24返回数字字符串
  String tf2(int e){
    switch(e){
      case 1: return "1";
      case 2: return "#1";
      case 3: return "2";
      case 4: return "#2";
      case 5: return "3";
      case 6: return "4";
      case 7: return "#4";
      case 8: return "5";
      case 9: return "#5";
      case 10: return "6";
      case 11: return "#6";
      case 12: return "7";
      case 1+12: return "1";
      case 2+12: return "#1";
      case 3+12: return "2";
      case 4+12: return "3";
      case 5+12: return "#3";
      case 6+12: return "4";
      case 7+12: return "#4";
      case 8+12: return "5";
      case 9+12: return "6";
      case 10+12: return "#6";
      case 11+12: return "7";
      case 12+12: return "#7";
      default: return "";
    }
  }
}
