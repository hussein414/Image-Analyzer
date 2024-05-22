package com.example.myapplication.utils.analyzer

import android.graphics.Bitmap
import com.example.myapplication.data.model.ResultProcess
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

fun processImage(originalBitmap: Bitmap): ResultProcess {
    val originalImage = BitmapToMat(originalBitmap)

    val bwImage = convertToBlackAndWhite(originalImage)

    val processedImage = applyRedFilter(bwImage)

    val edgeDetectionResult = performEdgeDetection(processedImage)

    val finalMarkedImage = markEdges(edgeDetectionResult.first, edgeDetectionResult.second)

    val processedBitmap = MatToBitmap(processedImage)

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
private fun convertToBlackAndWhite(image: Mat): Mat {
    val bwImage = Mat()
    Imgproc.cvtColor(image, bwImage, Imgproc.COLOR_RGB2GRAY)
    Imgproc.threshold(bwImage, bwImage, 128.0, 255.0, Imgproc.THRESH_BINARY)
    Imgproc.cvtColor(bwImage, bwImage, Imgproc.COLOR_GRAY2RGB)
    return bwImage
}
private fun applyRedFilter(image: Mat): Mat {
    val midX = image.cols() / 2
    for (i in 0 until image.rows()) {
        image.put(i, midX, *doubleArrayOf(255.0, 0.0, 0.0))
    }
    val buffer = ByteArray((image.total() * image.channels()).toInt())
    image[0, 0, buffer]
    for (i in buffer.indices step 3) {
        buffer[i + 1] = 0
        buffer[i + 2] = 0
    }
    image.put(0, 0, buffer)
    return image
}
private fun performEdgeDetection(image: Mat): Pair<Mat, Triple<Double?, Int?, Int?>> {
    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY)

    val edges = Mat()
    Imgproc.Canny(image, edges, 50.0, 150.0)

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
    val convertedUnits = if (mostLeftEdge != -1 && mostRightEdge != -1) {
        val pixelDistance = mostRightEdge - mostLeftEdge
        pixelDistance / 38.0
    } else {
        null
    }

    return Pair(image, Triple(convertedUnits, mostLeftEdge, mostRightEdge))
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
        Imgproc.circle(image, Point(mostRightEdge!!.toDouble(), middleY.toDouble()), 10, Scalar(0.0, 0.0, 255.0), -1)
    }
    return MatToBitmap(image)
}
private fun findEdges(image: Mat, middleY: Int): Pair<Int, Int> {
    val middleRow = ByteArray(image.cols())
    image[middleY, 0, middleRow]

    var mostLeftEdge = -1
    var mostRightEdge = -1
    for (i in middleRow.indices) {
        if (middleRow[i].toInt() != 0) {
            if (mostLeftEdge == -1) mostLeftEdge = i
            mostRightEdge = i
        }
    }
    return Pair(mostLeftEdge, mostRightEdge)
}