package com.example.myapplication.utils

import com.example.myapplication.R
import com.example.myapplication.data.model.OpticalCaliperModel


object Instance {
    val opticalModel= listOf(
        OpticalCaliperModel(image = R.drawable.caliper1),
        OpticalCaliperModel(image = R.drawable.caliper2),
        OpticalCaliperModel(image = R.drawable.caliper3),
    )
}