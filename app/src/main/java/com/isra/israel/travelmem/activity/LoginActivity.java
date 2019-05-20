package com.isra.israel.travelmem.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.a_login_et_email);
        passwordEditText = findViewById(R.id.a_login_et_password);

        findViewById(R.id.a_login_btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmailAndPassword();
            }
        });

        findViewById(R.id.a_login_btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_login_c_root, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        firebaseAuth.addIdTokenListener(new FirebaseAuth.IdTokenListener() {
            @Override
            public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                // TODO save new token
                float x =0;
            }
        });
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
                                // TODO save token to sp

                                // TODO open TravelsMapActivity
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
}
