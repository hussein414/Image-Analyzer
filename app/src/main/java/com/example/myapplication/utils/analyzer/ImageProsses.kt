package com.example.myapplication.utils.analyzer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.example.myapplication.utils.Constance
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.pow
import kotlin.math.sqrt

fun processImage(bitmap: Bitmap?): Bitmap? {
    // Convert the incoming Bitmap to a Mat object
    val image = Mat()
    Utils.bitmapToMat(bitmap, image)
    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2GRAY)

    // Apply Gaussian blur
    Imgproc.GaussianBlur(image, image, Size(5.0, 5.0), 0.0)

    // Apply Laplacian
    val laplacian = Mat()
    Imgproc.Laplacian(image, laplacian, CvType.CV_64F)
    Core.convertScaleAbs(laplacian, laplacian)

    // Convert to Binary using Adaptive Threshold
    val binary = Mat()
    Imgproc.adaptiveThreshold(
        laplacian,
        binary,
        255.0,
        Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
        Imgproc.THRESH_BINARY,
        11,
        2.0
    )

    // Convert the binary Mat back to a Bitmap to save or further use
    val laplacianBitmap = Bitmap.createBitmap(binary.cols(), binary.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(binary, laplacianBitmap)

    // Apply Canny edge detection and calculate distances
    val averagePixelDistance = calculateAverageDistanceWithCanny(binary)
    val ppmm = 8.658 // Pixels per millimeter
    val averageMmDistance = pixelsToMm(averagePixelDistance, ppmm)
    @SuppressLint("DefaultLocale") val averageMmDistanceStr =
        String.format("%.2f", averageMmDistance)
    val distResult = "Average mm distance:   $averageMmDistanceStr"
    distResult.also { Constance.AVERAGE_MM_DISTANCE = it }
    return laplacianBitmap
}

private fun calculateAverageDistanceWithCanny(binary: Mat): Double {
    val edges = Mat()
    Imgproc.Canny(binary, edges, 100.0, 200.0)
    val points: MutableList<Point> = ArrayList()
    for (y in 0 until edges.rows()) {
        for (x in 0 until edges.cols()) {
            if (edges[y, x][0] == 255.0) {
                points.add(Point(x.toDouble(), y.toDouble()))
            }
        }
    }
    if (points.size < 2) return Double.POSITIVE_INFINITY
    var totalDistance = 0.0
    var prev = points[0]
    for (i in 1 until points.size) {
        val cur = points[i]
        totalDistance += sqrt((cur.x - prev.x).pow(2.0) + (cur.y - prev.y).pow(2.0))
        prev = cur
    }
    return totalDistance / (points.size - 1)
}

private fun pixelsToMm(pixels: Double, ppmm: Double): Double = pixels / ppmm