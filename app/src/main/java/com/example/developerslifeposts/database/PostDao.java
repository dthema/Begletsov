package com.example.developerslifeposts.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx .room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM posts WHERE rankTop > 0 ORDER BY rankTop")
    LiveData<List<Post>> getTop();

    @Query("SELECT * FROM posts WHERE rankHot > 0 ORDER BY rankHot")
    LiveData<List<Post>> getHot();

    @Query("SELECT * FROM posts WHERE rankLatest > 0 ORDER BY rankLatest")
    LiveData<List<Post>> getLatest();

    @Query("SELECT * FROM posts WHERE id = :id")
    Post getById(int id);

    @Insert
    void insert(Post post);

    @Update
    void update(Post post);
}
