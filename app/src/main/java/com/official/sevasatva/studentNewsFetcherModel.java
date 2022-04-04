package com.official.sevasatva;

import java.util.ArrayList;

public class studentNewsFetcherModel {
    private final ArrayList<studentNewsModel> articles;

    public ArrayList<studentNewsModel> getArticles() {
        return articles;
    }

    public studentNewsFetcherModel(ArrayList<studentNewsModel> articles) {
        this.articles = articles;
    }
}
