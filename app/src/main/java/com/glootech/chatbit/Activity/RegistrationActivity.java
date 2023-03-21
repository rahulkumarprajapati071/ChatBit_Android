package com.glootech.chatbit.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.glootech.chatbit.R;
import com.glootech.chatbit.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView signInBtn;
    CircleImageView profile_image;
    EditText nameBox,emailBox,passwordBox,confirmPasswordBox;
    Button signUpBtn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;
    ProgressDialog dialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";
    String imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        signInBtn = findViewById(R.id.signInBtn);
        profile_image = findViewById(R.id.profile_image);
        nameBox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        confirmPasswordBox = findViewById(R.id.confirmPasswordBox);
        signUpBtn = findViewById(R.id.signUpBtn);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameBox.getText().toString();
                String email = emailBox.getText().toString();
                String password = passwordBox.getText().toString();
                String confirmPassword = confirmPasswordBox.getText().toString();
                String status = "Hey! I'm new user";
                dialog.show();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword))
                {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Enter valid details",Toast.LENGTH_SHORT).show();
                }
                else if(!email.matches(emailPattern))
                {
                    dialog.dismiss();
                    emailBox.setError("Enter valid email");
                    Toast.makeText(getApplicationContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword))
                {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                                StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

                                if(imageUri != null)
                                {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users users = new Users(name,email,imageURI, auth.getUid(),status);

                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    dialog.dismiss();
                                                                    startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                                    finish();
                                                                }else
                                                                {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else
                                {
                                    dialog.dismiss();
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/chatbit-65696.appspot.com/o/profile.png?alt=media&token=afecc21d-6fc1-4f79-abc4-2f80d060b3c1";
                                    Users users = new Users(name,email,imageURI, auth.getUid(),status);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                            } else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            if(data != null)
            {
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}