package com.bitaam.patanjalichikitsalayajaunpur.modals;

import java.io.Serializable;

public class PatientModel implements Serializable {

    String dateTime,name,phoneNo,gender,disease,reportImg,doctor,hospital;

    public PatientModel() {
    }

    public PatientModel(String dateTime, String name, String phoneNo, String gender, String disease, String reportImg, String doctor, String hospital) {
        this.dateTime = dateTime;
        this.name = name;
        this.phoneNo = phoneNo;
        this.gender = gender;
        this.disease = disease;
        this.reportImg = reportImg;
        this.doctor = doctor;
        this.hospital = hospital;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getReportImg() {
        return reportImg;
    }

    public void setReportImg(String reportImg) {
        this.reportImg = reportImg;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}
