package com.anshutiwari.baatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import Model.Chat;
import Model.User;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import viewModel.MessageAdapter;

public class MessageActivity extends AppCompatActivity {
    CircleImageView mCivMsgUser;
    TextView mTvMsgName;
    TextView mTvUserStatus;

    CircleImageView mFABSend;
    EditText mEtMessage;
    RecyclerView mRcMessage;


    FirebaseUser fUser;
    DatabaseReference reference;
    DatabaseReference showRef;

    MessageAdapter messageAdapter;
    List<Chat> chatList;

    String userId;
    Intent intent;
    Dialog dialog;

    //For last seen time
    Calendar calendar;
    String offlineTime;
    String msgTime;
    String msgDate;

    //Encryption Key
    private byte encryptionKey[] = {12, -81, 64, 49, 36, 25, -16, 9, 4, 100, 121, -69, -22, 21, 10, 98,12, -81, 64, 49, 36, -25, -16, 9, 7, 101, 121, 75, -22, 21, 10, -98};
    private Cipher cipher;
    private Cipher decipher;
    private SecretKeySpec secretKeySpec;

    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mCivMsgUser = findViewById(R.id.civ_profile_pic);
        mTvMsgName = findViewById(R.id.tv_user_profile_name);
        mTvUserStatus = findViewById(R.id.tv_user_status);
        mFABSend = findViewById(R.id.fab_send_msg);
        mEtMessage = findViewById(R.id.et_message);


        dialog = new Dialog(MessageActivity.this);
        dialog.setContentView(R.layout.dailog_profile);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRcMessage = findViewById(R.id.rv_msg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mRcMessage.setLayoutManager(linearLayoutManager);
        //Secret key specification
        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

        intent = getIntent();

        userId = intent.getStringExtra("visit_user_id");

        //Coding for Dailog
        showUserInfo(userId);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);
                mTvMsgName.setText(username);

                String imageUrl = snapshot.child("imageURL").getValue(String.class);
                if (imageUrl.equals("default")) {
                    mCivMsgUser.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(imageUrl).into(mCivMsgUser);
                }

                if (!status.equals("online")) {
                    mTvUserStatus.setText(status);
                } else {
                    mTvUserStatus.setText(status);
                }

                readMessage(fUser.getUid(), userId, imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mFABSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String msg = mEtMessage.getText().toString();
                    calendar = Calendar.getInstance();
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
                    msgTime = time.format(calendar.getTime());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    msgDate = dateFormat.format(calendar.getTime());
                    if (!msg.equals("")) {
                        sendMessage(fUser.getUid(), userId, msg, msgDate, msgTime);
                    } else {
                        Toast.makeText(MessageActivity.this, "Message can't be empty", Toast.LENGTH_SHORT).show();
                    }
                    mEtMessage.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        seenMessage(userId);
    }

    private void showUserInfo(String userId) {

        CircleImageView mCivProfileImage = dialog.findViewById(R.id.profile_image);
        TextView mTvProfileName = dialog.findViewById(R.id.tv_profile_userName);
        TextView mTvProfilePhone = dialog.findViewById(R.id.tv_profile_phone);
        TextView mTvProfileAbout = dialog.findViewById(R.id.tv_about);

        showRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        showRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mTvProfileName.setText(user.getUsername());
                mTvProfilePhone.setText(user.getPhone());
                mTvProfileAbout.setText(user.getAbout());

                if (user.getImageURL().equals("default")) {
                    mCivProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    if (getApplicationContext() == null) {
                        return;
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(mCivProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenMessage(String userId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message, String msgDate, String msgTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //Cipher the text
        try {
            cipher = Cipher.getInstance("AES");
            String encrypt = AESEncryptionMethod(message);
            String msgMillis = String.valueOf(System.currentTimeMillis());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", encrypt);
            hashMap.put("msgDate", msgDate);
            hashMap.put("msgTime", msgTime);
            hashMap.put("isSeen", false);
            hashMap.put("msgMillis", msgMillis);

            reference.child("Chats").push().setValue(hashMap);

            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(fUser.getUid())
                    .child(userId);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        chatRef.child("id").setValue(userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }




    }

    private void readMessage(String myId, String userId, String imageURL) {
        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        chatList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(getApplicationContext(), chatList);
                    mRcMessage.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void moveToChat(View view) {
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void currentUser(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userId);
        editor.apply();
    }

    public void status(String status) {

        reference = FirebaseDatabase.getInstance().getReference().child("User").child(fUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        offlineTime = simpleDateFormat.format(calendar.getTime());
        reference.removeEventListener(seenListener);
        status("last seen at  " + offlineTime);
        currentUser("none");
    }

    public void viewUserProfile(View view) {
        dialog.show();
    }

    private String AESEncryptionMethod(String string) {
        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String returnString = null;

        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

}