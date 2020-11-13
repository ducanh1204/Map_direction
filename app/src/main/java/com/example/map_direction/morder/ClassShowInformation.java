package com.example.map_direction.morder;

public class ClassShowInformation {
    private double latitude;
    private double longitude;
    private String title;
    private String content;
    private int position;
    private String imgFirstly;
    public String[] imgInformationList;




    public ClassShowInformation(double latitude, double longitude, String title, String content, int position, String imgFirstly) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.content = content;
        this.position = position;
        this.imgFirstly = imgFirstly;
    }

    public ClassShowInformation(String[] imgInformationList) {
        this.imgInformationList = imgInformationList;
    }

    public String[] getImgInformationList() {
        return imgInformationList;
    }

    public void setImgInformationList(String[] imgInformationList) {
        this.imgInformationList = imgInformationList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImgFirstly() {
        return imgFirstly;
    }

    public void setImgFirstly(String imgFirstly) {
        this.imgFirstly = imgFirstly;
    }
}
