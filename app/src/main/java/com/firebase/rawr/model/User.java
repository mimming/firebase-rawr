package com.firebase.rawr.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.ByteArrayOutputStream;

/**
 * Created by mimming on 8/13/14.
 */
public class User {
    private String name;
    private int rawrs;
    private String profileImage;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRawrs() {
        return rawrs;
    }

    public void setRawrs(int rawrs) {
        this.rawrs = rawrs;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    @JsonIgnore
    public Bitmap getProfileImageBitmap() {
        if(profileImage == null) {
            return null;
        }
        byte[] profileImageBytes = Base64.decode(profileImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
    }
    @JsonIgnore
    public void setProfileImageBitmap(Bitmap profileImageBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileImageBitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);

        profileImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }
}

