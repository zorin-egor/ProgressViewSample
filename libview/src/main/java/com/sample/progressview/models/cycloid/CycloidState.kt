package com.sample.progressview.models.cycloid

import android.os.Parcelable
import com.sample.progressview.models.ShapeModel
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CycloidState(
    val shapeModel: ShapeModel,
    val totalSpeed: Float,
    val deltaSpeed: Float,
    val totalRadius: Float,
    val deltaRadius: Float,
    val timeProgress: Long,
    val indexProgress: Int,
    val fromProgress: Int,
    val toProgress: Int,
    val isShapeDynamic: Boolean,
    val isColorDynamic: Boolean,
    val isRadiusDynamic: Boolean,
) : Parcelable