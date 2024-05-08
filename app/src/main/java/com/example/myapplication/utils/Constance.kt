package com.example.myapplication.utils

import android.Manifest
import android.graphics.Bitmap

object Constance {
    val CAMERAX_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    )
    const val CAMERA_PERMISSION = "PERMISSION"

    var AVERAGE_MM_DISTANCE = ""
    var AVERAGE_MM_DISTANCE_71 = ""
}