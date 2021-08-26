package com.bitaam.patanjalichikitsalayajaunpur.modals;

import java.io.Serializable;

public class NuskeModal implements Serializable {

    String name,nuskeName,vid,iconUrl,howE,howH,impE,impH,category,type;
    boolean visibility;

    public NuskeModal() {
    }

    public NuskeModal(String nuskeName, String vid, String iconUrl, String howE, String howH, String impE, String impH, String category, String type) {
        this.nuskeName = nuskeName;
        this.vid = vid;
        this.iconUrl = iconUrl;
        this.howE = howE;
        this.howH = howH;
        this.impE = impE;
        this.impH = impH;
        this.category = category;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getNuskeName() {
        return nuskeName;
    }

    public void setNuskeName(String nuskeName) {
        this.nuskeName = nuskeName;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
