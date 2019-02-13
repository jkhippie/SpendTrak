package com.danasoft.spendtrak.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;
    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.tv_main_date)).setText(TextUtils.getDate());
        toolbar.setNavigationIcon(R.drawable.spendtrak_title);
        toolbar.setTitle("");
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
        mFragmentManager.beginTransaction().replace(R.id.content_frame,
                Objects.requireNonNull(fragment)).commit();
        return  fragmentTag;
    }
}
