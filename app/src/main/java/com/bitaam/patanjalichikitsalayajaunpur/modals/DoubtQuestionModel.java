package com.bitaam.patanjalichikitsalayajaunpur.modals;

public class DoubtQuestionModel {

    String que,postDate,profileImgUrl,queImgUrl,name,auth;
    boolean visibility;

    public DoubtQuestionModel() {
    }

    public DoubtQuestionModel(String que, String postDate, String profileImgUrl, String queImgUrl, String name,boolean visibility,String auth) {
        this.que = que;
        this.postDate = postDate;
        this.profileImgUrl = profileImgUrl;
        this.queImgUrl = queImgUrl;
        this.name = name;
        this.visibility = visibility;
        this.auth = auth;
    }

    public String getQue() {
        return que;
    }

    public void setQue(String que) {
        this.que = que;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getQueImgUrl() {
        return queImgUrl;
    }

    public void setQueImgUrl(String queImgUrl) {
        this.queImgUrl = queImgUrl;
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

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
