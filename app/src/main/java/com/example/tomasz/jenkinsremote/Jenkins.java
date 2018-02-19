package com.example.tomasz.jenkinsremote;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Jenkins {


    @SerializedName("jobs")
    private ArrayList<com.example.tomasz.jenkinsremote.Project> jenkinsProjects = new ArrayList<>();

    static String getDate(String timestamp) {
        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(Long.parseLong(timestamp));
        return sf.format(date);
    }

    public ArrayList<com.example.tomasz.jenkinsremote.Project> getJenkinsProjects() {
        return jenkinsProjects;
    }

    public void setJenkinsProjects(ArrayList<com.example.tomasz.jenkinsremote.Project> jenkinsProjects) {
        this.jenkinsProjects = jenkinsProjects;
    }

    public class Build {

        @SerializedName("url")
        private String url;

        @SerializedName("number")
        private String number;

        @SerializedName("timestamp")
        private String timestamp;


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {


            return String.format("%s.\n Build Date %s  ", number, getDate(timestamp));

        }
    }

}
