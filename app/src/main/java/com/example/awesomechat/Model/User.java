package com.example.awesomechat.Model;

public class User {
    private String name;
    private String email;
    private String id;
    private int getAvatarMockUpResource;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGetAvatarMockUpResource() {
        return getAvatarMockUpResource;
    }

    public void setGetAvatarMockUpResource(int getAvatarMockUpResource) {
        this.getAvatarMockUpResource = getAvatarMockUpResource;
    }
}
