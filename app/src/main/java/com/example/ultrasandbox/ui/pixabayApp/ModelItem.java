package com.example.ultrasandbox.ui.pixabayApp;

public class ModelItem {

    private String imageUrl;
    private String author;
    private int likes;

    private int views;

    private int downloads;

    private String tags;

    public ModelItem() {
    }

    public ModelItem(String imageUrl, String author, int likes, int views, int downloads, String tags) {
        this.imageUrl = imageUrl;
        this.author = author;
        this.likes = likes;
        this.views = views;
        this.downloads = downloads;
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getViews() {
        return views;
    }

    public int getDownloads() {
        return downloads;
    }

    public String getAuthor() {
        return author;
    }

    public int getLikes() {
        return likes;
    }

    public String getTags() {
        return tags;
    }
}
