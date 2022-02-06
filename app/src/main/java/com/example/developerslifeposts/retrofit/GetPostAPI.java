package com.example.developerslifeposts.retrofit;

import com.example.developerslifeposts.database.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetPostAPI {
    @GET("{category}/{page}?json=true")
    Call<Page> getPage(@Path("category") String category, @Path("page") int page);
}
