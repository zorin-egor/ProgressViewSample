package com.sample.progressview

import android.content.res.Resources

internal val Int.toDp: Float
    get() = this * Resources.getSystem().displayMetrics.density

internal val Int.toDpI: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
