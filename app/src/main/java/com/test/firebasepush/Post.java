package com.test.firebasepush;

public class Post {

    String name;
    String designation;
    String postId;

    public Post(String name, String designation, String postId) {
        this.name = name;
        this.designation = designation;
        this.postId = postId;
    }

    public Post(){

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
