package fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anshutiwari.baatchit.DashboardActivity;
import com.anshutiwari.baatchit.LoginActivity;
import com.anshutiwari.baatchit.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    CircleImageView mCivProfileImage;
    RelativeLayout mRlLogout;

    TextView mTvProfileName;
    TextView mTvProfilePhone;
    TextView mTvProfileAbout;

    EditText mEtEditName;
    EditText mEtEditPhone;
    EditText mEtEditAbout;

    ImageView mIvEditName;
    ImageView mIvEditPhone;
    ImageView mIvEditAbout;

    ImageView mIvDoneName;
    ImageView mIvDonePhone;
    ImageView mIvDoneAbout;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mCivProfileImage = view.findViewById(R.id.profile_image);
        mRlLogout = view.findViewById(R.id.rl_logout);

        mTvProfileName = view.findViewById(R.id.tv_profile_userName);
        mTvProfilePhone = view.findViewById(R.id.tv_profile_phone);
        mTvProfileAbout = view.findViewById(R.id.tv_about);

        mEtEditName = view.findViewById(R.id.et_edited_userName);
        mEtEditPhone = view.findViewById(R.id.et_edit_phone);
        mEtEditAbout = view.findViewById(R.id.et_edit_about);

        mIvDoneAbout = view.findViewById(R.id.iv_done_about);
        mIvDoneName = view.findViewById(R.id.iv_done_username);
        mIvDonePhone = view.findViewById(R.id.iv_done_phone);

        mIvEditAbout = view.findViewById(R.id.iv_edit_about);
        mIvEditName = view.findViewById(R.id.iv_edit_username);
        mIvEditPhone = view.findViewById(R.id.iv_edit_phone);

        mRlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileLogout();
            }
        });
        mIvEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvProfilePhone.setVisibility(View.GONE);
                mIvEditPhone.setVisibility(View.GONE);
                mEtEditPhone.setVisibility(View.VISIBLE);
                mIvDonePhone.setVisibility(View.VISIBLE);

                mIvDonePhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String editPhone = mEtEditPhone.getText().toString();
                        if (editPhone.isEmpty()){
                            mEtEditPhone.setError("Field can't be vacant");
                        }else{
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("phone",editPhone);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("User")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mTvProfilePhone.setVisibility(View.VISIBLE);
                                    mTvProfilePhone.setText(editPhone);
                                    mIvEditPhone.setVisibility(View.VISIBLE);
                                    mEtEditPhone.setVisibility(View.GONE);
                                    mIvDonePhone.setVisibility(View.GONE);
                                }
                            });
                        }

                    }
                });

            }
        });
        mIvEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvProfileName.setVisibility(View.GONE);
                mIvEditName.setVisibility(View.GONE);
                mEtEditName.setVisibility(View.VISIBLE);
                mIvDoneName.setVisibility(View.VISIBLE);

                mIvDoneName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String editName = mEtEditName.getText().toString();
                        if (editName.isEmpty()){
                            mEtEditName.setError("Field can't be vacant");
                        }else{
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("username",editName);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("User")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mTvProfileName.setVisibility(View.VISIBLE);
                                    mTvProfileName.setText(editName);
                                    mIvEditName.setVisibility(View.VISIBLE);
                                    mEtEditName.setVisibility(View.GONE);
                                    mIvDoneName.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });

            }
        });
        mIvEditAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvProfileAbout.setVisibility(View.GONE);
                mIvEditAbout.setVisibility(View.GONE);
                mEtEditAbout.setVisibility(View.VISIBLE);
                mIvDoneAbout.setVisibility(View.VISIBLE);

                mIvDoneAbout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String editAbout = mEtEditAbout.getText().toString();
                        if (editAbout.isEmpty()){
                            mEtEditAbout.setError("Field can't be vacant");
                        }else{
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("about",editAbout);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("User")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mTvProfileAbout.setVisibility(View.VISIBLE);
                                    mTvProfileAbout.setText(editAbout);
                                    mIvEditAbout.setVisibility(View.VISIBLE);
                                    mEtEditAbout.setVisibility(View.GONE);
                                    mIvDoneAbout.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mTvProfileName.setText(user.getUsername());
                mTvProfilePhone.setText(user.getPhone());
               mTvProfileAbout.setText(user.getAbout());


                if (user.getImageURL().equals("default")) {
                    mCivProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    if (getActivity() == null) {
                        return;
                    } else {
                        Glide.with(getActivity()).load(user.getImageURL()).into(mCivProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mCivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        return view;
    }

    private void profileLogout() {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("token","default");

        FirebaseDatabase.getInstance().getReference()
                .child("User")
                .child(FirebaseAuth.getInstance().getUid())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Calendar calendar=Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                        String offlineTime = simpleDateFormat.format(calendar.getTime());
                        status("last seen at  " + offlineTime);
                        startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                });
    }

    public void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = imageReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", mUri);

                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

}