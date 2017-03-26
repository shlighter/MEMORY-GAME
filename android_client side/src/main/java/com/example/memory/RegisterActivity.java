package com.example.memory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 *Register Activity. This Activity displays a registration form to send to the server by a Register object
 * @author Shai
 */

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password;
    Register jsnToSrvr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.editTextName);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
    }

    public void addUser(View view) {

        // Get Name Edit View Value
        String nameString = name.getText().toString().toLowerCase();
        // Get Email Edit View Value
        String emailString = email.getText().toString();
        // Get Password Edit View Value
        String passwordString = password.getText().toString();

        if (nameString.equals("0"))
            Toast.makeText(getApplicationContext(), "Please enter a different username", Toast.LENGTH_LONG).show();


        else if (Utility.isNotNull(emailString) && Utility.isNotNull(passwordString) && Utility.isNotNull(nameString)) {
            // When Email entered is Valid
            if (Utility.validate(emailString)) {
                jsnToSrvr = new Register(nameString, emailString, passwordString, RegisterActivity.this);
            }

            // When Email is invalid
            else {
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();

        }
    }
}