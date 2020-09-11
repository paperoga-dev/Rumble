package com.github.rumble.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.rumble.R;
import com.github.rumble.api.array.CompletionInterface;
import com.github.rumble.blog.array.Followers;
import com.github.rumble.blog.simple.Info;
import com.github.rumble.exception.BaseException;
import com.github.rumble.user.array.Following;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Follow extends Base {
    private ArrayAdapter<String> followingButNotFollowersAdp;
    private ArrayAdapter<String> followersButNotFollowingAdp;
    private Set<String> followingSet;
    private Set<String> followersSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        this.followingButNotFollowersAdp = new ArrayAdapter<String>(this,R.layout.text_item);
        this.followersButNotFollowingAdp = new ArrayAdapter<String>(this,R.layout.text_item);

        ((ListView) findViewById(R.id.followersButNotFollowing)).setAdapter(this.followersButNotFollowingAdp);
        ((ListView) findViewById(R.id.followingButNotFollowers)).setAdapter(this.followingButNotFollowersAdp);

        this.followersSet = new HashSet<>();
        this.followingSet = new HashSet<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                fetchFollowing();
            }
        });
    }

    private void fetchFollowing()
    {
        getClient().call(
                com.github.rumble.user.array.Following.Api.class,
                0,
                -1,
                new CompletionInterface<Info.Base, Following.Data>() {
                    @Override
                    public void onFailure(BaseException e) {

                    }

                    @Override
                    public void onSuccess(List<Info.Base> result, int offset, int limit, int count) {
                        for (Info.Base blog : result)
                            followingSet.add(blog.getName());

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                fetchFollowers();
                            }
                        });
                    }
                }
        );
    }

    private void fetchFollowers()
    {
        getClient().call(
                com.github.rumble.blog.array.Followers.Api.class,
                getClient().getMe().getBlogs().get(0).getName(),
                0,
                -1,
                new CompletionInterface<Followers.User, Followers.Data>() {
                    @Override
                    public void onFailure(BaseException e) {

                    }

                    @Override
                    public void onSuccess(List<Followers.User> result, int offset, int limit, int count) {
                        for (Followers.User user : result)
                            followersSet.add(user.getName());

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                populateData();
                            }
                        });
                    }
                }
        );
    }

    private void populateData()
    {
        Set<String> followingButNotFollowersSet = new HashSet<>();
        followingButNotFollowersSet.addAll(followingSet);
        followingButNotFollowersSet.removeAll(followersSet);

        Set<String> followersButNotFollowingSet = new HashSet<>();
        followersButNotFollowingSet.addAll(followersSet);
        followersButNotFollowingSet.removeAll(followingSet);

        followersButNotFollowingAdp.addAll(followersButNotFollowingSet);
        followingButNotFollowersAdp.addAll(followingButNotFollowersSet);
    }
}