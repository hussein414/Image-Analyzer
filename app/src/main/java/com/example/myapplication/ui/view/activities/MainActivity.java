package com.example.myapplication.ui.view.activities;

import android.content.Intent;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.provider.MediaStore;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.utils.analyzer.ImageAnalyzer;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final int IMAGE_PICK_CODE = 1000;

    static {
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
    }

    private void bindViews() {
        OpenCVLoader.initDebug();
        binding.choseGallery.setOnClickListener(v -> {
            openGalleryForImage();
        });

    }

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                binding.image2.setImageBitmap(bitmap);
                ImageAnalyzer imageAnalyzer = new ImageAnalyzer();
                imageAnalyzer.processImage(bitmap,binding.image2,binding.rowEdges);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        if(requestCode==camera_code && data!=null)
//        {
//            bitmap=(Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(bitmap);
//
//            // using mat class
//            mat=new Mat();
//            Utils.bitmapToMat(bitmap,mat);
//            Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
//
//  ]          Utils.matToBitmap(mat,bitmap);
//  ]          imageView.setImageBitmap(bitmap);
//        }
    }

}



