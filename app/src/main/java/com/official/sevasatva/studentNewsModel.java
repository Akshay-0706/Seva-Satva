package com.official.sevasatva;

public class studentNewsModel {
    private final String title;
    private final String description;
    private final String urlToImage;
    private final String content;
    private final String url;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public studentNewsModel(String title, String description, String urlToImage, String content, String url) {
        this.title = title;
        this.description = description;
        this.urlToImage = urlToImage;
        this.content = content;
        this.url = url;
    }
}
