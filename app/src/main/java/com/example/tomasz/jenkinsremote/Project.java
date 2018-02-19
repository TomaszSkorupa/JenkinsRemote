package com.example.tomasz.jenkinsremote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Project {

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("color")
    private String color;

    @SerializedName("builds")
    private ArrayList<Jenkins.Build> builds = new ArrayList<>();

    @SerializedName("lastBuild")
    private Jenkins.Build lastBuild;


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<Jenkins.Build> getBuilds() {
        return builds;
    }

    public void setBuilds(ArrayList<Jenkins.Build> builds) {
        this.builds = builds;
    }

    @Override
    public String toString() {

        return String.format("%s \t Status: %s \n\nLast Build: %s", name, color.equals("blue") ? "built  " : "Not built", lastBuild == null ? "Not Built" : Jenkins.getDate(lastBuild.getTimestamp()));

    }


}

