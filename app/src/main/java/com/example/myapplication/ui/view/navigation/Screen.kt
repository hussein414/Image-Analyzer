package com.example.myapplication.ui.view.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object OpticalLand: Screen("optical-land")
    data object OpticalInfo: Screen("Optical_Info")
    data object OpticalSet : Screen("opticalSet/{bitmapUri}") {
        fun createRoute(bitmapUri: String) = "opticalSet/${Uri.encode(bitmapUri)}"
    }
    data object Camera: Screen("camera")
}