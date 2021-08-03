package com.bitaam.patanjalichikitsalayajaunpur.modals;

import java.io.Serializable;

public class UserModal implements Serializable {

    String name,role,phone,shopStatus,doctorStatus;

    public UserModal() {
    }

    public UserModal(String name, String role, String phone, String shopStatus, String doctorStatus) {
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.shopStatus = shopStatus;
        this.doctorStatus = doctorStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getDoctorStatus() {
        return doctorStatus;
    }

    public void setDoctorStatus(String doctorStatus) {
        this.doctorStatus = doctorStatus;
    }
}
