package com.ioannispriovolos.android.bakingapp;

import java.io.Serializable;

public class Step implements Serializable {

    public int id;
    public String shortDescription;
    public String description;
    public String videoURL;
    public String thumbnailURL;

    @Override
    public String toString() {
        return "Short description: "+ this.shortDescription+ " id: "+ this.id;
    }

    public String getShortDescription(){
        return this.shortDescription;
    }
    public String getLongDescription(){
        return this.description;
    }
    public String getVideoURL(){
        return this.videoURL;
    }
    public String getThumbnailURL(){return this.thumbnailURL;}

}