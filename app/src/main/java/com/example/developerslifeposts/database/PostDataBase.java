package com.example.developerslifeposts.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 1)
public abstract class PostDataBase extends RoomDatabase {
    public abstract PostDao postDao();
    public static volatile PostDataBase INSTANCE;

    public static PostDataBase getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(context, PostDataBase.class, "PostData")
                    .fallbackToDestructiveMigration()
                    .build();
        return INSTANCE;
    }
}
