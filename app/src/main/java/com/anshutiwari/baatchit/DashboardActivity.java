package com.anshutiwari.baatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import Model.Chat;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import fragment.ChatFragment;
import fragment.ProfileFragment;
import fragment.UserFragment;
import viewModel.HomePagerAdapter;

public class DashboardActivity extends AppCompatActivity {

    CircleImageView mCivDP;
    TextView mTvProfileName;
    ViewPager mVpHome;
    TabLayout mTabHome;

    String offlineTime;

    DatabaseReference userRef;
    private String currentUserId;
    List<User> userList;
    HomePagerAdapter homePagerAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mCivDP = findViewById(R.id.civ_profile_pic);
        mTvProfileName = findViewById(R.id.tv_profile_name);
        mVpHome = findViewById(R.id.home_viewPager);
        mTabHome = findViewById(R.id.tab_layout_home);

        userList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("User");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = firebaseUser.getUid();
        reference = database.getReference("User").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String username = snapshot.child("username").getValue(String.class);
                mTvProfileName.setText(username);
                String imageUrl = snapshot.child("imageURL").getValue(String.class);
                if (imageUrl.equals("default")) {
                    mCivDP.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(imageUrl).into(mCivDP);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DashboardActivity.this, "Error :" + error.toException(), Toast.LENGTH_SHORT).show();
            }
        });



        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.getisSeen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    homePagerAdapter.addFragment(new ChatFragment(), "Chats");
                }else {
                    homePagerAdapter.addFragment(new ChatFragment(), "Chats");
                }

                homePagerAdapter.addFragment(new UserFragment(), "Contacts");
                homePagerAdapter.addFragment(new ProfileFragment(), "Profile");

                mVpHome.setAdapter(homePagerAdapter);
                mTabHome.setupWithViewPager(mVpHome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    //Video call
    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        offlineTime = simpleDateFormat.format(calendar.getTime());
        status("last seen at  " + offlineTime);

    }
}