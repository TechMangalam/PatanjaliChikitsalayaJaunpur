package com.bitaam.patanjalichikitsalayajaunpur.modals;

import java.io.Serializable;

public class UpcharModal implements Serializable {

    String upcharName,vid,iconUrl,symptomE,symptomH,cureE,cureH,regimenE,regimenH,category;

    public UpcharModal() {
    }

    public UpcharModal(String upcharName, String vid, String iconUrl, String symptomE, String symptomH, String cureE, String cureH, String regimenE, String regimenH,String category) {
        this.upcharName = upcharName;
        this.vid = vid;
        this.iconUrl = iconUrl;
        this.symptomE = symptomE;
        this.symptomH = symptomH;
        this.cureE = cureE;
        this.cureH = cureH;
        this.regimenE = regimenE;
        this.regimenH = regimenH;
        this.category = category;
    }

    public String getUpcharName() {
        return upcharName;
    }

    public void setUpcharName(String upcharName) {
        this.upcharName = upcharName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getSymptomE() {
        return symptomE;
    }

    public void setSymptomE(String symptomE) {
        this.symptomE = symptomE;
    }

    public String getSymptomH() {
        return symptomH;
    }

    public void setSymptomH(String symptomH) {
        this.symptomH = symptomH;
    }

    public String getCureE() {
        return cureE;
    }

    public void setCureE(String cureE) {
        this.cureE = cureE;
    }

    public String getCureH() {
        return cureH;
    }

    public void setCureH(String cureH) {
        this.cureH = cureH;
    }

    public String getRegimenE() {
        return regimenE;
    }

    public void setRegimenE(String regimenE) {
        this.regimenE = regimenE;
    }

    public String getRegimenH() {
        return regimenH;
    }

    public void setRegimenH(String regimenH) {
        this.regimenH = regimenH;
    }
}
