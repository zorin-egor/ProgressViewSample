package com.sample.progressview.models.cycloid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CycloidState(
    val totalSpeed: Float,
    val deltaSpeed: Float,
    val totalRadius: Float,
    val deltaRadius: Float,
    val timeProgress: Long,
    val indexProgress: Int,
) : Parcelable