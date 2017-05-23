package com.excellence.user.blockx.activites;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.excellence.user.blockx.R;
import com.excellence.user.blockx.fragments.CalendarFragment;
import com.excellence.user.blockx.fragments.NewsListFragment;
import com.excellence.user.blockx.fragments.PostFragment;
import com.excellence.user.blockx.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        fragmentManager = getSupportFragmentManager();
        mFrameLayout = (FrameLayout) findViewById(R.id.main_container);
        FragmentTransaction transaction1 = fragmentManager.beginTransaction();
        transaction1.replace(R.id.main_container, new NewsListFragment()).commit();

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.homeItem:
                        fragment = new NewsListFragment();
                        break;
                    case R.id.eventItem:
                        fragment = new PostFragment();
                        break;
                    case R.id.calendarItem:
                        fragment = new CalendarFragment();
                        break;
                    case R.id.proItem:
                        fragment = new ProfileFragment();
                        break;

                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });

    }
}
