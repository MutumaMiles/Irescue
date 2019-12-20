package android.harmony.irescue.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

public class KinModel  {
    @PrimaryKey
    private String id;
    private String name, relationship, phoneNummber, imageUrl;

    public KinModel(String name, String relationship, String phoneNummber) {
        this.name = name;
        this.relationship = relationship;
        this.phoneNummber = phoneNummber;
    }

    public KinModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhoneNummber() {
        return phoneNummber;
    }

    public void setPhoneNummber(String phoneNummber) {
        this.phoneNummber = phoneNummber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
