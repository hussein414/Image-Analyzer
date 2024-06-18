package com.example.myapplication.utils.analyzer

import android.graphics.Bitmap
import android.util.Log
import com.example.myapplication.data.model.ResultProcess
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File

private const val TAG = "ImageProcessing"

fun processImage(originalBitmap: Bitmap): ResultProcess {
    val originalImage = BitmapToMat(originalBitmap)

    val edgeDetectionResult = performAdvancedEdgeDetection(originalImage)

    val finalMarkedImage = markEdges(edgeDetectionResult.first, edgeDetectionResult.second)

    val processedBitmap = MatToBitmap(edgeDetectionResult.first)

    return ResultProcess(
        processedBitmap, finalMarkedImage,
        edgeDetectionResult.second?.first ?: 0.0,
        edgeDetectionResult.second?.second,
        edgeDetectionResult.second?.third
    )
}

private fun BitmapToMat(bitmap: Bitmap): Mat {
    val mat = Mat()
    Utils.bitmapToMat(bitmap, mat)
    return mat
}

private fun MatToBitmap(mat: Mat): Bitmap {
    val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(mat, bitmap)
    return bitmap
}

private fun performAdvancedEdgeDetection(image: Mat): Pair<Mat, Triple<Double?, Int?, Int?>> {
    val tempImagePath = File.createTempFile("temp_image", ".jpg").absolutePath
    Imgcodecs.imwrite(tempImagePath, image)

    Log.d(TAG, "Image saved to $tempImagePath")

    val edges = advancedCanny(tempImagePath, lowThreshold = 30.0, highThreshold = 100.0, kernelSize = org.opencv.core.Size(7.0, 7.0), morphOperations = true)

    val middleY = edges.rows() / 2
    val (mostLeftEdge, mostRightEdge) = findEdges(edges, middleY)

    val convertedUnits = if (mostLeftEdge != -1 && mostRightEdge != -1) {
        val pixelDistance = mostRightEdge - mostLeftEdge
        pixelDistance / 31.95
    } else {
        null
    }

    return Pair(edges, Triple(convertedUnits, mostLeftEdge, mostRightEdge))
}

private fun advancedCanny(imagePath: String, lowThreshold: Double = 50.0, highThreshold: Double = 150.0, kernelSize: org.opencv.core.Size = org.opencv.core.Size(5.0, 5.0), morphOperations: Boolean = false): Mat {
    val image = Imgcodecs.imread(imagePath)
    if (image.empty()) {
        Log.e(TAG, "Failed to load image at path: $imagePath")
        throw IllegalArgumentException("Image at path $imagePath is empty or cannot be loaded")
    }

    val gray = Mat()
    Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)
    val blurred = Mat()
    Imgproc.GaussianBlur(gray, blurred, kernelSize, 0.0)
    val edges = Mat()
    Imgproc.Canny(blurred, edges, lowThreshold, highThreshold)

    if (morphOperations) {
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, org.opencv.core.Size(3.0, 3.0))
        Imgproc.morphologyEx(edges, edges, Imgproc.MORPH_CLOSE, kernel)
    }

    return edges
}

private fun markEdges(image: Mat, edgeData: Triple<Double?, Int?, Int?>): Bitmap {
    val middleY = image.rows() / 2
    val mostLeftEdge = edgeData.second
    val mostRightEdge = edgeData.third

    Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2RGB)
    if (mostLeftEdge != -1) {
        Imgproc.circle(image, Point(mostLeftEdge!!.toDouble(), middleY.toDouble()), 10, Scalar(0.0, 255.0, 0.0), -1)
    }
    if (mostRightEdge != -1) {
        Imgproc.circle(image, Point(mostRightEdge!!.toDouble(), middleY.toDouble()), 10, Scalar(0.0, 255.0, 0.0), -1)
    }
    return MatToBitmap(image)
}

private fun findEdges(image: Mat, middleY: Int): Pair<Int, Int> {
    val range = 200
    var leftEdges = mutableListOf<Int>()
    var rightEdges = mutableListOf<Int>()

    for (i in (middleY - range)..(middleY + range)) {
        val row = ByteArray(image.cols())
        image[i, 0, row]

        var mostLeftEdge = -1
        var mostRightEdge = -1

        for (j in row.indices) {
            if (row[j].toInt() != 0) {
                if (mostLeftEdge == -1) mostLeftEdge = j
                mostRightEdge = j
            }
        }

        if (mostLeftEdge != -1) leftEdges.add(mostLeftEdge)
        if (mostRightEdge != -1) rightEdges.add(mostRightEdge)
    }

    val avgLeftEdge = if (leftEdges.isNotEmpty()) leftEdges.average().toInt() else -1
    val avgRightEdge = if (rightEdges.isNotEmpty()) rightEdges.average().toInt() else -1

    return Pair(avgLeftEdge, avgRightEdge)
}
