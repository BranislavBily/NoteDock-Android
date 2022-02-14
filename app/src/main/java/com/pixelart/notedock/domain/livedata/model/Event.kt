package com.pixelart.notedock.domain.livedata.model

open class Event {
    var consumed = false
        private set

    fun consume() {
        consumed = true
    }
}