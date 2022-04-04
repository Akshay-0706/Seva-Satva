package com.official.sevasatva;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface studentNewsRetrofit {
    @GET
    Call<studentNewsFetcherModel> getAllNews(@Url String url);

    @GET
    Call<studentNewsFetcherModel> getNewsByCategory(@Url String url);
}
