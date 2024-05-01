package com.example.myapplication.ui.view.navigation

sealed class Screen(val route: String) {
    data object OpticalLand: Screen("optical-land")
    data object OpticalInfo: Screen("Optical_Info")
    data object OpticalSet: Screen("Optical_set")
    data object Camera: Screen("camera")
}