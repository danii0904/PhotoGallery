 package com.example.photogallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

 public class MainActivity extends AppCompatActivity {

     private Button btnCamera;
     private File photoAsFile;
     private static String PhotoDir;
     private RecyclerView recyclerView;

     private static final int REQUEST_PHOTO_CAPTURE = 10;
     private static final int REQUEST_TAKE_PHOTO = 10;

     SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);

     public static final String SHARED_PREFS = "sharedPrefs";
     public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

     private void openCamera() throws IOException {
         Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
             photoAsFile = createTakenPhoto();
             if (photoAsFile != null) {
                 Uri photoUri = FileProvider.getUriForFile(this, "com.app.photogallery", photoAsFile);
                 takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                 startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
             }
             startActivity(getIntent());
         }
     }

     private File createTakenPhoto() throws IOException {
        String currentPhotoPath;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoName = "JPG_ " + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(photoName, ".jpg", storageDir);
        currentPhotoPath = photo.getAbsolutePath();

         return photo;
     }

}