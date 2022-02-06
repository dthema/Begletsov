package com.example.developerslifeposts.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "posts", indices = @Index(value = {"id"}, unique = true))
public class Post {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    @Expose
    private int id;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    @Expose
    private String description;

    @SerializedName("gifURL")
    @ColumnInfo(name = "gifURL")
    @Expose
    private String gifURL;

    @ColumnInfo(name = "page")
    private int page;

    @ColumnInfo(name = "position")
    private int position;

    @ColumnInfo(name = "rankTop")
    private int rankTop = 0;

    @ColumnInfo(name = "rankHot")
    private int rankHot = 0;

    @ColumnInfo(name = "rankLatest")
    private int rankLatest = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGifURL() {
        return gifURL;
    }

    public void setGifURL(String gifURL) {
        this.gifURL = gifURL;
    }

    public int getPage() {
        return page;
    }

    public int getPosition() {
        return position;
    }

    public int getRankTop() {
        return rankTop;
    }

    public int getRankHot() {
        return rankHot;
    }

    public int getRankLatest() {
        return rankLatest;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setRankTop(int rankTop) {
        this.rankTop = rankTop;
    }

    public void setRankHot(int rankHot) {
        this.rankHot = rankHot;
    }

    public void setRankLatest(int rankLatest) {
        this.rankLatest = rankLatest;
    }
}