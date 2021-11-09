package com.example.ihome;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ihome.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private String phonePattern = "^(01)[0-46-9]*[0-9]{7,8}$";

    private TextInputEditText fullnameEt, usernameEt, phoneEt, emailEt, addressEt;
    private TextView changeTv;
    private ImageView closeIv, checkIv;

    private de.hdodenhof.circleimageview.CircleImageView profileIv;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference, fileReference;
    private StorageTask storageTask;

    private Uri resultUri, downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changeTv = findViewById(R.id.change);
        closeIv = findViewById(R.id.close);
        checkIv = findViewById(R.id.check);
        profileIv = findViewById(R.id.profile);
        fullnameEt = findViewById(R.id.fullname);
        usernameEt = findViewById(R.id.username);
        phoneEt = findViewById(R.id.phone);
        emailEt = findViewById(R.id.email);
        addressEt = findViewById(R.id.address);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readprofile();

        changeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
            }
        });

        checkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname, phone, address;
                fullname = fullnameEt.getText().toString();
                phone = phoneEt.getText().toString();
                address = addressEt.getText().toString();

                if (TextUtils.isEmpty(fullname)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter full name",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter phone",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (!phone.matches(phonePattern)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid phone number\n(Only digits allowed)",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter address",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    updateprofile(fullname, phone, address);
                }
            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                uploadImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getApplicationContext(),
                        "Something gone wrong",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void uploadImage() {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        if (resultUri != null) {
            fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(resultUri));

            storageTask = fileReference.putFile(resultUri);

            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult();
                        String mImageUri = downloadUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("User")
                                .child(firebaseUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", "" + mImageUri);

                        databaseReference.updateChildren(map);

                        Toast.makeText(getApplicationContext(),
                                "Upload successfully",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void updateprofile(final String fullname, final String phone, final String address) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("User").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", fullname);
        map.put("phone", phone);
        map.put("address", address);

        databaseReference.updateChildren(map);

        Toast.makeText(getApplicationContext(),
                "Update successfully",
                Toast.LENGTH_SHORT)
                .show();

        finish();
    }

    public void readprofile() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                fullnameEt.setText(user.getFullname());
                usernameEt.setText(user.getUsername());
                phoneEt.setText(user.getPhone());
                emailEt.setText(user.getEmail());
                addressEt.setText(user.getAddress());

                Glide.with(getApplication()).load(user.getImage()).into(profileIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
