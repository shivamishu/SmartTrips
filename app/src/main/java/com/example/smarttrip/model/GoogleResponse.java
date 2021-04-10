package com.example.smarttrip.model;

import java.io.Serializable;

public class GoogleResponse implements Serializable {
    private double lat;
    private double lng;
    private String openNow;
    private String rating;
    private String name;


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lng);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GoogleResponse other = (GoogleResponse) obj;
        if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
            return false;
        if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GoogleResponse{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", openNow='" + openNow + '\'' +
                ", rating='" + rating + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
