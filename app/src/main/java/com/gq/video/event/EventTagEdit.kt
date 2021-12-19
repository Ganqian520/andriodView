package com.gq.video.event

class EventTagEdit(name:String,position:Int) {
  var name: String
  var position = 0
  init {
    this.name = name
    this.position = position
  }
}