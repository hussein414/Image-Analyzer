package com.example.myapplication.data.model

import android.graphics.Bitmap

data class ResultProcess(
    val croppedImage: Bitmap,
    val markedEdgesImage: Bitmap,
    val convertedUnits: Double,
    val second: Int?,
    val third: Int?
)