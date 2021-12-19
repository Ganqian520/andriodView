package com.gq.music.spectrum;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gq.music.R;
import com.gq.music.util.Data;

public class FragmentSpectrum extends Fragment {
  private MediaPlayer mediaPlayer;
  private Visualizer visualizer;
  private ViewCanvas view_canvas;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_spectrum,container,false); //绑定布局
  }

  @Override
  public void onResume() {
    super.onResume();
    init();
  }

  @Override
  public void onPause() {
    super.onPause();
    visualizer.release();
  }

  void init(){
    view_canvas = getView().findViewById(R.id.view_canvas);
    mediaPlayer = Data.mediaPlayer;
    visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
    visualizer.setCaptureSize(128);//Visualizer.getCaptureSizeRange()[1]:1024, fft数组的长度，限制范围128~1024
    visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
      @Override
      public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
      @Override
      public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        int n = fft.length;
        float[] magnitudes = new float[n / 2 + 1];
        magnitudes[0] = (float)Math.abs(fft[0]);      // DC
        magnitudes[n / 2] = (float)Math.abs(fft[1]);  // Nyquist
        for (int k = 1;k < n / 2; k++) {
          int i = k * 2;
          magnitudes[k] = (float)Math.hypot(fft[i], fft[i + 1]);
        }
        view_canvas.myDraw(magnitudes);
      }
    }, Visualizer.getMaxCaptureRate()/2 , false, true);
    //getMaxCaptureRate() 采样最大频率：20KHz
    visualizer.setEnabled(true);
  }
}
