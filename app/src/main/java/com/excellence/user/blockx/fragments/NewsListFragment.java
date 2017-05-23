package com.excellence.user.blockx.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.excellence.user.blockx.R;
import com.excellence.user.blockx.adapters.NewsListAdapter;
import com.excellence.user.blockx.models.Post;
import com.excellence.user.blockx.util.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/19/2017.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private List<Post> posts;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_news_feed, container, false);

        initViews();

        getData();

        setUpViews();

        return view;
    }

    private void initViews() {
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        //Post.deleteAll(Post.class);
    }

    public void setUpViews(){

        posts = new ArrayList<Post>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsListAdapter = new NewsListAdapter(getContext());
        recyclerView.setAdapter(newsListAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                newsListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void loadData(JSONObject response){
        try {
            JSONArray feedArray = response.getJSONArray("postinfo");
            for (int i = 0; i < feedArray.length(); i++) {
                Post post1 = Post.findById(Post.class, i);
                if(post1 == null) {
                    JSONObject obj = (JSONObject) feedArray.get(i);
                    Post post = new Post(
                            i,
                            obj.getString("text"),
                            obj.getString("profilepic"),
                            obj.getString("displayname"),
                            obj.getString("position"),
                            obj.getString("photos"),
                            obj.getString("videos"),
                            obj.getString("thumbnail")
                    );
                    post.save();
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        //newsListAdapter = new NewsListAdapter(posts, getContext());
        //recyclerView.setAdapter(newsListAdapter);
    }

    private void getData(){

        //Creating a json array request to get the json from our api
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_FETCH, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Displaying our recycler
                        loadData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding our request to the queue
        requestQueue.add(jsonObjectRequest);
        recyclerView.smoothScrollToPosition(0);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getData();
        newsListAdapter.notifyDataSetChanged();
    }
}
