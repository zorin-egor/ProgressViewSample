package com.sample.progressview.models

import android.os.Parcelable

internal sealed interface ShapeModel : Parcelable {
    interface Cycloid : ShapeModel
}