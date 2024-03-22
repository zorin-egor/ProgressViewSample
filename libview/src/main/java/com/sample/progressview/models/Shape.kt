package com.sample.progressview.models

import android.os.Parcelable

interface Shape : Draw {

    fun onSizeChanged(width: Int, height: Int) {}

    fun onSave(): Parcelable? = null

    fun onRestore(state: Parcelable?) {}

    fun setProgress(value: Int)

}