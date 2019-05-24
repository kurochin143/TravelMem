package com.isra.israel.travelmem.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.isra.israel.travelmem.R;

public class RegisterFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String token;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        final EditText firstNameEditText = view.findViewById(R.id.f_register_et_first_name);
        final EditText lastNameEditText = view.findViewById(R.id.f_register_et_last_name);
        final EditText emailEditText = view.findViewById(R.id.f_register_et_email);
        final EditText passwordEditText = view.findViewById(R.id.f_register_et_password);
        final ProgressBar progressBar = view.findViewById(R.id.f_register_pb_registering);

        view.findViewById(R.id.f_register_b_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // register

                String firstNameStr = firstNameEditText.getText().toString();
                // TODO check valid first name
                String lastNameStr = lastNameEditText.getText().toString();
                // TODO check valid last name
                String emailStr = emailEditText.getText().toString();
                // TODO check valid email
                String passwordStr = passwordEditText.getText().toString();
                // TODO check valid password

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_LONG).show();
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(RegisterFragment.this)
                                    .commit();
                        } else {
                            Toast.makeText(getContext(), "Failed to register, " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return view;
    }

}
