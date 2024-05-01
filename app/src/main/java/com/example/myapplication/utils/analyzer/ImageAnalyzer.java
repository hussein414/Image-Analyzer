package com.example.myapplication.utils.analyzer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageAnalyzer {

    private static final double PIXEL_WIDTH_CALIBRATION = 247.0;  // Calibration: pixels
    private static final double REAL_WIDTH_CALIBRATION = 5.0;     // Calibration: millimeters


    public void detectLinesAndCalculateDistance(Bitmap bitmap, ImageView imageView, TextView textView) {
        // Convert bitmap to Mat
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        // Use Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(src, edges, 100, 200);

        // Use Hough Transform to find lines
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50, 30, 10);

        Point line1Start = new Point();
        Point line1End = new Point();
        Point line2Start = new Point();
        Point line2End = new Point();


        Mat output = new Mat();
        Core.bitwise_or(src, src, output);
        double minDistCm = Double.MAX_VALUE;

        if (lines.rows() > 1) {
            line1Start = new Point(lines.get(0, 0)[0], lines.get(0, 0)[1]);
            line1End = new Point(lines.get(0, 0)[2], lines.get(0, 0)[3]);
            line2Start = new Point(lines.get(1, 0)[0], lines.get(1, 0)[1]);
            line2End = new Point(lines.get(1, 0)[2], lines.get(1, 0)[3]);
            // Calculate minimum distance between two lines
            double minDistPixels = calculateDistanceBetweenParallelLines(line1Start, line1End, line2Start);
            minDistCm = convertPixelsToMillimeters(minDistPixels);
        }

        // Draw the lines for visualization
        Core.bitwise_or(src, src, output); // Just copy src into output for now
        Imgproc.line(output, line1Start, line1End, new Scalar(0, 0, 255), 3);
        Imgproc.line(output, line2Start, line2End, new Scalar(0, 0, 255), 3);

        // Convert back to bitmap and show on UI
        Bitmap resultBitmap = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(output, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
        displayDistance(minDistCm, textView);
        // Use the minDist value for your application needs
    }

    private double calculateDistanceBetweenParallelLines(Point line1Start, Point line1End, Point line2Start) {
        // Calculate the direction vector of line 1
        Point dirLine1 = new Point(line1End.x - line1Start.x, line1End.y - line1Start.y);

        // Calculate the vector from a point on line 1 to a point on line 2
        Point line1ToLine2 = new Point(line2Start.x - line1Start.x, line2Start.y - line1Start.y);

        // Calculate the normal vector of line 1 (by rotating the direction vector 90 degrees)
        Point normalLine1 = new Point(-dirLine1.y, dirLine1.x);

        // Normalize the normal vector
        double norm = Math.sqrt(normalLine1.x * normalLine1.x + normalLine1.y * normalLine1.y);
        normalLine1.x /= norm;
        normalLine1.y /= norm;

        // The distance between the lines is the dot product of the normal of line 1 and the vector from line 1 to line 2
        // This gives the projection of line1ToLine2 onto the normal, which is the shortest distance
        return Math.abs(normalLine1.x * line1ToLine2.x + normalLine1.y * line1ToLine2.y);
    }

    private void displayDistance(double distance, TextView textView) {
        if (distance != Double.MAX_VALUE) {
            textView.setText(String.format(Locale.US, "Distance: %.2f cm", distance));
        } else {
            textView.setText("Unable to calculate distance");
        }
    }


    // Method to calculate distance in millimeters from pixel distance
    private static double convertPixelsToMillimeters(double pixels) {
        return pixels / calculatePixelToMMRatio();
    }

    // Method to calculate the pixel to mm ratio
    private static double calculatePixelToMMRatio() {
        return PIXEL_WIDTH_CALIBRATION / REAL_WIDTH_CALIBRATION;
    }

    //TODO:CANNY
    public void cannyImage(Bitmap bitmap, ImageView imageView) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat gray = new Mat(src.rows(), src.cols(), src.type());
        Mat edges = new Mat(src.rows(), src.cols(), src.type());
        Mat dst = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));

        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.blur(gray, edges, new Size(3, 3));
        Imgproc.Canny(gray, edges, 100, 200);

        src.copyTo(dst, edges);
        Bitmap resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }


    public void applySobel(Bitmap bitmap, ImageView imageView) {
        // Convert bitmap to Mat
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(gray, gray, new org.opencv.core.Size(5, 5), 0);

        // Sobel operators
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Imgproc.Sobel(gray, grad_x, CvType.CV_16S, 1, 0);
        Imgproc.Sobel(gray, grad_y, CvType.CV_16S, 0, 1);

        // Converting back to CV_8U
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        // Combine gradients
        Mat sobel = new Mat();
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, sobel);

        // Add point to the center
        Imgproc.circle(sobel, new Point(sobel.cols() / 2, sobel.rows() / 2), 5, new Scalar(255, 0, 0), -1);


        Bitmap resultBitmap = Bitmap.createBitmap(sobel.cols(), sobel.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(sobel, resultBitmap);
        imageView.setImageBitmap(resultBitmap);

    }

    public void applyScharr(Bitmap bitmap, ImageView imageView) {
        // Convert bitmap to Mat
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(gray, gray, new org.opencv.core.Size(5, 5), 0);

        // Scharr operators
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Imgproc.Scharr(gray, grad_x, CvType.CV_16S, 1, 0);
        Imgproc.Scharr(gray, grad_y, CvType.CV_16S, 0, 1);

        // Converting back to CV_8U
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        // Combine gradients
        Mat scharr = new Mat();
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, scharr);

        // Convert back to bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(scharr.cols(), scharr.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(scharr, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }



    public void applyCanny(Bitmap imageBitmap, ImageView imageView) {
        // Convert the input image to grayscale
        Mat originalImage = new Mat();
        Utils.bitmapToMat(imageBitmap, originalImage);
        Imgproc.cvtColor(originalImage, originalImage, Imgproc.COLOR_BGR2GRAY);

        // Apply Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(originalImage, edges, 100, 200);

        // Convert the edges to bitmap
        Bitmap edgesBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, edgesBitmap);

        // Set the bitmap to the ImageView
        imageView.setImageBitmap(edgesBitmap);
    }
    public void processImage(Bitmap bitmap,ImageView imageView,TextView textView) {
        // Convert the incoming Bitmap to a Mat object
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2GRAY);

        // Apply Gaussian blur
        Imgproc.GaussianBlur(image, image, new org.opencv.core.Size(5, 5), 0);

        // Apply Laplacian
        Mat laplacian = new Mat();
        Imgproc.Laplacian(image, laplacian, CvType.CV_64F);
        Core.convertScaleAbs(laplacian, laplacian);

        // Convert to Binary using Adaptive Threshold
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(laplacian, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // Convert the binary Mat back to a Bitmap to save or further use
        Bitmap laplacianBitmap = Bitmap.createBitmap(binary.cols(), binary.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(binary, laplacianBitmap);

        // Apply Canny edge detection and calculate distances
        double averagePixelDistance = calculateAverageDistanceWithCanny(binary);
        double ppmm = 8.658; // Pixels per millimeter
        double averageMmDistance = pixelsToMm(averagePixelDistance, ppmm);

        String distResult = "Average mm distance: " + averageMmDistance;
        textView.setText(distResult);
        imageView.setImageBitmap(laplacianBitmap);
    }

    private double calculateAverageDistanceWithCanny(Mat binary) {
        Mat edges = new Mat();
        Imgproc.Canny(binary, edges, 100, 200);

        List<Point> points = new ArrayList<>();
        for (int y = 0; y < edges.rows(); y++) {
            for (int x = 0; x < edges.cols(); x++) {
                if (edges.get(y, x)[0] == 255) {
                    points.add(new Point(x, y));
                }
            }
        }
        if (points.size() < 2) return Double.POSITIVE_INFINITY;

        double totalDistance = 0;
        Point prev = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point cur = points.get(i);
            totalDistance += Math.sqrt(Math.pow(cur.x - prev.x, 2) + Math.pow(cur.y - prev.y, 2));
            prev = cur;
        }
        return totalDistance / (points.size() - 1);
    }

    private double pixelsToMm(double pixels, double ppmm) {
        return pixels / ppmm;
    }
}
