package com.example.developerslifeposts.retrofit;

import com.example.developerslifeposts.database.Page;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCallback {

    public Call<Page> getPage(String category, int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://developerslife.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetPostAPI api = retrofit.create(GetPostAPI.class);
        return api.getPage(category, page);
    }
}
