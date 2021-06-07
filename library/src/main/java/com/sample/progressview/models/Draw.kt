package com.sample.progressview.models

import android.graphics.Canvas

interface Draw {

    fun onSizeChanged(width: Int, height: Int) {}

    fun onDraw(canvas: Canvas)

}