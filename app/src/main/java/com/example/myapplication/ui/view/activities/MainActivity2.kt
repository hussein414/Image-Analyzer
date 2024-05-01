package com.example.myapplication.ui.view.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.view.navigation.Screen
import com.example.myapplication.ui.view.screen.camera.CameraScreen
import com.example.myapplication.ui.view.screen.optical.OpticalInfo
import com.example.myapplication.ui.view.screen.optical.OpticalLand
import com.example.myapplication.ui.view.screen.optical.OpticalSet
import org.opencv.android.OpenCVLoader

class MainActivity2 : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("opencv_java4")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.OpticalLand.route
                    ) {
                        composable(Screen.OpticalLand.route) {
                            OpticalLand(navController = navController)
                        }
                        composable(Screen.OpticalSet.route) {
                            OpticalSet(navController=navController)
                        }
                        composable(Screen.OpticalInfo.route) {
                            OpticalInfo(navController=navController)
                        }
                        composable(Screen.Camera.route) {
                            CameraScreen()
                        }
                    }
                }
            }
        }
    }
}

