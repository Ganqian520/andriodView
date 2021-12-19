package com.gq.video.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.gq.music.MyApplication
import com.gq.music.databinding.VideoAdapterVp2PlayBinding
import com.gq.music.util.Util
import com.gq.video.GD
import com.gq.video.bean.VMPlay
import com.gq.video.dialog.DialogEdit
import com.gq.video.event.EventChange
import com.gq.video.event.EventPlayClose
import com.gq.video.util.MyClick
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit


class AdapterVp2Play(val context:Context,var currentIndex:Int): RecyclerView.Adapter<AdapterVp2Play.Holder>() {
  
  var list_player: Array<ExoPlayer?> = arrayOfNulls<ExoPlayer>(GD.list_video_show.size)
  var list_binding: Array<VideoAdapterVp2PlayBinding?> = arrayOfNulls<VideoAdapterVp2PlayBinding>(GD.list_video_show.size)
  var isUserDrag = false
  var data = VMPlay()
  var handler = Handler()
  lateinit var runnable: Runnable
  
  init {
    runnable = Runnable {
      updateSeekBar()
      handler.postDelayed(runnable,1000)
    }
  }
  
  @Subscribe
  fun onChange(e:EventChange){
    handlePageChange(e.position)
  }
  @Subscribe
  fun onClose(e:EventPlayClose){
    list_player[currentIndex]?.release()
    if (currentIndex>0) list_player[currentIndex-1]?.release()
    if(currentIndex<list_player.size-1) list_player[currentIndex+1]?.release()
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVp2Play.Holder {
    return Holder(VideoAdapterVp2PlayBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }
  
  override fun getItemCount(): Int = GD.list_video_show.size
  
  @SuppressLint("ClickableViewAccessibility")
  override fun onBindViewHolder(holder: AdapterVp2Play.Holder, position: Int) {
    holder.binding.data = data
    holder.binding.tvDesc.text = GD.list_video_show[position].desc
    holder.binding.tvDesc.isSelected = true
    holder.binding.tvDuration.text = Util.transTime(GD.list_video_show[position].duration)
    holder.binding.sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        holder.binding.tvPosition.text = Util.transTime(progress)
      }
      override fun onStartTrackingTouch(seekBar: SeekBar?) {isUserDrag=true}
      override fun onStopTrackingTouch(seekBar: SeekBar?) {
        isUserDrag=false
        list_player[position]?.seekTo(holder.binding.sb.progress*1000.toLong())
      }
    })
    holder.binding.rvMask.setOnClickListener(MyClick(object :MyClick.CB{
      override fun singleClick() {
        if (!data.a) openProgress() else closeProgress()
      }
      override fun doubleClick() {
        var isPlay = list_player[position]!!.isPlaying
        if (isPlay) list_player[position]?.pause() else list_player[position]?.play()
      }
    }))
//    DialogEdit().show((context as FragmentActivity).supportFragmentManager,"tag")
    holder.binding.rvMask.setOnLongClickListener {
      DialogEdit().show((context as FragmentActivity).supportFragmentManager,"tag")
      true
    }
  }
  
  fun openProgress(){
    data.a = true
    handler.postDelayed(runnable,0)
  }
  fun closeProgress(){
    data.a = false
    handler.removeCallbacks(runnable)
  }
  
  fun updateSeekBar(){
    if(!isUserDrag){
      val int: Int = list_player[currentIndex]!!.currentPosition.toInt().div(1000)
      list_binding[currentIndex]?.tvPosition?.text =  Util.transTime(int)
      list_binding[currentIndex]?.sb?.progress = int
    }
  }
  
  private fun handlePageChange(position:Int){
    currentIndex = position
    
    if (data.a) closeProgress()
    
    if (currentIndex>0) list_player[currentIndex-1]?.pause()
    if(currentIndex<list_player.size-1) list_player[currentIndex+1]?.pause()
    list_player[position]?.play()
    list_binding[position]?.sb?.max = GD.list_video_show[position].duration
  }
  
  override fun onViewAttachedToWindow(holder: Holder) {
    super.onViewAttachedToWindow(holder)
    val player:ExoPlayer = ExoPlayer.Builder(context).build()
    holder.binding.playerView.player = player
    player.repeatMode = Player.REPEAT_MODE_ONE
    
//    val proxyUrl = MyApplication.getProxy(context).getProxyUrl(GD.list_video_show[holder.bindingAdapterPosition].url)
    val mediaItem:MediaItem = MediaItem.fromUri(GD.list_video_show[holder.bindingAdapterPosition].url)
    
    val dataSource = DefaultDataSource(context, true)
    val factory = DataSource.Factory { dataSource }
    val mediaSource = ProgressiveMediaSource
      .Factory(factory)
      .createMediaSource(mediaItem)
    player.setMediaSource(mediaSource)
    
    player.prepare()
    list_player[holder.bindingAdapterPosition] = player
    list_binding[holder.bindingAdapterPosition] = holder.binding
  }
  
  override fun onViewDetachedFromWindow(holder: Holder) {
    super.onViewDetachedFromWindow(holder)
    list_player[holder.bindingAdapterPosition]?.release()
    list_player[holder.bindingAdapterPosition] = null
    list_binding[holder.bindingAdapterPosition] = null
  }
  
  fun mYegister(){
    EventBus.getDefault().register(this)
  }
  fun unRegister(){
    EventBus.getDefault().unregister(this)
  }
  
  inner class Holder(itemView: VideoAdapterVp2PlayBinding) :RecyclerView.ViewHolder(itemView.root){
    var binding = itemView
  }

  
}