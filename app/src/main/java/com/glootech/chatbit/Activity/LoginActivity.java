   package com.glootech.chatbit.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glootech.chatbit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.protobuf.Api;

   public class LoginActivity extends AppCompatActivity {

    TextView sighUpBtn;
    EditText emailBox,passwordBox;
    Button signInBtn;
    FirebaseAuth auth;
    ProgressDialog dialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //google sign option implementation

//        googleIcon = findViewById(R.id.googleIcon);

//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this,gso);
//
//        googleIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn();
//            }
//        });


        sighUpBtn = findViewById(R.id.signUpBtn);
        signInBtn = findViewById(R.id.signInBtn);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        dialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        signInBtn.setOnClickListener(v -> {
            dialog.show();
            String email = emailBox.getText().toString();
            String pass = passwordBox.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass))
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Enter valid details",Toast.LENGTH_SHORT).show();
            }else if(!email.matches(emailPattern)){
                dialog.dismiss();
                emailBox.setError("Envalid email");
                Toast.makeText(getApplicationContext(),"Envalid email",Toast.LENGTH_SHORT).show();
            }
            else{
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        dialog.dismiss();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }else{
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"email or password wrong",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        sighUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
                finish();
            }
        });
    }
//    void signIn()
//    {
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent,1000);
//    }
//
//       @Override
//       protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//           super.onActivityResult(requestCode, resultCode, data);
//           Task<GoogleSignInAccount> task = null;
//           if (requestCode == 1000) {
//               task = GoogleSignIn.getSignedInAccountFromIntent(data);
//           }
//           try {
//               GoogleSignInAccount account = task.getResult(ApiException.class);
//               firebaseAuthWithGoogle(account.getIdToken());
//
//           } catch (ApiException e) {
//               Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT);
//           }
//       }
//       private void firebaseAuthWithGoogle(String idToken)
//       {
//           AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
//           auth.signInWithCredential(credential)
//                   .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                       @Override
//                       public void onComplete(@NonNull Task<AuthResult> task) {
//                           if(task.isSuccessful())
//                           {
//                               FirebaseUser user = auth.getCurrentUser();
//                               Toast.makeText(getApplicationContext(),"Google sign-in Succesfull",Toast.LENGTH_SHORT).show();
//                               startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//                               finish();
//                           }else
//                           {
//                               Toast.makeText(getApplicationContext(),"Google sign Failed",Toast.LENGTH_SHORT).show();
//                           }
//                       }
//                   });
//       }
   }