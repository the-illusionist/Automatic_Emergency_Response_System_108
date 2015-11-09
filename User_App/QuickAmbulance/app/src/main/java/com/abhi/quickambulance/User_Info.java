package com.abhi.quickambulance;

/**
 * Created by Abhishek Kumar, IIT Jodhpur
 */

public class User_Info {
    private String name;
    private String contact;
    private String latitude;
    private String longitude;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User_Info [name=" + name + ", contact=" + contact + ", latitude=" + latitude + ", longitude=" + longitude + ", id=" +id+"]";
    }
}
