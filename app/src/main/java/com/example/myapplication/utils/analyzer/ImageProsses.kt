package com.example.myapplication.utils.analyzer

import android.graphics.Bitmap
import com.example.myapplication.data.model.ResultProcess
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min

fun processImage(originalBitmap: Bitmap): ResultProcess {
    // Convert Bitmap to Mat for processing
    val originalImage = Mat()
    Utils.bitmapToMat(originalBitmap, originalImage)

    // Convert to black and white
    val bwImage = Mat()
    Imgproc.cvtColor(originalImage, bwImage, Imgproc.COLOR_RGB2GRAY)
    Imgproc.threshold(bwImage, bwImage, 128.0, 255.0, Imgproc.THRESH_BINARY)

    // Convert back to RGB to process in color
    val processedImage = Mat()
    Imgproc.cvtColor(bwImage, processedImage, Imgproc.COLOR_GRAY2RGB)

    // Drawing red vertical line in the middle
    val midX = processedImage.cols() / 2
    for (i in 0 until processedImage.rows()) {
        processedImage.put(i, midX, *doubleArrayOf(255.0, 0.0, 0.0))
    }

    // Red filter on the image
    val buffer = ByteArray((processedImage.total() * processedImage.channels()).toInt())
    processedImage[0, 0, buffer]
    run {
        var i = 0
        while (i < buffer.size) {
            buffer[i + 1] = 0 // Zero out the green channel
            buffer[i + 2] = 0 // Zero out the blue channel
            i += 3
        }
    }
    processedImage.put(0, 0, buffer)

    // Find middle of the red area
    var temp: DoubleArray
    var redAreaMiddle = processedImage.rows() / 2
    for (i in 0 until processedImage.rows()) {
        temp = processedImage[i, midX]
        if (temp[0] == 255.0) {
            redAreaMiddle = i
            break
        }
    }

    // Draw a horizontal red line through the middle of the red area
    for (i in 0 until processedImage.cols()) {
        processedImage.put(redAreaMiddle, i, *doubleArrayOf(255.0, 0.0, 0.0))
    }

    // Cropping the image
    val topBoundary = max(0.0, (redAreaMiddle - 100).toDouble()).toInt()
    val bottomBoundary =
        min(processedImage.rows().toDouble(), (redAreaMiddle + 100).toDouble()).toInt()
    val croppedImage = processedImage.submat(topBoundary, bottomBoundary, 0, processedImage.cols())

    // Convert the cropped Mat back to a Bitmap
    val croppedBitmap =
        Bitmap.createBitmap(croppedImage.cols(), croppedImage.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(croppedImage, croppedBitmap)

    // Edge detection on the cropped image
    val croppedImageCv = Mat()
    Utils.bitmapToMat(croppedBitmap, croppedImageCv)
    Imgproc.cvtColor(croppedImageCv, croppedImageCv, Imgproc.COLOR_RGB2GRAY)

    val edges = Mat()
    Imgproc.Canny(croppedImageCv, edges, 50.0, 150.0)

    val middleY = edges.rows() / 2
    val middleRow = ByteArray(edges.cols())
    edges[middleY, 0, middleRow]

    var mostLeftEdge = -1
    var mostRightEdge = -1
    for (i in middleRow.indices) {
        if (middleRow[i].toInt() != 0) {
            if (mostLeftEdge == -1) mostLeftEdge = i
            mostRightEdge = i
        }
    }

    var convertedUnits: Double? = null
    if (mostLeftEdge != -1 && mostRightEdge != -1) {
        val pixelDistance = mostRightEdge - mostLeftEdge
        convertedUnits = pixelDistance / 14.7
    }

    val imageWithLine = Mat()
    Imgproc.cvtColor(croppedImageCv, imageWithLine, Imgproc.COLOR_GRAY2RGB)
    if (mostLeftEdge != -1) {
        Imgproc.circle(
            imageWithLine,
            Point(mostLeftEdge.toDouble(), middleY.toDouble()),
            10,
            Scalar(0.0, 255.0, 0.0),
            -1
        )
    }
    if (mostRightEdge != -1) {
        Imgproc.circle(
            imageWithLine,
            Point(mostRightEdge.toDouble(), middleY.toDouble()),
            10,
            Scalar(0.0, 0.0, 255.0),
            -1
        )
    }

    val finalMarkedImage = Bitmap.createBitmap(imageWithLine.cols(), imageWithLine.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(imageWithLine, finalMarkedImage)

    return ResultProcess(
        croppedBitmap, finalMarkedImage,
        convertedUnits!!
    )
}
