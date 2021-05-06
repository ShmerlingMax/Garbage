package com.example.garbagekings;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_FRAGMENT_MAP = "map";
    private static final String TAG_FRAGMENT_ORDER = "order";
    private static final String TAG_FRAGMENT_PROFILE = "profile";
    private static Fragment currentFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        fragmentManager = getSupportFragmentManager();
        loadFragment(new MapsActivity(), TAG_FRAGMENT_MAP);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String tag = null;

        switch (item.getItemId()) {
            case R.id.navigation_map:
                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_MAP);
                if (fragment == null)
                {
                    fragment = new MapsActivity();
                }
                tag = TAG_FRAGMENT_MAP;
                break;

            case R.id.navigation_order:
                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ORDER);
                if (fragment == null)
                {
                    fragment = new OrderActivity();
                }
                tag = TAG_FRAGMENT_ORDER;
                break;

            case R.id.navigation_profile:
                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_PROFILE);
                if (fragment == null)
                {
                    fragment = new ProfileActivity();
                }
                tag = TAG_FRAGMENT_PROFILE;
                break;
        }
        return loadFragment(fragment, tag);
    }

    private boolean loadFragment(Fragment fragment, String tag) {
        //switching fragment
        if (fragment != null && !fragment.equals(currentFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, tag)
                    .commit();
            currentFragment = fragment;
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}