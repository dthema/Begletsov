package com.example.developerslifeposts.ui.main;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.developerslifeposts.R;
import com.example.developerslifeposts.database.Page;
import com.example.developerslifeposts.database.Post;
import com.example.developerslifeposts.databinding.FragmentMainBinding;
import com.example.developerslifeposts.retrofit.RetrofitCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LAST_POST_ERROR = "Публикации закончились.";
    private static final String CONNECTION_ERROR = "Не удалось загрузить пост.";
    private static final String PAGE_NOT_FOUND = "Страница не найдена.";

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;
    private LiveData<List<Post>> liveData;
    private Observer<List<Post>> observer;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null)
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        final FloatingActionButton next = binding.next;
        final FloatingActionButton prev = binding.prev;

        next.setOnClickListener(l -> {
            if (!pageViewModel.isPostLast()) {
                updateUI(pageViewModel.nextPostPosition());
                return;
            }
            getNextPost();
        });

        prev.setOnClickListener(l -> {
            if (pageViewModel.isOnError())
                updateUI(pageViewModel.postPosition());
            else
                updateUI(pageViewModel.prevPostPosition());
        });

        binding.imageText.setMovementMethod(new ScrollingMovementMethod());

        liveData = pageViewModel.getPostLiveData();

        final boolean[] isFirstOpen = {true};

        observer = posts -> {
            if (isFirstOpen[0]) {
                updateUI(pageViewModel.postPosition());
                isFirstOpen[0] = false;
            } else
                updateUI(pageViewModel.nextPostPosition());
        };

        liveData.observe(getViewLifecycleOwner(), observer);

        checkFirstPost();

        return root;
    }

    void getNextPost() {
        getNextPost(pageViewModel.getNextPostPage(), pageViewModel.getNextPositionAtPage(), true);
    }

    void checkFirstPost() {
        getNextPost(0, 0, false);
    }

    @SuppressLint("CheckResult")
    void updateUI(int position) {
        List<Post> posts = liveData.getValue();
        if (posts == null) return;
        binding.prev.setClickable(position > 0);
        if (posts.size() > 0) {
            pageViewModel.setOnError(false);
            binding.imageText.setText(posts.get(position).getDescription());
            String url = posts.get(position).getGifURL();
            StringBuilder httpsBuilder = new StringBuilder(url);
            if (httpsBuilder.charAt(4) != 's')
                httpsBuilder.insert(4, 's');
            binding.progressBar.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(httpsBuilder.toString())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_error));
                            return false;
                        }
                    })
                    .into(binding.imageView);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void errorUpdateUI(String error) {
        pageViewModel.setOnError(true);
        binding.imageText.setText(error);
        if (error.equals(LAST_POST_ERROR))
            binding.imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_last_post));
        else
            binding.imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_error));
    }

    void getNextPost(int nextPostPage, int nextPostPositionAtPage, boolean updatePost) {
        RetrofitCallback callback = new RetrofitCallback();
        String category = pageViewModel.getCategory();
        if (category != null) {
            callback.getPage(category, nextPostPage).enqueue(new Callback<Page>() {
                @Override
                public void onResponse(@NonNull Call<Page> call, @NonNull Response<Page> response) {
                    if (response.isSuccessful()) {
                        Page page = response.body();
                        if (page == null) {
                            errorUpdateUI(PAGE_NOT_FOUND);
                            return;
                        }
                        if (pageViewModel.getCurrentListSize() == page.getTotalCount()) {
                            errorUpdateUI(LAST_POST_ERROR);
                            return;
                        }
                        Post newPost = page.getResult().get(nextPostPositionAtPage);
                        Post post = pageViewModel.getPostById(newPost.getId());
                        if (post != null && post.getId() == newPost.getId()) {
                            if (updatePost) {
                                pageViewModel.postSetRank(post, (nextPostPage * 5) + nextPostPositionAtPage + 1);
                                pageViewModel.updatePost(post);
                            }
                        } else {
                            newPost.setPosition(nextPostPositionAtPage);
                            newPost.setPage(nextPostPage);
                            pageViewModel.postSetRank(newPost, (nextPostPage * 5) + nextPostPositionAtPage + 1);
                            pageViewModel.addPost(newPost);
                        }
                        updateUI(pageViewModel.postPosition());
                    } else
                        errorUpdateUI(CONNECTION_ERROR);
                }

                @Override
                public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {
                    errorUpdateUI(CONNECTION_ERROR);
                }
            });
        }
    }

    @SuppressLint("WrongThread")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        liveData.removeObserver(observer);
    }
}