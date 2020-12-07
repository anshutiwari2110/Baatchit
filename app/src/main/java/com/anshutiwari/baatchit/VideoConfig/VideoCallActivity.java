package com.anshutiwari.baatchit.VideoConfig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.anshutiwari.baatchit.DashboardActivity;
import com.anshutiwari.baatchit.MessageActivity;
import com.anshutiwari.baatchit.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import android.opengl.GLSurfaceView;
import android.widget.ImageView;


import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoCallActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_KEY = "47019224";
    private static String SESSION_ID = "1_MX40NzAxOTIyNH5-MTYwNzMxNjYyMTA5OX5CNFVzRG5LQy9XdnlsKzFOUEtWRS9qRXB-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NzAxOTIyNCZzaWc9OTQyNThjNDJiNzFiZmU3YmI1MmExNjNjYTMxMDc3NTM5MjcxNjUzMjpzZXNzaW9uX2lkPTFfTVg0ME56QXhPVEl5Tkg1LU1UWXdOek14TmpZeU1UQTVPWDVDTkZWelJHNUxReTlYZG5sc0t6Rk9VRXRXUlM5cVJYQi1mZyZjcmVhdGVfdGltZT0xNjA3MzE2Njg1Jm5vbmNlPTAuMjY1MzQyMTcyMjAxNjM4OSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjA5OTA4Njg0JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = VideoCallActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    ImageView mIvDisconnected;
    DatabaseReference userRef;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        mIvDisconnected = findViewById(R.id.btn_disconnected);

        mIvDisconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(userId).hasChild("Ringing")){
                            userRef.child(userId).child("Ringing").removeValue();
                            if(mPublisher !=null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber !=null){
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoCallActivity.this, DashboardActivity.class));
                            finish();
                        }
                        if(dataSnapshot.child(userId).hasChild("Calling")){
                            userRef.child(userId).child("Calling").removeValue();
                            if(mPublisher !=null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber !=null){
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoCallActivity.this, DashboardActivity.class));
                            finish();
                        }
                        else{
                            startActivity(new Intent(VideoCallActivity.this,DashboardActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);


            // initialize and connect to the session
            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onConnected(Session session) {

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }
}