package com.sample.progressview.models.cycloid

import com.sample.progressview.models.ShapeModel
import com.sample.progressview.toDp
import kotlinx.parcelize.Parcelize

@Parcelize
data class CycloidModel(
    val colors: CycloidColors,
    val x1: Float, val y1: Float,
    val x2: Float, val y2: Float,
    val lineWidth: Float = 7.toDp,
    val particleRadius: Float = 15.toDp,
) : ShapeModel.Cycloid

internal fun getOneTypeCycloid(colors: CycloidColors, lineWidth: Float = 7.toDp, particleRadius: Float = 15.toDp,): CycloidModel =
    CycloidModel(colors, 1.286000f, 4.242000f, 1.286000f, 4.242000f, lineWidth, particleRadius)

internal fun getTwoTypeCycloid(colors: CycloidColors, lineWidth: Float = 7.toDp, particleRadius: Float = 15.toDp,): CycloidModel =
    CycloidModel(colors, 4.283176f, 3.518178f, 3.683177f, 3.668177f, lineWidth, particleRadius)

internal fun getThreeTypeCycloid(colors: CycloidColors, lineWidth: Float = 7.toDp, particleRadius: Float = 15.toDp,): CycloidModel =
    CycloidModel(colors, 2.150000f, 2.299000f, 2.150000f, 2.299000f, lineWidth, particleRadius)

internal fun getFourTypeCycloid(colors: CycloidColors, lineWidth: Float = 7.toDp, particleRadius: Float = 15.toDp,): CycloidModel =
    CycloidModel(colors, 3.216887f, 4.066366f, 3.216887f, 4.066366f, lineWidth, particleRadius)

internal fun getFiveTypeCycloid(colors: CycloidColors, lineWidth: Float = 7.toDp, particleRadius: Float = 15.toDp,): CycloidModel =
    CycloidModel(colors, 1.745000f, 1.575000f, 1.745000f, 1.575000f, lineWidth, particleRadius)