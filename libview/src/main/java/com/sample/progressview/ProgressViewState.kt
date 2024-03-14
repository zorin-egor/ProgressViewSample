package com.sample.progressview

import android.os.Parcelable
import android.view.View
import kotlinx.parcelize.Parcelize

@Parcelize
internal class ProgressViewState(
    val parcelable: Parcelable?,
    val state: Parcelable?
) : View.BaseSavedState(parcelable)