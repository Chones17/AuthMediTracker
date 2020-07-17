package com.example.authbeta;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * This is the main activity for the user and is a registration page. This
 * activity also contains the ability to create a login if a new user.
 *
 * @author Joaquin Solis, Tanner Olson and Travis Stirling.
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity {

    // Declare Firebase Authorization variable.
    private FirebaseAuth mAuth;

    // Declare Database variable.
    private DatabaseReference mDatabase;

    // Declare EditText variables.
    private EditText mRegisterName, mRegisterEmail, mRegisterPassword;

    // Declare String variables.
    private String name;
    private String email;
    private String password;


    /**
     * Create the RegisterActivity and set the view of that activity.
     *
     * @param savedInstanceState Pass the state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set the variables to the user input.
        mRegisterName = findViewById(R.id.editTextRegisterName);
        mRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        mRegisterPassword = findViewById(R.id.editTextRegisterPassword);

        // Initialize Firebase Authorization.
        mAuth = FirebaseAuth.getInstance();

        // Get instance of Database.
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    /**
     * Validates that the user has filled out all fields and that their password
     * is at least six digits.
     *
     * @param view Pass the view.
     */
    public void clickRegister(View view) {

        // Set the values given by the user.
        name = mRegisterName.getText().toString();
        email = mRegisterEmail.getText().toString();
        password = mRegisterPassword.getText().toString();

        // Conditional statement to validate that all fields are not empty.
        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){

            // Conditional statement to validate password has at least six digits.
            if (password.length() >= 6){

                // Calls the method to Register the user.
                registerUser();
            } else {

                // If password is less than six digits, notify user of minimum requirement.
                Toast.makeText(RegisterActivity.this,
                        "The password needs at least six characters.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            // If some fields are empty, notify the user of minimum requirement.
            Toast.makeText(RegisterActivity.this,
                    "All field must be completed to register.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Create the Login activity if the user selects 'I Have An Account'.
     *
     * @param view Pass the view.
     */
    public void clickAccount(View view) {

        // Start the Login activity.
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

        // Finish Register activity.
        finish();
    }


    /**
     * Register the user and add that user to the Firebase Database.
     */
    private void registerUser(){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)

            /*
              Add the user to the Firebase Database.
              @param task Pass the task authentication.
             */
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    //create a map to upload the new values
                    // map is a data structure with a key and a value
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", password);

                    String id = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Patients").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                        /**
                         * Start the ProfileActivity.
                         * @param task2 Passes the task authentication.
                         */
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){

                                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                                finish();
                            }

                        }
                    });



                } else {
                    Toast.makeText(RegisterActivity.this, "There is a problem with register", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    /**
     * Validates the current user against the firebase database. If the user exists, start
     * the Profile activity.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Conditional statement to validate whether the user exists in database.
        if (mAuth.getCurrentUser() != null){

            // If the user does exist, start the Profile activity.
            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));

            // Finish Register activity.
            finish();
        }
    } 
}
