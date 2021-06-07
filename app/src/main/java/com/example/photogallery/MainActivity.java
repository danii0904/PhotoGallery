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

import android.widget.Button;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


 public class MainActivity extends AppCompatActivity {


     String currentPhotoPath;
     private File photoAsFile;
     private RecyclerView recyclerView;
     private static List<Photo> photosList = null;
     private int[] photos = {R.drawable.ic_launcher_background};

     private static final int REQUEST_PHOTO_CAPTURE = 101;
     private static final int REQUEST_TAKE_PHOTO = 100;


     public static final String SHARED_PREFS = "sharedPrefs";
     public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        boolean photosFirstTry = sharedPreferences.getBoolean("photosFirstTry", true);

        if (photosFirstTry){
            UserPhotosSaved(this);
        }

        Button photoBtn = findViewById(R.id.btnCamera);
        photoBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                openCamera();
            else
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photosList = examplePhotos(this.getBaseContext());
        viewAdapter vAdapter = new viewAdapter(photosList);
        recyclerView.setAdapter(vAdapter);

    }

     private void openCamera() {
         Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             photoAsFile = createTakenPhoto();
             if (photoAsFile != null) {
                 Uri photoUri = FileProvider.getUriForFile(this, "com.example.photogallery", photoAsFile);
                 cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                 startActivityForResult(cameraIntent, REQUEST_PHOTO_CAPTURE);
             }
             startActivity(getIntent());
     }

     private File createTakenPhoto() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoName = "JPG_ " + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
         try {
             File photo = File.createTempFile(photoName, ".jpg", storageDir);
             currentPhotoPath = photo.getAbsolutePath();
             return photo;
         } catch (IOException e) {
             e.printStackTrace();
         }

         return null;
     }

     public static List<Photo> examplePhotos(Context context) {
         List<Photo> imagesList = new ArrayList<>();

         BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
         bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

         String dirPhotos;
         Bitmap bitmap;
         try {
             dirPhotos = context.getFilesDir() + File.separator + "photos";
             File[] listImages = new File(dirPhotos).listFiles();
             for (File files : listImages) {
                 if (files.getName().endsWith(".jpg")) {
                     bitmap = BitmapFactory.decodeFile(files.getPath(), bitmapOptions);
                     Photo photo = new Photo(files.getName(), bitmap, PhotoComments.commentsOnXML(context, files.getName()));
                     imagesList.add(photo);
                 }
             }

             File userImages = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
             File[] photos = userImages.listFiles();
             if (photos != null) {
                 for (File file : photos) {
                     bitmap = BitmapFactory.decodeFile(file.getPath(), bitmapOptions);
                     Photo it = new Photo(file.getName(), bitmap, PhotoComments.commentsOnXML(context, file.getName()));
                     imagesList.add(it);
                 }
             }

             return imagesList;
         } catch (Exception e) {
             e.printStackTrace();
         }
         return null;
     }

     public static void UserPhotosSaved(Context context) {
         AssetManager assetManager = context.getAssets();
         String[] PhotosDir = null;

         InputStream inputStream;
         OutputStream outputStream;
         byte[] buffer = new byte[1024];

         try {
             PhotosDir = assetManager.list("UserPhotos");
         } catch (IOException e) {
             e.printStackTrace();
         }

         for (String name : PhotosDir) {
             File outFile = new File(context.getFilesDir(), "photos" + File.separator + name);
             File imagesDir = new File(context.getFilesDir(), "photos");

             try {
                 inputStream = assetManager.open("UserPhotos" + File.separator + name);
                 if (!imagesDir.exists())
                     imagesDir.mkdirs();
                 outputStream = new FileOutputStream(outFile);

                 int read;
                 while ((read = inputStream.read(buffer)) != -1) {
                     outputStream.write(buffer, 0, read);
                 }

//                 copyFile(inputStream, outputStream);
                 inputStream.close();
                 outputStream.flush();
                 outputStream.close();

             } catch (IOException e) {
                 e.printStackTrace();
             }
         }

         SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putBoolean("photosFirstTry", false);
         editor.apply();
     }

}