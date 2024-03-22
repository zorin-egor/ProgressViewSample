package com.sample.progressview.models

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorModel(
    @IntRange(from = 0, to = 255) val red: Int,
    @IntRange(from = 0, to = 255) val green: Int,
    @IntRange(from = 0, to = 255) val blue: Int,
    @IntRange(from = 0, to = 255) val alpha: Int
) : Parcelable

val ColorModel.toIntColor: Int get() = Color.argb(alpha, red, green, blue)

val Int.toColorModel: ColorModel
    get() = ColorModel(
        red = Color.red(this),
        green = Color.green(this),
        blue = Color.blue(this),
        alpha = Color.alpha(this),
    )
