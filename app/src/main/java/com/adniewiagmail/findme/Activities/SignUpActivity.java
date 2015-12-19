package com.adniewiagmail.findme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adniewiagmail.findme.Activities.MainActivity.MainActivity;
import com.adniewiagmail.findme.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Adaś on 2015-11-12.
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText firstNameText;
    private EditText lastNameText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = (EditText)findViewById(R.id.usernameField);
        passwordEditText = (EditText)findViewById(R.id.passwordField);
        emailEditText = (EditText)findViewById(R.id.emailField);
        firstNameText = (EditText)findViewById(R.id.firstNameField);
        lastNameText = (EditText)findViewById(R.id.lastNameField);
        signUpButton = (Button)findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String firstName = firstNameText.getText().toString();
                String lastName = lastNameText.getText().toString();
                firstName = firstName.trim();
                lastName = lastName.trim();
                username = username.trim();
                password = password.trim();
                email = email.trim();
                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put("firstName", firstName);
                    newUser.put("lastName", lastName);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                startMainActivity();
                            } else {
                                errorToast(getString(R.string.errorRegistrationFailure) + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void errorToast(String message) {
        Toast toast = Toast.makeText(SignUpActivity.this, message, Toast
                .LENGTH_SHORT);
        toast.show();
    }

}