package com.isra.israel.travelmem.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressBar signingInProgressBar;
    private ImageView signInIconImageView;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.a_login_et_email);
        passwordEditText = findViewById(R.id.a_login_et_password);

        signingInProgressBar = findViewById(R.id.a_login_pb_signing_in);
        signInIconImageView = findViewById(R.id.a_login_i_sign_in_icon);

        findViewById(R.id.a_login_btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signingInProgressBar.setVisibility(View.VISIBLE);
                signInIconImageView.setVisibility(View.INVISIBLE);

                signInWithEmailAndPassword();
            }
        });

        findViewById(R.id.a_login_t_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,0, 0, android.R.anim.slide_out_right)
                        .add(R.id.a_login_fl_root, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

//        firebaseAuth.signInWithEmailAndPassword("test_email@testing.com", "test_password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                onRequestSignInComplete(task);
//            }
//        });

//        firebaseAuth.addIdTokenListener(new FirebaseAuth.IdTokenListener() {
//            @Override
//            public void onIdTokenChanged(@NonNull final FirebaseAuth firebaseAuth) {
//                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                if (firebaseUser != null) {
//                    firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<GetTokenResult> task) {
//                            if (task.isSuccessful()) {
//                                GetTokenResult getTokenResult = task.getResult();
//                                if (getTokenResult != null) {
//                                    String token = getTokenResult.getToken();
//                                    // save token
//                                    FirebaseSessionSPDAO.setUid(LoginActivity.this, firebaseUser.getUid());
//                                    FirebaseSessionSPDAO.setIdToken(LoginActivity.this, token);
//
//                                    Intent intent = new Intent(LoginActivity.this, TravelsActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    public void signInWithEmailAndPassword() {
        // "test_email@testing.com", "test_password"
        String emailStr = emailEditText.getText().toString();
        // TODO check valid email
        String passwordStr = passwordEditText.getText().toString();
        // TODO check valid password

        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                onRequestSignInComplete(task);
            }
        });
    }

    private void onRequestSignInComplete(@NonNull Task<AuthResult> task) {

        signingInProgressBar.setVisibility(View.INVISIBLE);
        signInIconImageView.setVisibility(View.VISIBLE);

        if (task.isSuccessful()) {
            AuthResult authResult = task.getResult();
            if (authResult != null) {
                final FirebaseUser firebaseUser = task.getResult().getUser();
                firebaseUser.getIdToken(true).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            GetTokenResult getTokenResult = task.getResult();
                            if (getTokenResult != null) {
                                String token = getTokenResult.getToken();
                                // save token
                                FirebaseSessionSPDAO.setUid(LoginActivity.this, firebaseUser.getUid());
                                FirebaseSessionSPDAO.setIdToken(LoginActivity.this, token);

                                Intent intent = new Intent(LoginActivity.this, TravelsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast toast = Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            }

            Toast toast = Toast.makeText(LoginActivity.this, "Sign in success", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_LONG);
            toast.show();

            if (task.getException() != null) {
                task.getException().printStackTrace();
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
