package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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
}
