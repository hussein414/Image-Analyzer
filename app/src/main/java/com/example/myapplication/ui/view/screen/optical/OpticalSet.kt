package com.example.myapplication.ui.view.screen.optical

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.model.ResultProcess
import com.example.myapplication.ui.view.navigation.Screen
import com.example.myapplication.utils.analyzer.processImage
import com.example.myapplication.utils.common.getCorrectlyOrientedBitmap
import com.example.myapplication.utils.common.saveImageToGallery

@Suppress("DEPRECATION")
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "DefaultLocale")
@Composable
fun OpticalSet(navController: NavController, bitmapUri: String?) {
    var selectedImages by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var processedImageBitmap by remember { mutableStateOf<ResultProcess?>(null) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            selectedImages = uri
            uri?.let {
                var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                bitmap = getCorrectlyOrientedBitmap(context, it)
                processedImageBitmap = processImage(bitmap)
                saveImageToGallery(context, processedImageBitmap!!.croppedImage, "Calculation Result: ${processedImageBitmap?.convertedUnits ?: "N/A"}")
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val averageMmDistanceStr =
        String.format("%.2f", processedImageBitmap?.convertedUnits ?: 0.0) + "mm"

    LaunchedEffect(bitmapUri) {
        bitmapUri?.let {
            try {
                var bitmap =
                    context.contentResolver.openInputStream(Uri.parse(it))?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                bitmap = getCorrectlyOrientedBitmap(context, Uri.parse(it))
                processedImageBitmap = bitmap?.let { it1 -> processImage(it1) }
                saveImageToGallery(context, processedImageBitmap!!.croppedImage, "Calculation Result: ${processedImageBitmap?.convertedUnits ?: "N/A"}")
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("OpticalSet", "Failed to load bitmap: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Optical Caliper",
                    fontSize = 30.sp,
                    modifier = Modifier.weight(2f),
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Image(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.2f)
                        .clickable {
                            navController.navigate(Screen.OpticalInfo.route)
                        },
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.fillMaxSize()) {
            processedImageBitmap?.let {
                Image(
                    bitmap = it.croppedImage.asImageBitmap(),
                    contentDescription = "Processed Image",
                    modifier = Modifier
                        .height(600.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = averageMmDistanceStr,
                color = Color.Black,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .padding(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Choose Gallery", color = Color.White, fontSize = 15.sp)
                }

                Button(
                    onClick = {
                        navController.navigate(Screen.Camera.route)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .padding(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Choose Camera", color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }
}