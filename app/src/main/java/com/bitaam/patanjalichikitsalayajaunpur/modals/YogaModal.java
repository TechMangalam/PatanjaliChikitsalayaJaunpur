package com.bitaam.patanjalichikitsalayajaunpur.modals;

import java.io.Serializable;

public class YogaModal implements Serializable {

    String yogaName,vid,iconUrl,howE,howH,impE,impH,notE,notH,category;

    public YogaModal() {
    }

    public YogaModal(String yogaName, String vid, String iconUrl, String howE, String howH, String impE, String impH, String notE, String notH,String category) {
        this.yogaName = yogaName;
        this.vid = vid;
        this.iconUrl = iconUrl;
        this.howE = howE;
        this.howH = howH;
        this.impE = impE;
        this.impH = impH;
        this.notE = notE;
        this.notH = notH;
        this.category = category;
    }

    public String getYogaName() {
        return yogaName;
    }

    public void setYogaName(String yogaName) {
        this.yogaName = yogaName;
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

    public String getHowE() {
        return howE;
    }

    public void setHowE(String howE) {
        this.howE = howE;
    }

    public String getHowH() {
        return howH;
    }

    public void setHowH(String howH) {
        this.howH = howH;
    }

    public String getImpE() {
        return impE;
    }

    public void setImpE(String impE) {
        this.impE = impE;
    }

    public String getImpH() {
        return impH;
    }

    public void setImpH(String impH) {
        this.impH = impH;
    }

    public String getNotE() {
        return notE;
    }

    public void setNotE(String notE) {
        this.notE = notE;
    }

    public String getNotH() {
        return notH;
    }

    public void setNotH(String notH) {
        this.notH = notH;
    }
}
