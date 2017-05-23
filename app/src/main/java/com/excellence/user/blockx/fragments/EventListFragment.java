package com.excellence.user.blockx.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.excellence.user.blockx.R;

/**
 * Created by User on 2/19/2017.
 */

public class EventListFragment extends Fragment {
    private View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_news_feed, container, false);

        initViews();

        getData();

        return view;
    }

    private void initViews() {
    }
    private void getData(){

    }
}
