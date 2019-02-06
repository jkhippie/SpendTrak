package com.danasoft.spendtrak;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.jetbrains.annotations.Contract;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FragmentManager mFragmentManager;
    String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        displayFragment(TrackFragment.TAG);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_track:
                mCurrentFragment = displayFragment(TrackFragment.TAG);
                return true;
            case R.id.navigation_visualize:
                mCurrentFragment = displayFragment(VisualizeFragment.TAG);
                return true;
            case R.id.navigation_notifications:
                //mTextMessage.setText(R.string.title_notifications);
                return true;
        }
        return false;
    }

    @Contract("_ -> param1")
    private String displayFragment(@org.jetbrains.annotations.NotNull String fragmentTag) {
        Fragment fragment = null;
        switch(fragmentTag) {
            case TrackFragment.TAG:
                fragment = TrackFragment.newInstance();
                break;
            case VisualizeFragment.TAG:
                fragment = VisualizeFragment.newInstance();
                break;
        }
        assert fragment != null;
        mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        return  fragmentTag;
    }
}
