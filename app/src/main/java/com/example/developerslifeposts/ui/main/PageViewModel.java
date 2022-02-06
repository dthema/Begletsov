package com.example.developerslifeposts.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.developerslifeposts.Repository;
import com.example.developerslifeposts.database.Post;

import java.util.List;

public class PageViewModel extends AndroidViewModel {

    private static final int GET_LATEST = 1, GET_TOP = 2, GET_HOT = 3;
    private static final int INC = 10, DEC = 11;
    private int latestPosition = 0, topPosition = 0, hotPosition = 0;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private final Repository repository;
    private static final String LATEST = "latest";
    private static final String TOP = "top";
    private static final String HOT = "hot";
    private boolean latestOnError = false, topOnError = false, hotOnError = false;

    public PageViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<List<Post>> getPostLiveData() {
        if (mIndex.getValue() == null) return null;
        switch (mIndex.getValue()) {
            case GET_LATEST: return repository.getLatestPosts;
            case GET_TOP: return repository.getTopPosts;
            case GET_HOT: return repository.getHotPosts;
            default: return null;
        }
    }

    public String getCategory() {
        if (mIndex.getValue() == null) return null;
        switch (mIndex.getValue()) {
            case GET_LATEST: return LATEST;
            case GET_TOP: return TOP;
            case GET_HOT: return HOT;
            default: return null;
        }
    }

    private int postPosition(int option) {
        if (mIndex.getValue() == null)
            return 0;
        int currentPosition = 0;
        switch (mIndex.getValue()) {
            case GET_LATEST: currentPosition = option == INC ? ++latestPosition :
                option == DEC ? --latestPosition : latestPosition;
            case GET_TOP: currentPosition = option == INC ? ++topPosition :
                    option == DEC ? --topPosition : topPosition;
            case GET_HOT: currentPosition = option == INC ? ++hotPosition :
                    option == DEC ? --hotPosition : hotPosition;
        }
        return currentPosition;
    }

    public int postPosition() {
        return postPosition(0);
    }

    public int nextPostPosition() {
        List<Post> list = getPostLiveData().getValue();
        if (list == null || list.size() == 0)
            return 0;
        int result = postPosition(INC);
        if (result >= list.size())
            return postPosition(DEC);
        return result;
    }

    public int prevPostPosition() {
        return postPosition(DEC);
    }

    public boolean isPostLast() {
        List<Post> list = getPostLiveData().getValue();
        if (mIndex.getValue() == null || list == null || list.size() == 0)
            return true;
        return postPosition() == list.size()-1;
    }

    public int getPositionAtPage() {
        List<Post> list = getPostLiveData().getValue();
        if (list == null || list.size() == 0)
            return 0;
        return list.get(list.size() - 1).getPosition();
    }

    public int getNextPositionAtPage() {
        List<Post> list = getPostLiveData().getValue();
        if (list == null || list.size() == 0)
            return 0;
        int position = list.get(list.size() - 1).getPosition();
        return position == 4 ? 0 : position+1;
    }

    public int getNextPostPage() {
        List<Post> list = getPostLiveData().getValue();
        if (list == null || list.size() < 4)
            return 0;
        int page = list.get(list.size() - 1).getPage();
        int position = getPositionAtPage();
        return position == 4 ? page + 1 : page;
    }

    public int getCurrentListSize() {
        List<Post> list = getPostLiveData().getValue();
        if (list == null)
            return 0;
        return list.size();
    }

    public void postSetRank(Post post, int rank) {
        if (mIndex.getValue() == null) return;
        switch (mIndex.getValue()) {
            case GET_LATEST:
                post.setRankLatest(rank);
                break;
            case GET_TOP:
                post.setRankTop(rank);
                break;
            case GET_HOT:
                post.setRankHot(rank);
                break;
        }
    }

    public Post getPostById(int id) {
        return repository.getPostById(id);
    }

    public void addPost(Post post) {
        repository.insert(post);
    }

    public void updatePost(Post post) {
        repository.updatePost(post);
    }

    public void setOnError(boolean onError) {
        if (mIndex.getValue() == null) return;
        switch (mIndex.getValue()) {
            case GET_LATEST: latestOnError = onError;
            case GET_TOP: topOnError = onError;
            case GET_HOT: hotOnError = onError;
        }
    }

    public boolean isOnError() {
        if (mIndex.getValue() == null) return false;
        switch (mIndex.getValue()) {
            case GET_LATEST: return latestOnError;
            case GET_TOP: return topOnError;
            case GET_HOT: return hotOnError;
        }
        return false;
    }
}