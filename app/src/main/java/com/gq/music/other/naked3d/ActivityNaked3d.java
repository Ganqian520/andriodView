package com.gq.music.other.naked3d;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gq.music.R;
import com.gq.music.util.Data;

public class ActivityNaked3d extends AppCompatActivity implements SensorEventListener {

  private SensorManager sensorManager;
  private Sensor magneticSensor;  //磁场
  private Sensor accelerometerSensor; //重力加速度
  private float[] gravity = new float[3];//保存加速度传感器的值
  private float[] r = new float[9];
  private float[] magnetic = new float[3];
  private float[] values = new float[3];//最终结果
  private int z,x,y;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.other_activity_naked3d);
    getScreen();
    //后去传感器管理实例
    sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    //获取传感器
    magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    //注册
    sensorManager.registerListener(this,magneticSensor,SensorManager.SENSOR_DELAY_FASTEST);
    sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_FASTEST);

  }
  //监听传感器数据
  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
      magnetic = event.values;
    }
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      gravity = event.values;
      // r从这里返回
      SensorManager.getRotationMatrix(r, null, gravity, magnetic);
      //values从这里返回
      SensorManager.getOrientation(r, values);
      //提取数据
      z = (int) Math.toDegrees(values[0]);
      x= (int) Math.toDegrees(values[1]);
      y = (int) Math.toDegrees(values[2]);
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          TextView tv = findViewById(R.id.tv);
//          tv.setText("z："+z+"   x："+x+"  y："+y);
//          ImageView bg = findViewById(R.id.bg);
          RelativeLayout bg = findViewById(R.id.bg);
          tv.setX(-y*1+((float)Data.w)/2-800/2);
//          tv.setY(x*2+  ((float)(Data.sbH+Data.h))/2 -100/2 );
          bg.setX(y*1);
//          bg.setY(-x*2);
        }
      });
    }
  }
  void getScreen(){
    int statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    Data.sbH = statusBarHeight;
    Data.h = displayMetrics.heightPixels;
    Data.w = displayMetrics.widthPixels;
  }
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    sensorManager.unregisterListener(this,magneticSensor);
    sensorManager.unregisterListener(this,accelerometerSensor);
  }
}