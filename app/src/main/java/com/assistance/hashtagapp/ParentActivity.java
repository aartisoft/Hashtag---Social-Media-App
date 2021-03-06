package com.assistance.hashtagapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.assistance.hashtagapp.Fragments.HomeFragment;
import com.assistance.hashtagapp.Fragments.InboxFragment;
import com.assistance.hashtagapp.Fragments.ProfileFragment;
import com.assistance.hashtagapp.Fragments.SearchFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ParentActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    CollectionReference userRef;
    ImageButton homeBtn, searchBtn, inboxBtn, profileBtn;
    ImageView homeDot, searchDot, inboxDot, profileDot;
    BottomAppBar bottomAppBar;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        //Transparent StatusBar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initViews();
        initFirebase();
        setBottomNavigation();

        homeBtn.performClick();
    }

    private void initViews() {
        homeBtn = findViewById(R.id.bottom_nav_home);
        homeDot = findViewById(R.id.bottom_nav_home_dot);
        searchBtn = findViewById(R.id.bottom_nav_search);
        searchDot = findViewById(R.id.bottom_nav_search_dot);
        inboxBtn = findViewById(R.id.bottom_nav_inbox);
        inboxDot = findViewById(R.id.bottom_nav_inbox_dot);
        profileBtn = findViewById(R.id.bottom_nav_profile);
        profileDot = findViewById(R.id.bottom_nav_profile_dot);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        fabAdd = findViewById(R.id.fab_btn_add);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseFirestore.getInstance().collection("Users");
    }

    private void setBottomNavigation() {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBtn.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                searchBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                inboxBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                profileBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                homeDot.setVisibility(View.VISIBLE);
                homeDot.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                searchDot.setVisibility(View.GONE);
                inboxDot.setVisibility(View.GONE);
                profileDot.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.parent_fragment_container, new HomeFragment()).commit();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBtn.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                homeBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                inboxBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                profileBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                searchDot.setVisibility(View.VISIBLE);
                searchDot.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                homeDot.setVisibility(View.GONE);
                inboxDot.setVisibility(View.GONE);
                profileDot.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.parent_fragment_container, new SearchFragment()).commit();
            }
        });

        inboxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxBtn.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                homeBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                searchBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                profileBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                inboxDot.setVisibility(View.VISIBLE);
                inboxDot.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                homeDot.setVisibility(View.GONE);
                searchDot.setVisibility(View.GONE);
                profileDot.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.parent_fragment_container, new InboxFragment()).commit();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileBtn.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                homeBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                searchBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                inboxBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.inactiveBottomNavColor)));
                profileDot.setVisibility(View.VISIBLE);
                profileDot.setImageTintList(ColorStateList.valueOf(getColor(android.R.color.white)));
                homeDot.setVisibility(View.GONE);
                searchDot.setVisibility(View.GONE);
                inboxDot.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.parent_fragment_container, new ProfileFragment()).commit();
            }
        });
    }

    public static void setWindowFlag(ParentActivity parentActivity, final int bits, boolean on) {
        Window window = parentActivity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }
}
