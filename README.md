Image-Analyzer

Hi, I'm a master's student at Haagse Hoge School Delft. 
The goal of this project is to replace the optical caliper (shown in the photo above) made by Fidelis from the Smart Sensor System group, 
which is part of the GEGOGE project, with smartphones and tablets. 
The optical caliper is designed to measure the thickness of plant stems with an accuracy of 0.5 millimeters.
Similarly, this application is created with the same objective and can measure the thickness of plant stems,
take pictures of them, and store the images in its memory

![photo_1_2024-05-01_14-31-38](https://github.com/hussein414/Image-Analyzer/assets/76725925/c786d357-9f27-4658-b8b5-7df9a0b4358e)

![photo_3_2024-05-01_14-31-38](https://github.com/hussein414/Image-Analyzer/assets/76725925/1da97a82-54dd-400d-8aba-6ba7036faca4)


![photo_2_2024-05-01_14-31-38](https://github.com/hussein414/Image-Analyzer/assets/76725925/764cdd16-60b7-4440-9a00-77c834fcdde0)

he processImage function takes a Bitmap image as input and performs a series of image processing steps to detect the middle of a red area and measure the distance between the leftmost and rightmost edges of the red area.
Steps:
1- Convert Bitmap to Mat:
The Utils.bitmapToMat function converts the input Bitmap to a Mat object, which is used for image processing in OpenCV.

2- Convert to Black and White:
The Imgproc.cvtColor function converts the Mat to grayscale using the COLOR_RGB2GRAY flag.
Imgproc.threshold then converts the grayscale image to a binary image, where pixels above a certain threshold are set to white and the rest to black.

3- Convert Back to RGB:
The Imgproc.cvtColor function converts the binary image back to RGB to allow for color processing.

4- Draw Red Vertical Line:
A red vertical line is drawn in the middle of the image using a loop that iterates over the rows and sets the pixel values at the middle column to red.

5- Red Filter:
A red filter is applied to the image by iterating over the pixel data and setting the green and blue channels to zero.

6- Find Middle of Red Area:
The function iterates over the rows of the image at the middle column and searches for the first row where the red channel is 255. This row represents the middle of the red area.

7- Draw Horizontal Red Line:
A horizontal red line is drawn through the middle of the red area using a loop that iterates over the columns and sets the pixel values at the middle row to red.

8- Crop the Image:
The image is cropped to focus on the red area by specifying the top and bottom boundaries based on the middle of the red area.

9- Convert Cropped Mat to Bitmap:
The Utils.matToBitmap function converts the cropped Mat back to a Bitmap.

10- Edge Detection:
The cropped Bitmap is converted back to a Mat and then to grayscale.
The Imgproc.Canny function is used to detect edges in the grayscale image.

11- Find Leftmost and Rightmost Edges:
The function iterates over the middle row of the edge-detected image and searches for the first and last non-zero pixels, which represent the leftmost and rightmost edges of the red area.


12- Calculate Distance:
If both leftmost and rightmost edges are found, the pixel distance between them is calculated.
This distance is then converted to real-world units using a known conversion factor.


13- Mark Edges on Image:
The leftmost and rightmost edge points are marked on the image with circles.

14- Convert Mat to Bitmap:
The final marked image is converted back to a Bitmap and returned along with the cropped image and the calculated distance.

Return Value:
The function returns a ResultProcess object containing the cropped image, the final marked image, and the converted distance in real-world units.


