package com.example.developerslifeposts.database;

import java.util.List;

import com.example.developerslifeposts.database.Post;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Page {

    @SerializedName("result")
    @Expose
    private final List<Post> result = null;

    @SerializedName("totalCount")
    @Expose
    private int totalCount;

    public List<Post> getResult() {
        return result;
    }

    public int getTotalCount() {
        return totalCount;
    }
}