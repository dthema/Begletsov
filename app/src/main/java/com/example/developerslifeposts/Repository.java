package com.example.developerslifeposts;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.developerslifeposts.database.Post;
import com.example.developerslifeposts.database.PostDao;
import com.example.developerslifeposts.database.PostDataBase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    public final PostDataBase dataBase;
    public final PostDao postDao;
    public final LiveData<List<Post>> getTopPosts;
    public final LiveData<List<Post>> getHotPosts;
    public final LiveData<List<Post>> getLatestPosts;

    public Repository(Application application) {
        dataBase = PostDataBase.getInstance(application);
        postDao = dataBase.postDao();
        getTopPosts = postDao.getTop();
        getHotPosts = postDao.getHot();
        getLatestPosts = postDao.getLatest();
    }

    public Post getPostById(int id) {
        try {
            return new DoOutAsync(postDao).execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePost(Post post) {
        new DoInAsync(postDao, DoInAsync.Options.UPDATE).execute(post);
    }

    public void insert(Post post) {
        new DoInAsync(postDao, DoInAsync.Options.INSERT).execute(post);
    }

    private static class DoInAsync extends AsyncTask<Post, Void, Void> {
        private final PostDao postDao;
        private final Options option;
        protected enum Options {
            INSERT,
            UPDATE,
        }

        public DoInAsync(PostDao postDao, Options option) {
            this.postDao = postDao;
            this.option = option;
        }

        @Override
        protected Void doInBackground(Post... posts) {
            switch (option) {
                case INSERT: postDao.insert(posts[0]);
                case UPDATE: postDao.update(posts[0]);
            }
            return null;
        }
    }

    private static class DoOutAsync extends AsyncTask<Integer, Void, Post> {
        private final PostDao postDao;

        public DoOutAsync(PostDao postDao) {
            this.postDao = postDao;
        }

        @Override
        protected Post doInBackground(Integer... ids) {
            return postDao.getById(ids[0]);
        }
    }
}
