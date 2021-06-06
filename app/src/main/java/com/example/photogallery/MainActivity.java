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
     private static List<Photo> photosList = null;

     private static final int REQUEST_PHOTO_CAPTURE = 101;
     private static final int REQUEST_TAKE_PHOTO = 100;


     public static final String SHARED_PREFS = "sharedPrefs";
     public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);


        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (!firstStart){
            UserPhotosSaved(this);
        }

        btnCamera = findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(v -> {
            Log.d("holis", "holis");
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photosList = examplePhotos(this.getBaseContext());

    }

     private void openCamera() throws IOException {
         Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
             photoAsFile = createTakenPhoto();
             if (photoAsFile != null) {
                 Uri photoUri = FileProvider.getUriForFile(this, "com.example.photogallery", photoAsFile);
                 takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                 startActivityForResult(takePhotoIntent, REQUEST_PHOTO_CAPTURE);
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

     public static List<Photo> examplePhotos(Context context) {
         List<Photo> imagesList = new ArrayList<Photo>();

         BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
         bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

         String photosDirectory;
         Bitmap bitmap;
         try {
             photosDirectory = context.getFilesDir() + File.separator + "Photos";
             File[] listImages = new File(photosDirectory).listFiles();
             for (File files : listImages) {
                 if (files.getName().endsWith(".jpg") || files.getName().endsWith(".png")) {
                     bitmap = BitmapFactory.decodeFile(files.getPath(), bitmapOptions);
                     Photo photo = new Photo(files.getName(), bitmap, PhotoComments.commentFinder(context, files.getName()));
                     imagesList.add(photo);
                 }
             }

             File userImages = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
             File[] photos = userImages.listFiles();
             if (photos != null) {
                 for (File file : photos) {
                     bitmap = BitmapFactory.decodeFile(file.getPath(), bitmapOptions);
                     Photo it = new Photo(file.getName(), bitmap, PhotoComments.commentFinder(context, file.getName()));
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
         editor.putBoolean("firstStart", false);
         editor.apply();
     }

}