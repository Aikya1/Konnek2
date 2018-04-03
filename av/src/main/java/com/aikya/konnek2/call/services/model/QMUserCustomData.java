package com.aikya.konnek2.call.services.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lenovo on 08-11-2017.
 */

public class QMUserCustomData implements Serializable {

    public static String TAG_AVATAR_URL = "avatarUrl";
    public static String TAG_STATUS = "status";
    public static String TAG_COUNTRY = "country";
    public static String TAG_CITY = "city";
    public static String TAG_deviceType = "deviceType";
    public static String TAG_prefLanguage = "prefLanguage";
    public static String TAG_addressLine1 = "addressLine1";
    public static String TAG_signUpType = "signUpType";
    public static String TAG_countryCode = "countryCode";
    public static String TAG_deviceToken = "deviceToken";
    public static String TAG_isEuropean = "isEuropean";
    public static String TAG_prefLanguage1 = "prefLanguage1";
    public static String TAG_prefEmail = "prefEmail";
    public static String TAG_prefInApp = "prefInApp";
    public static String TAG_deviceUdid = "deviceUdid";
    public static String TAG_prefLanguage3 = "prefLanguage3";
    public static String TAG_firstName = "firstname";
    public static String TAG_lastName = "lastname";
    public static String TAG_contactNo = "contactNo";
    public static String TAG_age = "age";



    /*  public static String TAG_LANGUAGE_1 = "language_1";
      public static String TAG_EMAIL = "email";
      public static String TAG_DEVICE_TYPE = "devicetype";
      public static String TAG_LANGUAGE = "language";
      public static String TAG_FIRSTNAME = "firstname";
      public static String TAG_LASTNAME = "lastname";
      public static String TAG_PHONE = "phoneno";*/


    private String age;
    private String firstName;
    private String lastName;
    private String contactno;
    private String gender;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

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

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDeviceUDid() {
        return deviceUDid;
    }

    public void setDeviceUDid(String deviceUDid) {
        this.deviceUDid = deviceUDid;
    }

    public String getIsEuropean() {
        return isEuropean;
    }

    public void setIsEuropean(String isEuropean) {
        this.isEuropean = isEuropean;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getPrefLanguage() {
        return prefLanguage;
    }

    public void setPrefLanguage(String prefLanguage) {
        this.prefLanguage = prefLanguage;
    }

    public String getPrefLanguage1() {
        return prefLanguage1;
    }

    public void setPrefLanguage1(String prefLanguage1) {
        this.prefLanguage1 = prefLanguage1;
    }

    public String getPrefLanguage3() {
        return prefLanguage3;
    }

    public void setPrefLanguage3(String prefLanguage3) {
        this.prefLanguage3 = prefLanguage3;
    }

    public String getPrefInApp() {
        return prefInApp;
    }

    public void setPrefInApp(String prefInApp) {
        this.prefInApp = prefInApp;
    }

    public String getPrefEmail() {
        return prefEmail;
    }

    public void setPrefEmail(String prefEmail) {
        this.prefEmail = prefEmail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSignUpType() {
        return signUpType;
    }

    public void setSignUpType(String signUpType) {
        this.signUpType = signUpType;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    private String deviceUDid;
    private String isEuropean;
    private String deviceToken;
    private String prefLanguage;
    private String prefLanguage1;
    private String prefLanguage3;
    private String prefInApp;
    private String prefEmail;
    private String city;
    private String country;
    private String status;
    private String avatarUrl;
    private String countryCode;
    private String signUpType;
    private String addressLine1;

}
