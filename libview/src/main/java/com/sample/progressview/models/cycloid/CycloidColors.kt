package com.sample.progressview.models.cycloid

import android.os.Parcelable
import com.sample.progressview.models.ColorModel
import com.sample.progressview.models.ShapeColors
import kotlinx.parcelize.Parcelize

@Parcelize
data class CycloidColors(
    override val backgroundColor: ColorModel,
    override val defaultColor: ColorModel,
    override val progressColor: ColorModel,
    val particleDefaultColor: ColorModel = defaultColor,
    val particleProgressColor: ColorModel = progressColor,
    val lineDefaultColor: ColorModel = defaultColor,
    val lineProgressColor: ColorModel = progressColor,
) : ShapeColors, Parcelable
