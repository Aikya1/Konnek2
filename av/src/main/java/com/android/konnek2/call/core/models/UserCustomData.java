package com.android.konnek2.call.core.models;

import com.google.gson.annotations.SerializedName;
import com.android.konnek2.call.core.utils.ConstsCore;


import java.io.Serializable;

public class UserCustomData implements Serializable {

    public static String TAG_AVATAR_URL = "avatar_url";
    public static String TAG_STATUS = "status";
    public static String TAG_IS_IMPORT = "is_import";


    private int age;
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String lastName;
    private String gender;




    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("status")
    private String status;

    @SerializedName("is_import")
    private String isImport;


    public UserCustomData() {
        avatarUrl = ConstsCore.EMPTY_STRING;
        status = ConstsCore.EMPTY_STRING;
    }

    public UserCustomData(String avatarUrl, String status, String isImport) {
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.isImport = isImport;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsImport() {
        return isImport;
    }

    public void setIsImport(String isImport) {
        this.isImport = isImport;
    }
}