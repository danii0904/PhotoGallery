package com.example.photogallery;

import android.graphics.Bitmap;

public class Photo {

    String nom;
    String comentari;
    Bitmap bitmap;


    public Photo() {}

    public Photo(String name, Bitmap bitmap, String comment) {
        this.nom = name;
        this.comentari = comment;
        this.bitmap = bitmap;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getComentari() {
        return comentari;
    }

    public void setComentari(String comment) {
        this.comentari = comentari;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }



}
