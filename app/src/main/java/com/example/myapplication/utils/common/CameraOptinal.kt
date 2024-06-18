package com.example.myapplication.utils.common

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.R
import com.example.myapplication.utils.Constance
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Uri) -> Unit,
    context: Context,
    enableFlash: Boolean = false,
    calculationResult: String
) {
    if (enableFlash) {
        controller.cameraControl?.enableTorch(true)
    }

    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.toBitmap()
                val correctedBitmap = correctImageOrientation(bitmap, image)
                val imageUri = saveImageToGallery(context, correctedBitmap, calculationResult)
                imageUri?.let {
                    onPhotoTaken(it)
                }
                playShutterSound(context)
                image.close()

                if (enableFlash) {
                    controller.cameraControl?.enableTorch(false)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)

                if (enableFlash) {
                    controller.cameraControl?.enableTorch(false)
                }
            }
        }
    )
}

fun hasRequiredPermissions(context: Context): Boolean {
    return Constance.CAMERAX_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "temp_image.png")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun saveImageToGallery(context: Context, bitmap: Bitmap, calculationResult: String): Uri? {
    val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
    val bitmapWithText = addTextToBitmap(bitmap, calculationResult)
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "processed_image_$date.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    imageUri?.let { uri ->
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            bitmapWithText.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }
        return uri
    }

    return null
}

private fun addTextToBitmap(bitmap: Bitmap, text: String): Bitmap {
    val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(newBitmap)
    val paint = Paint()
    paint.color = Color.WHITE
    paint.textSize = 40f
    paint.isAntiAlias = true
    paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)

    val rectPaint = Paint()
    rectPaint.color = Color.BLACK
    rectPaint.alpha = 255

    val rect = Rect()
    paint.getTextBounds(text, 0, text.length, rect)
    val padding = 20
    val rectLeft = newBitmap.width - rect.width() - padding
    val rectTop = newBitmap.height - rect.height() - padding
    val rectRight = newBitmap.width - padding
    val rectBottom = newBitmap.height - padding

    canvas.drawRect(rectLeft.toFloat(), rectTop.toFloat(), rectRight.toFloat(), rectBottom.toFloat(), rectPaint)
    canvas.drawText(text, rectLeft.toFloat(), (newBitmap.height - padding - rect.exactCenterY()), paint)

    return newBitmap
}

private fun playShutterSound(context: Context) {
    val mp = MediaPlayer.create(context, R.raw.camera)
    mp.start()
    mp.setOnCompletionListener { mp.release() }
}
// hazv kardade adad va taghire function baraye nacharkhidane safhe
private fun correctImageOrientation(bitmap: Bitmap, image: ImageProxy): Bitmap {
    // Simply return the bitmap without modifying the orientation
    return bitmap
}

private fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
}

fun getCorrectlyOrientedBitmap(context: Context, imageUri: Uri): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val exif = inputStream?.let { ExifInterface(it) }
    val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}