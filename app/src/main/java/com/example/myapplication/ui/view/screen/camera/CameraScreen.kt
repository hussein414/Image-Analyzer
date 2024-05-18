package com.example.myapplication.ui.view.screen.camera


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.view.component.CameraPreview
import com.example.myapplication.ui.view.component.PhotoBottomSheetContent
import com.example.myapplication.ui.view.component.Ring
import com.example.myapplication.ui.view.navigation.Screen
import com.example.myapplication.ui.viewmodel.CameraTakeViewModel
import com.example.myapplication.utils.Constance
import com.example.myapplication.utils.Constance.CAMERAX_PERMISSIONS
import com.example.myapplication.utils.common.MyHighlightIndication
import com.example.myapplication.utils.common.hasRequiredPermissions
import com.example.myapplication.utils.common.saveBitmapToCache
import com.example.myapplication.utils.common.takePhoto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }
    val viewModel = viewModel<CameraTakeViewModel>()
    val bitmaps by viewModel.bitmap.collectAsState()
    val highlightIndication = remember { MyHighlightIndication() }
    val interactionSource = remember { MutableInteractionSource() }

    val hasPermissions = remember {
        hasRequiredPermissions(context)
    }

    val requestPermissionsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val cameraPermissionGranted = permissions[Constance.CAMERA_PERMISSION] ?: false
            if (cameraPermissionGranted) {
                takePhoto(
                    controller = controller,
                    onPhotoTaken = { bitmap ->
                        viewModel.onTakePhoto(bitmap)
                        val bitmapUri = saveBitmapToCache(context, bitmap).toString()
                        navController.navigate(Screen.OpticalSet.createRoute(bitmapUri)) {
                            popUpTo(Screen.Camera.route) { inclusive = true }
                        }
                    },
                    context = context
                )
            }
        }

    LaunchedEffect(hasPermissions) {
        if (!hasPermissions) {
            requestPermissionsLauncher.launch(CAMERAX_PERMISSIONS)
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheetContent(
                bitmap = bitmaps,
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = interactionSource,
                    indication = highlightIndication,
                    enabled = true,
                    onClick = {
                        takePhoto(
                            controller = controller,
                            onPhotoTaken = { bitmap ->
                                viewModel.onTakePhoto(bitmap)
                                val bitmapUri = saveBitmapToCache(context, bitmap).toString()
                                navController.navigate(Screen.OpticalSet.createRoute(bitmapUri)) {
                                    popUpTo(Screen.Camera.route) { inclusive = true }
                                }
                            },
                            context = context
                        )
                    }
                )
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier.offset(16.dp, 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera"
                )
            }

            Ring(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center),
                diameter = 50.dp,
                color = Color.White
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open gallery",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            onPhotoTaken = { bitmap ->
                                viewModel.onTakePhoto(bitmap)
                                val bitmapUri = saveBitmapToCache(context, bitmap).toString()
                                navController.navigate(Screen.OpticalSet.createRoute(bitmapUri)) {
                                    popUpTo(Screen.Camera.route) { inclusive = true }
                                }
                            },
                            context = context
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


