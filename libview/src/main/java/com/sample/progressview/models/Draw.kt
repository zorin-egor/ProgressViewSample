package com.sample.progressview.models

import android.graphics.Canvas
import android.os.Parcelable

internal interface Draw {

    fun onSizeChanged(width: Int, height: Int) {}

    fun onSave(): Parcelable? = null

    fun onRestore(state: Parcelable?) {}

    fun onDraw(canvas: Canvas)

}